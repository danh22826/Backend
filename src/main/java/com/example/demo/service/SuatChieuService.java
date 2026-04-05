package com.example.demo.service;

import com.example.demo.dto.request.SuatChieu.CreateSuatChieuRequest;
import com.example.demo.dto.request.SuatChieu.UpdateSuatChieuRequest;
import com.example.demo.dto.response.PhimTheoNgayResponse;
import com.example.demo.dto.response.SuatChieuConHanResponse;
import com.example.demo.dto.response.SuatChieuResponse;
import com.example.demo.entity.Phim;
import com.example.demo.entity.PhongChieu;
import com.example.demo.entity.SuatChieu;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.PhimRepository;
import com.example.demo.repository.PhongChieuRepository;
import com.example.demo.repository.SuatChieuRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class SuatChieuService {

    private final SuatChieuRepository suatChieuRepository;
    private final PhimRepository phimRepository;
    private final PhongChieuRepository phongChieuRepository;

    public SuatChieuService(SuatChieuRepository suatChieuRepository,
                            PhimRepository phimRepository,
                            PhongChieuRepository phongChieuRepository) {
        this.suatChieuRepository = suatChieuRepository;
        this.phimRepository = phimRepository;
        this.phongChieuRepository = phongChieuRepository;
    }

    public List<PhimTheoNgayResponse> getPhimTheoNgayVaRap(String maRap, String ngay) {
        LocalDate localDate = LocalDate.parse(ngay);

        return suatChieuRepository.timPhimTheoNgayVaRap(maRap, localDate)
                .stream()
                .map(this::mapToPhimTheoNgayResponse)
                .toList();
    }

    public List<SuatChieuConHanResponse> getSuatChieuConHan(String maPhim, String maRap, String ngay) {
        LocalDate localDate = LocalDate.parse(ngay);

        return suatChieuRepository.timSuatChieuConHan(maPhim, maRap, localDate)
                .stream()
                .map(this::mapToSuatChieuConHanResponse)
                .toList();
    }

    public List<SuatChieuResponse> getAll() {
        return suatChieuRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SuatChieuResponse getById(String id) {
        SuatChieu suatChieu = suatChieuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy suất chiếu"));
        return toResponse(suatChieu);
    }

    @Transactional
    public SuatChieuResponse create(CreateSuatChieuRequest req) {
        if (suatChieuRepository.existsById(req.getMaSuat())) {
            throw new ConflictException("Mã suất chiếu đã tồn tại");
        }

        Phim phim = phimRepository.findById(req.getMaPhim())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phim"));

        PhongChieu phong = phongChieuRepository.findById(req.getMaPhong())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng chiếu"));

        SuatChieu suatChieu = new SuatChieu();
        suatChieu.setMaSuat(req.getMaSuat());
        suatChieu.setPhim(phim);
        suatChieu.setPhongChieu(phong);
        suatChieu.setNgayChieu(req.getNgayChieu());
        suatChieu.setGioChieu(req.getGioChieu());
        suatChieu.setGia(req.getGia());

        try {
            return toResponse(suatChieuRepository.save(suatChieu));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Suất chiếu đã tồn tại hoặc vi phạm ràng buộc dữ liệu");
        }
    }

    @Transactional
    public SuatChieuResponse update(String id, UpdateSuatChieuRequest req) {
        SuatChieu suatChieu = suatChieuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy suất chiếu"));

        Phim phim = phimRepository.findById(req.getMaPhim())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phim"));

        PhongChieu phong = phongChieuRepository.findById(req.getMaPhong())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng chiếu"));

        suatChieu.setPhim(phim);
        suatChieu.setPhongChieu(phong);
        suatChieu.setNgayChieu(req.getNgayChieu());
        suatChieu.setGioChieu(req.getGioChieu());
        suatChieu.setGia(req.getGia());

        try {
            return toResponse(suatChieuRepository.save(suatChieu));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Suất chiếu đã tồn tại hoặc vi phạm ràng buộc dữ liệu");
        }
    }

    @Transactional
    public void delete(String id) {
        if (!suatChieuRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy suất chiếu");
        }
        suatChieuRepository.deleteById(id);
    }

    private SuatChieuResponse toResponse(SuatChieu suatChieu) {
        SuatChieuResponse response = new SuatChieuResponse();

        response.setMaSuat(suatChieu.getMaSuat());
        response.setNgayChieu(suatChieu.getNgayChieu());
        response.setGioChieu(suatChieu.getGioChieu());
        response.setGia(suatChieu.getGia());

        response.setMaPhim(suatChieu.getPhim().getMaPhim());
        response.setTenPhim(suatChieu.getPhim().getTenPhim());
        response.setPoster(suatChieu.getPhim().getPoster());

        response.setMaPhong(suatChieu.getPhongChieu().getMaPhong());
        response.setTenPhong(suatChieu.getPhongChieu().getTenPhong());

        response.setMaRap(suatChieu.getPhongChieu().getRap().getMaRap());
        response.setTenRap(suatChieu.getPhongChieu().getRap().getTenRap());

        return response;
    }

    private PhimTheoNgayResponse mapToPhimTheoNgayResponse(Map<String, Object> row) {
        PhimTheoNgayResponse response = new PhimTheoNgayResponse();

        response.setMaPhim((String) row.get("MaPhim"));
        response.setTenPhim((String) row.get("TenPhim"));
        response.setMoTa((String) row.get("MoTa"));
        response.setPoster((String) row.get("Poster"));
        response.setThoiLuong(
                row.get("ThoiLuong") != null ? ((Number) row.get("ThoiLuong")).intValue() : null
        );

        Object ngay = row.get("NgayKhoiChieu");
        if (ngay instanceof java.sql.Date sqlDate) {
            response.setNgayKhoiChieu(sqlDate.toLocalDate());
        } else if (ngay instanceof LocalDate localDate) {
            response.setNgayKhoiChieu(localDate);
        }

        response.setDoTuoiPhuHop((String) row.get("DoTuoiPhuHop"));
        response.setNgonNgu((String) row.get("NgonNgu"));

        return response;
    }

    private SuatChieuConHanResponse mapToSuatChieuConHanResponse(Map<String, Object> row) {
        SuatChieuConHanResponse response = new SuatChieuConHanResponse();

        response.setMaSuat((String) row.get("MaSuat"));
        response.setGioChieu((String) row.get("GioChieu"));

        Object gia = row.get("Gia");
        if (gia instanceof BigDecimal bigDecimal) {
            response.setGia(bigDecimal);
        } else if (gia instanceof Number number) {
            response.setGia(BigDecimal.valueOf(number.doubleValue()));
        }

        response.setMaPhong((String) row.get("MaPhong"));
        response.setTenPhong((String) row.get("TenPhong"));
        response.setTenLoaiPhong((String) row.get("TenLoaiPhong"));

        response.setTongGhe(
                row.get("TongGhe") != null ? ((Number) row.get("TongGhe")).intValue() : null
        );

        response.setSoGheTrong(
                row.get("SoGheTrong") != null ? Math.max(((Number) row.get("SoGheTrong")).intValue(), 0) : null
        );

        return response;
    }
}
