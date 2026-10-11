package com.franciscoortega.banco_api.cliente.dto;

import java.time.OffsetDateTime;

import com.franciscoortega.banco_api.cliente.model.Cliente;

public record ClienteResponse(Long id, String documento, String nombre,
                              String apellidos, String email, OffsetDateTime fechaAlta) {

                                public static ClienteResponse from (Cliente c){
                                    return new ClienteResponse(c.getId(), c.getDocumento(), c.getNombre(),
                c.getApellidos(), c.getEmail(), c.getFechaAlta());

                                }


}
