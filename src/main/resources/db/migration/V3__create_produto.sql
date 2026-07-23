CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,
    categoria_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    preco NUMERIC(10,2) NOT NULL,
    imagem VARCHAR(500),
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id)
);