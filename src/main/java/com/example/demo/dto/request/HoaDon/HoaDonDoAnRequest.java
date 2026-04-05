package com.example.demo.dto.request.HoaDon;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HoaDonDoAnRequest {

    @NotBlank(message = "Ma do an khong duoc de trong")
    private String maDoAn;

    @NotNull(message = "So luong khong duoc de trong")
    @Min(value = 1, message = "So luong phai >= 1")
    private Integer soLuong;

    private String ghiChu;

    public String getMaDoAn() {
        return maDoAn;
    }

    public void setMaDoAn(String maDoAn) {
        this.maDoAn = maDoAn;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
