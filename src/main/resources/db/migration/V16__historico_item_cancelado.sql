ALTER TABLE pedido_item
ADD COLUMN motivo_cancelamento VARCHAR(500);

ALTER TABLE pedido_item
ADD COLUMN cancelado_em TIMESTAMP;

ALTER TABLE pedido_item
ADD COLUMN cancelado_por VARCHAR(120);