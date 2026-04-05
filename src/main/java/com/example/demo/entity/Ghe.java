package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(
        name = "Ghe",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_phong_hang_cot", columnNames = {"MaPhong", "SoHang", "SoCot"})
        } // Không có 2 bản ghi trùng ma,hang, cot
)
public class Ghe {

    @Id
    @Column(name = "MaGhe", nullable = false, length = 20)
    private String maGhe;

    // ===== FK -> PhongChieu =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaPhong", nullable = false)
    @JsonIgnoreProperties({"ghes", "suatChieus"}) // tránh vòng lặp
    private PhongChieu phongChieu;

    // ===== Vị trí ghế =====
    @Column(name = "SoHang", nullable = false, length = 10)
    private String soHang;

    @Column(name = "SoCot", nullable = false)
    private Integer soCot;

    // ===== FK -> LoaiGhe =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoaiGhe", nullable = false)
    @JsonIgnoreProperties("dsGhe")
    private LoaiGhe loaiGhe;

    // ===== Constructor =====
    public Ghe() {
    }

    public Ghe(String maGhe, PhongChieu phongChieu, String soHang, Integer soCot, LoaiGhe loaiGhe) {
        this.maGhe = maGhe;
        this.phongChieu = phongChieu;
        this.soHang = soHang;
        this.soCot = soCot;
        this.loaiGhe = loaiGhe;
    }

    // ===== Getter & Setter =====
    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public PhongChieu getPhongChieu() {
        return phongChieu;
    }

    public void setPhongChieu(PhongChieu phongChieu) {
        this.phongChieu = phongChieu;
    }

    public String getSoHang() {
        return soHang;
    }

    public void setSoHang(String soHang) {
        this.soHang = soHang;
    }

    public Integer getSoCot() {
        return soCot;
    }

    public void setSoCot(Integer soCot) {
        this.soCot = soCot;
    }

    public LoaiGhe getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(LoaiGhe loaiGhe) {
        this.loaiGhe = loaiGhe;
    }

    // ===== Helper method (optional) =====
    public String getTenGhe() {
        return soHang + soCot; // ví dụ: A10
    }
}