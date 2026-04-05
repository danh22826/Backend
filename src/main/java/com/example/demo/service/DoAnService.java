package com.example.demo.service;

import com.example.demo.dto.request.DoAn.CreateDoAnRequest;
import com.example.demo.dto.request.DoAn.UpdateDoAnRequest;
import com.example.demo.dto.response.DoAnResponse;
import com.example.demo.entity.DoAn;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.DoAnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DoAnService {

    private static final String ACTIVE_STATUS = "DANG_BAN";
    private static final String INACTIVE_STATUS = "NGUNG_BAN";

    private final DoAnRepository doAnRepository;

    public DoAnService(DoAnRepository doAnRepository) {
        this.doAnRepository = doAnRepository;
    }

    @Transactional(readOnly = true)
    public List<DoAnResponse> getAll() {
        return doAnRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DoAnResponse> getDangBan() {
        return doAnRepository.findByTrangThaiIgnoreCase(ACTIVE_STATUS).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DoAnResponse getById(String id) {
        return toResponse(findEntity(id));
    }

    public DoAnResponse create(CreateDoAnRequest request) {
        if (doAnRepository.existsById(request.getMaDoAn())) {
            throw new IllegalArgumentException("Ma do an da ton tai");
        }

        DoAn doAn = new DoAn();
        doAn.setMaDoAn(request.getMaDoAn().trim());
        apply(doAn, request.getTenDoAn(), request.getLoaiDoAn(), request.getDonGia(), request.getMoTa(), request.getHinhAnh(), request.getTrangThai());
        doAn.setNgayTao(LocalDateTime.now());
        return toResponse(doAnRepository.save(doAn));
    }

    public DoAnResponse update(String id, UpdateDoAnRequest request) {
        DoAn doAn = findEntity(id);
        apply(doAn, request.getTenDoAn(), request.getLoaiDoAn(), request.getDonGia(), request.getMoTa(), request.getHinhAnh(), request.getTrangThai());
        return toResponse(doAnRepository.save(doAn));
    }

    public void delete(String id) {
        DoAn doAn = findEntity(id);
        doAnRepository.delete(doAn);
    }

    private DoAn findEntity(String id) {
        return doAnRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Khong tim thay do an"));
    }

    private void apply(DoAn doAn,
                       String tenDoAn,
                       String loaiDoAn,
                       java.math.BigDecimal donGia,
                       String moTa,
                       String hinhAnh,
                       String trangThai) {
        doAn.setTenDoAn(tenDoAn == null ? "" : tenDoAn.trim());
        doAn.setLoaiDoAn(loaiDoAn == null ? null : loaiDoAn.trim());
        doAn.setDonGia(donGia);
        doAn.setMoTa(moTa == null ? null : moTa.trim());
        doAn.setHinhAnh(hinhAnh == null ? null : hinhAnh.trim());
        doAn.setTrangThai(normalizeStatus(trangThai));
    }

    private String normalizeStatus(String trangThai) {
        String normalized = trangThai == null ? "" : trangThai.trim().toUpperCase();
        return INACTIVE_STATUS.equals(normalized) ? INACTIVE_STATUS : ACTIVE_STATUS;
    }

    private DoAnResponse toResponse(DoAn doAn) {
        DoAnResponse response = new DoAnResponse();
        response.setMaDoAn(doAn.getMaDoAn());
        response.setTenDoAn(doAn.getTenDoAn());
        response.setLoaiDoAn(doAn.getLoaiDoAn());
        response.setDonGia(doAn.getDonGia());
        response.setMoTa(doAn.getMoTa());
        response.setHinhAnh(doAn.getHinhAnh());
        response.setTrangThai(doAn.getTrangThai());
        response.setNgayTao(doAn.getNgayTao());
        return response;
    }
}
