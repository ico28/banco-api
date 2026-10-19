package com.franciscoortega.banco_api.movimiento.service;

import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import com.franciscoortega.banco_api.common.PaginaResponse;
import com.franciscoortega.banco_api.cuenta.model.Cuenta;
import com.franciscoortega.banco_api.cuenta.repository.CuentaRepository;
import com.franciscoortega.banco_api.movimiento.dto.MovimientoRequest;
import com.franciscoortega.banco_api.movimiento.dto.MovimientoResponse;
import com.franciscoortega.banco_api.movimiento.model.Movimiento;
import com.franciscoortega.banco_api.movimiento.model.TipoMovimiento;
import com.franciscoortega.banco_api.movimiento.repository.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    @Transactional
    public MovimientoResponse ingresar(MovimientoRequest request, Long idCuenta) {

        Cuenta cuenta = cuentaRepository.findByIdForUpdate(idCuenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cuenta no encontrada " + idCuenta));
        BigDecimal nuevoSaldo = cuenta.getSaldo().add(request.importe());
        cuenta.setSaldo(nuevoSaldo);
        Movimiento nuevoMovimiento = new Movimiento();
        nuevoMovimiento.setCuenta(cuenta);
        nuevoMovimiento.setTipo(TipoMovimiento.INGRESO);
        nuevoMovimiento.setImporte(request.importe());
        nuevoMovimiento.setSaldoResultante(nuevoSaldo);

        return MovimientoResponse.from(movimientoRepository.save(nuevoMovimiento));

    }

    @Transactional
    public MovimientoResponse retirar(MovimientoRequest request, Long idCuenta) {

        Cuenta cuenta = cuentaRepository.findByIdForUpdate(idCuenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cuenta no encontrada con id: " + idCuenta));
        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(request.importe());

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "No hay saldo suficiente");
        }
        cuenta.setSaldo(nuevoSaldo);
        Movimiento nuevoMovimiento = new Movimiento();
        nuevoMovimiento.setCuenta(cuenta);
        nuevoMovimiento.setTipo(TipoMovimiento.RETIRADA);
        nuevoMovimiento.setImporte(request.importe());
        nuevoMovimiento.setSaldoResultante(nuevoSaldo);

        return MovimientoResponse.from(movimientoRepository.save(nuevoMovimiento));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<MovimientoResponse> historial(Long idCuenta, int pagina, int tam) {
        if (!cuentaRepository.existsById(idCuenta)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Cuenta no encontrada con id " + idCuenta);
        }

        Sort orden = Sort.by(Sort.Order.desc("fechaCreacion"), Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(pagina, tam, orden);

        return PaginaResponse.from(movimientoRepository.findByCuentaId(idCuenta, pageable), MovimientoResponse::from);

    }
}
