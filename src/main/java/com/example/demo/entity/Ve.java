package com.example.demo.entity;

import com.example.demo.constant.VeStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Ve")
public class Ve {

    @Id
    @Column(name = "MaVe")
    private String maVe;

    @ManyToOne
    @JoinColumn(name = "MaSuat", nullable = false)
    private SuatChieu suatChieu;

    @ManyToOne
    @JoinColumn(name = "MaGhe", nullable = false)
    private Ghe ghe;

    @ManyToOne
    @JoinColumn(name = "MaDon", nullable = false)
    private HoaDon hoaDon;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiVe", nullable = false)
    private VeStatus trangThaiVe;

    @Column(name = "GiaVeCoBan")
    private BigDecimal giaVeCoBan;

    @Column(name = "PhuThu")
    private BigDecimal phuThu;

    @Column(name = "ThanhTien")
    private BigDecimal thanhTien;

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public SuatChieu getSuatChieu() {
        return suatChieu;
    }

    public void setSuatChieu(SuatChieu suatChieu) {
        this.suatChieu = suatChieu;
    }

    public Ghe getGhe() {
        return ghe;
    }

    public void setGhe(Ghe ghe) {
        this.ghe = ghe;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public VeStatus getTrangThaiVe() {
        return trangThaiVe;
    }

    public void setTrangThaiVe(VeStatus trangThaiVe) {
        this.trangThaiVe = trangThaiVe;
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
}
