package com.example.demo.dto.request.LoaiPhong;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateLoaiPhongRequest {

    @NotBlank(message = "Mã loại phòng không được để trống")
    @Size(max = 20, message = "Mã loại phòng tối đa 20 ký tự")
    private String maLoaiPhong;

    @NotBlank(message = "Tên loại phòng không được để trống")
    @Size(max = 100, message = "Tên loại phòng tối đa 100 ký tự")
    private String tenLoaiPhong;

    public CreateLoaiPhongRequest() {
    }

    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }
}