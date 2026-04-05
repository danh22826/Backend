package com.example.demo.dto.response;

import java.time.LocalDate;
import java.util.List;

public class PhimResponse {

    private String maPhim;
    private String tenPhim;
    private String moTa;
    private String poster;
    private Integer thoiLuong;
    private LocalDate ngayKhoiChieu;
    private String doTuoiPhuHop;
    private String ngonNgu;
    private String trailerUrl;

    private List<String> theLoais;

    // 1. Constructor mặc định (Bắt buộc cho quá trình parse JSON)
    public PhimResponse() {
    }

    // 2. Getters và Setters
    public String getMaPhim() {
        return maPhim;
    }

    public void setMaPhim(String maPhim) {
        this.maPhim = maPhim;
    }

    public String getTenPhim() {
        return tenPhim;
    }

    public void setTenPhim(String tenPhim) {
        this.tenPhim = tenPhim;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Integer getThoiLuong() {
        return thoiLuong;
    }

    public void setThoiLuong(Integer thoiLuong) {
        this.thoiLuong = thoiLuong;
    }

    public LocalDate getNgayKhoiChieu() {
        return ngayKhoiChieu;
    }

    public void setNgayKhoiChieu(LocalDate ngayKhoiChieu) {
        this.ngayKhoiChieu = ngayKhoiChieu;
    }

    public String getDoTuoiPhuHop() {
        return doTuoiPhuHop;
    }

    public void setDoTuoiPhuHop(String doTuoiPhuHop) {
        this.doTuoiPhuHop = doTuoiPhuHop;
    }

    public String getNgonNgu() {
        return ngonNgu;
    }

    public void setNgonNgu(String ngonNgu) {
        this.ngonNgu = ngonNgu;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public List<String> getTheLoais() {
        return theLoais;
    }

    public void setTheLoais(List<String> theLoais) {
        this.theLoais = theLoais;
    }
}
