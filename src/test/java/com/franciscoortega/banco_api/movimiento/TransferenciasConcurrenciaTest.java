package com.franciscoortega.banco_api.movimiento;

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
import com.franciscoortega.banco_api.movimiento.dto.TransferenciaRequest;
import com.franciscoortega.banco_api.movimiento.repository.MovimientoRepository;
import com.franciscoortega.banco_api.movimiento.service.TransferenciaService;

@SpringBootTest
@Testcontainers
class TransferenciasConcurrenciaTest {
   
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");
    
    private Long cuentaId1;
    private Long cuentaId2;

    private String cuentaIban1;
    private String cuentaIban2;

    @Autowired TransferenciaService transferenciaService;
    @Autowired CuentaRepository cuentaRepository;
    @Autowired ClienteRepository clienteRepository;
    @Autowired IbanGenerator ibanGenerator;
    @Autowired MovimientoRepository movimientoRepository;

     @AfterEach
    void limpiar() {
        movimientoRepository.deleteAll();
        cuentaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @BeforeEach
    void prepararCuentaConMilEuros() {
        Cliente cliente1 = new Cliente();
        cliente1.setDocumento(UUID.randomUUID().toString().substring(0, 20));
        cliente1.setNombre("Test1");
        cliente1.setApellidos("Concurrencia1");
        cliente1.setEmail(UUID.randomUUID() + "@test1.com");
        clienteRepository.save(cliente1);
    
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setCliente(cliente1);
        cuenta1.setIban(ibanGenerator.generar());
        cuenta1.setSaldo(new BigDecimal("1000.00"));
        cuenta1.setDivisa("EUR");
        cuentaId1 = cuentaRepository.save(cuenta1).getId();
        cuentaIban1 = cuenta1.getIban();

        Cliente cliente2 = new Cliente();
        cliente2.setDocumento(UUID.randomUUID().toString().substring(0, 19)+ 2);
        cliente2.setNombre("Test2");
        cliente2.setApellidos("Concurrencia2");
        cliente2.setEmail(UUID.randomUUID() + "@test2.com");
        clienteRepository.save(cliente2);
    
        Cuenta cuenta2 = new Cuenta();
        cuenta2.setCliente(cliente2);
        cuenta2.setIban(ibanGenerator.generar());
        cuenta2.setSaldo(new BigDecimal("1000.00"));
        cuenta2.setDivisa("EUR");
        cuentaId2 = cuentaRepository.save(cuenta2).getId();
        cuentaIban2 = cuenta2.getIban();
    }

    @Test
    void transferenciasSimultaneas() throws Exception {
        int hilos = 20;
        ExecutorService pool = Executors.newFixedThreadPool(hilos);
        CountDownLatch salida = new CountDownLatch(1);
        AtomicInteger exitos = new AtomicInteger();
        AtomicInteger rechazos = new AtomicInteger();
        List<Future<?>> tareas = new ArrayList<>();

        for (int i = 0; i<hilos; i++){
            final boolean intercalo = i%2==0;
            tareas.add(pool.submit(() -> {
                salida.await();
                try{
                    if(intercalo){
                        transferenciaService.transferir(new TransferenciaRequest(cuentaId1, cuentaIban2, new BigDecimal("10.00")));
                    }else{
                        transferenciaService.transferir(new TransferenciaRequest(cuentaId2, cuentaIban1, new BigDecimal("10.00")));
                    }    
                    exitos.incrementAndGet();
                } catch (ResponseStatusException e) {
                    rechazos.incrementAndGet();
                }
                return null;
            }));
        }

        salida.countDown();
         for (Future<?> tarea : tareas) {
            tarea.get(30, TimeUnit.SECONDS);
        }
        pool.shutdown();

        Cuenta c1 = cuentaRepository.findById(cuentaId1).orElseThrow();
        Cuenta c2 = cuentaRepository.findById(cuentaId2).orElseThrow();
        assertEquals(20, exitos.get());
        assertEquals(0, new BigDecimal("1000.00").compareTo(c1.getSaldo()));
        assertEquals(0, new BigDecimal("1000.00").compareTo(c2.getSaldo()));
        BigDecimal total = c1.getSaldo().add(c2.getSaldo());
        assertEquals(0, new BigDecimal("2000.00").compareTo(total));
        assertEquals(40, movimientoRepository.count());
    }
}
