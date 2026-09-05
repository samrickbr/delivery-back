ALTER TABLE pedido
    ADD COLUMN canal_venda_id BIGINT;

ALTER TABLE pedido
    ADD COLUMN canal_venda JSONB;
