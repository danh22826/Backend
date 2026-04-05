package com.example.demo.service;

import com.example.demo.dto.request.ThanhPho.CreateThanhPhoRequest;
import com.example.demo.dto.request.ThanhPho.UpdateThanhPhoRequest;
import com.example.demo.dto.response.ThanhPhoResponse;
import com.example.demo.entity.ThanhPho;
import com.example.demo.repository.ThanhPhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ThanhPhoService {
    private final ThanhPhoRepository thanhPhoRepository;

    public ThanhPhoService(ThanhPhoRepository thanhPhoRepository) {
        this.thanhPhoRepository = thanhPhoRepository;
    }

    public List<ThanhPhoResponse> getAllThanhPho() {
        return thanhPhoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ThanhPhoResponse getThanhPhoById(String maThanhPho) {
        ThanhPho thanhPho = thanhPhoRepository.findById(maThanhPho)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phố với mã: " + maThanhPho));

        return toResponse(thanhPho);
    }
    @Transactional
    public ThanhPhoResponse createThanhPho(CreateThanhPhoRequest request) {
        if (thanhPhoRepository.existsById(request.getMaThanhPho())) {
            throw new RuntimeException("Mã thành phố đã tồn tại: " + request.getMaThanhPho());
        }

        ThanhPho thanhPho = new ThanhPho();
        thanhPho.setMaThanhPho(request.getMaThanhPho());
        thanhPho.setTenThanhPho(request.getTenThanhPho());

        ThanhPho saved = thanhPhoRepository.save(thanhPho);
        return toResponse(saved);
    }
    @Transactional
    public ThanhPhoResponse updateThanhPho(String maThanhPho, UpdateThanhPhoRequest request) {
        ThanhPho thanhPho = thanhPhoRepository.findById(maThanhPho)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phố với mã: " + maThanhPho));

        thanhPho.setTenThanhPho(request.getTenThanhPho());

        ThanhPho updated = thanhPhoRepository.save(thanhPho);
        return toResponse(updated);
    }
    @Transactional
    public void deleteThanhPho(String maThanhPho) {
        if (!thanhPhoRepository.existsById(maThanhPho)) {
            throw new RuntimeException("Không tìm thấy thành phố với mã: " + maThanhPho);
        }

        thanhPhoRepository.deleteById(maThanhPho);
    }

    private ThanhPhoResponse toResponse(ThanhPho thanhPho) {
        ThanhPhoResponse response = new ThanhPhoResponse();
        response.setMaThanhPho(thanhPho.getMaThanhPho());
        response.setTenThanhPho(thanhPho.getTenThanhPho());
        return response;
    }
}