# Documentação da Pipeline de CI/CD - API Java para Fluig

## Análise do Código e Estrutura de Diretórios

### Arquivos Essenciais da Pipeline

| Arquivo/Localização | Propósito |
|---------------------|-----------|
| `.github/workflows/deploy-homolog-unique.yml` | Workflow GitHub Actions que automatiza as etapas de login, build e deploy no Fluig. |
| `jobs/login.py` | Script Python para login no servidor Fluig e geração de cookies de sessão. |
| `jobs/requirements.txt` | Lista de dependências Python (como `requests`, `jq`, etc.). |
| `configs/fluighub{cliente}-hml.properties` e `configs/fluighub{cliente}-prod.properties` | Arquivos de configuração por cliente e ambiente. |
| `fluigshub/src/main/resources/application.info` | Define metadados da aplicação a ser publicada no Fluig. |
| `fluigshub/target/*.war` | Artefato final do build gerado pelo Maven. |
| `.github/workflows/cria-branch-release.yml` | Workflow GitHub Actions que automatiza a criação de branches de release a partir da main, usando a última tag como versão. É acionado manualmente ou ao mesclar um PR na main |

---

## Formato, Convenção de Nomeação e Variaveis

### Variáveis Secretas do Repositório

Para o correto funcionamento do projeto, é necessário configurar duas variáveis secretas no repositório:

- `**SERVIDORES_HOMOLOG_JSON**`: Contém as credenciais de acesso aos servidores de **homologação**.
- `**SERVIDORES_PROD_JSON**`: Contém as credenciais de acesso aos servidores de **produção**.

Ambas devem seguir o seguinte padrão de conteúdo, inserido como **string JSON em uma única linha, sem identação**:

```json
{"strategi":{"host":"strategiconsultoria176588.fluig.cloudtotvs.com.br","port":"2450","username":"admin","password":"lcsVHVGR1IGwRQrj"},"sebraern":{"host":"fluighml.rn.sebrae.com.br","port":"443","username":"anderson.santos","password":"123456"}}

```
### Arquivos de Configuração

- **Arquivos `.properties`** devem seguir a convenção:  
  `fluighub{cliente_ID}-{ambiente}.properties`  
  Exemplo: `fluighubsebraern-hml.properties`

- **application.info** é recriado dinamicamente com base no cliente e versão da tag.

### Variáveis de Ambiente e Branches

- **Variáveis esperadas**:  
  - `FLUIG_HOST`, `FLUIG_PORT`, `FLUIG_USERNAME`, `FLUIG_PASSWORD`
  - `LAST_TAG`: versão da aplicação baseada na última tag Git.

- **Branches**:
  - O workflow cria uma branch temporária baseada na opção `base_branch`.
  - Nome da branch segue o padrão: `fluighub-{cliente_ID}/{base_branch}`

---

## Etapas da Pipeline

### 1. `login_fluig`
- **Objetivo**: Realiza login no servidor Fluig e salva cookies.
- **Ferramentas usadas**: Python (`login.py`), `jq`, variáveis secretas do GitHub.

### 2. `build`
- **Objetivo**: Compilar a aplicação com Maven e gerar o artefato `.war`.
- **Etapas**:
  - Criação da branch temporária.
  - Cópia do `.properties` correspondente.
  - Geração do `application.info`.
  - Compilação com `mvn clean install`.
- **Ferramentas usadas**: Maven, Java 11 (Temurin).

### 3. `deploy`
- **Objetivo**: Envia o `.war` gerado para o servidor Fluig.
- **Etapas**:
  - Leitura da versão.
  - Download dos artefatos (`.war` e cookies).
  - Envio via script Python (presumidamente `upload.py` ou via REST no `login.py`).

---

## Integração com o Servidor Fluig



### Modo de Publicação

- **Via HTTP/REST**, utilizando cookies obtidos no login e parâmetros como:
  - `FLUIG_HOST`, `FLUIG_PORT`, `FLUIG_USERNAME`, `FLUIG_PASSWORD`
- Os dados vêm de JSONs secretos no GitHub: `SERVIDORES_HOMOLOG_JSON`, `SERVIDORES_PROD_JSON`.

### Parâmetros de Configuração

```json
{
  "sebraern": {
    "host": "fluig.sebraern.org.br",
    "port": "8080",
    "username": "admin",
    "password": "senha123"
  }
}
```

---

## Exemplo de Execução/Configuração

### Inputs da Pipeline

```yaml
name: Deploy Homolog Unique
on:
  workflow_dispatch:
    inputs:
      server:
        type: choice
        options: [homologação, produção]
      base_branch:
        type: choice
        options: [main, release/v3.0.0,...]
      cliente_ID:
        type: choice
        options: [sebreaam, sebraern, doisa, strategi, elastri]
```

### Exemplo `.properties` (fluighubsebraern-hml.properties)

```properties
SCHEME=https
DOMAIN=fluig.rn.sebrae.com.br
CONSUMER_KEY=acessoPublico
CONSUMER_SECRET=acessoPublicSecret
ACCESS_TOKEN=2e8ff776-a9ee-4b71-ba4c-c071b5aa5f40
TOKEN_SECRET=293c350a-0564-430c-9895-d7057406e8cc201a9425-2c0e-49f5-ab93-da300b62de57
COMPANY=1
INITIAL_MINUTES=10
TOTAL_MINUTES=10
USER_FLUIG=acessoPublico
PASS_FLUIG=?T_Q-5Z#T2Zwf_KBDF9*
WSDL_URL=https://fluig.rn.sebrae.com.br/webdesk/ECMDocumentService?wsdl
#Serviços disponíveis
service.allow=sebraern
#Endpoints API
endpoint.dataset=true
endpoint.token=true
endpoint.zipfiles=true
endpoint.crypto=true
endpoint.folder=true
endpoint.qrcode=true
endpoint.htmltopdf=true
endpoint.mergepdf=true
endpoint.process=true
#Datasets
datasets=fluighubsebraern
datasets.permitidos=datasetId=dsDadosFiscaisDecriptacao, \
datasetId=dsDadosFiscaisEnviaEmailIdentificador, \
datasetId=dsConsultaEventoHub, \
datasetId=dsAprovacaoNotasFiscais, \
datasetId=dsformEditalTerroir, \
datasetId=dsEditalTerroirAtualizarMoverSolicitacao, \
datasetId=dsConsultaNumSeqMvto, \
datasetId=dsConsultaFuncionarioIntermediario, \
datasetId=dsConsultaContratoIntermediario, \
datasetId=dsConsultaFornecedorIntermediario, \
datasetId=dsConsultaDataLimiteRecebimento, \
datasetId=dsPagamentoPixGerarQrCodeChave, \
datasetId=dsConsultaFornecedorPgtoIntermediario, \
datasetId=dsWorkflowProcessInstanceId, \
datasetId=dsFormSolicitacaoServicoTerceirizadoIntermediario, \
datasetId=dsCadastroFornecedorConsultaNumDoc, \
datasetId=dsPagamentoWebhook, \
datasetId=dsformInternoEditalTerroirPeriodoExposicao, \
datasetId=dsRecebimentoNfV2VerificaSolicitacaoExistente, \
datasetId=dsPagamentoPixGerarQrCodeChave, \
datasetId=dsEditalFiartIntermediario, \
datasetId=dsFormEditalRodadaNegociosIntermediario, \
datasetId=dsSGSCategoriaMotorizacao, \
datasetId=dsSolicitarCarroCalculaDistancia, \
datasetId=dsSolicitarCarroModelosDeCarros, \
datasetId=dsSolicitarViagemVerificaSaldoCDC, \
datasetId=dsFormEduEmpreendedoraIntermediario, \
datasetId=dsGetStateName, \
datasetId=dsMoverSolicitacaoByClass, \
datasetId=dsFormEduEmpreendedoraIntermediario

```

### Exemplo application.info (gerado dinamicamente)

```properties
application.type=widget
application.code=fluighub-sebraern
application.version=3.0.0-42
application.title=fluighub-sebraern
```

---

## ✅ Conclusão

Esta pipeline automatiza a entrega contínua de widgets Fluig, garantindo controle de versão, configuração por cliente, e segurança via cookies e secrets do GitHub. A modularização do login, build e deploy permite reutilização e manutenção facilitada.