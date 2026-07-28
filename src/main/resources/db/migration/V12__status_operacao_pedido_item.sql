ALTER TABLE pedido_item
ADD COLUMN status_operacao VARCHAR(30);

UPDATE pedido_item
SET status_operacao = 'APROVADO'
WHERE status_operacao IS NULL;