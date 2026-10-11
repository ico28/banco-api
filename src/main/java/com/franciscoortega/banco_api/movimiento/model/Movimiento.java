package com.franciscoortega.banco_api.movimiento.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.franciscoortega.banco_api.cuenta.model.Cuenta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "movimiento")
@Getter 
@Setter 
@NoArgsConstructor 
public class Movimiento {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "cuenta_id")
    private Cuenta cuenta;

    @Enumerated (EnumType.STRING)
    private TipoMovimiento tipo;

    private BigDecimal importe;
    private BigDecimal saldoResultante;


    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;
    
}
