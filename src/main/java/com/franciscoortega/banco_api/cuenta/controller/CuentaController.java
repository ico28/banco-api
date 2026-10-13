package com.franciscoortega.banco_api.cuenta.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.franciscoortega.banco_api.cuenta.dto.CuentaRequest;
import com.franciscoortega.banco_api.cuenta.dto.CuentaResponse;
import com.franciscoortega.banco_api.cuenta.service.CuentaService;
import com.franciscoortega.banco_api.movimiento.dto.MovimientoRequest;
import com.franciscoortega.banco_api.movimiento.dto.MovimientoResponse;
import com.franciscoortega.banco_api.movimiento.service.MovimientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController 
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor 
public class CuentaController {
    
    private final CuentaService cuentaService;
    private final MovimientoService movimientoService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponse crear(@Valid @RequestBody CuentaRequest request) {
        return cuentaService.crearCuenta(request);
    }
    
    @GetMapping("/{id}")
    public CuentaResponse obtener(@PathVariable Long id) {
        return cuentaService.obtenerCuenta(id);
    }

    @GetMapping("/{id}/movimientos")
    public List<MovimientoResponse> historial(@PathVariable Long id){
        return movimientoService.historial(id);
    } 

    @PostMapping("/{id}/ingresos")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponse ingresar(@Valid @RequestBody MovimientoRequest request, @PathVariable Long id) {
        return movimientoService.ingresar(request, id);
    }

    @PostMapping("/{id}/retiradas")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponse retirar(@Valid @RequestBody MovimientoRequest request, @PathVariable Long id) {
        return movimientoService.retirar(request, id);
    }
    
    

}
