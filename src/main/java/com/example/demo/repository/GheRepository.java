package com.example.demo.repository;

import com.example.demo.entity.Ghe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GheRepository extends JpaRepository<Ghe, String> {

    List<Ghe> findByPhongChieu_MaPhong(String maPhong);

    boolean existsByPhongChieu_MaPhongAndSoHangAndSoCot(
            String maPhong,
            String soHang,
            Integer soCot
    );

    long countByPhongChieu_MaPhong(String maPhong);
}
