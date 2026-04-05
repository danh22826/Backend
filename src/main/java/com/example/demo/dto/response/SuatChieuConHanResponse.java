package com.example.demo.dto.response;

import java.math.BigDecimal;

public class SuatChieuConHanResponse {

    private String maSuat;
    private String gioChieu;
    private BigDecimal gia;
    private String maPhong;
    private String tenPhong;
    private String tenLoaiPhong;
    private Integer tongGhe;
    private Integer soGheTrong;

    public SuatChieuConHanResponse() {
    }

    public String getMaSuat() {
        return maSuat;
    }

    public void setMaSuat(String maSuat) {
        this.maSuat = maSuat;
    }

    public String getGioChieu() {
        return gioChieu;
    }

    public void setGioChieu(String gioChieu) {
        this.gioChieu = gioChieu;
    }

    public BigDecimal getGia() {
        return gia;
    }

    public void setGia(BigDecimal gia) {
        this.gia = gia;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public Integer getTongGhe() {
        return tongGhe;
    }

    public void setTongGhe(Integer tongGhe) {
        this.tongGhe = tongGhe;
    }

    public Integer getSoGheTrong() {
        return soGheTrong;
    }

    public void setSoGheTrong(Integer soGheTrong) {
        this.soGheTrong = soGheTrong;
    }
}