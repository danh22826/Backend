package com.example.demo.service;

import com.example.demo.dto.request.PhongChieu.CreatePhongChieuRequest;
import com.example.demo.dto.request.PhongChieu.UpdatePhongChieuRequest;
import com.example.demo.dto.response.PhongChieuResponse;
import com.example.demo.entity.LoaiPhong;
import com.example.demo.entity.PhongChieu;
import com.example.demo.entity.Rap;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.LoaiPhongRepository;
import com.example.demo.repository.PhongChieuRepository;
import com.example.demo.repository.RapRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PhongChieuService {

    private final PhongChieuRepository phongChieuRepository;
    private final RapRepository rapRepository;
    private final LoaiPhongRepository loaiPhongRepository;

    public PhongChieuService(
            PhongChieuRepository phongChieuRepository,
            RapRepository rapRepository,
            LoaiPhongRepository loaiPhongRepository
    ) {
        this.phongChieuRepository = phongChieuRepository;
        this.rapRepository = rapRepository;
        this.loaiPhongRepository = loaiPhongRepository;
    }

    public List<PhongChieuResponse> getByRap(String maRap) {
        return phongChieuRepository.findByRap_MaRap(maRap)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PhongChieuResponse create(CreatePhongChieuRequest request) {
        if (phongChieuRepository.existsByRap_MaRapAndTenPhong(
                request.getMaRap(),
                request.getTenPhong()
        )) {
            throw new ConflictException("Tên phòng đã tồn tại trong rạp");
        }

        Rap rap = rapRepository.findById(request.getMaRap())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy rạp"));

        LoaiPhong loaiPhong = loaiPhongRepository.findById(request.getMaLoaiPhong())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại phòng"));

        PhongChieu phongChieu = new PhongChieu();
        phongChieu.setMaPhong(request.getMaPhong());
        phongChieu.setTenPhong(request.getTenPhong());
        phongChieu.setSucChua(request.getSucChua());
        phongChieu.setRap(rap);
        phongChieu.setLoaiPhong(loaiPhong);

        try {
            return toResponse(phongChieuRepository.save(phongChieu));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Phòng chiếu đã tồn tại hoặc vi phạm ràng buộc dữ liệu");
        }
    }

    @Transactional
    public PhongChieuResponse update(String maPhong, UpdatePhongChieuRequest request) {
        PhongChieu phongChieu = phongChieuRepository.findById(maPhong)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng"));

        int soGheHienCo = phongChieu.getGhes() == null ? 0 : phongChieu.getGhes().size();
        if (request.getSucChua() < soGheHienCo) {
            throw new ConflictException("Sức chứa không được nhỏ hơn số ghế hiện có trong phòng");
        }

        LoaiPhong loaiPhong = loaiPhongRepository.findById(request.getMaLoaiPhong())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại phòng"));

        phongChieu.setTenPhong(request.getTenPhong());
        phongChieu.setSucChua(request.getSucChua());
        phongChieu.setLoaiPhong(loaiPhong);

        try {
            return toResponse(phongChieuRepository.save(phongChieu));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Phòng chiếu đã tồn tại hoặc vi phạm ràng buộc dữ liệu");
        }
    }

    @Transactional
    public void delete(String maPhong) {
        if (!phongChieuRepository.existsById(maPhong)) {
            throw new NotFoundException("Không tìm thấy phòng");
        }
        phongChieuRepository.deleteById(maPhong);
    }

    private PhongChieuResponse toResponse(PhongChieu phongChieu) {
        PhongChieuResponse response = new PhongChieuResponse();
        int sucChuaThucTe = phongChieu.getGhes() == null || phongChieu.getGhes().isEmpty()
                ? phongChieu.getSucChua()
                : phongChieu.getGhes().size();

        response.setMaPhong(phongChieu.getMaPhong());
        response.setTenPhong(phongChieu.getTenPhong());
        response.setSucChua(sucChuaThucTe);

        response.setMaRap(phongChieu.getRap().getMaRap());
        response.setTenRap(phongChieu.getRap().getTenRap());

        response.setMaLoaiPhong(phongChieu.getLoaiPhong().getMaLoaiPhong());
        response.setTenLoaiPhong(phongChieu.getLoaiPhong().getTenLoaiPhong());

        return response;
    }
}
