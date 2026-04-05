//package com.example.demo.service;
//
//import com.example.demo.constant.VeStatus;
//import com.example.demo.entity.Ve;
//import com.example.demo.repository.VeRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@Transactional(readOnly = true)
//public class VeService {
//
//    private final VeRepository veRepository;
//
//    public VeService(VeRepository veRepository) {
//        this.veRepository = veRepository;
//    }
//
//    public List<Ve> getGheTrong(String maSuat) {
//        return veRepository.findBySuatChieu_MaSuatAndTrangThaiVe(maSuat, VeStatus.TRONG);
//    }
//
//    @Transactional
//    public Ve datVe(Ve ve) {
//        ve.setTrangThaiVe(VeStatus.DA_DAT);
//        return veRepository.save(ve);
//    }
//
//    @Transactional
//    public Ve thanhToanVe(String maVe) {
//        Ve ve = veRepository.findById(maVe).orElseThrow();
//        ve.setTrangThaiVe(VeStatus.DA_THANH_TOAN);
//        return veRepository.save(ve);
//    }
//
//    @Transactional
//    public Ve huyVe(String maVe) {
//        Ve ve = veRepository.findById(maVe).orElseThrow();
//        ve.setTrangThaiVe(VeStatus.DA_HUY);
//        return veRepository.save(ve);
//    }
//}
