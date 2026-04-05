package com.example.demo.controller;

import com.example.demo.dto.request.Ghe.CreateGheRequest;
import com.example.demo.dto.response.GheResponse;
import com.example.demo.dto.response.GheTheoSuatResponse;
import com.example.demo.service.GheService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ghe")
@CrossOrigin(origins = "*")
public class GheController {

    private final GheService service;

    public GheController(GheService service) {
        this.service = service;
    }

    @GetMapping
    public List<GheResponse> getByPhong(@RequestParam String maPhong) {
        return service.getByPhong(maPhong);
    }

    @GetMapping("/theo-suat/{maSuat}")
    public List<GheTheoSuatResponse> getBySuat(@PathVariable String maSuat) {
        return service.getBySuat(maSuat);
    }

    @PostMapping
    public GheResponse create(@Valid @RequestBody CreateGheRequest req) {
        return service.create(req);
    }
}
