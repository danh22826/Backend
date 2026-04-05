package com.example.demo.service;

import com.example.demo.constant.HoaDonStatus;
import com.example.demo.constant.VeStatus;
import com.example.demo.dto.request.Ghe.CreateGheRequest;
import com.example.demo.dto.response.GheResponse;
import com.example.demo.dto.response.GheTheoSuatResponse;
import com.example.demo.entity.Ghe;
import com.example.demo.entity.LoaiGhe;
import com.example.demo.entity.PhongChieu;
import com.example.demo.entity.SuatChieu;
import com.example.demo.entity.Ve;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.GheRepository;
import com.example.demo.repository.LoaiGheRepository;
import com.example.demo.repository.PhongChieuRepository;
import com.example.demo.repository.SuatChieuRepository;
import com.example.demo.repository.VeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GheService {

    private static final int HOLD_MINUTES = 10;

    private final GheRepository gheRepo;
    private final PhongChieuRepository phongRepo;
    private final LoaiGheRepository loaiRepo;
    private final SuatChieuRepository suatChieuRepo;
    private final VeRepository veRepo;

    public GheService(GheRepository gheRepo,
                      PhongChieuRepository phongRepo,
                      LoaiGheRepository loaiRepo,
                      SuatChieuRepository suatChieuRepo,
                      VeRepository veRepo) {
        this.gheRepo = gheRepo;
        this.phongRepo = phongRepo;
        this.loaiRepo = loaiRepo;
        this.suatChieuRepo = suatChieuRepo;
        this.veRepo = veRepo;
    }

    public List<GheResponse> getByPhong(String maPhong) {
        return gheRepo.findByPhongChieu_MaPhong(maPhong)
                .stream()
                .sorted(Comparator.comparing(Ghe::getSoHang).thenComparing(Ghe::getSoCot))
                .map(this::toResponse)
                .toList();
    }

    public List<GheTheoSuatResponse> getBySuat(String maSuat) {
        SuatChieu suatChieu = suatChieuRepo.findById(maSuat)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy suất chiếu"));

        String maPhong = suatChieu.getPhongChieu().getMaPhong();
        List<Ghe> gheTheoPhong = gheRepo.findByPhongChieu_MaPhong(maPhong)
                .stream()
                .sorted(Comparator.comparing(Ghe::getSoHang).thenComparing(Ghe::getSoCot))
                .toList();

        List<Ve> veTheoSuat = veRepo.findBySuatChieu_MaSuatAndTrangThaiVeIn(
                maSuat,
                EnumSet.of(VeStatus.DA_DAT, VeStatus.DA_THANH_TOAN)
        );

        LocalDateTime now = LocalDateTime.now();
        Map<String, Ve> veTheoMaGhe = veTheoSuat.stream()
                .filter(ve -> isSeatLocked(ve, now))
                .collect(Collectors.toMap(ve -> ve.getGhe().getMaGhe(), Function.identity(), (left, right) -> left));

        return gheTheoPhong.stream()
                .map(ghe -> toResponse(ghe, veTheoMaGhe.containsKey(ghe.getMaGhe())))
                .toList();
    }

    @Transactional
    public GheResponse create(CreateGheRequest req) {
        if (gheRepo.existsByPhongChieu_MaPhongAndSoHangAndSoCot(
                req.getMaPhong(),
                req.getSoHang(),
                req.getSoCot()
        )) {
            throw new ConflictException("Ghế đã tồn tại trong phòng");
        }

        PhongChieu phong = phongRepo.findById(req.getMaPhong())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng"));

        LoaiGhe loaiGhe = loaiRepo.findById(req.getMaLoaiGhe())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại ghế"));

        Ghe ghe = new Ghe();
        ghe.setMaGhe(req.getMaGhe());
        ghe.setPhongChieu(phong);
        ghe.setSoHang(req.getSoHang());
        ghe.setSoCot(req.getSoCot());
        ghe.setLoaiGhe(loaiGhe);

        Ghe saved = gheRepo.save(ghe);

        phong.setSucChua((int) gheRepo.countByPhongChieu_MaPhong(req.getMaPhong()));
        phongRepo.save(phong);

        return toResponse(saved);
    }

    private boolean isSeatLocked(Ve ve, LocalDateTime now) {
        if (ve.getTrangThaiVe() == VeStatus.DA_THANH_TOAN) {
            return true;
        }

        if (ve.getTrangThaiVe() != VeStatus.DA_DAT || ve.getHoaDon() == null) {
            return false;
        }

        if (ve.getHoaDon().getTrangThai() != HoaDonStatus.CHUA_THANH_TOAN) {
            return false;
        }

        LocalDateTime thoiGianDat = ve.getHoaDon().getThoiGianDat();
        return thoiGianDat != null && thoiGianDat.plusMinutes(HOLD_MINUTES).isAfter(now);
    }

    private GheResponse toResponse(Ghe ghe) {
        GheResponse response = new GheResponse();
        response.setMaGhe(ghe.getMaGhe());
        response.setTenGhe(ghe.getTenGhe());
        response.setMaPhong(ghe.getPhongChieu().getMaPhong());
        response.setMaLoaiGhe(ghe.getLoaiGhe().getMaLoaiGhe());
        response.setTenLoaiGhe(ghe.getLoaiGhe().getTenLoaiGhe());
        response.setGiaPhuThu(ghe.getLoaiGhe().getGiaPhuThu());
        return response;
    }

    private GheTheoSuatResponse toResponse(Ghe ghe, boolean daDat) {
        GheTheoSuatResponse response = new GheTheoSuatResponse();
        response.setMaGhe(ghe.getMaGhe());
        response.setTenGhe(ghe.getTenGhe());
        response.setMaPhong(ghe.getPhongChieu().getMaPhong());
        response.setMaLoaiGhe(ghe.getLoaiGhe().getMaLoaiGhe());
        response.setTenLoaiGhe(ghe.getLoaiGhe().getTenLoaiGhe());
        response.setGiaPhuThu(ghe.getLoaiGhe().getGiaPhuThu());
        response.setTrangThai(daDat ? "BOOKED" : "AVAILABLE");
        response.setCoTheDat(!daDat);
        return response;
    }
}
