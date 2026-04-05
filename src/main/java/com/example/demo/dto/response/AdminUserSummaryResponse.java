package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminUserSummaryResponse {

    private Long id;
    private String username;
    private String role;
    private String maKhachHang;
    private String tenKhachHang;
    private String email;
    private Integer soHoaDon;
    private BigDecimal tongChiTieu;
    private LocalDateTime lanMuaCuoi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(Integer soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public BigDecimal getTongChiTieu() {
        return tongChiTieu;
    }

    public void setTongChiTieu(BigDecimal tongChiTieu) {
        this.tongChiTieu = tongChiTieu;
    }

    public LocalDateTime getLanMuaCuoi() {
        return lanMuaCuoi;
    }

    public void setLanMuaCuoi(LocalDateTime lanMuaCuoi) {
        this.lanMuaCuoi = lanMuaCuoi;
    }
}
