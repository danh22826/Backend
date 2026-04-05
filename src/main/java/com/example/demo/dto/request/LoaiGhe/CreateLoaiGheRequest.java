package com.example.demo.dto.request.LoaiGhe;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateLoaiGheRequest {

    @NotBlank(message = "Mã loại ghế không được để trống")
    @Size(max = 20, message = "Mã loại ghế tối đa 20 ký tự")
    private String maLoaiGhe;

    @NotBlank(message = "Tên loại ghế không được để trống")
    @Size(max = 100, message = "Tên loại ghế tối đa 100 ký tự")
    private String tenLoaiGhe;

    @NotNull(message = "Giá phụ thu không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá phụ thu phải >= 0")
    private BigDecimal giaPhuThu;

    public CreateLoaiGheRequest() {}

    public String getMaLoaiGhe() { return maLoaiGhe; }
    public void setMaLoaiGhe(String maLoaiGhe) { this.maLoaiGhe = maLoaiGhe; }

    public String getTenLoaiGhe() { return tenLoaiGhe; }
    public void setTenLoaiGhe(String tenLoaiGhe) { this.tenLoaiGhe = tenLoaiGhe; }

    public BigDecimal getGiaPhuThu() { return giaPhuThu; }
    public void setGiaPhuThu(BigDecimal giaPhuThu) { this.giaPhuThu = giaPhuThu; }
}