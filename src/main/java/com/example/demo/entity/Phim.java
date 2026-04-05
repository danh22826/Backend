package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Phim")
public class Phim {

    @Id
    @Column(name = "MaPhim", nullable = false, length = 20)
    private String maPhim;

    @Column(name = "TenPhim", nullable = false, length = 255)
    private String tenPhim;

    @Column(name = "MoTa")
    private String moTa;

    @Column(name = "Poster", length = 500)
    private String poster;

    @Column(name = "ThoiLuong", nullable = false)
    private Integer thoiLuong;

    @Column(name = "NgayKhoiChieu")
    private LocalDate ngayKhoiChieu;

    @Column(name = "DoTuoiPhuHop", length = 20)
    private String doTuoiPhuHop;

    @Column(name = "NgonNgu", length = 50)
    private String ngonNgu;

    @Column(name = "TrailerUrl", length = 1000)
    private String trailerUrl;

    // ===== MANY TO MANY THE LOAI =====
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "PhimTheLoai",
            joinColumns = @JoinColumn(name = "MaPhim"),
            inverseJoinColumns = @JoinColumn(name = "MaTheLoai")
    )
    @JsonIgnoreProperties("phims")
    private List<TheLoai> theLoais;

    // ===== ONE TO MANY SUAT CHIEU =====
    @OneToMany(mappedBy = "phim", fetch = FetchType.LAZY)
    @JsonIgnore // tránh loop JSON
    private List<SuatChieu> suatChieus;

    public Phim() {
    }

    // ===== Getter Setter =====

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

    public List<TheLoai> getTheLoais() {
        return theLoais;
    }

    public void setTheLoais(List<TheLoai> theLoais) {
        this.theLoais = theLoais;
    }

    public List<SuatChieu> getSuatChieus() {
        return suatChieus;
    }

    public void setSuatChieus(List<SuatChieu> suatChieus) {
        this.suatChieus = suatChieus;
    }
}
