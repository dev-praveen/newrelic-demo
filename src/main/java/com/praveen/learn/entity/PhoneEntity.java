package com.praveen.learn.entity;

import com.praveen.learn.model.PhoneDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phone_models", schema = "ecommerce")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneEntity {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "year")
    private Integer year;

    @Column(name = "price")
    private Double price;

    @Column(name = "cpu_model")
    private String cpuModel;

    @Column(name = "hard_disk_size")
    private String hardDiskSize;

    public static PhoneEntity toEntity(PhoneDto dto) {
        PhoneEntity e = new PhoneEntity();
        e.setId(dto.id());
        e.setName(dto.name());
        if (dto.data() != null) {
            e.setYear(dto.data().year());
            e.setPrice(dto.data().price());
            e.setCpuModel(dto.data().cpuModel());
            e.setHardDiskSize(dto.data().hardDiskSize());
        }
        return e;
    }

    public static PhoneDto toDto(PhoneEntity e) {
        PhoneDto.Data data = new PhoneDto.Data(e.getYear(), e.getPrice(), e.getCpuModel(), e.getHardDiskSize());
        return new PhoneDto(e.getId(), e.getName(), data);
    }
}
