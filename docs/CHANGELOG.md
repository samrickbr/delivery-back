# Changelog - SIGIN Delivery Backend

## 22/07/2026

### Pedido / Cozinha

Implementado:

- Criação de pedidos
- Aprovação de pedidos
- Fluxo de produção
- Finalização de pedidos
- Status PENDENTE
- Observação operacional
- Endpoint pedidos cozinha
- Endpoint pedidos finalizados

### Decisões

A cozinha controla somente produção.

Entrega será tratada em módulo separado.

# Changelog SIGIN Delivery


## [0.1.0] - Sprint Delivery Inicial


### Adicionado


## Backend

Implementado módulo inicial de Delivery.


Funcionalidades:

- cadastro de produtos
- cadastro de categorias
- cadastro de setores
- criação de pedidos
- cálculo automático de valores
- itens de pedido
- fluxo de status do pedido
- operações de balcão
- operações de produção
- fluxo de entrega


---

## Fluxo de pedidos


Implementado ciclo:


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


Fluxos alternativos:

- PENDENTE
- CANCELADO


---

## Produção por setor


Implementado direcionamento de produção por setor.


Setores atuais:

- COZINHA
- PIZZARIA


Itens são relacionados ao setor através:


Produto

↓

Categoria

↓

Setor


---

## API REST


Criados endpoints para:


Pedidos:

- criar pedido
- listar balcão
- aprovar pedido
- iniciar produção
- colocar pendente
- finalizar produção
- cancelar pedido
- listar entrega
- sair para entrega
- confirmar entrega
- histórico


---

## Frontend


Criadas telas operacionais:


- Balcão
- Cozinha/Pizzaria
- Lanchonete
- Entrega
- Histórico


Implementado:

- navegação entre telas
- atualização automática dos pedidos
- filtros por categoria
- ações operacionais
- confirmação de ações
- justificativa de cancelamento
- observação operacional


---

# Correções realizadas


## Setor obrigatório em categorias

Corrigido problema onde categorias podiam ser criadas sem setor.


Regra:

Toda categoria operacional deve possuir um setor.


---

## Status alterado

Adicionado controle de:

status_alterado_em


Utilizado para ordenação operacional.


---

## Cancelamento

Implementado cancelamento com justificativa obrigatória.


A justificativa é armazenada em:

observacao_operacao


---

## Integração frontend/backend

Ajustado fluxo completo:

Balcão

↓

Produção

↓

Entrega


---

# Pontos conhecidos


## Status por item

Ainda não implementado.


Atualmente o pedido possui apenas um status geral.


---

## Despacho

Atualmente:

FINALIZADO


Futuramente:

PRONTO_DESPACHO


---

# Próximas evoluções previstas


- acesso operacional via celular
- usuários por setor
- permissões
- status individual por item
- pedidos via WhatsApp
- impressão automática
- dashboards
- integração com entregadores

