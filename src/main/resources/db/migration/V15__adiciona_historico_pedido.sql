CREATE TABLE pedido_historico (

    id BIGSERIAL PRIMARY KEY,

    pedido_id BIGINT NOT NULL,

    data_hora TIMESTAMP NOT NULL,

    usuario_id BIGINT,

    usuario_nome VARCHAR(120),

    setor VARCHAR(50) NOT NULL,

    acao VARCHAR(80) NOT NULL,

    descricao VARCHAR(500),

    CONSTRAINT fk_historico_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedido(id)
);