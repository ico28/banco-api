package com.franciscoortega.banco_api.movimiento.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.franciscoortega.banco_api.cuenta.model.Cuenta;
import com.franciscoortega.banco_api.cuenta.repository.CuentaRepository;
import com.franciscoortega.banco_api.movimiento.dto.TransferenciaRequest;
import com.franciscoortega.banco_api.movimiento.dto.TransferenciaResponse;
import com.franciscoortega.banco_api.movimiento.model.Movimiento;
import com.franciscoortega.banco_api.movimiento.model.TipoMovimiento;
import com.franciscoortega.banco_api.movimiento.repository.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service  
@RequiredArgsConstructor
public class TransferenciaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest request){
        Cuenta cuentaOrigen = cuentaRepository.findById(request.cuentaOrigenId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha encontrado la cuenta origen con id " + request.cuentaOrigenId()));
        Cuenta cuentaDestino = cuentaRepository.findByIban(request.ibanDestino()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha encontrado la cuenta destino con iban " + request.ibanDestino()));

        if (cuentaDestino.getId().equals(cuentaOrigen.getId())){
            throw new ResponseStatusException(HttpStatusCode.valueOf(422), "No se pueden realizar transferencias en la misma cuenta bancaria");
        }

        if (!cuentaDestino.getDivisa().equals(cuentaOrigen.getDivisa())){
            throw new ResponseStatusException(HttpStatusCode.valueOf(422), "No se pueden realizar transferencias con diferente divisa");
        }

        if (cuentaOrigen.getSaldo().compareTo(request.importe())<0){
            throw new ResponseStatusException(HttpStatusCode.valueOf(422), "El saldo de la cuenta origen no es suficiente");
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(request.importe()));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(request.importe()));

        Movimiento nuevoMovimientoEnvio = new Movimiento();
        nuevoMovimientoEnvio.setCuenta(cuentaOrigen);
        nuevoMovimientoEnvio.setTipo(TipoMovimiento.TRANSFERENCIA_ENVIADA);
        nuevoMovimientoEnvio.setImporte(request.importe());
        nuevoMovimientoEnvio.setSaldoResultante(cuentaOrigen.getSaldo());

        movimientoRepository.save(nuevoMovimientoEnvio);

        Movimiento nuevoMovimientoRecibido = new Movimiento();
        nuevoMovimientoRecibido.setCuenta(cuentaDestino);
        nuevoMovimientoRecibido.setTipo(TipoMovimiento.TRANSFERENCIA_RECIBIDA);
        nuevoMovimientoRecibido.setImporte(request.importe());
        nuevoMovimientoRecibido.setSaldoResultante(cuentaDestino.getSaldo());

        movimientoRepository.save(nuevoMovimientoRecibido);

        return TransferenciaResponse.from(nuevoMovimientoEnvio, nuevoMovimientoRecibido);
    }

}
