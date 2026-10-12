package com.franciscoortega.banco_api.cuenta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.franciscoortega.banco_api.cuenta.model.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    List<Cuenta> findByClienteId(Long clienteId);
    Optional<Cuenta> findByIban(String iban);
}
