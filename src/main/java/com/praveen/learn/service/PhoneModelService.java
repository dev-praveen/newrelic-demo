package com.praveen.learn.service;

import com.praveen.learn.entity.PhoneEntity;
import com.praveen.learn.model.PhoneDto;
import com.praveen.learn.repository.PhoneModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneModelService {

    private static final String API_BASE_URL = "https://api.restful-api.dev/objects";

    private final RestClient restClient;
    private final PhoneModelRepository phoneModelRepository;

    public PhoneDto getPhoneModelById(String id) {
        log.info("Fetching phone entity with id: {}", id);

        try {
            // Check if the data already exists in the database
            Optional<PhoneEntity> existingModel = phoneModelRepository.findById(id);
            if (existingModel.isPresent()) {
                log.info("Phone entity with id {} found in database", id);
                return PhoneEntity.toDto(existingModel.get());
            }

            // Call the external API
            String apiUrl = API_BASE_URL + "/" + id;
            log.info("Calling external API: {}", apiUrl);
            PhoneDto response = restClient.get().uri(apiUrl).retrieve().body(PhoneDto.class);

            // Parse the response, save to database and return DTO
            assert response != null;
            PhoneEntity entity = PhoneEntity.toEntity(response);
            phoneModelRepository.save(entity);
            log.info("Phone entity with id {} saved to database", id);
            return response;

        } catch (Exception e) {
            log.error("Error fetching phone entity with id: {}", id, e);
            throw new RuntimeException("Failed to fetch phone entity: " + e.getMessage(), e);
        }
    }

    public PhoneDto refreshPhoneModel(String id) {
        log.info("Refreshing phone entity with id: {}", id);

        try {
            // Delete existing record if exists
            phoneModelRepository.deleteById(id);

            // Call the external API
            String apiUrl = API_BASE_URL + "/" + id;
            log.info("Calling external API: {}", apiUrl);
            PhoneDto response = restClient.get().uri(apiUrl).retrieve().body(PhoneDto.class);

            // Parse the response, save to database and return DTO
            assert response != null;
            PhoneEntity entity = PhoneEntity.toEntity(response);
            phoneModelRepository.save(entity);
            log.info("Phone entity with id {} refreshed and saved to database", id);
            return response;

        } catch (Exception e) {
            log.error("Error refreshing phone entity with id: {}", id, e);
            throw new RuntimeException("Failed to refresh phone entity: " + e.getMessage(), e);
        }
    }
}
