package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDetailResponse {

    private String maDon;
    private String maKhachHang;
    private BigDecimal tongTien;
    private BigDecimal tongTienVe;
    private BigDecimal tongTienDoAn;
    private BigDecimal tongTienThanhToan;
    private LocalDateTime thoiGianDat;
    private LocalDateTime thoiGianThanhToan;
    private LocalDateTime hanThanhToan;
    private String trangThai;
    private String phuongThucThanhToan;

    private String maSuat;
    private LocalDate ngayChieu;
    private LocalTime gioChieu;
    private BigDecimal giaVeCoBan;

    private String maPhim;
    private String tenPhim;
    private String poster;

    private String maPhong;
    private String tenPhong;
    private Integer tongGhe;

    private String maRap;
    private String tenRap;

    private Integer soVe;
    private boolean coTheThanhToan;
    private List<HoaDonVeResponse> dsVe = new ArrayList<>();
    private List<HoaDonDoAnResponse> dsDoAn = new ArrayList<>();

    public String getMaDon() {
        return maDon;
    }

    public void setMaDon(String maDon) {
        this.maDon = maDon;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getTongTienVe() {
        return tongTienVe;
    }

    public void setTongTienVe(BigDecimal tongTienVe) {
        this.tongTienVe = tongTienVe;
    }

    public BigDecimal getTongTienDoAn() {
        return tongTienDoAn;
    }

    public void setTongTienDoAn(BigDecimal tongTienDoAn) {
        this.tongTienDoAn = tongTienDoAn;
    }

    public BigDecimal getTongTienThanhToan() {
        return tongTienThanhToan;
    }

    public void setTongTienThanhToan(BigDecimal tongTienThanhToan) {
        this.tongTienThanhToan = tongTienThanhToan;
    }

    public LocalDateTime getThoiGianDat() {
        return thoiGianDat;
    }

    public void setThoiGianDat(LocalDateTime thoiGianDat) {
        this.thoiGianDat = thoiGianDat;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public LocalDateTime getHanThanhToan() {
        return hanThanhToan;
    }

    public void setHanThanhToan(LocalDateTime hanThanhToan) {
        this.hanThanhToan = hanThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public String getMaSuat() {
        return maSuat;
    }

    public void setMaSuat(String maSuat) {
        this.maSuat = maSuat;
    }

    public LocalDate getNgayChieu() {
        return ngayChieu;
    }

    public void setNgayChieu(LocalDate ngayChieu) {
        this.ngayChieu = ngayChieu;
    }

    public LocalTime getGioChieu() {
        return gioChieu;
    }

    public void setGioChieu(LocalTime gioChieu) {
        this.gioChieu = gioChieu;
    }

    public BigDecimal getGiaVeCoBan() {
        return giaVeCoBan;
    }

    public void setGiaVeCoBan(BigDecimal giaVeCoBan) {
        this.giaVeCoBan = giaVeCoBan;
    }

    public String getMaPhim() {
        return maPhim;
    }

    public void setMaPhim(String maPhim) {
        this.maPhim = maPhim;
    }

    public String getTenPhim() {
        return tenPhim;
    }

    public void setTenPhim(String tenPhim) {
        this.tenPhim = tenPhim;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
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

    public Integer getTongGhe() {
        return tongGhe;
    }

    public void setTongGhe(Integer tongGhe) {
        this.tongGhe = tongGhe;
    }

    public String getMaRap() {
        return maRap;
    }

    public void setMaRap(String maRap) {
        this.maRap = maRap;
    }

    public String getTenRap() {
        return tenRap;
    }

    public void setTenRap(String tenRap) {
        this.tenRap = tenRap;
    }

    public Integer getSoVe() {
        return soVe;
    }

    public void setSoVe(Integer soVe) {
        this.soVe = soVe;
    }

    public boolean isCoTheThanhToan() {
        return coTheThanhToan;
    }

    public void setCoTheThanhToan(boolean coTheThanhToan) {
        this.coTheThanhToan = coTheThanhToan;
    }

    public List<HoaDonVeResponse> getDsVe() {
        return dsVe;
    }

    public void setDsVe(List<HoaDonVeResponse> dsVe) {
        this.dsVe = dsVe;
    }

    public List<HoaDonDoAnResponse> getDsDoAn() {
        return dsDoAn;
    }

    public void setDsDoAn(List<HoaDonDoAnResponse> dsDoAn) {
        this.dsDoAn = dsDoAn;
    }
}
