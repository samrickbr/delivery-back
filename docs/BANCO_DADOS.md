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
- status_operacao
- separado
- motivo_cancelamento
- cancelado_em
- cancelado_por


Status_operacao:

- APROVADO
- PENDENTE
- EM_PRODUCAO
- FINALIZADO
- CANCELADO


Relacionamentos:

PedidoItem pertence a:

- Pedido
- Produto


---
---

# PedidoHistorico

Tabela:

pedido_historico


Responsável por armazenar o histórico operacional do pedido.


Campos principais:

- id
- pedido_id
- usuario_nome
- setor
- acao
- descricao
- data_hora


Relacionamento:

Pedido possui vários históricos.


Relacionamento:

Pedido 1:N PedidoHistorico


Exemplos de ações:

- APROVADO
- PRODUCAO_INICIADA
- ITEM_CANCELADO
- FINALIZADO
- CONFERENCIA
- SEPARACAO
- PEDIDO_CANCELADO

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
Pedido


Pedido
1
|
N
PedidoHistorico
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

---

## Conferência e Separação


Após todos os setores finalizarem:


FINALIZADO

↓

Conferência do balcão

↓

AGUARDANDO_SEPARACAO

↓

SEPARADO

↓

SAIU_ENTREGA

↓

ENTREGUE


Responsável:

Balcão


Processos:

- conferir itens produzidos
- validar cancelamentos
- separar pedido
- liberar entrega

---

# Pontos de evolução


# Controle operacional por item


Implementado.


Cada PedidoItem possui seu próprio ciclo operacional.


Exemplo:


Pedido:

- Pizza Calabresa
- X Salada


Itens podem possuir estados diferentes:


Pizza Calabresa:

FINALIZADO


X Salada:

CANCELADO


Objetivo:

Permitir que setores trabalhem de forma independente.


---

# Nova etapa futura

# Evolução futura


Avaliar criação do status:

PRONTO_DESPACHO


Motivo:


Atualmente:

FINALIZADO

representa:

- produção concluída
- conferência realizada
- pedido liberado


Uma evolução futura pode separar:


FINALIZADO

↓

PRONTO_DESPACHO


para representar melhor o momento entre produção e entrega.

---

# Histórico de alterações

## Sprint inicial Delivery

Implementado:

- cadastro de produtos
- categorias
- setores
- pedidos
- PedidoItem com controle operacional
- produção independente por setor
- pendência de produção
- cancelamento por item
- histórico operacional
- conferência do balcão
- separação
- entrega

