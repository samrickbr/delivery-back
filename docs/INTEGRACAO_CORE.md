# SIGIN DELIVERY — INTEGRAÇÃO COM SIGIN CORE

## 1. Objetivo

O SIGIN Delivery utiliza o SIGIN Core como autoridade para os dados de domínio compartilhados entre os sistemas.

O Delivery é responsável pela operação específica do canal de delivery, enquanto o Core permanece responsável pelos dados comerciais e cadastrais que pertencem ao domínio central do SIGIN.

A integração deve evitar duplicação de autoridade e permitir que o Delivery evolua independentemente sua experiência operacional e seus clientes consumidores.

---

## 2. Arquitetura

A relação entre os sistemas é:

SIGIN Delivery
|
| HTTP
v
SIGIN Core

O SIGIN Core permanece como autoridade para:

- Pessoa
- Usuário
- Perfil
- Permissão
- Produto
- Categoria
- Canal de Venda
- Produto × Canal de Venda
- ProdutoVenda
- demais dados centrais do domínio

O SIGIN Delivery permanece responsável por:

- operação de pedidos do delivery
- produção
- cozinha
- pizzaria
- separação
- entrega
- histórico operacional
- experiência específica dos dispositivos do Delivery

---

## 3. Autenticação entre Delivery e Core

O SIGIN Core utiliza autenticação baseada em JWT.

O fluxo de comunicação entre Delivery e Core deve considerar autenticação em todas as chamadas protegidas.

Fluxo:

Delivery
|
| POST /auth/login
| login + senha
v
Core
|
| JWT Bearer Token
v
Delivery
|
| Authorization: Bearer <token>
v
Endpoints protegidos do Core

As credenciais utilizadas pelo Delivery para autenticação no Core devem ser configuradas por ambiente.

Não armazenar credenciais diretamente no código-fonte.

---

## 4. P0.1 — Core Client

Status: FECHADO

Commit:

72f58cf feat(delivery): add SIGIN Core client infrastructure

Objetivo:

Criar a infraestrutura inicial de comunicação entre Delivery e Core.

Componentes principais:

delivery/core/client/CoreClient.java

delivery/core/config/CoreClientConfig.java

delivery/core/config/CoreClientProperties.java

delivery/core/exception/CoreIntegrationException.java

A comunicação utiliza Spring RestClient.

A configuração do endereço do Core é feita por propriedade:

sigin.core.url

---

## 5. P0.2 — Catálogo Comercial

Status: FECHADO

Commit:

1324e8f feat(delivery): consume SIGIN Core commercial catalog

Objetivo:

Remover a dependência do catálogo local do Delivery para a composição comercial do cardápio.

Fluxo:

GET /produtos/cardapio
|
v
Delivery ProdutoService
|
v
CoreClient
|
v
GET /api/catalogo/{canalVendaId}
|
v
SIGIN Core
|
v
Catálogo comercial

O Core fornece:

- produto
- produtoId
- canalVendaId
- preço de venda
- imagem
- demais informações definidas pelo contrato do catálogo

O Delivery transforma o contrato do Core no contrato utilizado pelo próprio Delivery.

O canal de venda é configurável por ambiente.

Propriedade:

sigin.core.canal-venda-id

---

## 6. P0.3 — Pessoa / Cliente

Status: IMPLEMENTADO

Objetivo:

Permitir que o Delivery resolva a pessoa responsável pelo pedido através do SIGIN Core, sem criar uma autoridade paralela de clientes.

A regra arquitetural é:

cliente informado no Delivery
|
v
resolução da Pessoa no Core
|
+-- Pessoa encontrada
|       |
|       v
|    pessoaId
|
+-- Pessoa não encontrada
|
v
criação no Core
|
v
pessoaId

O Delivery não deve criar uma entidade Cliente paralela para substituir a Pessoa do Core.

---

## 7. Pessoa no Core

Contratos existentes confirmados no SIGIN Core:

GET /pessoas/por-telefone

GET /pessoas/por-documento

POST /pessoas

Contrato de consulta por telefone:

GET /pessoas/por-telefone?telefone={telefone}

Contrato de consulta por documento:

GET /pessoas/por-documento?documento={documento}

Contrato de criação:

POST /pessoas

Request mínimo confirmado:

nome

Campos adicionais disponíveis:

tipoDocumento
documento
telefone
email
observacao

A resposta de Pessoa contém, entre outros:

id
nome
tipoDocumento
documento
telefone
email
observacao
ativo
dataCriacao
tipos

---

## 8. Resolução do cliente

O Delivery possui uma camada de resolução responsável por utilizar o Core para localizar ou criar a Pessoa.

O objetivo dessa camada é reduzir o acoplamento do fluxo de pedido com os detalhes HTTP da integração.

Fluxo conceitual:

clienteWhatsapp
|
v
PessoaResolver
|
v
CoreClient
|
v
SIGIN Core
|
v
PessoaResponse
|
v
pessoaId

O identificador retornado pelo Core será utilizado posteriormente na integração de Pedido.

---

## 9. Auto cadastro

O cardápio do Delivery será público.

O cliente não dependerá de um cadastro administrativo prévio para iniciar sua utilização do cardápio.

O fluxo futuro deverá permitir:

Cliente acessa cardápio público
|
v
visualiza catálogo
|
v
informa seus dados
|
v
Delivery resolve Pessoa no Core
|
+-- encontrada
|      |
|      v
|   utiliza pessoaId
|
+-- não encontrada
|
v
cria Pessoa no Core
|
v
pessoaId
|
v
criação do pedido

A Pessoa continua pertencendo ao Core.

O Delivery não deve transformar o auto cadastro em um cadastro paralelo.

---

## 10. Cardápio público

O cardápio será público e deverá ser adequado principalmente para dispositivos móveis.

O catálogo comercial deve continuar sendo obtido a partir do Core.

Princípio:

SIGIN Core
|
v
catálogo comercial
|
v
Delivery
|
v
cardápio público

O cardápio público não deve possuir uma cópia independente dos produtos comerciais do Core.

Alterações comerciais realizadas no Core deverão refletir no catálogo consumido pelo Delivery conforme o fluxo de integração definido.

---

## 11. Fluxo operacional do Delivery

A integração com o Core não altera a autoridade operacional do Delivery.

O Delivery continua responsável pelos fluxos:

- recebido
- aprovado
- produção
- pendência
- finalização
- conferência
- separação
- saída para entrega
- entrega
- cancelamento

A operação de cozinha e pizzaria continua sendo controlada pelos estados operacionais dos itens.

Não substituir os estados operacionais do Delivery pelos estados comerciais do Core.

---

## 12. P0.4 — Pedido Core

Status:

NÃO INICIADO

Próximo passo da integração.

Objetivo futuro:

fazer com que a criação de um pedido no Delivery também resulte na criação/registro correspondente no SIGIN Core.

O fluxo deverá utilizar o pessoaId obtido no P0.3.

Fluxo futuro:

cliente
|
v
Pessoa no Core
|
v
pessoaId
|
v
Pedido Delivery
|
v
Pedido Core

O P0.4 não deve ser iniciado neste checkpoint.

---

## 13. Estado atual da integração

P0.1 — Core Client

FECHADO

Commit:

72f58cf


P0.2 — Catálogo Comercial

FECHADO

Commit:

1324e8f


P0.3 — Pessoa / Cliente

IMPLEMENTADO

A integração de autenticação com o Core foi necessária porque os endpoints do Core são protegidos.

O cardápio foi validado através do Delivery após a autenticação do Delivery com o Core.

Validação realizada:

GET http://localhost:8081/produtos/cardapio

Resultado:

HTTP 200

O catálogo retornado pelo Delivery foi:

produto:

teste de edição

produtoId:

6

preço:

22.34

---

## 14. Pausa estratégica

A integração de backend está sendo pausada neste ponto.

Não iniciar P0.4 neste momento.

Próxima frente:

SIGIN Delivery Front

Objetivo imediato:

evoluir a experiência do cardápio público e preparar a experiência de auto cadastro do cliente.

A evolução do Front não deve quebrar os contratos já existentes do Delivery Back.

---

## 15. Próxima etapa — Delivery Front

A evolução do Front deverá considerar desde o início:

- cardápio público
- experiência mobile
- catálogo proveniente do Core através do Delivery Back
- auto cadastro de cliente
- identificação do cliente
- preparação para criação do pedido
- baixo acoplamento ao backoffice
- separação entre experiência pública e operação interna

O Front público não deve assumir responsabilidade sobre dados que pertencem ao Core.

---

## 16. Regras para retomada do Backend

Quando a integração de backend for retomada:

1. partir do estado atual do branch de integração;
2. não reabrir P0.1;
3. não reabrir P0.2;
4. não refazer P0.3;
5. iniciar diretamente o planejamento/execução do P0.4;
6. manter autenticação entre Delivery e Core;
7. manter o Core como autoridade de Pessoa;
8. manter o Core como autoridade comercial;
9. preservar a operação interna do Delivery;
10. evitar migrations sem necessidade comprovada.

---

## 17. Princípio arquitetural permanente

O SIGIN Delivery não deve se tornar uma cópia do SIGIN Core.

A integração deve seguir:

CORE
|
+-- domínio central
+-- cadastro
+-- comercial
+-- identidade
|
v
DELIVERY
|
+-- canal delivery
+-- experiência pública
+-- pedido operacional
+-- produção
+-- separação
+-- entrega

Cada sistema mantém sua responsabilidade.

O objetivo da integração é conectar os domínios, não duplicá-los.