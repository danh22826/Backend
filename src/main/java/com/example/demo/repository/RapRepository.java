package com.example.demo.repository;

import com.example.demo.entity.Rap;
import com.example.demo.entity.ThanhPho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RapRepository extends JpaRepository<Rap, String> {
    List<Rap> findByThanhPho(ThanhPho thanhPho);

    List<Rap> findByThanhPho_MaThanhPhoOrderByTenRapAsc(String maThanhPho);
}