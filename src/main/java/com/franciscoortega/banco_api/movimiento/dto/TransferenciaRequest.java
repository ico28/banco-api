package com.franciscoortega.banco_api.movimiento.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record TransferenciaRequest(
    @NotNull Long cuentaOrigenId, 
    @NotBlank @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{11,30}$", message = "Formato de IBAN no válido") String ibanDestino, 
    @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal importe) {

}
