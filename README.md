# SIGIN Delivery - Backend

Backend da aplicação Delivery desenvolvido com:

- Java 21
- Spring Boot
- PostgreSQL
- Maven

## Objetivo

Gerenciar pedidos, produção e fluxo operacional do delivery.

## Fluxo de Pedido

PENDENTE
↓
APROVADO
↓
EM_PRODUCAO
↓
FINALIZADO
↓
SAIU_PARA_ENTREGA
↓
ENTREGUE

## Módulo Pedido

Implementado:

- Criar pedido
- Aprovar pedido
- Iniciar produção
- Finalizar produção
- Colocar pedido em espera
- Observação operacional
- Consulta pedidos cozinha
- Consulta pedidos finalizados

## API

Pedidos:

GET /pedidos/cozinha

GET /pedidos/finalizados

## Próximos passos

- Módulo entrega
- Autenticação
- Clientes
- Produtos
- Pagamentos