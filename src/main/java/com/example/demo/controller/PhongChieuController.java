package com.example.demo.controller;

import com.example.demo.dto.request.PhongChieu.CreatePhongChieuRequest;
import com.example.demo.dto.request.PhongChieu.UpdatePhongChieuRequest;
import com.example.demo.dto.response.PhongChieuResponse;
import com.example.demo.service.PhongChieuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phong-chieu")
public class PhongChieuController {

    private final PhongChieuService phongChieuService;

    public PhongChieuController(PhongChieuService phongChieuService) {
        this.phongChieuService = phongChieuService;
    }

    @GetMapping
    public List<PhongChieuResponse> getByRap(@RequestParam String maRap) {
        return phongChieuService.getByRap(maRap);
    }

    @PostMapping
    public PhongChieuResponse create(@Valid @RequestBody CreatePhongChieuRequest request) {
        return phongChieuService.create(request);
    }

    @PutMapping("/{maPhong}")
    public PhongChieuResponse update(
            @PathVariable String maPhong,
            @Valid @RequestBody UpdatePhongChieuRequest request
    ) {
        return phongChieuService.update(maPhong, request);
    }

    @DeleteMapping("/{maPhong}")
    public void delete(@PathVariable String maPhong) {
        phongChieuService.delete(maPhong);
    }
}