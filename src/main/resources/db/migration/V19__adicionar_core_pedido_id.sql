ALTER TABLE pedido
    ADD COLUMN core_pedido_id BIGINT;

ALTER TABLE pedido
    ADD CONSTRAINT uk_pedido_core_pedido_id
        UNIQUE (core_pedido_id);