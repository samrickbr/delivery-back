CREATE TABLE configuracao (

    id BIGSERIAL PRIMARY KEY,

    empresa VARCHAR(150),

    telefone VARCHAR(20),

    whatsapp VARCHAR(20),

    taxa_entrega NUMERIC(10,2),

    ativo BOOLEAN NOT NULL DEFAULT TRUE

);