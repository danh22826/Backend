package com.example.demo.dto.request.TheLoai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTheLoaiRequest {

    @NotBlank(message = "Mã thể loại không được để trống")
    @Size(max = 20, message = "Mã thể loại không được vượt quá 20 ký tự")
    private String maTheLoai;

    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 100, message = "Tên thể loại không được vượt quá 100 ký tự")
    private String tenTheLoai;

    public CreateTheLoaiRequest() {
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
}