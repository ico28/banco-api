package com.franciscoortega.banco_api.movimiento.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.franciscoortega.banco_api.cliente.model.Cliente;
import com.franciscoortega.banco_api.cliente.repository.ClienteRepository;
import com.franciscoortega.banco_api.cuenta.model.Cuenta;
import com.franciscoortega.banco_api.cuenta.repository.CuentaRepository;
import com.franciscoortega.banco_api.cuenta.service.IbanGenerator;
import com.franciscoortega.banco_api.movimiento.dto.MovimientoRequest;
import com.franciscoortega.banco_api.movimiento.repository.MovimientoRepository;

@SpringBootTest
@Testcontainers
class MovimientoConcurrenciaTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired MovimientoService movimientoService;
    @Autowired MovimientoRepository movimientoRepository;
    @Autowired CuentaRepository cuentaRepository;
    @Autowired ClienteRepository clienteRepository;
    @Autowired IbanGenerator ibanGenerator;

    private Long cuentaId;

    @BeforeEach
    void prepararCuentaConCienEuros() {
        Cliente cliente = new Cliente();
        cliente.setDocumento(UUID.randomUUID().toString().substring(0, 20));
        cliente.setNombre("Test");
        cliente.setApellidos("Concurrencia");
        cliente.setEmail(UUID.randomUUID() + "@test.com");
        clienteRepository.save(cliente);

        Cuenta cuenta = new Cuenta();
        cuenta.setCliente(cliente);
        cuenta.setIban(ibanGenerator.generar());
        cuenta.setSaldo(new BigDecimal("100.00"));
        cuenta.setDivisa("EUR");
        cuentaId = cuentaRepository.save(cuenta).getId();
    }

    @AfterEach
    void limpiar() {
        movimientoRepository.deleteAll();
        cuentaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void veinteRetiradasSimultaneasDeDiezEurosSoloTienenExitoDiez() throws Exception {
        int hilos = 20;
        ExecutorService pool = Executors.newFixedThreadPool(hilos);
        CountDownLatch salida = new CountDownLatch(1);
        AtomicInteger exitos = new AtomicInteger();
        AtomicInteger rechazos = new AtomicInteger();
        List<Future<?>> tareas = new ArrayList<>();

        for (int i = 0; i < hilos; i++) {
            tareas.add(pool.submit(() -> {
                salida.await(); // todos esperan a la señal de salida
                try {
                    movimientoService.retirar(new MovimientoRequest(new BigDecimal("10.00")), cuentaId);
                    exitos.incrementAndGet();
                } catch (ResponseStatusException e) {
                    rechazos.incrementAndGet();
                }
                return null;
            }));
        }

        salida.countDown(); // disparo simultáneo
        for (Future<?> tarea : tareas) {
            tarea.get(30, TimeUnit.SECONDS);
        }
        pool.shutdown();

        Cuenta cuenta = cuentaRepository.findById(cuentaId).orElseThrow();
        assertEquals(10, exitos.get());
        assertEquals(10, rechazos.get());
        assertEquals(0, BigDecimal.ZERO.compareTo(cuenta.getSaldo()));
        assertEquals(10, movimientoRepository.findByCuentaIdOrderByFechaCreacionDesc(cuentaId).size());
    }

    
}