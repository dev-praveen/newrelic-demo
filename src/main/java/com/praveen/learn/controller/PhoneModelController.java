package com.praveen.learn.controller;

import com.praveen.learn.model.PhoneDto;
import com.praveen.learn.service.PhoneModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/phone-entity")
public class PhoneModelController {

    private final PhoneModelService phoneModelService;

    @GetMapping("/{id}")
    public ResponseEntity<PhoneDto> getPhoneModel(@PathVariable String id) {
        log.info("Request received to get phone entity with id: {}", id);
        PhoneDto phoneDto = phoneModelService.getPhoneModelById(id);
        return ResponseEntity.ok(phoneDto);
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<PhoneDto> refreshPhoneModel(@PathVariable String id) {
        log.info("Request received to refresh phone entity with id: {}", id);
        PhoneDto phoneDto = phoneModelService.refreshPhoneModel(id);
        return ResponseEntity.ok(phoneDto);
    }
}
