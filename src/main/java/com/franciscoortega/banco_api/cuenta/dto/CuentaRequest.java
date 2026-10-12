package com.franciscoortega.banco_api.cuenta.dto;

import jakarta.validation.constraints.NotNull;

public record CuentaRequest(@NotNull Long clienteId) {

}
