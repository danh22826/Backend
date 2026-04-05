package com.example.demo.dto.request.Ghe;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateGheRequest {

    @NotBlank(message = "Mã ghế không được để trống")
    @Size(max = 20, message = "Mã ghế không được vượt quá 20 ký tự")
    private String maGhe;

    @NotBlank(message = "Mã phòng không được để trống")
    private String maPhong;

    @NotBlank(message = "Số hàng không được để trống")
    @Pattern(regexp = "^[A-Z]{1,2}$", message = "Số hàng phải là chữ cái in hoa (VD: A, B, C... hoặc AA, BB)")
    private String soHang;

    @NotNull(message = "Số cột không được để trống")
    @Min(value = 1, message = "Số cột phải từ 1 trở lên")
    private Integer soCot;

    @NotBlank(message = "Mã loại ghế không được để trống")
    private String maLoaiGhe;

    public CreateGheRequest() {
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

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