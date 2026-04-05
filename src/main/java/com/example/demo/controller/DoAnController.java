package com.example.demo.controller;

import com.example.demo.dto.request.DoAn.CreateDoAnRequest;
import com.example.demo.dto.request.DoAn.UpdateDoAnRequest;
import com.example.demo.dto.response.DoAnResponse;
import com.example.demo.service.DoAnService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/do-an")
@CrossOrigin(origins = "*")
public class DoAnController {

    private final DoAnService doAnService;

    public DoAnController(DoAnService doAnService) {
        this.doAnService = doAnService;
    }

    @GetMapping
    public List<DoAnResponse> getAll() {
        return doAnService.getAll();
    }

    @GetMapping("/dang-ban")
    public List<DoAnResponse> getDangBan() {
        return doAnService.getDangBan();
    }

    @GetMapping("/{id}")
    public DoAnResponse getById(@PathVariable String id) {
        return doAnService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoAnResponse create(@Valid @RequestBody CreateDoAnRequest request) {
        return doAnService.create(request);
    }

    @PutMapping("/{id}")
    public DoAnResponse update(@PathVariable String id, @Valid @RequestBody UpdateDoAnRequest request) {
        return doAnService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        doAnService.delete(id);
    }
}
