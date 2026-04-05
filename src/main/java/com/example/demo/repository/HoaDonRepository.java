package com.example.demo.repository;

import com.example.demo.constant.HoaDonStatus;
import com.example.demo.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {

    // ✅ Sửa: Bổ sung chữ "KhachHang_" để JPA biết đường chui vào Object KhachHang tìm MaKhachHang
    List<HoaDon> findByKhachHang_MaKhachHang(String maKhachHang);

    List<HoaDon> findByKhachHang_MaKhachHangOrderByThoiGianDatDesc(String maKhachHang);

    List<HoaDon> findByTrangThai(HoaDonStatus trangThai);

    // ✅ Sửa tương tự cho hàm kết hợp nhiều điều kiện
    List<HoaDon> findByKhachHang_MaKhachHangAndTrangThai(String maKhachHang, HoaDonStatus trangThai);
}
