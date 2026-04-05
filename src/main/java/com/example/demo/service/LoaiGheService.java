package com.example.demo.service;

import com.example.demo.dto.request.LoaiGhe.CreateLoaiGheRequest;
import com.example.demo.dto.request.LoaiGhe.UpdateLoaiGheRequest;
import com.example.demo.dto.response.LoaiGheResponse;
import com.example.demo.entity.LoaiGhe;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.LoaiGheRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoaiGheService {

    private final LoaiGheRepository loaiGheRepository;

    public LoaiGheService(LoaiGheRepository loaiGheRepository) {
        this.loaiGheRepository = loaiGheRepository;
    }

    public List<LoaiGheResponse> getAllLoaiGhe() {
        return loaiGheRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LoaiGheResponse getLoaiGheById(String maLoaiGhe) {
        LoaiGhe loaiGhe = loaiGheRepository.findById(maLoaiGhe)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại ghế với mã: " + maLoaiGhe));

        return toResponse(loaiGhe);
    }

    @Transactional
    public LoaiGheResponse createLoaiGhe(CreateLoaiGheRequest request) {
        if (loaiGheRepository.existsById(request.getMaLoaiGhe())) {
            throw new ConflictException("Mã loại ghế đã tồn tại");
        }

        if (loaiGheRepository.existsByTenLoaiGhe(request.getTenLoaiGhe())) {
            throw new ConflictException("Tên loại ghế đã tồn tại");
        }

        LoaiGhe loaiGhe = new LoaiGhe();
        loaiGhe.setMaLoaiGhe(request.getMaLoaiGhe());
        loaiGhe.setTenLoaiGhe(request.getTenLoaiGhe());
        loaiGhe.setGiaPhuThu(request.getGiaPhuThu());

        try {
            return toResponse(loaiGheRepository.save(loaiGhe));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Loại ghế đã tồn tại hoặc vi phạm ràng buộc dữ liệu");
        }
    }

    @Transactional
    public LoaiGheResponse updateLoaiGhe(String maLoaiGhe, UpdateLoaiGheRequest request) {
        LoaiGhe loaiGhe = loaiGheRepository.findById(maLoaiGhe)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại ghế với mã: " + maLoaiGhe));

        loaiGhe.setTenLoaiGhe(request.getTenLoaiGhe());
        loaiGhe.setGiaPhuThu(request.getGiaPhuThu());

        try {
            return toResponse(loaiGheRepository.save(loaiGhe));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Tên loại ghế đã tồn tại hoặc vi phạm ràng buộc dữ liệu");
        }
    }

    @Transactional
    public void deleteLoaiGhe(String maLoaiGhe) {
        if (!loaiGheRepository.existsById(maLoaiGhe)) {
            throw new NotFoundException("Không tìm thấy loại ghế với mã: " + maLoaiGhe);
        }

        loaiGheRepository.deleteById(maLoaiGhe);
    }

    private LoaiGheResponse toResponse(LoaiGhe loaiGhe) {
        LoaiGheResponse response = new LoaiGheResponse();
        response.setMaLoaiGhe(loaiGhe.getMaLoaiGhe());
        response.setTenLoaiGhe(loaiGhe.getTenLoaiGhe());
        response.setGiaPhuThu(loaiGhe.getGiaPhuThu());
        return response;
    }
}