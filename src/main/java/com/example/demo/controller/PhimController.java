package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.PhimResponse;
import com.example.demo.dto.request.Phim.CreatePhimRequest;
import com.example.demo.dto.request.Phim.UpdatePhimRequest;
import com.example.demo.service.PhimService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phim")
@CrossOrigin(origins = "*") // Cái này hỗ trợ thêm CORS ở mức Controller
public class PhimController {

    private final PhimService service;

    public PhimController(PhimService service) {
        this.service = service;
    }

    @GetMapping
    public List<PhimResponse> getAll() {
        return service.getAll();
    }

    // 👉 THÊM ĐOẠN NÀY: API để Frontend lấy danh sách phim đang chiếu
    @GetMapping("/dang-chieu")
    public List<PhimResponse> getPhimDangChieu() {
        // Tạm thời gọi hàm getAll() để trả về toàn bộ phim
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PhimResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public PhimResponse create(@Valid @RequestBody CreatePhimRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public PhimResponse update(@PathVariable String id,
                               @Valid @RequestBody UpdatePhimRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}