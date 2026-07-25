-- CATEGORIAS

INSERT INTO categoria (nome, descricao, ativo, setor_id)
VALUES
('Pizzas', 'Pizzas salgadas e doces', TRUE, 1),
('Lanches', 'Lanches tradicionais', TRUE, 2);


-- PRODUTOS

INSERT INTO produto (
    categoria_id,
    nome,
    descricao,
    preco,
    imagem,
    disponivel,
    ativo
)
VALUES
(
    1,
    'Pizza Calabresa',
    'Calabresa com cebola',
    49.90,
    'calabresa.jpg',
    TRUE,
    TRUE
),
(
    2,
    'X Salada',
    'X salada simples',
    20.90,
    'x-salada.jpg',
    TRUE,
    TRUE
);