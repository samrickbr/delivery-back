# Fluxo de Pedidos - Delivery

## Status atual do pedido

Fluxo implementado:

RECEBIDO
↓
APROVADO
↓
EM_PRODUCAO
↓
FINALIZADO
↓
SAIU_ENTREGA
↓
ENTREGUE

Cancelamento:

RECEBIDO/APROVADO/PENDENTE/EM_PRODUCAO
↓
CANCELADO


---

# Módulos envolvidos

## Pedido

Responsável pelo controle geral da venda.

Campos principais:

- cliente
- valores
- observações
- status
- data criação
- data alteração de status


## PedidoItem

Representa cada item dentro do pedido.

Relacionamento:

Pedido
└── PedidoItem
└── Produto


## Produto

Possui:

- categoria
- preço
- disponibilidade


## Categoria

Agrupa produtos.

Exemplos:

- Lanches
- Pizzas Doces


## Setor

Define onde o item será produzido.

Setores atuais:

- COZINHA
- PIZZARIA


---

# Fluxo operacional atual


## 1 - Balcão

Responsável por receber o pedido.

Endpoint:

POST /pedidos


Status inicial:

RECEBIDO


Ação:

Aprovar pedido


Resultado:

APROVADO


---

## 2 - Produção

Pedidos são direcionados conforme setor do item.


Exemplo:

Produto:
Pizza Calabresa

Categoria:
Pizzas Doces

Setor:
PIZZARIA


Endpoint:

GET /pedidos/cozinha?setor=PIZZARIA


Produto:
X Salada

Setor:
COZINHA


Endpoint:

GET /pedidos/cozinha?setor=COZINHA


---

# Endpoints validados


## Criar pedido

POST /pedidos


## Aprovar

PUT /pedidos/{id}/aprovar


## Iniciar produção

PUT /pedidos/{id}/producao


## Colocar pendente

PUT /pedidos/{id}/pendente


## Finalizar

PUT /pedidos/{id}/finalizar


## Cancelar

PUT /pedidos/{id}/cancelar


Necessita:

```json
{
 "justificativa":"Motivo do cancelamento"
}

```
## Entrega

PUT /pedidos/{id}/sair-entrega

PUT /pedidos/{id}/entregar


---

# Pontos identificados para próximas sprints


## Status por item

Problema atual:

O pedido possui apenas um status geral.

Necessário evoluir para:

Pedido
|
├── Item Pizza
│      status produção
|
└── Item Lanche
status produção


Objetivo:

Permitir que setores trabalhem de forma independente.


---

## Novo status futuro

Substituir conceito atual:

FINALIZADO


Por:

PRONTO_DESPACHO


Motivo:

Após produção terminar, o balcão ainda precisa:

- separar bebidas
- conferir pedido
- enviar para entrega


---

## Próximas evoluções

- usuários por setor
- permissões
- pedidos via WhatsApp
- dashboard operacional
- impressão automática
- integração entregadores