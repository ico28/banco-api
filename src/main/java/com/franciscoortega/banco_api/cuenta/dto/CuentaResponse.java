package com.franciscoortega.banco_api.cuenta.dto;

import com.franciscoortega.banco_api.cuenta.model.Cuenta;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CuentaResponse(Long id, Long clienteId, String iban, BigDecimal saldo, String divisa, OffsetDateTime fechaCreacion) {
    public static CuentaResponse from(Cuenta c){
        return new CuentaResponse(c.getId(), c.getCliente().getId(), c.getIban(), c.getSaldo(), c.getDivisa(), c.getFechaCreacion());
    }
}
