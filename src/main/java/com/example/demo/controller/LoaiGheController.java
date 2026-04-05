package com.example.demo.controller;

import com.example.demo.dto.request.LoaiGhe.CreateLoaiGheRequest;
import com.example.demo.dto.request.LoaiGhe.UpdateLoaiGheRequest;
import com.example.demo.dto.response.LoaiGheResponse;
import com.example.demo.service.LoaiGheService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import java.util.List;

@RestController
@RequestMapping("/api/loai-ghe")
@CrossOrigin(origins = "*")
public class LoaiGheController {

    private final LoaiGheService loaiGheService;

    public LoaiGheController(LoaiGheService loaiGheService) {
        this.loaiGheService = loaiGheService;
    }

    @GetMapping
    public ResponseEntity<List<LoaiGheResponse>> getAllLoaiGhe() {
        return ResponseEntity.ok(loaiGheService.getAllLoaiGhe());
    }

    @GetMapping("/{maLoaiGhe}")
    public ResponseEntity<LoaiGheResponse> getLoaiGheById(@PathVariable String maLoaiGhe) {
        return ResponseEntity.ok(loaiGheService.getLoaiGheById(maLoaiGhe));
    }

    @PostMapping
    public ResponseEntity<LoaiGheResponse> createLoaiGhe(
            @Valid @RequestBody CreateLoaiGheRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loaiGheService.createLoaiGhe(request));
    }

    @PutMapping("/{maLoaiGhe}")
    public ResponseEntity<LoaiGheResponse> updateLoaiGhe(
            @PathVariable String maLoaiGhe,
            @Valid @RequestBody UpdateLoaiGheRequest request
    ) {
        return ResponseEntity.ok(loaiGheService.updateLoaiGhe(maLoaiGhe, request));
    }

    @DeleteMapping("/{maLoaiGhe}")
    public ResponseEntity<Void> deleteLoaiGhe(@PathVariable String maLoaiGhe) {
        loaiGheService.deleteLoaiGhe(maLoaiGhe);
        return ResponseEntity.noContent().build();
    }
}