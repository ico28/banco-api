package com.franciscoortega.banco_api.movimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.franciscoortega.banco_api.movimiento.model.Movimiento;


public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaIdOrderByFechaCreacionDesc(Long cuentaId);
}
