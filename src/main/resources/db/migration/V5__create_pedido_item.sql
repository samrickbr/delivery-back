CREATE TABLE pedido_item (
    id BIGSERIAL PRIMARY KEY,

    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,

    quantidade INTEGER NOT NULL,

    valor_unitario NUMERIC(10,2) NOT NULL,
    valor_total NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_pedido_item_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedido(id),

    CONSTRAINT fk_pedido_item_produto
        FOREIGN KEY (produto_id)
        REFERENCES produto(id)
);