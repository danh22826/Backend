package com.example.demo.dto.response;

import java.math.BigDecimal;

public class LoaiGheResponse {

    private String maLoaiGhe;
    private String tenLoaiGhe;
    private BigDecimal giaPhuThu;

    public LoaiGheResponse() {
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
}