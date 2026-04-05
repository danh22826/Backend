package com.example.demo.entity;

import com.example.demo.constant.HoaDonStatus;
import com.example.demo.converter.HoaDonStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
public class HoaDon {

    @Id
    @Column(name = "MaDon")
    private String maDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    private KhachHang khachHang;

    @Column(name = "TongTien")
    private BigDecimal tongTien;

    @Column(name = "TongTienVe")
    private BigDecimal tongTienVe;

    @Column(name = "TongTienDoAn")
    private BigDecimal tongTienDoAn;

    @Column(name = "TongTienThanhToan")
    private BigDecimal tongTienThanhToan;

    @Column(name = "ThoiGianDat")
    private LocalDateTime thoiGianDat;

    @Column(name = "ThoiGianThanhToan")
    private LocalDateTime thoiGianThanhToan;

    @Convert(converter = HoaDonStatusConverter.class)
    @Column(name = "TrangThai")
    private HoaDonStatus trangThai;

    @Column(name = "PhuongThucThanhToan")
    private String phuongThucThanhToan;

    public HoaDon() {
    }

    public String getMaDon() {
        return maDon;
    }

    public void setMaDon(String maDon) {
        this.maDon = maDon;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
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

    public HoaDonStatus getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(HoaDonStatus trangThai) {
        this.trangThai = trangThai;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
}
