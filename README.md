
# GitHub Actions - Deploy Homolog Unique

## Descrição

Este repositório contém o workflow de deploy de um widget do Fluig no ambiente de homologação de clientes específicos.

O processo automatizado realiza as seguintes etapas:

- Criação de branch temporária com arquivos `.properties` e `application.info` do cliente;
- Compilação do projeto Java, gerando o arquivo `.war`;
- Autenticação no servidor Fluig do cliente;
- Upload do `.war` para o Fluig.

---

## Estrutura do Repositório

- `configs/`: Contém arquivos `.properties` de configuração de cada cliente. Se for necessário alterar algum data set, insira as alterações no arquivo do cliente correspondente nesta pasta.
- `job/`: Contém os scripts Python `login.py` e `upload.py` responsáveis por autenticar e realizar o upload para o Fluig.

---

## Variáveis de Ambiente

### `SERVIDORES_HOMOLOG_JSON`

JSON com os dados de todos os clientes, incluindo host, port, username e password. Deve seguir o seguinte formato:

```json
{
  "strategi": {
    "host": "strategiconsultoria176588.fluig.cloudtotvs.com.br",
    "port": "2450",
    "username": "admin",
    "password": "lcsVHVGR1IGwRQrj"
  },
  "sebraern": {
    "host": "fluighml.rn.sebrae.com.br",
    "port": "443",
    "username": "anderson.santos",
    "password": "123456"
  }
}
```

---

## Disparo Manual

Este workflow é iniciado manualmente (`workflow_dispatch`) com os seguintes parâmetros:

- `base_branch` (string): branch base para criação da branch temporária;
- `cliente_ID` (string): ID do cliente, usado para localizar o arquivo `.properties` e acessar os dados de login.

---

## Jobs

### 1. build

**Objetivo:** Preparar a aplicação com as configurações do cliente e compilar o projeto Java.

**Etapas:**

- Define `destino_branch` como `fluighub[cliente_ID]-prod`;
- Faz checkout do repositório e configura Git;
- Cria uma branch temporária baseada na `base_branch`;
- Copia o `.properties` do cliente da branch main;
- Cria o `application.info` com dados personalizados;
- Salva a versão (`LAST_TAG`) como artefato;
- Comita e faz push da branch temporária;
- Compila o projeto com Maven e gera o `.war`;
- Faz upload do `.war` como artefato.

### 2. login_fluig

**Objetivo:** Autenticar no servidor Fluig do cliente.

**Etapas:**

- Faz checkout do repositório;
- Instala dependências Python;
- Extrai credenciais do cliente de `SERVIDORES_HOMOLOG_JSON`;
- Executa `jobs/login.py` para gerar `cookies.json`;
- Salva `cookies.json` como artefato.

### 3. deploy

**Objetivo:** Realizar o upload do `.war` para o Fluig.

**Etapas:**

- Faz checkout do repositório;
- Baixa os artefatos: `last_tag.txt`, `.war`, e `cookies.json`;
- Lê a versão do widget;
- Instala dependências Python;
- Executa `jobs/upload.py` para subir o `.war`.

---

## Artefatos

- `last_tag.txt`: versão usada no nome do artefato final;
- `fluighub[cliente_ID]-[LAST_TAG]-[run_number]`: arquivo `.war` compilado;
- `cookies.json`: sessão autenticada no Fluig.

---

## Licença

Este projeto é de uso interno.
