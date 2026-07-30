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
Cada PedidoItem possui controle operacional independente.

Um mesmo pedido pode possuir itens em estados diferentes conforme o setor responsável.

---

# Iniciar produção


Um pedido aprovado pode iniciar produção.


Alteração:

PedidoItem:

APROVADO → EM_PRODUCAO


---

# Pedido pendente


Um pedido pode ser colocado em espera durante a produção.


Exemplo:

- falta ingrediente
- aguardando confirmação
- problema operacional


Alteração:

PedidoItem:

EM_PRODUCAO → PENDENTE


Obrigatório informar:

- motivo da pendência


O motivo é armazenado em:

observacao_operacao


---

# Retomar produção

## Futuro

Pedidos pendentes poderão retornar para produção.

Alteração:

PedidoItem:

PENDENTE → EM_PRODUCAO

---

# Finalização da produção


# Finalização da produção


Cada setor finaliza seus próprios PedidoItem.


Um pedido somente recebe:

FINALIZADO


quando todos os itens estiverem:


FINALIZADO

ou

CANCELADO

O pedido fica disponível para o fluxo de entrega.


---

# Entrega


## Enviar para entrega
Alteração:


SEPARADO → SAIU_ENTREGA


# Conferência e separação


Após produção:


FINALIZADO


O balcão realiza:


- conferência dos itens
- validação do pedido
- separação


Fluxo:


FINALIZADO

↓

AGUARDANDO_SEPARACAO

↓

SEPARADO


Somente pedidos SEPARADOS podem seguir para entrega.

---

## Confirmar entrega


Após entrega ao cliente:


Alteração:

SAIU_ENTREGA → ENTREGUE


---

# Cancelamento


## Cancelamento de itens


PedidoItem pode ser cancelado durante o fluxo operacional.


Status permitidos:


APROVADO

PENDENTE

EM_PRODUCAO

FINALIZADO


↓

CANCELADO



Obrigatório informar:


- justificativa


A justificativa é armazenada no item cancelado.



## Cancelamento do pedido


O Pedido recebe:


CANCELADO


somente quando todos os seus itens estiverem cancelados.


Status final:

CANCELADO


Obrigatório informar:

- justificativa do cancelamento


A justificativa é armazenada em:

observacao_operacao


---

# Histórico


Pedidos considerados encerrados:

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

# Controle operacional por item


Implementado.


Cada PedidoItem possui seu próprio ciclo:


- APROVADO
- PENDENTE
- EM_PRODUCAO
- FINALIZADO
- CANCELADO


Permite que setores diferentes trabalhem no mesmo pedido de forma independente.


---

# Evolução futura


Avaliar criação do status:


PRONTO_DESPACHO


para separar claramente:


FINALIZADO

(produção concluída)


de


PRONTO_DESPACHO

(pedido conferido e pronto para envio)


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

O histórico operacional deverá registrar o usuário real responsável pela ação.




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