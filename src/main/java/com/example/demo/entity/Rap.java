package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Rap")
public class Rap {

    @Id
    @Column(name = "MaRap", nullable = false, length = 20)
    private String maRap;

    @Column(name = "TenRap", nullable = false, length = 100)
    private String tenRap;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaThanhPho", nullable = false)
    @JsonIgnoreProperties("dsRap")
    private ThanhPho thanhPho;

    @OneToMany(mappedBy = "rap", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PhongChieu> phongChieus = new ArrayList<>();

    public Rap() {
    }

    public Rap(String maRap, String tenRap, String diaChi, ThanhPho thanhPho) {
        this.maRap = maRap;
        this.tenRap = tenRap;
        this.diaChi = diaChi;
        this.thanhPho = thanhPho;
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

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public ThanhPho getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(ThanhPho thanhPho) {
        this.thanhPho = thanhPho;
    }

    public List<PhongChieu> getPhongChieus() {
        return phongChieus;
    }

    public void setPhongChieus(List<PhongChieu> phongChieus) {
        this.phongChieus = phongChieus;
    }
}