package com.example.demo.controller;

import com.example.demo.dto.request.ThanhPho.CreateThanhPhoRequest;
import com.example.demo.dto.request.ThanhPho.UpdateThanhPhoRequest;
import com.example.demo.dto.response.ThanhPhoResponse;
import com.example.demo.service.ThanhPhoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thanh-pho")
@CrossOrigin(origins = "*")
public class ThanhPhoController {

    private final ThanhPhoService thanhPhoService;

    public ThanhPhoController(ThanhPhoService thanhPhoService) {
        this.thanhPhoService = thanhPhoService;
    }

    @GetMapping
    public ResponseEntity<List<ThanhPhoResponse>> getAllThanhPho() {
        return ResponseEntity.ok(thanhPhoService.getAllThanhPho());
    }

    @GetMapping("/{maThanhPho}")
    public ResponseEntity<ThanhPhoResponse> getThanhPhoById(@PathVariable String maThanhPho) {
        return ResponseEntity.ok(thanhPhoService.getThanhPhoById(maThanhPho));
    }

    @PostMapping
    public ResponseEntity<ThanhPhoResponse> createThanhPho(
            @Valid @RequestBody CreateThanhPhoRequest request
    ) {
        ThanhPhoResponse response = thanhPhoService.createThanhPho(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{maThanhPho}")
    public ResponseEntity<ThanhPhoResponse> updateThanhPho(
            @PathVariable String maThanhPho,
            @Valid @RequestBody UpdateThanhPhoRequest request
    ) {
        return ResponseEntity.ok(thanhPhoService.updateThanhPho(maThanhPho, request));
    }

    @DeleteMapping("/{maThanhPho}")
    public ResponseEntity<Void> deleteThanhPho(@PathVariable String maThanhPho) {
        thanhPhoService.deleteThanhPho(maThanhPho);
        return ResponseEntity.noContent().build();
    }
}