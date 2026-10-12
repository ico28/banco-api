package com.franciscoortega.banco_api.cuenta.service;


import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import com.franciscoortega.banco_api.cliente.repository.ClienteRepository;
import com.franciscoortega.banco_api.cuenta.dto.CuentaRequest;
import com.franciscoortega.banco_api.cuenta.dto.CuentaResponse;
import com.franciscoortega.banco_api.cuenta.repository.CuentaRepository;
import com.franciscoortega.banco_api.cuenta.model.Cuenta;
import com.franciscoortega.banco_api.cliente.model.Cliente;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuentaService {
    
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final IbanGenerator generator;


    @Transactional 
    public CuentaResponse crearCuenta(CuentaRequest request){
        
        Optional<Cliente> cliente = clienteRepository.findById(request.clienteId());
        if (cliente.isPresent()){
            Cuenta cuenta = new Cuenta();
            cuenta.setCliente(cliente.get());
            cuenta.setDivisa("EUR");
            cuenta.setSaldo(BigDecimal.ZERO);
            cuenta.setIban(generator.generar());
            return CuentaResponse.from(cuentaRepository.save(cuenta));

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha podido encontrar al cliente con id " + request.clienteId());
        }
    }

    @Transactional(readOnly = true)
    public CuentaResponse obtenerCuenta(Long id){
        Cuenta cuenta = cuentaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha podido encontrar la cuenta con id " + id));
        return CuentaResponse.from(cuenta);
    }
}
