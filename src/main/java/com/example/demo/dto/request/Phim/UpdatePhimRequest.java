package com.example.demo.dto.request.Phim;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class UpdatePhimRequest {

    @NotBlank(message = "Tên phim không được để trống")
    @Size(max = 255, message = "Tên phim không được vượt quá 255 ký tự")
    private String tenPhim;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String moTa;

    private String poster;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải từ 1 phút trở lên")
    private Integer thoiLuong;

    @NotNull(message = "Ngày khởi chiếu không được để trống")
    private LocalDate ngayKhoiChieu;

    @NotBlank(message = "Độ tuổi phù hợp không được để trống (VD: P, T13, T16, T18)")
    @Size(max = 10, message = "Độ tuổi phù hợp tối đa 10 ký tự")
    private String doTuoiPhuHop;

    @NotBlank(message = "Ngôn ngữ không được để trống")
    @Size(max = 50, message = "Ngôn ngữ tối đa 50 ký tự")
    private String ngonNgu;

    @Size(max = 1000, message = "Trailer URL toi da 1000 ky tu")
    private String trailerUrl;

    @NotEmpty(message = "Phim phải thuộc ít nhất 1 thể loại")
    private List<String> maTheLoais;

    // 1. Constructor mặc định (Bắt buộc cho Jackson)
    public UpdatePhimRequest() {
    }

    // 2. Getters và Setters
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

    public List<String> getMaTheLoais() {
        return maTheLoais;
    }

    public void setMaTheLoais(List<String> maTheLoais) {
        this.maTheLoais = maTheLoais;
    }
}
