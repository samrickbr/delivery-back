# Regras de Negócio SIGIN Delivery


# Pedido


## Criação do pedido

Todo pedido criado inicia obrigatoriamente com o status:

RECEBIDO


Ao criar um pedido:

- cliente deve ser informado
- whatsapp deve ser informado
- pedido deve possuir pelo menos um item
- valores devem ser calculados automaticamente


Fluxo inicial:

RECEBIDO


---

# Aprovação


## Aprovar pedido

Pedidos recebidos pelo balcão podem ser aprovados.


Alteração:

RECEBIDO → APROVADO


Após aprovação:

- pedido fica disponível para produção
- setores responsáveis passam a visualizar seus itens


---

# Produção


## Produção por setor

A produção é dividida por setores.


Exemplos:

- PIZZARIA
- COZINHA


O setor do item é definido através:


Produto

↓

Categoria

↓

Setor


Cada operação deve visualizar somente itens pertencentes ao seu setor.


---

# Iniciar produção


Um pedido aprovado pode iniciar produção.


Alteração:

APROVADO → EM_PRODUCAO


---

# Pedido pendente


Um pedido pode ser colocado em espera durante a produção.


Exemplo:

- falta ingrediente
- aguardando confirmação
- problema operacional


Alteração:

EM_PRODUCAO → PENDENTE


Obrigatório informar:

- motivo da pendência


O motivo é armazenado em:

observacao_operacao


---

# Retomar produção


Pedidos pendentes podem retornar para produção.


Alteração:

PENDENTE → EM_PRODUCAO


---

# Finalização da produção


Quando todos os setores necessários concluírem o pedido:

Alteração:

EM_PRODUCAO → FINALIZADO


O pedido fica disponível para o fluxo de entrega.


---

# Entrega


## Enviar para entrega


Pedidos finalizados podem sair para entrega.


Alteração:

FINALIZADO → SAIU_ENTREGA


---

## Confirmar entrega


Após entrega ao cliente:


Alteração:

SAIU_ENTREGA → ENTREGUE


---

# Cancelamento


## Cancelar pedido


Um pedido pode ser cancelado durante o fluxo operacional.


Status final:

CANCELADO


Obrigatório informar:

- justificativa do cancelamento


A justificativa é armazenada em:

observacao_operacao


---

# Histórico


Pedidos considerados encerrados:


- FINALIZADO
- SAIU_ENTREGA
- ENTREGUE
- CANCELADO


---

# Valores


## Cálculo do pedido


Valor dos produtos:


Quantidade × preço unitário


Valor total:


Valor dos produtos + taxa de entrega


Atualmente:

taxa de entrega = 0


---

# Categorias e setores


## Categoria


Toda categoria operacional deve possuir um setor.


Exemplo:


Categoria:

Pizzas Doces


Setor:

PIZZARIA



Categoria:

Lanches


Setor:

COZINHA


---

# Regras futuras


## Status por item


Problema atual:

O status pertence ao pedido inteiro.


Exemplo:


Pedido:

- Pizza
- Lanche


Ambos compartilham o mesmo status.


Evolução:


Pedido

|

├── Item Pizza

│   status produção


└── Item Lanche

    status produção



Objetivo:

Permitir que setores trabalhem de forma independente.


---

# Novo status futuro


Substituir conceito:

FINALIZADO


Por:

PRONTO_DESPACHO


Motivo:


Finalização da produção não significa que o pedido está pronto para envio.


Ainda podem existir etapas:

- separar bebidas
- conferir pedido
- preparar despacho
- enviar entregador


---

# Usuários e permissões futuras


Cada setor deverá possuir usuários específicos.


Exemplo:


Usuário Pizzaria:

- visualiza pedidos PIZZARIA


Usuário Cozinha:

- visualiza pedidos COZINHA


Usuário Entrega:

- visualiza pedidos liberados para entrega



---

# Integrações futuras


Previsto:


- pedidos via WhatsApp
- impressão automática
- integração com entregadores
- dashboards operacionais
- API pública


---

# Princípios do sistema


O SIGIN deve evoluir como uma plataforma ERP modular.


Separação prevista:


Core:

- usuários
- permissões
- empresas
- auditoria
- cadastros básicos


Módulos:

- Delivery
- Estoque
- Financeiro
- Vendas
- Outros segmentos futuros