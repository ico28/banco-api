package com.franciscoortega.banco_api.movimiento.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.franciscoortega.banco_api.movimiento.model.Movimiento;
import com.franciscoortega.banco_api.movimiento.model.TipoMovimiento;

public record MovimientoResponse(Long id, Long cuentaId, TipoMovimiento tipo, BigDecimal importe,
        BigDecimal saldoResultante, OffsetDateTime fechaCreacion) {

    public static MovimientoResponse from(Movimiento m) {
        return new MovimientoResponse(m.getId(), m.getCuenta().getId(), m.getTipo(), m.getImporte(),
                m.getSaldoResultante(), m.getFechaCreacion());
    }

}
