package com.franciscoortega.banco_api.cliente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.franciscoortega.banco_api.cliente.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocumento(String documento);
    Optional<Cliente> findById(Long id);
}