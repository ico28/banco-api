package com.franciscoortega.banco_api.movimiento.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.franciscoortega.banco_api.movimiento.dto.TransferenciaRequest;
import com.franciscoortega.banco_api.movimiento.dto.TransferenciaResponse;
import com.franciscoortega.banco_api.movimiento.service.TransferenciaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/transferencias")
@RequiredArgsConstructor 
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse transferir(@Valid @RequestBody TransferenciaRequest request) {
        return transferenciaService.transferir(request);
    }
    
}
