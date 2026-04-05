package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ThanhPho")
public class ThanhPho {

    @Id
    @Column(name = "MaThanhPho", nullable = false, length = 20)
    private String maThanhPho;

    @Column(name = "TenThanhPho", nullable = false, length = 100)
    private String tenThanhPho;

    @OneToMany(mappedBy = "thanhPho", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Rap> dsRap = new ArrayList<>();

    public ThanhPho() {}

    public ThanhPho(String maThanhPho, String tenThanhPho) {
        this.maThanhPho = maThanhPho;
        this.tenThanhPho = tenThanhPho;
    }

    public String getMaThanhPho() {
        return maThanhPho;
    }

    public void setMaThanhPho(String maThanhPho) {
        this.maThanhPho = maThanhPho;
    }

    public String getTenThanhPho() {
        return tenThanhPho;
    }

    public void setTenThanhPho(String tenThanhPho) {
        this.tenThanhPho = tenThanhPho;
    }

    public List<Rap> getDsRap() {
        return dsRap;
    }

    public void setDsRap(List<Rap> dsRap) {
        this.dsRap = dsRap;
    }
}