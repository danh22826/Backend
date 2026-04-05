package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.TheLoaiResponse;
import com.example.demo.entity.TheLoai;
import com.example.demo.repository.TheLoaiRepository;
import com.example.demo.dto.request.TheLoai.CreateTheLoaiRequest;
import com.example.demo.dto.request.TheLoai.UpdateTheLoaiRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TheLoaiService {
    private final TheLoaiRepository repo;

    public TheLoaiService(TheLoaiRepository repo) {
        this.repo = repo;
    }

    public List<TheLoaiResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public TheLoaiResponse getById(String id) {
        TheLoai tl = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));
        return toResponse(tl);
    }
    @Transactional
    public TheLoaiResponse create(CreateTheLoaiRequest req) {

        if (repo.existsById(req.getMaTheLoai())) {
            throw new RuntimeException("Trùng mã thể loại");
        }

        TheLoai tl = new TheLoai();
        tl.setMaTheLoai(req.getMaTheLoai());
        tl.setTenTheLoai(req.getTenTheLoai());

        return toResponse(repo.save(tl));
    }
    @Transactional
    public TheLoaiResponse update(String id, UpdateTheLoaiRequest req) {

        TheLoai tl = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));

        tl.setTenTheLoai(req.getTenTheLoai());

        return toResponse(repo.save(tl));
    }
    @Transactional
    public void delete(String id) {
        repo.deleteById(id);
    }

    private TheLoaiResponse toResponse(TheLoai tl) {
        TheLoaiResponse res = new TheLoaiResponse();
        res.setMaTheLoai(tl.getMaTheLoai());
        res.setTenTheLoai(tl.getTenTheLoai());
        return res;
    }
}