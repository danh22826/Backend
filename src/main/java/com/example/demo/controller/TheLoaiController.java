package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.TheLoaiResponse;
import com.example.demo.service.TheLoaiService;
import jakarta.validation.Valid;
import com.example.demo.dto.request.TheLoai.CreateTheLoaiRequest;
import com.example.demo.dto.request.TheLoai.UpdateTheLoaiRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/the-loai")
@CrossOrigin("*")
public class TheLoaiController {

    private final TheLoaiService service;

    public TheLoaiController(TheLoaiService service) {
        this.service = service;
    }

    @GetMapping
    public List<TheLoaiResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TheLoaiResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public TheLoaiResponse create(@Valid @RequestBody CreateTheLoaiRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public TheLoaiResponse update(@PathVariable String id,
                                  @Valid @RequestBody UpdateTheLoaiRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}