package com.franciscoortega.banco_api.cuenta.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.franciscoortega.banco_api.cuenta.dto.CuentaRequest;
import com.franciscoortega.banco_api.cuenta.dto.CuentaResponse;
import com.franciscoortega.banco_api.cuenta.service.CuentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController 
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor 
public class CuentaController {
    
    private final CuentaService cuentaService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponse crear(@Valid @RequestBody CuentaRequest request) {
        return cuentaService.crearCuenta(request);
    }
    
    @GetMapping("/{id}")
    public CuentaResponse obtener(@PathVariable Long id) {
        return cuentaService.obtenerCuenta(id);
    }
    

}
