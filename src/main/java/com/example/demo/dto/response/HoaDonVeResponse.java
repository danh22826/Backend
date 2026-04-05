package com.example.demo.dto.response;

import java.math.BigDecimal;

public class HoaDonVeResponse {

    private String maVe;
    private String maGhe;
    private String tenGhe;
    private String tenLoaiGhe;
    private BigDecimal giaVeCoBan;
    private BigDecimal phuThu;
    private BigDecimal thanhTien;
    private String trangThaiVe;

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getTenGhe() {
        return tenGhe;
    }

    public void setTenGhe(String tenGhe) {
        this.tenGhe = tenGhe;
    }

    public String getTenLoaiGhe() {
        return tenLoaiGhe;
    }

    public void setTenLoaiGhe(String tenLoaiGhe) {
        this.tenLoaiGhe = tenLoaiGhe;
    }

    public BigDecimal getGiaVeCoBan() {
        return giaVeCoBan;
    }

    public void setGiaVeCoBan(BigDecimal giaVeCoBan) {
        this.giaVeCoBan = giaVeCoBan;
    }

    public BigDecimal getPhuThu() {
        return phuThu;
    }

    public void setPhuThu(BigDecimal phuThu) {
        this.phuThu = phuThu;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getTrangThaiVe() {
        return trangThaiVe;
    }

    public void setTrangThaiVe(String trangThaiVe) {
        this.trangThaiVe = trangThaiVe;
    }
}
