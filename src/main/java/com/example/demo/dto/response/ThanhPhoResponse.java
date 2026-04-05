package com.example.demo.dto.response;

public class ThanhPhoResponse {

    private String maThanhPho;
    private String tenThanhPho;

    public ThanhPhoResponse() {
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
}