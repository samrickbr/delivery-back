CREATE TABLE pedido (
    id BIGSERIAL PRIMARY KEY,

    cliente_nome VARCHAR(150) NOT NULL,
    cliente_whatsapp VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL,

    valor_produtos NUMERIC(10,2) NOT NULL DEFAULT 0,
    taxa_entrega NUMERIC(10,2) NOT NULL DEFAULT 0,
    valor_total NUMERIC(10,2) NOT NULL DEFAULT 0,

    forma_pagamento VARCHAR(30),

    observacao VARCHAR(500),

    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);