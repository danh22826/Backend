package com.example.demo.dto.request.HoaDon;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class CreateHoaDonRequest {

    @NotBlank(message = "Ma suat chieu khong duoc de trong")
    private String maSuat;

    @NotBlank(message = "Ma khach hang khong duoc de trong")
    private String maKhachHang;

    @NotEmpty(message = "Danh sach ghe khong duoc de trong")
    private List<String> dsGhe;

    @Valid
    private List<HoaDonDoAnRequest> dsDoAn = new ArrayList<>();

    public String getMaSuat() {
        return maSuat;
    }

    public void setMaSuat(String maSuat) {
        this.maSuat = maSuat;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public List<String> getDsGhe() {
        return dsGhe;
    }

    public void setDsGhe(List<String> dsGhe) {
        this.dsGhe = dsGhe;
    }

    public List<HoaDonDoAnRequest> getDsDoAn() {
        return dsDoAn;
    }

    public void setDsDoAn(List<HoaDonDoAnRequest> dsDoAn) {
        this.dsDoAn = dsDoAn;
    }
}
