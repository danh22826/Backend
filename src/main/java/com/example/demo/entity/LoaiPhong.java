package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "LoaiPhong")
public class LoaiPhong {

    @Id
    @Column(name = "MaLoaiPhong", length = 20)
    private String maLoaiPhong;

    @Column(name = "TenLoaiPhong", nullable = false, length = 100)
    private String tenLoaiPhong;

    @OneToMany(mappedBy = "loaiPhong")
    @JsonIgnore
    private List<PhongChieu> dsPhongChieu;

    public LoaiPhong() {
    }

    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
    }

    // 3. Getters và Setters
    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public List<PhongChieu> getDsPhongChieu() {
        return dsPhongChieu;
    }

    public void setDsPhongChieu(List<PhongChieu> dsPhongChieu) {
        this.dsPhongChieu = dsPhongChieu;
    }
}