ALTER TABLE pedido_item
    DROP CONSTRAINT IF EXISTS fk_pedido_item_produto;

ALTER TABLE pedido_item
    DROP COLUMN IF EXISTS produto_id;