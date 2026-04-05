package com.example.demo.service;

import com.example.demo.constant.HoaDonStatus;
import com.example.demo.constant.VeStatus;
import com.example.demo.dto.request.HoaDon.CreateHoaDonRequest;
import com.example.demo.dto.request.HoaDon.HoaDonDoAnRequest;
import com.example.demo.dto.response.HoaDonDetailResponse;
import com.example.demo.dto.response.HoaDonDoAnResponse;
import com.example.demo.dto.response.HoaDonVeResponse;
import com.example.demo.entity.ChiTietDoAn;
import com.example.demo.entity.DoAn;
import com.example.demo.entity.Ghe;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SuatChieu;
import com.example.demo.entity.Ve;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ChiTietDoAnRepository;
import com.example.demo.repository.DoAnRepository;
import com.example.demo.repository.GheRepository;
import com.example.demo.repository.HoaDonRepository;
import com.example.demo.repository.KhachHangRepository;
import com.example.demo.repository.SuatChieuRepository;
import com.example.demo.repository.VeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HoaDonService {

    private static final int HOLD_MINUTES = 10;
    private static final int CANCELLATION_LOCK_HOURS = 3;
    private static final String FOOD_ACTIVE_STATUS = "DANG_BAN";
    private static final EnumSet<VeStatus> ACTIVE_VE_STATUSES = EnumSet.of(VeStatus.DA_DAT, VeStatus.DA_THANH_TOAN);

    private final HoaDonRepository hoaDonRepository;
    private final KhachHangRepository khachHangRepository;
    private final SuatChieuRepository suatChieuRepository;
    private final GheRepository gheRepository;
    private final VeRepository veRepository;
    private final DoAnRepository doAnRepository;
    private final ChiTietDoAnRepository chiTietDoAnRepository;

    public HoaDonService(HoaDonRepository hoaDonRepository,
                         KhachHangRepository khachHangRepository,
                         SuatChieuRepository suatChieuRepository,
                         GheRepository gheRepository,
                         VeRepository veRepository,
                         DoAnRepository doAnRepository,
                         ChiTietDoAnRepository chiTietDoAnRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.khachHangRepository = khachHangRepository;
        this.suatChieuRepository = suatChieuRepository;
        this.gheRepository = gheRepository;
        this.veRepository = veRepository;
        this.doAnRepository = doAnRepository;
        this.chiTietDoAnRepository = chiTietDoAnRepository;
    }

    @Transactional
    public HoaDonDetailResponse createPendingInvoice(CreateHoaDonRequest request) {
        String maSuat = normalize(request.getMaSuat());
        String maKhachHang = normalize(request.getMaKhachHang());
        Set<String> dsGhe = sanitizeSeatIds(request.getDsGhe());
        List<FoodSelection> dsDoAn = sanitizeFoodItems(request.getDsDoAn());

        if (maKhachHang.isBlank()) {
            throw new BadRequestException("Ma khach hang khong duoc de trong");
        }

        if (!khachHangRepository.existsById(maKhachHang)) {
            throw new BadRequestException("Khong tim thay khach hang tuong ung voi ma dang nhap hien tai");
        }

        if (dsGhe.size() > 8) {
            throw new BadRequestException("Toi da 8 ghe cho moi hoa don");
        }

        releaseExpiredPendingInvoices(maSuat);

        SuatChieu suatChieu = suatChieuRepository.findById(maSuat)
                .orElseThrow(() -> new NotFoundException("Khong tim thay suat chieu"));

        List<Ghe> ghes = gheRepository.findAllById(dsGhe)
                .stream()
                .sorted(Comparator.comparing(Ghe::getSoHang).thenComparing(Ghe::getSoCot))
                .toList();

        if (ghes.size() != dsGhe.size()) {
            throw new BadRequestException("Co ghe khong ton tai trong he thong");
        }

        String maPhong = suatChieu.getPhongChieu().getMaPhong();
        List<String> gheSaiPhong = ghes.stream()
                .filter(ghe -> !maPhong.equals(ghe.getPhongChieu().getMaPhong()))
                .map(Ghe::getMaGhe)
                .toList();
        if (!gheSaiPhong.isEmpty()) {
            throw new ConflictException("Ghe khong thuoc phong cua suat chieu: " + String.join(", ", gheSaiPhong));
        }

        List<Ve> veDaCo = veRepository.findBySuatChieu_MaSuatAndGhe_MaGheInAndTrangThaiVeIn(maSuat, dsGhe, ACTIVE_VE_STATUSES)
                .stream()
                .filter(this::isTicketActive)
                .toList();

        if (!veDaCo.isEmpty()) {
            String dsGheDaDat = veDaCo.stream()
                    .map(ve -> ve.getGhe().getTenGhe())
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new ConflictException("Ghe da duoc giu hoac thanh toan: " + dsGheDaDat);
        }

        long tongGhePhong = gheRepository.countByPhongChieu_MaPhong(maPhong);
        long soVeDangHoatDong = veRepository.findBySuatChieu_MaSuatAndTrangThaiVeIn(maSuat, ACTIVE_VE_STATUSES)
                .stream()
                .filter(this::isTicketActive)
                .count();

        if (soVeDangHoatDong + dsGhe.size() > tongGhePhong) {
            throw new ConflictException("So ve cua suat chieu vuot suc chua phong");
        }

        Map<String, DoAn> doAnMap = loadFoodMap(dsDoAn);
        BigDecimal tongTienVe = calculateTicketTotal(suatChieu, ghes);
        BigDecimal tongTienDoAn = calculateFoodTotal(dsDoAn, doAnMap);
        BigDecimal tongTien = tongTienVe.add(tongTienDoAn);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaDon(generateId("HD", hoaDonRepository::existsById));

        KhachHang khachHangEntity = khachHangRepository.findById(maKhachHang)
                .orElseThrow(() -> new NotFoundException("Khong tim thay khach hang"));
        hoaDon.setKhachHang(khachHangEntity);
        hoaDon.setThoiGianDat(LocalDateTime.now());
        hoaDon.setTrangThai(HoaDonStatus.CHUA_THANH_TOAN);
        hoaDon.setTongTienVe(tongTienVe);
        hoaDon.setTongTienDoAn(tongTienDoAn);
        hoaDon.setTongTien(tongTien);
        hoaDon.setTongTienThanhToan(tongTien);

        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

        List<Ve> dsVe = new ArrayList<>();
        for (Ghe ghe : ghes) {
            BigDecimal giaVeCoBan = getTicketBasePrice(suatChieu);
            BigDecimal phuThu = getTicketSurcharge(ghe);

            Ve ve = new Ve();
            ve.setMaVe(generateId("VE", veRepository::existsById));
            ve.setHoaDon(savedHoaDon);
            ve.setSuatChieu(suatChieu);
            ve.setGhe(ghe);
            ve.setTrangThaiVe(VeStatus.DA_DAT);
            ve.setGiaVeCoBan(giaVeCoBan);
            ve.setPhuThu(phuThu);
            ve.setThanhTien(giaVeCoBan.add(phuThu));
            dsVe.add(ve);
        }

        try {
            veRepository.saveAll(dsVe);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Rat tiec, ghe ban chon vua co nguoi nhanh tay dat mat roi. Vui long tai lai trang va chon ghe khac.");
        }

        List<ChiTietDoAn> dsChiTietDoAn = createFoodLines(savedHoaDon, dsDoAn, doAnMap);
        if (!dsChiTietDoAn.isEmpty()) {
            chiTietDoAnRepository.saveAll(dsChiTietDoAn);
        }

        return toDetail(savedHoaDon, dsVe, dsChiTietDoAn);
    }

    @Transactional
    public HoaDonDetailResponse getDetail(String maDon) {
        HoaDon hoaDon = hoaDonRepository.findById(maDon)
                .orElseThrow(() -> new NotFoundException("Khong tim thay hoa don"));

        expireIfNeeded(hoaDon);
        List<Ve> dsVe = veRepository.findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(maDon);
        return toDetail(hoaDon, dsVe);
    }

    @Transactional
    public List<HoaDonDetailResponse> getByCustomer(String maKhachHang) {
        String customerId = normalize(maKhachHang);

        if (customerId.isBlank()) {
            throw new BadRequestException("Ma khach hang khong duoc de trong");
        }

        if (!khachHangRepository.existsById(customerId)) {
            throw new NotFoundException("Khong tim thay khach hang");
        }

        List<HoaDon> dsHoaDon = hoaDonRepository.findByKhachHang_MaKhachHangOrderByThoiGianDatDesc(customerId);
        List<HoaDonDetailResponse> results = new ArrayList<>();

        for (HoaDon hoaDon : dsHoaDon) {
            expireIfNeeded(hoaDon);
            List<Ve> dsVe = veRepository.findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(hoaDon.getMaDon());
            results.add(toDetail(hoaDon, dsVe));
        }

        return results;
    }

    @Transactional
    public HoaDonDetailResponse pay(String maDon, String phuongThucThanhToan) {
        HoaDon hoaDon = hoaDonRepository.findById(maDon)
                .orElseThrow(() -> new NotFoundException("Khong tim thay hoa don"));

        expireIfNeeded(hoaDon);

        if (hoaDon.getTrangThai() == HoaDonStatus.DA_HUY) {
            throw new ConflictException("Hoa don da bi huy");
        }

        if (hoaDon.getTrangThai() == HoaDonStatus.DA_THANH_TOAN) {
            return getDetail(maDon);
        }

        if (hoaDon.getTongTienThanhToan() == null) {
            hoaDon.setTongTienThanhToan(defaultIfNull(hoaDon.getTongTien()));
        }
        hoaDon.setTrangThai(HoaDonStatus.DA_THANH_TOAN);
        hoaDon.setThoiGianThanhToan(LocalDateTime.now());
        hoaDon.setPhuongThucThanhToan(normalize(phuongThucThanhToan));
        hoaDonRepository.save(hoaDon);

        List<Ve> dsVe = veRepository.findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(maDon);
        for (Ve ve : dsVe) {
            ve.setTrangThaiVe(VeStatus.DA_THANH_TOAN);
        }
        veRepository.saveAll(dsVe);

        return toDetail(hoaDon, dsVe);
    }

    @Transactional
    public HoaDonDetailResponse cancel(String maDon) {
        HoaDon hoaDon = hoaDonRepository.findById(maDon)
                .orElseThrow(() -> new NotFoundException("Khong tim thay hoa don"));

        if (hoaDon.getTrangThai() == HoaDonStatus.DA_THANH_TOAN) {
            throw new ConflictException("Khong the huy hoa don da thanh toan");
        }

        List<Ve> dsVe = veRepository.findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(maDon);
        if (isCancellationClosed(dsVe)) {
            throw new ConflictException("Khong the huy giu cho khi con duoi 3 gio truoc suat chieu hoac suat chieu da bat dau");
        }
        hoaDon.setTrangThai(HoaDonStatus.DA_HUY);
        hoaDonRepository.save(hoaDon);
        cancelTickets(dsVe);
        veRepository.saveAll(dsVe);

        return toDetail(hoaDon, dsVe);
    }

    @Transactional
    public HoaDonDetailResponse cancelPaidInvoice(String maDon) {
        HoaDon hoaDon = hoaDonRepository.findById(maDon)
                .orElseThrow(() -> new NotFoundException("Khong tim thay hoa don"));

        if (hoaDon.getTrangThai() == HoaDonStatus.DA_HUY) {
            return getDetail(maDon);
        }

        if (hoaDon.getTrangThai() != HoaDonStatus.DA_THANH_TOAN) {
            throw new ConflictException("Chi hoa don da thanh toan moi co the huy ve");
        }

        List<Ve> dsVe = veRepository.findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(maDon);
        if (isCancellationClosed(dsVe)) {
            throw new ConflictException("Khong the huy ve khi con duoi 3 gio truoc suat chieu hoac suat chieu da bat dau");
        }

        hoaDon.setTrangThai(HoaDonStatus.DA_HUY);
        hoaDonRepository.save(hoaDon);
        cancelTickets(dsVe);
        veRepository.saveAll(dsVe);

        return toDetail(hoaDon, dsVe);
    }

    protected void releaseExpiredPendingInvoices(String maSuat) {
        List<Ve> veHetHan = veRepository.findBySuatChieu_MaSuatAndHoaDon_TrangThaiAndHoaDon_ThoiGianDatBefore(
                maSuat,
                HoaDonStatus.CHUA_THANH_TOAN,
                LocalDateTime.now().minusMinutes(HOLD_MINUTES)
        );

        Set<String> maHoaDons = veHetHan.stream()
                .map(ve -> ve.getHoaDon().getMaDon())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String maDon : maHoaDons) {
            HoaDon hoaDon = hoaDonRepository.findById(maDon).orElse(null);
            if (hoaDon == null || hoaDon.getTrangThai() != HoaDonStatus.CHUA_THANH_TOAN) {
                continue;
            }
            hoaDon.setTrangThai(HoaDonStatus.DA_HUY);
            hoaDonRepository.save(hoaDon);
            List<Ve> dsVe = veRepository.findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(maDon);
            cancelTickets(dsVe);
            veRepository.saveAll(dsVe);
        }
    }

    protected void expireIfNeeded(HoaDon hoaDon) {
        if (hoaDon.getTrangThai() != HoaDonStatus.CHUA_THANH_TOAN) {
            return;
        }

        LocalDateTime thoiGianDat = hoaDon.getThoiGianDat();
        if (thoiGianDat == null || thoiGianDat.plusMinutes(HOLD_MINUTES).isAfter(LocalDateTime.now())) {
            return;
        }

        hoaDon.setTrangThai(HoaDonStatus.DA_HUY);
        hoaDonRepository.save(hoaDon);
        List<Ve> dsVe = veRepository.findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(hoaDon.getMaDon());
        cancelTickets(dsVe);
        veRepository.saveAll(dsVe);
    }

    private boolean isTicketActive(Ve ve) {
        if (ve.getTrangThaiVe() == VeStatus.DA_THANH_TOAN) {
            return true;
        }

        if (ve.getTrangThaiVe() != VeStatus.DA_DAT || ve.getHoaDon() == null) {
            return false;
        }

        return ve.getHoaDon().getTrangThai() == HoaDonStatus.CHUA_THANH_TOAN
                && ve.getHoaDon().getThoiGianDat() != null
                && ve.getHoaDon().getThoiGianDat().plusMinutes(HOLD_MINUTES).isAfter(LocalDateTime.now());
    }

    private BigDecimal calculateTicketTotal(SuatChieu suatChieu, Collection<Ghe> ghes) {
        BigDecimal basePrice = getTicketBasePrice(suatChieu);
        return ghes.stream()
                .map(ghe -> basePrice.add(getTicketSurcharge(ghe)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateFoodTotal(List<FoodSelection> dsDoAn, Map<String, DoAn> doAnMap) {
        return dsDoAn.stream()
                .map(item -> getFoodPrice(doAnMap.get(item.getMaDoAn())).multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ChiTietDoAn> createFoodLines(HoaDon hoaDon, List<FoodSelection> dsDoAn, Map<String, DoAn> doAnMap) {
        List<ChiTietDoAn> results = new ArrayList<>();
        for (FoodSelection item : dsDoAn) {
            DoAn doAn = doAnMap.get(item.getMaDoAn());
            BigDecimal donGia = getFoodPrice(doAn);

            ChiTietDoAn chiTiet = new ChiTietDoAn();
            chiTiet.setMaChiTietDoAn(generateId("CTD", chiTietDoAnRepository::existsById));
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setDoAn(doAn);
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setDonGia(donGia);
            chiTiet.setThanhTien(donGia.multiply(BigDecimal.valueOf(item.getSoLuong())));
            chiTiet.setGhiChu(item.getGhiChu());
            results.add(chiTiet);
        }
        return results;
    }

    private void cancelTickets(List<Ve> dsVe) {
        for (Ve ve : dsVe) {
            ve.setTrangThaiVe(VeStatus.DA_HUY);
        }
    }

    private boolean isCancellationClosed(List<Ve> dsVe) {
        LocalDateTime showtimeStart = getShowtimeStart(dsVe);
        if (showtimeStart == null) {
            return false;
        }

        return !showtimeStart.minusHours(CANCELLATION_LOCK_HOURS).isAfter(LocalDateTime.now());
    }

    private LocalDateTime getShowtimeStart(List<Ve> dsVe) {
        if (dsVe == null || dsVe.isEmpty()) {
            return null;
        }

        SuatChieu suatChieu = dsVe.get(0).getSuatChieu();
        if (suatChieu == null || suatChieu.getNgayChieu() == null || suatChieu.getGioChieu() == null) {
            return null;
        }

        return LocalDateTime.of(suatChieu.getNgayChieu(), suatChieu.getGioChieu());
    }

    private BigDecimal getTicketBasePrice(SuatChieu suatChieu) {
        return suatChieu.getGia() == null ? BigDecimal.ZERO : suatChieu.getGia();
    }

    private BigDecimal getTicketSurcharge(Ghe ghe) {
        return ghe.getLoaiGhe().getGiaPhuThu() == null ? BigDecimal.ZERO : ghe.getLoaiGhe().getGiaPhuThu();
    }

    private BigDecimal getFoodPrice(DoAn doAn) {
        return doAn == null || doAn.getDonGia() == null ? BigDecimal.ZERO : doAn.getDonGia();
    }

    private Map<String, DoAn> loadFoodMap(List<FoodSelection> dsDoAn) {
        if (dsDoAn.isEmpty()) {
            return Map.of();
        }

        Set<String> maDoAns = dsDoAn.stream()
                .map(FoodSelection::getMaDoAn)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, DoAn> doAnMap = doAnRepository.findAllById(maDoAns)
                .stream()
                .collect(Collectors.toMap(DoAn::getMaDoAn, doAn -> doAn));

        if (doAnMap.size() != maDoAns.size()) {
            List<String> missingIds = maDoAns.stream()
                    .filter(maDoAn -> !doAnMap.containsKey(maDoAn))
                    .toList();
            throw new BadRequestException("Co do an khong ton tai trong he thong: " + String.join(", ", missingIds));
        }

        List<String> unavailableFoods = doAnMap.values().stream()
                .filter(doAn -> !isFoodActive(doAn))
                .map(DoAn::getTenDoAn)
                .toList();
        if (!unavailableFoods.isEmpty()) {
            throw new ConflictException("Co mon an tam thoi khong con phuc vu: " + String.join(", ", unavailableFoods));
        }

        return doAnMap;
    }

    private boolean isFoodActive(DoAn doAn) {
        String status = normalize(doAn.getTrangThai()).toUpperCase();
        return status.isBlank() || FOOD_ACTIVE_STATUS.equals(status);
    }

    private HoaDonDetailResponse toDetail(HoaDon hoaDon, List<Ve> dsVe) {
        List<ChiTietDoAn> dsDoAn = chiTietDoAnRepository.findByHoaDon_MaDon(hoaDon.getMaDon());
        return toDetail(hoaDon, dsVe, dsDoAn);
    }

    private HoaDonDetailResponse toDetail(HoaDon hoaDon, List<Ve> dsVe, List<ChiTietDoAn> dsDoAn) {
        HoaDonDetailResponse response = new HoaDonDetailResponse();
        response.setMaDon(hoaDon.getMaDon());
        response.setMaKhachHang(hoaDon.getKhachHang().getMaKhachHang());

        BigDecimal tongTienVe = hoaDon.getTongTienVe() == null ? calculateTicketAmountFromTickets(dsVe) : hoaDon.getTongTienVe();
        BigDecimal tongTienDoAn = hoaDon.getTongTienDoAn() == null ? calculateFoodAmountFromLines(dsDoAn) : hoaDon.getTongTienDoAn();
        BigDecimal tongTien = hoaDon.getTongTien() == null ? tongTienVe.add(tongTienDoAn) : hoaDon.getTongTien();
        BigDecimal tongTienThanhToan = hoaDon.getTongTienThanhToan() == null ? tongTien : hoaDon.getTongTienThanhToan();

        response.setTongTien(tongTien);
        response.setTongTienVe(tongTienVe);
        response.setTongTienDoAn(tongTienDoAn);
        response.setTongTienThanhToan(tongTienThanhToan);
        response.setThoiGianDat(hoaDon.getThoiGianDat());
        response.setThoiGianThanhToan(hoaDon.getThoiGianThanhToan());
        response.setHanThanhToan(hoaDon.getThoiGianDat() == null ? null : hoaDon.getThoiGianDat().plusMinutes(HOLD_MINUTES));
        response.setTrangThai(hoaDon.getTrangThai() == null ? null : hoaDon.getTrangThai().name());
        response.setPhuongThucThanhToan(hoaDon.getPhuongThucThanhToan());
        response.setSoVe(dsVe.size());
        response.setCoTheThanhToan(hoaDon.getTrangThai() == HoaDonStatus.CHUA_THANH_TOAN
                && response.getHanThanhToan() != null
                && response.getHanThanhToan().isAfter(LocalDateTime.now()));

        if (!dsVe.isEmpty()) {
            Ve veDauTien = dsVe.get(0);
            SuatChieu suatChieu = veDauTien.getSuatChieu();
            response.setMaSuat(suatChieu.getMaSuat());
            response.setNgayChieu(suatChieu.getNgayChieu());
            response.setGioChieu(suatChieu.getGioChieu());
            response.setGiaVeCoBan(veDauTien.getGiaVeCoBan() == null ? getTicketBasePrice(suatChieu) : veDauTien.getGiaVeCoBan());
            response.setMaPhim(suatChieu.getPhim().getMaPhim());
            response.setTenPhim(suatChieu.getPhim().getTenPhim());
            response.setPoster(suatChieu.getPhim().getPoster());
            response.setMaPhong(suatChieu.getPhongChieu().getMaPhong());
            response.setTenPhong(suatChieu.getPhongChieu().getTenPhong());
            response.setTongGhe((int) gheRepository.countByPhongChieu_MaPhong(suatChieu.getPhongChieu().getMaPhong()));
            response.setMaRap(suatChieu.getPhongChieu().getRap().getMaRap());
            response.setTenRap(suatChieu.getPhongChieu().getRap().getTenRap());
        }

        response.setDsVe(dsVe.stream().map(this::toTicketResponse).toList());
        response.setDsDoAn(dsDoAn.stream().map(this::toFoodResponse).toList());
        return response;
    }

    private HoaDonVeResponse toTicketResponse(Ve ve) {
        HoaDonVeResponse response = new HoaDonVeResponse();
        BigDecimal giaVeCoBan = ve.getGiaVeCoBan() == null ? getTicketBasePrice(ve.getSuatChieu()) : ve.getGiaVeCoBan();
        BigDecimal phuThu = ve.getPhuThu() == null ? getTicketSurcharge(ve.getGhe()) : ve.getPhuThu();
        BigDecimal thanhTien = ve.getThanhTien() == null ? giaVeCoBan.add(phuThu) : ve.getThanhTien();

        response.setMaVe(ve.getMaVe());
        response.setMaGhe(ve.getGhe().getMaGhe());
        response.setTenGhe(ve.getGhe().getTenGhe());
        response.setTenLoaiGhe(ve.getGhe().getLoaiGhe().getTenLoaiGhe());
        response.setGiaVeCoBan(giaVeCoBan);
        response.setPhuThu(phuThu);
        response.setThanhTien(thanhTien);
        response.setTrangThaiVe(ve.getTrangThaiVe().name());
        return response;
    }

    private HoaDonDoAnResponse toFoodResponse(ChiTietDoAn chiTietDoAn) {
        HoaDonDoAnResponse response = new HoaDonDoAnResponse();
        response.setMaChiTietDoAn(chiTietDoAn.getMaChiTietDoAn());
        response.setMaDoAn(chiTietDoAn.getDoAn().getMaDoAn());
        response.setTenDoAn(chiTietDoAn.getDoAn().getTenDoAn());
        response.setLoaiDoAn(chiTietDoAn.getDoAn().getLoaiDoAn());
        response.setSoLuong(chiTietDoAn.getSoLuong());
        response.setDonGia(chiTietDoAn.getDonGia());
        response.setThanhTien(chiTietDoAn.getThanhTien());
        response.setHinhAnh(chiTietDoAn.getDoAn().getHinhAnh());
        response.setGhiChu(chiTietDoAn.getGhiChu());
        return response;
    }

    private BigDecimal calculateTicketAmountFromTickets(List<Ve> dsVe) {
        return dsVe.stream()
                .map(ve -> ve.getThanhTien() == null
                        ? defaultIfNull(ve.getGiaVeCoBan()).add(defaultIfNull(ve.getPhuThu()))
                        : ve.getThanhTien())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateFoodAmountFromLines(List<ChiTietDoAn> dsDoAn) {
        return dsDoAn.stream()
                .map(chiTietDoAn -> chiTietDoAn.getThanhTien() == null
                        ? defaultIfNull(chiTietDoAn.getDonGia()).multiply(BigDecimal.valueOf(chiTietDoAn.getSoLuong() == null ? 0 : chiTietDoAn.getSoLuong()))
                        : chiTietDoAn.getThanhTien())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<String> sanitizeSeatIds(List<String> dsGhe) {
        if (dsGhe == null || dsGhe.isEmpty()) {
            throw new BadRequestException("Danh sach ghe khong duoc de trong");
        }

        return dsGhe.stream()
                .map(this::normalize)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<FoodSelection> sanitizeFoodItems(List<HoaDonDoAnRequest> dsDoAn) {
        if (dsDoAn == null || dsDoAn.isEmpty()) {
            return List.of();
        }

        List<FoodSelection> results = new ArrayList<>();
        for (HoaDonDoAnRequest item : dsDoAn) {
            String maDoAn = normalize(item.getMaDoAn());
            if (maDoAn.isBlank()) {
                throw new BadRequestException("Ma do an khong duoc de trong");
            }
            if (item.getSoLuong() == null || item.getSoLuong() <= 0) {
                throw new BadRequestException("So luong do an phai >= 1");
            }
            results.add(new FoodSelection(maDoAn, item.getSoLuong(), normalizeNullable(item.getGhiChu())));
        }
        return results;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeNullable(String value) {
        String normalized = normalize(value);
        return normalized.isBlank() ? null : normalized;
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String generateId(String prefix, Predicate<String> exists) {
        for (int i = 0; i < 20; i++) {
            String id = prefix + UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
            if (!exists.test(id)) {
                return id;
            }
        }
        throw new ConflictException("Khong the tao ma moi, vui long thu lai");
    }

    private static final class FoodSelection {
        private final String maDoAn;
        private final Integer soLuong;
        private final String ghiChu;

        private FoodSelection(String maDoAn, Integer soLuong, String ghiChu) {
            this.maDoAn = maDoAn;
            this.soLuong = soLuong;
            this.ghiChu = ghiChu;
        }

        public String getMaDoAn() {
            return maDoAn;
        }

        public Integer getSoLuong() {
            return soLuong;
        }

        public String getGhiChu() {
            return ghiChu;
        }
    }
}
