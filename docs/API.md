# API SIGIN Delivery

## Pedidos

## Status atuais do pedido

RECEBIDO

Pedido criado aguardando aprovação.

APROVADO

Pedido aprovado e aguardando produção.

EM_PRODUCAO

Algum setor está produzindo itens.

PENDENTE

Produção pausada por algum motivo.

FINALIZADO

Todos os setores finalizaram seus itens.

AGUARDANDO_SEPARACAO

Pedido conferido pelo balcão aguardando montagem final.

SEPARADO

Pedido pronto para despacho.

SAIU_ENTREGA

Pedido com entregador.

ENTREGUE

Pedido concluído.

CANCELADO

Pedido encerrado por cancelamento.

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
## Listar pedidos do balcão

GET /pedidos/balcao


Retorna pedidos que necessitam ação do balcão.


Status:

RECEBIDO
APROVADO
FINALIZADO
AGUARDANDO_SEPARACAO

## Finalizar produção

PUT /pedidos/{id}/finalizar


Finaliza os itens pertencentes ao setor informado.


Quando todos os itens estiverem:

FINALIZADO
ou
CANCELADO


o pedido passa para:

FINALIZADO

## Cancelar itens

PUT /pedidos/{id}/cancelar-itens


Cancela itens específicos do pedido.


PedidoItem:

APROVADO
PENDENTE
EM_PRODUCAO
FINALIZADO

podem ser cancelados.


Pedido:

Somente recebe status CANCELADO quando todos os itens estiverem CANCELADOS.

## Cancelar pedido completo

PUT /pedidos/{id}/cancelar-pedido


Cancela todos os itens e encerra o pedido.


Status final:

CANCELADO

Request:

{
  "justificativa": "Cliente desistiu"
}

Altera:

PedidoItem:

APROVADO
PENDENTE
EM_PRODUCAO
FINALIZADO

podem ser cancelados.


Pedido:

Somente recebe status CANCELADO quando todos os itens estiverem CANCELADOS.

## Conferir pedido

PUT /pedidos/{id}/conferir

Responsável:

BALCÃO


Altera:

FINALIZADO → AGUARDANDO_SEPARACAO


Registra:

- data da conferência
- usuário responsável
- histórico operacional

## Conferência e Separação

Após todos os setores finalizarem a produção, o pedido entra no fluxo do balcão.

Fluxo:

FINALIZADO
↓
AGUARDA CONFERÊNCIA
↓
AGUARDANDO_SEPARACAO
↓
SEPARADO
↓
SAIU_ENTREGA
↓
ENTREGUE

## Separar pedido

PUT /pedidos/{id}/separar


Altera:

FINALIZADO → SEPARADO

## Liberar entrega após separação

PUT /pedidos/{id}/liberar-entrega


Responsável:

BALCÃO


Processo:

- conferir itens produzidos
- marcar itens separados
- validar itens cancelados
- liberar despacho


Altera:

AGUARDANDO_SEPARACAO → SEPARADO


Processo:

- validar itens separados
- ignorar itens cancelados
- confirmar separação


Altera:

AGUARDANDO_SEPARACAO → SEPARADO

## Listar pedidos entrega

GET /pedidos/entrega


Retorna pedidos:

SEPARADO
SAIU_ENTREGA

Confirmar entrega
PUT /pedidos/{id}/entregar

Altera:

SAIU_ENTREGA → ENTREGUE

## Histórico de pedidos

GET /pedidos/finalizados


Retorna pedidos encerrados:

ENTREGUE
CANCELADO


Pedidos em operação podem ser acompanhados pelo histórico operacional:
GET /pedidos/{id}/historico

## Histórico operacional

GET /pedidos/{id}/historico


Retorna:

- ações realizadas
- setor responsável
- usuário
- data/hora
- descrição

---

## Formas de pagamento

### GET /formas-pagamento

Consulta as formas de pagamento oficiais disponibilizadas pelo SIGIN Core.

O Delivery Back não mantém cadastro local de formas de pagamento.

Integração interna:

GET /financeiro/formas-pagamento

Contrato:

```json
[
  {
    "id": 1,
    "descricao": "PIX",
    "ativo": true,
    "baixaAutomatica": false
  }
]