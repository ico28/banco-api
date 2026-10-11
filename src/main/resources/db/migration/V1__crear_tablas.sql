CREATE TABLE cliente (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    documento   VARCHAR(20)     NOT NULL UNIQUE,
    nombre      VARCHAR (100)   NOT NULL,
    apellidos   VARCHAR(150)    NOT NULL,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    fecha_alta  TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE TABLE cuenta (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id          BIGINT                  NOT NULL REFERENCES cliente(id),
    iban                VARCHAR(34)             NOT NULL UNIQUE,
    saldo               NUMERIC(19,2)           NOT NULL DEFAULT 0,
    divisa              CHAR(3)                 NOT NULL DEFAULT 'EUR',
    fecha_creacion      TIMESTAMPTZ             NOT NULL DEFAULT now(),  
    CONSTRAINT chk_cuenta_saldo_no_negativo CHECK (saldo >= 0)
);

CREATE TABLE movimiento (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cuenta_id           BIGINT          REFERENCES cuenta(id) NOT NULL,
    tipo                VARCHAR(50)     NOT NULL,
    importe             NUMERIC(19,2)   NOT NULL,
    saldo_resultante    NUMERIC(19,2)   NOT NULL,
    fecha_creacion      TIMESTAMPTZ     NOT NULL DEFAULT now(),  

    CONSTRAINT chk_tipo_movimiento_valido CHECK (tipo IN ('INGRESO', 'RETIRADA', 'TRANSFERENCIA_ENVIADA', 'TRANSFERENCIA_RECIBIDA')),
    CONSTRAINT chk_movimiento_importe_positivo CHECK (importe > 0)
);

CREATE INDEX idx_cuenta_cliente ON cuenta(cliente_id);
CREATE INDEX idx_movimiento_cuenta_fecha ON movimiento(cuenta_id, fecha_creacion);
