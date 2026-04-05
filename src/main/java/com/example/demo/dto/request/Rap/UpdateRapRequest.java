package com.example.demo.dto.request.Rap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateRapRequest {

    @NotBlank(message = "Tên rạp không được để trống")
    @Size(max = 100)
    private String tenRap;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 100)
    private String diaChi;

    @NotBlank(message = "Mã thành phố không được để trống")
    private String maThanhPho;

    public UpdateRapRequest() {
    }

    public String getTenRap() {
        return tenRap;
    }

    public void setTenRap(String tenRap) {
        this.tenRap = tenRap;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getMaThanhPho() {
        return maThanhPho;
    }

    public void setMaThanhPho(String maThanhPho) {
        this.maThanhPho = maThanhPho;
    }
}