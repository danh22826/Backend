package com.example.demo.repository;

import com.example.demo.entity.PhongChieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhongChieuRepository extends JpaRepository<PhongChieu, String> {

    List<PhongChieu> findByRap_MaRap(String maRap);

    boolean existsByRap_MaRapAndTenPhong(String maRap, String tenPhong);
}