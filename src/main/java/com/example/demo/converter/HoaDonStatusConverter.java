package com.example.demo.converter;

import com.example.demo.constant.HoaDonStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class HoaDonStatusConverter implements AttributeConverter<HoaDonStatus, String> {

    @Override
    public String convertToDatabaseColumn(HoaDonStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public HoaDonStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        return switch (dbData.trim()) {
            case "CHUA_THANH_TOAN", "Chờ thanh toán" -> HoaDonStatus.CHUA_THANH_TOAN;
            case "DA_THANH_TOAN", "Đã thanh toán" -> HoaDonStatus.DA_THANH_TOAN;
            case "DA_HUY", "Đã hủy" -> HoaDonStatus.DA_HUY;
            default -> throw new IllegalArgumentException("Giá trị trạng thái hóa đơn không hợp lệ: " + dbData);
        };
    }
}
