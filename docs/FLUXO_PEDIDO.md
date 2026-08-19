# Fluxo de Pedidos - Delivery

## Status atual do pedido

RECEBIDO
↓
APROVADO
↓
EM_PRODUCAO
↓
FINALIZADO
↓
AGUARDANDO_SEPARACAO
↓
SEPARADO
↓
SAIU_ENTREGA
↓
ENTREGUE

- AGUARDANDO_SEPARACAO é um status de transição entre produção e entrega.
- SEPARADO indica que o balcão conferiu e preparou o pedido para envio.
- FINALIZADO representa que todos os setores de produção concluíram seus itens.
- Após FINALIZADO, o pedido passa por conferência do balcão antes da separação.

PedidoItem:

APROVADO
PENDENTE
EM_PRODUCAO
FINALIZADO

↓

CANCELADO


Pedido:

Somente será CANCELADO quando todos os itens estiverem cancelados.


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
└── N PedidoItem
└── 1 Produto

## PedidoHistorico

Responsável pelo rastreamento operacional do pedido.

Registra:

- ação realizada
- data/hora
- setor responsável
- usuário
- descrição

Exemplos:

- APROVADO
- PRODUCAO_INICIADA
- ITEM_CANCELADO
- FINALIZADO
- CONFERENCIA
- SEPARACAO


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

Após produção finalizada:

O balcão realiza:

- conferência do pedido
- validação dos itens
- liberação para separação


Fluxo:

FINALIZADO
↓
CONFERÊNCIA
↓
AGUARDANDO_SEPARACAO

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

## Forma de pagamento

O fechamento do pedido aceita o campo `formaPagamento`.

Valores válidos:

- PIX
- CARTAO
- DINHEIRO

A forma de pagamento é apenas informativa nesta etapa.

Não existe processamento financeiro, gateway, cobrança, confirmação
de pagamento ou integração com provedor externo.

Exemplo:

POST /pedidos

{
"clienteNome": "Cliente Teste",
"clienteWhatsapp": "42999999999",
"observacao": "",
"formaPagamento": "PIX",
"itens": [
{
"produtoId": 1,
"quantidade": 1
}
]
}

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

## 3 - Separação

Responsável:

Balcão


Objetivo:

Preparar o pedido para envio.


Processo:

- conferir itens
- marcar itens separados
- liberar pedido


Status inicial:

AGUARDANDO_SEPARACAO


Resultado:

SEPARADO

Itens cancelados não participam da separação.

Somente itens com status diferente de CANCELADO
são obrigatórios para liberação.

## Conferência

PUT /pedidos/{id}/conferir


## Separação

PUT /pedidos/{id}/liberar-entrega

## Entrega

PUT /pedidos/{id}/sair-entrega

PUT /pedidos/{id}/entregar


---

# Pontos identificados para próximas sprints


## Controle operacional por item

Implementado.

Cada PedidoItem possui seu próprio fluxo operacional:

- APROVADO
- PENDENTE
- EM_PRODUCAO
- FINALIZADO
- CANCELADO


Objetivo:

Permitir que setores trabalhem de forma independente.

---

## Próximas evoluções

- usuários por setor
- permissões
- pedidos via WhatsApp
- dashboard operacional
- impressão automática
- integração entregadores

---

# Refatoração futura

O PedidoService atualmente concentra várias responsabilidades.

Após estabilização do MVP, avaliar separação em:

- PedidoCriacaoService
- PedidoProducaoService
- PedidoConferenciaService
- PedidoSeparacaoService
- PedidoEntregaService
- PedidoCancelamentoService


