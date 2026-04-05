package com.example.demo.service;

import com.example.demo.dto.request.LoaiPhong.CreateLoaiPhongRequest;
import com.example.demo.dto.request.LoaiPhong.UpdateLoaiPhongRequest;
import com.example.demo.dto.response.LoaiPhongResponse;
import com.example.demo.entity.LoaiPhong;
import com.example.demo.repository.LoaiPhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
@Transactional(readOnly = true)
@Service
public class LoaiPhongService {
    private final LoaiPhongRepository loaiPhongRepository;

    public LoaiPhongService(LoaiPhongRepository loaiPhongRepository) {
        this.loaiPhongRepository = loaiPhongRepository;
    }

    public List<LoaiPhongResponse> getAllLoaiPhong() {
        return loaiPhongRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LoaiPhongResponse getLoaiPhongById(String maLoaiPhong) {
        LoaiPhong loaiPhong = loaiPhongRepository.findById(maLoaiPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với mã: " + maLoaiPhong));

        return toResponse(loaiPhong);
    }
    @Transactional
    public LoaiPhongResponse createLoaiPhong(CreateLoaiPhongRequest request) {
        if (loaiPhongRepository.existsById(request.getMaLoaiPhong())) {
            throw new RuntimeException("Mã loại phòng đã tồn tại");
        }

        if (loaiPhongRepository.existsByTenLoaiPhong(request.getTenLoaiPhong())) {
            throw new RuntimeException("Tên loại phòng đã tồn tại");
        }

        LoaiPhong loaiPhong = new LoaiPhong();
        loaiPhong.setMaLoaiPhong(request.getMaLoaiPhong());
        loaiPhong.setTenLoaiPhong(request.getTenLoaiPhong());

        return toResponse(loaiPhongRepository.save(loaiPhong));
    }
    @Transactional
    public LoaiPhongResponse updateLoaiPhong(String maLoaiPhong, UpdateLoaiPhongRequest request) {
        LoaiPhong loaiPhong = loaiPhongRepository.findById(maLoaiPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với mã: " + maLoaiPhong));

        loaiPhong.setTenLoaiPhong(request.getTenLoaiPhong());

        return toResponse(loaiPhongRepository.save(loaiPhong));
    }
    @Transactional
    public void deleteLoaiPhong(String maLoaiPhong) {
        if (!loaiPhongRepository.existsById(maLoaiPhong)) {
            throw new RuntimeException("Không tìm thấy loại phòng với mã: " + maLoaiPhong);
        }

        loaiPhongRepository.deleteById(maLoaiPhong);
    }

    private LoaiPhongResponse toResponse(LoaiPhong loaiPhong) {
        LoaiPhongResponse response = new LoaiPhongResponse();
        response.setMaLoaiPhong(loaiPhong.getMaLoaiPhong());
        response.setTenLoaiPhong(loaiPhong.getTenLoaiPhong());
        return response;
    }
}