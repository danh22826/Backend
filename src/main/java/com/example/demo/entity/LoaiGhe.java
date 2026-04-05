package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "LoaiGhe")
public class LoaiGhe {

    @Id
    @Column(name = "MaLoaiGhe")
    private String maLoaiGhe;

    @Column(name = "TenLoaiGhe")
    private String tenLoaiGhe;

    @Column(name = "GiaPhuThu")
    private BigDecimal giaPhuThu;

    @OneToMany(mappedBy = "loaiGhe")
    @JsonIgnore
    private List<Ghe> dsGhe;

    public LoaiGhe() {
    }

    public String getMaLoaiGhe() {
        return maLoaiGhe;
    }

    public void setMaLoaiGhe(String maLoaiGhe) {
        this.maLoaiGhe = maLoaiGhe;
    }

    public String getTenLoaiGhe() {
        return tenLoaiGhe;
    }

    public void setTenLoaiGhe(String tenLoaiGhe) {
        this.tenLoaiGhe = tenLoaiGhe;
    }

    public BigDecimal getGiaPhuThu() {
        return giaPhuThu;
    }

    public void setGiaPhuThu(BigDecimal giaPhuThu) {
        this.giaPhuThu = giaPhuThu;
    }

    public List<Ghe> getDsGhe() {
        return dsGhe;
    }

    public void setDsGhe(List<Ghe> dsGhe) {
        this.dsGhe = dsGhe;
    }
}