package com.example.demo.dto.request.DoAn;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateDoAnRequest {

    @NotBlank(message = "Ma do an khong duoc de trong")
    private String maDoAn;

    @NotBlank(message = "Ten do an khong duoc de trong")
    private String tenDoAn;

    private String loaiDoAn;

    @NotNull(message = "Don gia khong duoc de trong")
    @DecimalMin(value = "0.0", inclusive = true, message = "Don gia phai >= 0")
    private BigDecimal donGia;

    private String moTa;

    private String hinhAnh;

    private String trangThai;

    public String getMaDoAn() {
        return maDoAn;
    }

    public void setMaDoAn(String maDoAn) {
        this.maDoAn = maDoAn;
    }

    public String getTenDoAn() {
        return tenDoAn;
    }

    public void setTenDoAn(String tenDoAn) {
        this.tenDoAn = tenDoAn;
    }

    public String getLoaiDoAn() {
        return loaiDoAn;
    }

    public void setLoaiDoAn(String loaiDoAn) {
        this.loaiDoAn = loaiDoAn;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
