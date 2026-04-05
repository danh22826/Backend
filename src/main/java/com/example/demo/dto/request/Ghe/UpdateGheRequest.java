package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UpdateGheRequest {

    @NotBlank(message = "Số hàng không được để trống")
    @Pattern(regexp = "^[A-Z]{1,2}$", message = "Số hàng phải là chữ cái in hoa (VD: A, B, C... hoặc AA, BB)")
    private String soHang;

    @NotNull(message = "Số cột không được để trống")
    @Min(value = 1, message = "Số cột phải từ 1 trở lên")
    private Integer soCot;

    @NotBlank(message = "Mã loại ghế không được để trống")
    private String maLoaiGhe;

    // 1. Constructor mặc định (Bắt buộc cho Jackson)
    public UpdateGheRequest() {
    }

    // 2. Getters và Setters
    public String getSoHang() {
        return soHang;
    }

    public void setSoHang(String soHang) {
        this.soHang = soHang;
    }

    public Integer getSoCot() {
        return soCot;
    }

    public void setSoCot(Integer soCot) {
        this.soCot = soCot;
    }

    public String getMaLoaiGhe() {
        return maLoaiGhe;
    }

    public void setMaLoaiGhe(String maLoaiGhe) {
        this.maLoaiGhe = maLoaiGhe;
    }
}