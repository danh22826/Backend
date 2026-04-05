package com.example.demo.controller;

import com.example.demo.dto.request.Rap.CreateRapRequest;
import com.example.demo.dto.request.Rap.UpdateRapRequest;
import com.example.demo.dto.response.RapResponse;
import com.example.demo.service.RapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rap")
@CrossOrigin(origins = "*")
public class RapController {

    private final RapService rapService;

    public RapController(RapService rapService) {
        this.rapService = rapService;
    }

    @GetMapping
    public ResponseEntity<List<RapResponse>> getRapTheoThanhPho(
            @RequestParam String maThanhPho
    ) {
        return ResponseEntity.ok(rapService.getRapTheoMaThanhPho(maThanhPho));
    }

    @GetMapping("/{maRap}")
    public ResponseEntity<RapResponse> getRapById(@PathVariable String maRap) {
        return ResponseEntity.ok(rapService.getRapById(maRap));
    }

    @PostMapping
    public ResponseEntity<RapResponse> createRap(
            @Valid @RequestBody CreateRapRequest request
    ) {
        return ResponseEntity.ok(rapService.createRap(request));
    }

    @PutMapping("/{maRap}")
    public ResponseEntity<RapResponse> updateRap(
            @PathVariable String maRap,
            @Valid @RequestBody UpdateRapRequest request
    ) {
        return ResponseEntity.ok(rapService.updateRap(maRap, request));
    }

    @DeleteMapping("/{maRap}")
    public ResponseEntity<Void> deleteRap(@PathVariable String maRap) {
        rapService.deleteRap(maRap);
        return ResponseEntity.noContent().build();
    }
}