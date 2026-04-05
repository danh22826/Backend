package com.example.demo.service;

import com.example.demo.dto.request.Rap.CreateRapRequest;
import com.example.demo.dto.request.Rap.UpdateRapRequest;
import com.example.demo.dto.response.RapResponse;
import com.example.demo.entity.Rap;
import com.example.demo.entity.ThanhPho;
import com.example.demo.repository.RapRepository;
import com.example.demo.repository.ThanhPhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RapService {

    private final RapRepository rapRepository;
    private final ThanhPhoRepository thanhPhoRepository;

    public RapService(RapRepository rapRepository, ThanhPhoRepository thanhPhoRepository) {
        this.rapRepository = rapRepository;
        this.thanhPhoRepository = thanhPhoRepository;
    }

    public List<RapResponse> getRapTheoMaThanhPho(String maThanhPho) {
        return rapRepository.findByThanhPho_MaThanhPhoOrderByTenRapAsc(maThanhPho)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RapResponse getRapById(String maRap) {
        Rap rap = rapRepository.findById(maRap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp"));

        return toResponse(rap);
    }
    @Transactional
    public RapResponse createRap(CreateRapRequest request) {

        if (rapRepository.existsById(request.getMaRap())) {
            throw new RuntimeException("Mã rạp đã tồn tại");
        }

        ThanhPho tp = thanhPhoRepository.findById(request.getMaThanhPho())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phố"));

        Rap rap = new Rap();
        rap.setMaRap(request.getMaRap());
        rap.setTenRap(request.getTenRap());
        rap.setDiaChi(request.getDiaChi());
        rap.setThanhPho(tp);

        return toResponse(rapRepository.save(rap));
    }
    @Transactional
    public RapResponse updateRap(String maRap, UpdateRapRequest request) {

        Rap rap = rapRepository.findById(maRap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp"));

        ThanhPho tp = thanhPhoRepository.findById(request.getMaThanhPho())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phố"));

        rap.setTenRap(request.getTenRap());
        rap.setDiaChi(request.getDiaChi());
        rap.setThanhPho(tp);

        return toResponse(rapRepository.save(rap));
    }
@Transactional
    public void deleteRap(String maRap) {
        rapRepository.deleteById(maRap);
    }

    private RapResponse toResponse(Rap rap) {
        RapResponse res = new RapResponse();

        res.setMaRap(rap.getMaRap());
        res.setTenRap(rap.getTenRap());
        res.setDiaChi(rap.getDiaChi());

        res.setMaThanhPho(rap.getThanhPho().getMaThanhPho());
        res.setTenThanhPho(rap.getThanhPho().getTenThanhPho());

        return res;
    }
}