package com.example.demo.dto.response;

import java.math.BigDecimal;

public class HoaDonDoAnResponse {

    private String maChiTietDoAn;
    private String maDoAn;
    private String tenDoAn;
    private String loaiDoAn;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String hinhAnh;
    private String ghiChu;

    public String getMaChiTietDoAn() {
        return maChiTietDoAn;
    }

    public void setMaChiTietDoAn(String maChiTietDoAn) {
        this.maChiTietDoAn = maChiTietDoAn;
    }

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

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
