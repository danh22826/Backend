package com.example.demo.dto.request.ThanhPho;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateThanhPhoRequest {
    @NotBlank(message = "Tên thành phố không được để trống")
    @Size(max = 100, message = "Tên thành phố tối đa 100 ký tự")
    private String tenThanhPho;

    @NotBlank(message = "Ma thành phố không được để trống")
    @Size(max = 100, message = "Ma thành phố tối đa 100 ký tự")
    private String maThanhPho;
    public CreateThanhPhoRequest() {
    }

    public String getTenThanhPho() {
        return tenThanhPho;
    }

    public void setTenThanhPho(String tenThanhPho) {
        this.tenThanhPho = tenThanhPho;
    }

    public String getMaThanhPho() {
        return maThanhPho;
    }
    public void setMaThanhPho(String maThanhPho) {
        this.maThanhPho = maThanhPho;
    }
}