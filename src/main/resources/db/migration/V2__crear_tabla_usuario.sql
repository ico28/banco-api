CREATE TABLE usuario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email               VARCHAR(255)    NOT NULL,
    password_hash       VARCHAR(100)    NOT NULL,
    rol                 VARCHAR(20)     NOT NULL,
    cliente_id          BIGINT          UNIQUE REFERENCES cliente(id),
    fecha_creacion      TIMESTAMPTZ     NOT NULL DEFAULT now(), 

    CONSTRAINT chk_usuario_rol_valido  CHECK (rol IN('CLIENTE', 'ADMIN')),
    CONSTRAINT chk_usuario_cliente_obligatorio CHECK (
        (rol = 'ADMIN' AND cliente_id IS NULL)
        OR (rol = 'CLIENTE' AND cliente_id IS NOT NULL)
));

CREATE UNIQUE INDEX uq_usuario_email ON usuario (lower(email));
