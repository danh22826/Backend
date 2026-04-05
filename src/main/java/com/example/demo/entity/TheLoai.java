package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "TheLoai")
public class TheLoai {

    @Id
    @Column(name = "MaTheLoai", nullable = false, length = 20)
    private String maTheLoai;

    @Column(name = "TenTheLoai", nullable = false, length = 100)
    private String tenTheLoai;

    @ManyToMany(mappedBy = "theLoais")
    @JsonIgnore
    private List<Phim> phims;

    public TheLoai() {
    }

    public TheLoai(String maTheLoai, String tenTheLoai) {
        this.maTheLoai = maTheLoai;
        this.tenTheLoai = tenTheLoai;
    }

    public String getMaTheLoai() {
        return maTheLoai;
    }

    public void setMaTheLoai(String maTheLoai) {
        this.maTheLoai = maTheLoai;
    }

    public String getTenTheLoai() {
        return tenTheLoai;
    }

    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }

    public List<Phim> getPhims() {
        return phims;
    }

    public void setPhims(List<Phim> phims) {
        this.phims = phims;
    }
}