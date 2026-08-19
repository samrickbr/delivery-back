# Changelog - SIGIN Delivery Backend
---

## [0.2.0] - Evolução Fluxo Operacional Delivery


### Alterado


## Backend

Implementado aprimoramento do fluxo operacional de pedidos.


Alterações:

- Controle de status operacional por PedidoItem
- Cancelamento individual de itens
- Histórico operacional do pedido
- Processo de conferência pelo balcão
- Processo de separação antes da entrega
- Novo status AGUARDANDO_SEPARACAO
- Novo status SEPARADO


---

## Fluxo atualizado


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


---

## Cancelamento


Alterado fluxo de cancelamento.


Agora:

PedidoItem:

- APROVADO
- PENDENTE
- EM_PRODUCAO
- FINALIZADO

podem ser cancelados.


Pedido:

Recebe status CANCELADO somente quando todos os itens estiverem cancelados.


---

## Frontend


Ajustado fluxo operacional:


- Nova etapa de conferência
- Nova etapa de separação
- Filtros por status atualizados
- Validação de itens cancelados na separação
- Liberação de entrega após separação


---

## Correções


Corrigido fluxo onde pedidos com itens cancelados eram enviados diretamente para entrega.


Agora:


Produção finalizada

↓

Conferência

↓

Separação

↓

Entrega


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

## 30/07/2026

# Delivery v0.2.0 - Deploy Produção

## Ambiente publicado

Primeira versão do SIGIN Delivery disponível online.

Infraestrutura:

- Backend publicado no Railway
- Banco PostgreSQL publicado no Railway
- Frontend publicado no Vercel
- Comunicação frontend/backend via HTTPS
- Swagger configurado para ambiente de produção

---

# Fluxo operacional validado

Fluxo completo testado em ambiente online:
CLIENTE
↓
BALCÃO
↓
PRODUÇÃO
↓
SEPARAÇÃO
↓
ENTREGA
↓
HISTÓRICO


---

# Funcionalidades validadas

## Pedidos

- criação de pedidos pelo cardápio
- recebimento no balcão
- aprovação de pedidos
- cancelamento com justificativa

## Produção

- direcionamento por setor
- produção independente por setor
- setores validados:

    - LANCHONETE
    - PIZZARIA

- início de produção
- pedidos pendentes
- retomada de produção
- finalização de produção

## Entrega

- separação operacional
- liberação para entrega
- saída para entrega
- confirmação de entrega

---

# Correções realizadas

## Infraestrutura

Corrigido problema de Swagger em produção.

Causa:

Railway utiliza proxy reverso e o Spring gerava URLs HTTP no OpenAPI.

Correção:

Configuração de headers forward no ambiente de produção.

---

## Setores

Corrigido cadastro operacional:

- criação do setor PIZZARIA
- ajuste da categoria Pizzas
- validação Produto → Categoria → Setor

---

## Frontend

Corrigido filtro da tela Lanchonete.

Antes:
setor=COZINHA

Depois:
setor=LANCHONETE

---

# Arquitetura validada

Modelo atual:
Produto
↓
Categoria
↓
Setor
↓
PedidoItem
↓
Operação

Essa estrutura será futuramente migrada para o módulo SIGIN Core.

---

# Próxima etapa

Evolução para arquitetura ERP modular.

Planejado:

SIGIN Core

- empresas
- usuários
- permissões
- auditoria
- cadastros base

Após estabilização do Core:

- migração de Produto
- migração de Categoria
- migração de Setor

O Delivery passará a consumir as informações do Core.
---

# P0.3 — Identificação e autenticação do cliente

Implementado e validado o fluxo de cliente do Delivery integrado ao SIGIN Core.

## Cadastro

- Cadastro do cliente pelo Delivery.
- CPF utilizado como identificador/login do cliente no Core.
- Cadastro de nome, telefone, CPF, senha e e-mail opcional.
- Associação do cliente ao perfil `CLIENTE`.

## Autenticação

- Login do cliente pelo CPF e senha.
- CPF normalizado antes do envio ao Core.
- Autenticação realizada através de `POST /auth/login`.
- JWT retornado pelo SIGIN Core.
- Validação realizada com CPF e senha do cliente efetivamente cadastrado.

## Integração Core

- Removida autenticação técnica do cadastro de cliente, pois o endpoint de Delivery do Core é público.
- Mantida autenticação técnica para operações administrativas que consultam o Core.
- Tratamento das mensagens de erro retornadas pelo Core.
- `CoreIntegrationException` tratado globalmente pelo Delivery.

## Validação

Fluxo validado:

Cliente
→ cadastro
→ `Usuario.login = CPF`
→ senha
→ login pelo CPF
→ SIGIN Core
→ JWT
→ Delivery

P0.3 concluído.

## P0.4 — Contrato de pagamento

- Adicionado `formaPagamento` ao contrato existente de `POST /pedidos`.
- Valores aceitos: `PIX`, `CARTAO` e `DINHEIRO`.
- A informação é persistida/transportada junto ao pedido conforme o modelo existente.
- O campo é retornado no `PedidoResponse`.
- O pagamento permanece exclusivamente informativo nesta etapa.
- Não foi implementado gateway ou processamento financeiro.