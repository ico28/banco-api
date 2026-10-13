package com.franciscoortega.banco_api.movimiento.dto;

import com.franciscoortega.banco_api.movimiento.model.Movimiento;

public record TransferenciaResponse(MovimientoResponse origen, MovimientoResponse destino) {

    public static TransferenciaResponse from(Movimiento origen, Movimiento destino){
        return new TransferenciaResponse(MovimientoResponse.from(origen), MovimientoResponse.from(destino));
    }

}
