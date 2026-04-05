package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(
        name = "PhongChieu",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_rap_tenphong", columnNames = {"MaRap", "TenPhong"})
        }
)
public class PhongChieu {

    @Id
    @Column(name = "MaPhong", nullable = false, length = 20)
    private String maPhong;

    @Column(name = "TenPhong", nullable = false, length = 100)
    private String tenPhong;

    @Column(name = "SucChua", nullable = false)
    private Integer sucChua;

    // ===== FK -> Rap =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaRap", nullable = false)
    @JsonIgnoreProperties({"phongChieus"}) // tránh vòng lặp
    private Rap rap;

    // ===== FK -> LoaiPhong =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoaiPhong", nullable = false)
    @JsonIgnoreProperties({"dsPhongChieu"})
    private LoaiPhong loaiPhong;

    // ===== Quan hệ với Ghế =====
    @OneToMany(mappedBy = "phongChieu")
    @JsonIgnore
    private List<Ghe> ghes;

    // ===== Quan hệ với Suất chiếu =====
    @OneToMany(mappedBy = "phongChieu")
    @JsonIgnore
    private List<SuatChieu> suatChieus;

    // ===== Constructor =====
    public PhongChieu() {
    }

    public PhongChieu(String maPhong, String tenPhong, Integer sucChua, Rap rap, LoaiPhong loaiPhong) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.sucChua = sucChua;
        this.rap = rap;
        this.loaiPhong = loaiPhong;
    }

    // ===== Getter & Setter =====
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

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public Rap getRap() {
        return rap;
    }

    public void setRap(Rap rap) {
        this.rap = rap;
    }

    public LoaiPhong getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public List<Ghe> getGhes() {
        return ghes;
    }

    public void setGhes(List<Ghe> ghes) {
        this.ghes = ghes;
    }

    public List<SuatChieu> getSuatChieus() {
        return suatChieus;
    }

    public void setSuatChieus(List<SuatChieu> suatChieus) {
        this.suatChieus = suatChieus;
    }
}