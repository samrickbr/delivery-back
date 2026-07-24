# Arquitetura SIGIN Delivery

## Visão geral

O SIGIN Delivery é um sistema ERP para gerenciamento de operações de delivery, desenvolvido com arquitetura web separando backend e frontend.

O sistema foi estruturado pensando em evolução futura para uma plataforma ERP modular, permitindo inclusão de novos segmentos e regras de negócio.


---

# Estrutura atual


## Backend

Tecnologias utilizadas:

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven


Responsabilidades:

- regras de negócio
- persistência de dados
- controle de fluxo dos pedidos
- comunicação com frontend através de API REST


---

## Frontend

Tecnologias utilizadas:

- React
- Vite
- React Router
- Bootstrap


Responsabilidades:

- telas operacionais
- interação dos usuários
- consumo da API


---

# Módulos atuais


## Pedido

Responsável pelo ciclo de vida do pedido.


Fluxo atual:

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


Também possui fluxo alternativo:

PENDENTE

CANCELADO


---

# Fluxo operacional


## Balcão

Responsável por:

- receber pedidos
- conferir informações
- aprovar pedidos


Após aprovação:

Pedido segue para produção.


---

## Produção

A produção é separada por setor.

Exemplos atuais:

- PIZZARIA
- COZINHA


Cada setor visualiza apenas os itens relacionados ao seu setor.


---

## Entrega

Responsável por:

- receber pedidos finalizados
- enviar para entrega
- confirmar entrega


---

# Estrutura de domínio


## Pedido

Representa a solicitação do cliente.


Possui:

- cliente
- observações
- valores
- status
- data de criação
- itens


Relacionamento:

Pedido

1:N

PedidoItem


---

## PedidoItem

Representa cada item solicitado.


Possui:

- produto
- quantidade
- valor unitário
- valor total


O setor de produção é obtido através da categoria do produto.


---

## Produto

Representa o item comercializado.


Possui:

- nome
- descrição
- preço
- categoria
- disponibilidade


---

## Categoria

Agrupa produtos.


Exemplos:

- Lanches
- Pizzas Doces


Possui relacionamento com:

Setor


---

## Setor

Define onde o item será produzido.


Exemplos:

- COZINHA
- PIZZARIA


Utilizado para filtrar pedidos por área operacional.


---

# Decisões arquiteturais


## Status atual no pedido

O status pertence ao pedido inteiro.


Exemplo:

Pedido com:

- Pizza
- Lanche


Possui apenas um status geral.


Essa abordagem atende o fluxo inicial, porém possui limitação para operações independentes.


---

# Evolução planejada

Futuro:

Controle de status por item.


Modelo esperado:

Pedido

|

├── Item Pizza

│   status produção


└── Item Lanche

    status produção


Objetivo:

Permitir que setores trabalhem independentemente.


---

# Próximas evoluções previstas

- usuários por setor
- permissões
- pedidos via WhatsApp
- dashboards operacionais
- impressão automática
- integração com entregadores

