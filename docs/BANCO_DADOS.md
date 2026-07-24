# Banco de Dados SIGIN Delivery

## Banco atual

SGBD:

PostgreSQL


O banco atual utiliza modelo relacional com entidades separadas e relacionamentos utilizando chaves estrangeiras.


---

# Modelo de entidades


## Pedido

Tabela:

pedido


Responsável por armazenar os dados principais do pedido.


Campos principais:

- id
- cliente_nome
- cliente_whatsapp
- status
- status_alterado_em
- observacao
- observacao_operacao
- valor_produtos
- taxa_entrega
- valor_total
- data_criacao


Relacionamentos:

Pedido possui vários itens.


Relacionamento:

Pedido 1:N PedidoItem


---

# PedidoItem

Tabela:

pedido_item


Representa os produtos adicionados ao pedido.


Campos principais:

- id
- pedido_id
- produto_id
- quantidade
- valor_unitario
- valor_total


Relacionamentos:

PedidoItem pertence a:

- Pedido
- Produto


---

# Produto

Tabela:

produto


Representa os produtos comercializados.


Campos principais:

- id
- nome
- descricao
- preco
- imagem
- ativo
- disponivel
- categoria_id


Relacionamento:

Produto pertence a uma Categoria.


---

# Categoria

Tabela:

categoria


Agrupa produtos por tipo.


Campos principais:

- id
- nome
- descricao
- ativo
- setor_id


Exemplos:

- Lanches
- Pizzas Doces


Relacionamento:

Categoria pertence a um Setor.


---

# Setor

Tabela:

setor


Define o setor responsável pela produção.


Campos principais:

- id
- nome


Exemplos:

- COZINHA
- PIZZARIA


Utilizado para direcionamento operacional dos pedidos.


---

# Relacionamentos

```json
Setor

1

|

N

Categoria

1

|

N

Produto

1

|

N

PedidoItem

N

|

1
```
Pedido
---

# Fluxo de dados


## Criação do pedido

1. Cliente informa dados.
2. Pedido é criado com status RECEBIDO.
3. Produtos são adicionados como PedidoItem.
4. Valores são calculados.


---

## Produção


O setor é definido através:

Produto

↓

Categoria

↓

Setor


O frontend filtra os pedidos conforme o setor operacional.


Exemplo:


PIZZARIA:

Pizza Calabresa


COZINHA:

X Salada


---

# Pontos de evolução


## Status por item

Problema atual:

O status pertence ao pedido.


Exemplo:

Pedido:

- Pizza
- Lanche


Ambos possuem o mesmo status.


Evolução futura:

Adicionar controle individual por PedidoItem.


Modelo futuro:

Pedido

|

├── PedidoItem Pizza

│   status_producao

|

└── PedidoItem Lanche

    status_producao


Objetivo:

Permitir produção independente por setor.


---

# Nova etapa futura


Substituir:

FINALIZADO


Por:

PRONTO_DESPACHO


Motivo:

Após produção finalizar, ainda existem etapas:

- separar bebidas
- conferir pedido
- preparar despacho
- enviar para entrega


---

# Histórico de alterações

## Sprint inicial Delivery

Implementado:

- cadastro de produtos
- categorias
- setores
- pedidos
- fluxo operacional
- produção por setor
- entrega
- cancelamento com justificativa

