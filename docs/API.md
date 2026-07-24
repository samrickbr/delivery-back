# API SIGIN Delivery

## Pedidos


---

# Criar pedido

## POST /pedidos

Cria um novo pedido.

Status inicial:

RECEBIDO


### Request

```json
{
  "clienteNome": "Pedro",
  "clienteWhatsapp": "42999999999",
  "observacao": "Sem cebola",
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    },
    {
      "produtoId": 2,
      "quantidade": 1
    }
  ]
}
```
Listar pedidos do balcão
GET /pedidos/balcao

Retorna pedidos aguardando aprovação.

Status:

RECEBIDO
Aprovar pedido
PUT /pedidos/{id}/aprovar

Altera o pedido para:

APROVADO

Listar pedidos por setor de produção
GET /pedidos/cozinha?setor={SETOR}

Exemplos:

GET /pedidos/cozinha?setor=PIZZARIA
GET /pedidos/cozinha?setor=COZINHA

Retorna pedidos em produção relacionados ao setor.

Status considerados:

APROVADO
PENDENTE
EM_PRODUCAO
Iniciar produção
PUT /pedidos/{id}/producao

Altera status:

APROVADO → EM_PRODUCAO

Colocar pedido pendente
PUT /pedidos/{id}/pendente

Request:

{
  "motivo": "Aguardando ingrediente"
}

Altera:

EM_PRODUCAO → PENDENTE

Finalizar produção
PUT /pedidos/{id}/finalizar

Altera:

EM_PRODUCAO → FINALIZADO

Cancelar pedido
PUT /pedidos/{id}/cancelar

Request:

{
  "justificativa": "Cliente desistiu"
}

Altera:

qualquer etapa permitida → CANCELADO

Listar pedidos entrega
GET /pedidos/entrega

Retorna pedidos:

FINALIZADO
SAIU_ENTREGA
Saiu para entrega
PUT /pedidos/{id}/sair-entrega

Altera:

FINALIZADO → SAIU_ENTREGA

Confirmar entrega
PUT /pedidos/{id}/entregar

Altera:

SAIU_ENTREGA → ENTREGUE

Histórico
GET /pedidos/finalizados

Retorna pedidos encerrados:

FINALIZADO
SAIU_ENTREGA
CANCELADO