package com.example.demo.controller;

import com.example.demo.dto.request.LoaiPhong.CreateLoaiPhongRequest;
import com.example.demo.dto.request.LoaiPhong.UpdateLoaiPhongRequest;
import com.example.demo.dto.response.LoaiPhongResponse;
import com.example.demo.service.LoaiPhongService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loai-phong")
@CrossOrigin(origins = "*")
public class LoaiPhongController {

    private final LoaiPhongService loaiPhongService;

    public LoaiPhongController(LoaiPhongService loaiPhongService) {
        this.loaiPhongService = loaiPhongService;
    }

    @GetMapping
    public ResponseEntity<List<LoaiPhongResponse>> getAllLoaiPhong() {
        return ResponseEntity.ok(loaiPhongService.getAllLoaiPhong());
    }

    @GetMapping("/{maLoaiPhong}")
    public ResponseEntity<LoaiPhongResponse> getLoaiPhongById(@PathVariable String maLoaiPhong) {
        return ResponseEntity.ok(loaiPhongService.getLoaiPhongById(maLoaiPhong));
    }

    @PostMapping
    public ResponseEntity<LoaiPhongResponse> createLoaiPhong(
            @Valid @RequestBody CreateLoaiPhongRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loaiPhongService.createLoaiPhong(request));
    }

    @PutMapping("/{maLoaiPhong}")
    public ResponseEntity<LoaiPhongResponse> updateLoaiPhong(
            @PathVariable String maLoaiPhong,
            @Valid @RequestBody UpdateLoaiPhongRequest request
    ) {
        return ResponseEntity.ok(loaiPhongService.updateLoaiPhong(maLoaiPhong, request));
    }

    @DeleteMapping("/{maLoaiPhong}")
    public ResponseEntity<Void> deleteLoaiPhong(@PathVariable String maLoaiPhong) {
        loaiPhongService.deleteLoaiPhong(maLoaiPhong);
        return ResponseEntity.noContent().build();
    }
}