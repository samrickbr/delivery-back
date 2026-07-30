INSERT INTO setor (nome)
SELECT 'PIZZARIA'
WHERE NOT EXISTS (
    SELECT 1 FROM setor WHERE nome = 'PIZZARIA'
);


UPDATE categoria
SET setor_id = (
    SELECT id
    FROM setor
    WHERE nome = 'PIZZARIA'
)
WHERE nome = 'Pizzas';