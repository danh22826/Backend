package com.example.demo.dto.request.ThanhPho;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateThanhPhoRequest {

    @NotBlank(message = "Tên thành phố không được để trống")
    @Size(max = 100, message = "Tên thành phố tối đa 100 ký tự")
    private String tenThanhPho;

    public UpdateThanhPhoRequest() {
    }

    public String getTenThanhPho() {
        return tenThanhPho;
    }

    public void setTenThanhPho(String tenThanhPho) {
        this.tenThanhPho = tenThanhPho;
    }
}