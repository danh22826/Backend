package com.example.demo.controller;

import com.example.demo.dto.request.SuatChieu.CreateSuatChieuRequest;
import com.example.demo.dto.request.SuatChieu.UpdateSuatChieuRequest;
import com.example.demo.dto.response.PhimTheoNgayResponse;
import com.example.demo.dto.response.SuatChieuConHanResponse;
import com.example.demo.dto.response.SuatChieuResponse;
import com.example.demo.service.SuatChieuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suat-chieu")
@CrossOrigin(origins = "*")
public class SuatChieuController {

    private final SuatChieuService suatChieuService;

    public SuatChieuController(SuatChieuService suatChieuService) {
        this.suatChieuService = suatChieuService;
    }

    @GetMapping("/tim-phim")
    public ResponseEntity<List<PhimTheoNgayResponse>> timPhimTheoNgayVaRap(
            @RequestParam String maRap,
            @RequestParam String ngay
    ) {
        return ResponseEntity.ok(suatChieuService.getPhimTheoNgayVaRap(maRap, ngay));
    }

    @GetMapping("/tim-suat")
    public ResponseEntity<List<SuatChieuConHanResponse>> timSuatChieuConHan(
            @RequestParam String maPhim,
            @RequestParam String maRap,
            @RequestParam String ngay
    ) {
        return ResponseEntity.ok(suatChieuService.getSuatChieuConHan(maPhim, maRap, ngay));
    }

    @GetMapping
    public ResponseEntity<List<SuatChieuResponse>> getAll() {
        return ResponseEntity.ok(suatChieuService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuatChieuResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(suatChieuService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SuatChieuResponse> create(
            @Valid @RequestBody CreateSuatChieuRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(suatChieuService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuatChieuResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateSuatChieuRequest request
    ) {
        return ResponseEntity.ok(suatChieuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        suatChieuService.delete(id);
        return ResponseEntity.noContent().build();
    }
}