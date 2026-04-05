package com.example.demo.dto.request.SuatChieu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateSuatChieuRequest {

    @NotBlank(message = "Mã phim không được để trống")
    private String maPhim;

    @NotBlank(message = "Mã phòng chiếu không được để trống")
    private String maPhong;

    @NotNull(message = "Ngày chiếu không được để trống")
    private LocalDate ngayChieu;

    @NotNull(message = "Giờ chiếu không được để trống")
    private LocalTime gioChieu;

    @NotNull(message = "Giá vé không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá vé phải từ 0 đồng trở lên")
    private BigDecimal gia;

    public UpdateSuatChieuRequest() {
    }

    public String getMaPhim() { return maPhim; }
    public void setMaPhim(String maPhim) { this.maPhim = maPhim; }

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public LocalDate getNgayChieu() { return ngayChieu; }
    public void setNgayChieu(LocalDate ngayChieu) { this.ngayChieu = ngayChieu; }

    public LocalTime getGioChieu() { return gioChieu; }
    public void setGioChieu(LocalTime gioChieu) { this.gioChieu = gioChieu; }

    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
}