package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "SuatChieu")
public class SuatChieu {

    @Id
    @Column(name = "MaSuat", nullable = false, length = 20)
    private String maSuat;

    @Column(name = "NgayChieu", nullable = false)
    private LocalDate ngayChieu;

    @Column(name = "GioChieu", nullable = false)
    private LocalTime gioChieu;

    @Column(name = "Gia", nullable = false)
    private BigDecimal gia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaPhim", nullable = false)
    @JsonIgnoreProperties({"suatChieus"})
    private Phim phim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaPhong", nullable = false)
    @JsonIgnoreProperties({"ghes", "suatChieus"})
    private PhongChieu phongChieu;

    public SuatChieu() {}

    public SuatChieu(String maSuat, LocalDate ngayChieu, LocalTime gioChieu,
                     BigDecimal gia, Phim phim, PhongChieu phongChieu) {
        this.maSuat = maSuat;
        this.ngayChieu = ngayChieu;
        this.gioChieu = gioChieu;
        this.gia = gia;
        this.phim = phim;
        this.phongChieu = phongChieu;
    }

    public String getMaSuat() { return maSuat; }
    public void setMaSuat(String maSuat) { this.maSuat = maSuat; }

    public LocalDate getNgayChieu() { return ngayChieu; }
    public void setNgayChieu(LocalDate ngayChieu) { this.ngayChieu = ngayChieu; }

    public LocalTime getGioChieu() { return gioChieu; }
    public void setGioChieu(LocalTime gioChieu) { this.gioChieu = gioChieu; }

    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }

    public Phim getPhim() { return phim; }
    public void setPhim(Phim phim) { this.phim = phim; }

    public PhongChieu getPhongChieu() { return phongChieu; }
    public void setPhongChieu(PhongChieu phongChieu) { this.phongChieu = phongChieu; }
}