package com.example.demo.repository;

import com.example.demo.constant.HoaDonStatus;
import com.example.demo.constant.VeStatus;
import com.example.demo.entity.Ve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface VeRepository extends JpaRepository<Ve, String> {

    List<Ve> findBySuatChieu_MaSuatAndTrangThaiVe(String maSuat, VeStatus status);

    List<Ve> findBySuatChieu_MaSuatAndTrangThaiVeIn(String maSuat, Collection<VeStatus> statuses);

    List<Ve> findBySuatChieu_MaSuatAndGhe_MaGheInAndTrangThaiVeIn(
            String maSuat,
            Collection<String> maGhes,
            Collection<VeStatus> statuses
    );

    List<Ve> findByHoaDon_MaDonOrderByGhe_SoHangAscGhe_SoCotAsc(String maDon);

    List<Ve> findByHoaDon_TrangThaiAndHoaDon_ThoiGianDatBefore(HoaDonStatus trangThai, LocalDateTime thoiDiem);

    List<Ve> findBySuatChieu_MaSuatAndHoaDon_TrangThaiAndHoaDon_ThoiGianDatBefore(
            String maSuat,
            HoaDonStatus trangThai,
            LocalDateTime thoiDiem
    );

    long countBySuatChieu_MaSuatAndTrangThaiVeIn(String maSuat, Collection<VeStatus> statuses);

    void deleteByHoaDon_MaDon(String maDon);
}
