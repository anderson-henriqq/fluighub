# Índice:
   - [Mudanças por versão](UtilsDoc/updateversion.md)
   - [Manutenção/Alteração](#alterando-o-fluighub)
   - [Upload FluigHub](#upload-fluighub-em-servidor)
   - [Encontrar erros a partir do Log ](#encontrar-erros-a-partir-do-log-do-servidor-fluig)
   - [Resumo StackTrace FluigHub](#resumo-stacktrace-fluighub)
   - [Endpoint '/qrcode'](#endpoint-qrcode)
   - [Endpoint '/datasearch'](#endpoint-datasearch)
   - [Endpoint '/zipfiles'](#endpoint-zipfiles)
   - [Endpoint '/movestart-process'](#endpoint-movestart-process)
   - [Endpoint '/topdf'](#endpoint-topdf)
   - [Endpoint '/crypto'](#endpoint-crypto)
   - [Endpoint '/encode'](#endpoint-encode)
   - [Endpoint '/uploadfile'](#endpoint-uploadfile)
   - [Endpoint '/folder'](#endpoint-folder)
   - [Endpoint '/mergepdf'](#endpoint-mergepdf)
   - [Endpoint '/uploadanexo'](#endpoint-uploadanexo)
   - [Endpoint '/attach'](#endpoint-attach)
   - [Endpoint '/deleteattach'](#endpoint-deleteattach)
   - [Endpoint '/version'](#endpoint-version)
   - [Endpoint '/distancia'](#endpoint-distancia)


## base url: /fluighub/rest/service/execute

[Diretório com imagens-exemplo de requisições no Insomnia](./ImgsDoc/)

# Endpoint '/qrcode'
### Método : POST
### Descrição:
    Envia                 o parâmetro "texttobase64" para o serviço de qrcode, que irá gerar um código QR com o texto passado.
    O retorno será um JSON            com a imagem do QRcode em base64. (Utilizar com links)
### Restrições:
    - Parâmetro          texttobase64       não pode            ser vazio.

#### exemplo de envio:
```json
            {
            	"endpoint": "stringtoqr",
            	"texttobase64": "link_para_pagamento_do_pix"
            }
```
### exemplo de resposta:
```json
{
   "message": "<Imagem do QRCode em base64>",
   "error": false,
   "code": 200
}
```

# Endpoint '/datasearch'
### Método : POST
### Descrição:
    Envia o parâmetro "params" para o serviço de datasearch, que irá consultar o dataset, 
    sempre passar com "datasetId" como no exemplo.
    O retorno será um JSON com os dados do dataset.
### Restrições:
    - Parâmetros não podem ser vazios.
    - Parâmetros adicionais no JSON não serão aceitos.
    - se "datasetId" não for um dataset válido ou permitido, não será aceito.
#### Exemplo de envio Javascript:
```javascript
try {
            const url = `${baseUrl.value}/fluighub/rest/service/execute/datasearch`
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(options)
            })
            const res: any = await response.json()

            if (response.status != 200) {
                throw new Error('Erro ao buscar dados de documentos do fornecedor')
            }

            if (res.code != 200) {
                throw new Error('Erro ao buscar dados de documentos do fornecedor')
            }

            let resMessage = JSON.parse(res.message)
            resMessage = JSON.parse(resMessage.values[0].values)
            documentosContratosStore.setDocumentos(resMessage)
        } catch (err) {
            error.value = (err as Error).message
        } finally {
            isLoading.value = false
        }
    }
```
#### exemplo de envio Json:
```json
           {
           	"endpoint": "dataset",
           	"method": "get",
           	"params": "datasetId=dsConsultaEventoHub"
           }
```
         >parâmetro   endpoint: Obrigatório - *(Manter o valor "dataset")*
         >parâmetro   params:   Obrigatório - (O dataset a ser consultado no formato do exemplo)
         >parâmetro   method:   Obrigatório - *(Manter o valor "get")*

### exemplo de resposta:
```json
{
   "message": "{\"columns\":[\"<nome_da_coluna_1>\",\"<nome_da_coluna_2>\"],\"values\":[<dados_do_dataset>]}",
   "error": false,
   "code": 200
}
```

# Endpoint '/zipfiles'
### Método : POST
### Descrição:
    Envia os parâmetros para o serviço de zipfiles, que irá criar um arquivo .ZIP com os arquivos passados
    no parâmetro "attachments" e colocar o arquivo no GED de acordo com o pathId passado. Os arquivos podem ser
    colocados em subpastas dentro do .ZIP, passando o caminho da pasta no parâmetro "path".

### Restrições:
    - Parâmetros adicionais no JSON não serão aceitos.
    - se "key" dentro do parâmetro "attachments" não for uma URL, não será aceito.
    - se o caminho da pasta não conter nome do arquivo + extensão, irá subir no GED sem identificação do tipo de arquivo.
#### Exemplo de Javascript:
```javascript
try {
        const clientService = fluigAPI.getAuthorizeClientService();
        const documentService = fluigAPI.getDocumentService();
        let dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd");
        let date = java.util.Date();
        date.setDate(date.getDate() - 1);
        let yesterday = dateFormat.format(date);//"2024-05-24" // 
        let centrosCusto = getCentrosCusto(yesterday);
        if (centrosCusto.rowsCount) {
            for (let i = 0; i < centrosCusto.rowsCount; i++) {
                let CODCCUSTO = centrosCusto.getValue(i, "CODCCUSTO");
                let anexosURLArr = getAnexos(CODCCUSTO, yesterday);
                let emailsArr = getEmails(CODCCUSTO);
                if (!anexosURLArr.length) {
                    ds.addRow(["success", "Nenhum anexo encontrado para enviar por email para o centro de custo " + CODCCUSTO]);
                    continue;
                }
                if (!emailsArr.length) {
                    ds.addRow(["success", "Nenhum email encontrado para enviar os anexos por email para o centro de custo " + CODCCUSTO]);
                    continue;
                }
                let data = {
                    companyId: '1',
                    serviceCode: 'fluighub',
                    endpoint: '/fluighub/rest/service/execute/zipfiles',
                    method: 'post',
                    timeoutService: '100',
                    options: {
                        encoding: 'UTF-8',
                        mediaType: 'application/json',
                    },
                    params: {
                        pathId: "1162651",
                        namefile: "NFS_FATDIRETOS - " + yesterday,
                        endpoint: "uploadstream",
                        method: "post",
                        body: {
                            attachments: anexosURLArr
                        }
                    }
                }
                let result = clientService.invoke(JSONUtil.toJSON(data));
                result = JSON.parse(result.getResult());
                if (result.error == "true") throw result.message;
                result = JSON.parse(result.message);
                let zipDocumentId = result.documentId;
                let zipDownloadUrl = documentService.getDownloadURL(zipDocumentId);
                let emailData = {
                    emails: emailsArr,
                    subject: "Anexos Diários",
                    content: "<p>Segue em anexo os arquivos diários, <a target='_blank' href='" + zipDownloadUrl + "'>Clique aqui</a> para fazer o download dos arquivos</p>"
                }
```
#### exemplo de envio Json:
```json
        {
          "endpoint": "uploadstream",
          "method": "post",
          "pathId": "123186",
          "namefile": "testeFiltroURL",
          "body": {
                "attachments": [
                    {
                    "key": "https://fluig.rn.sebrae.com.br/volume/stream/Rmx1aWc\u003d/ARQUIVO.pdf", 
                    "path": "pasta1/MeusProcessos0.pdf"
                    },
                    {
                    "key": "URL_DO_SEU_ARQUIVO",
                    "path": "CAMINHO_DA_PASTA/ARQUIVO.EXTENSÃO"
                    }
                ]
            }
        },
```
         
         >parâmetro   endpoint:      Obrigatório - *(Manter o valor "uploadstream")*
         >parâmetro   attachments:   Obrigatório -(
                                                   key: URL do arquivo a ser baixado + 
                                                   path: caminho da pasta/nome do arquivo com a extensão
                                                  )
         >parâmetro   method:        Obrigatório - *(Manter o valor "post")*

### exemplo de resposta:
```json
{
   "message": "<dados_do_documento>", //por exemplo: ID do documento, versão do documento, etc.
   "error": false,
   "code": 200
}
```
# Endpoint '/movestart-process'
### Método : POST
### Descrição:
    Inicializa ou move um processo Fluig de acordo com o caminho no diagrama do processo.
### Restirções:
    - Dentro de params deve-se obrigatoriamente ter os campos:
     "targetState": 0, - **Atividade em que o processo está sendo o valor 0 o inicial.**
     "targetAssignee": "string", - **Usuário alvo da próxima atividade.**
     "comment": "string",
    - parâmetros adicionais não serão aceitos.
    - valor do  parâmetro 'params' deve estar no formato de acordo com o exemplo. (Stringficado)
#### exemplo de envio Javascript:
```javascript
const body = {
            endpoint: 'start',
            method: 'post',
            params: JSON.stringify({
                targetState: 0,
                targetAssignee: 'Pool:Group:Admin', // trocar para o grupo correto quando subir para produção
                comment: `Solicitação aberta ${date} ${time}`,
                //formFields: {
                //    documentIdAnexo: docid
                //}
                formFields: formFields
            }),
            process: 'C7itE/Nyi7inuEP4QkI5dJJmR2dhYHq7ukytf5EzxGg='
        }

        try {
            const url = `${baseUrl.value}/fluighub/rest/service/execute/movestart-process`
            const response = await fetch(url, {
                method: 'POST',
                body: JSON.stringify(body)
            })
            const res: any = await response.json()
            if (res.code != 200) {
                throw new Error('Erro ao carregar o arquivo')
            }

            return JSON.parse(res.message).processInstanceId
        } catch (err) {
            error.value = (err as Error).message
        } finally {
            isLoading.value = false
        }
    }

```
#### exemplo de envio Json:
```json
            {
              "endpoint": "moveprocess",
              "method": "post",

              "params": '{"comment":"Movido Pelo Widget","movementSequence":"","assignee":"POOL:GROUP:access_public","targetState":"73","targetAssignee":"isaac.strategi","asManager":true,"formFields"
              :{"cpf_solicitante":"878.076.454-15","nome_solicitante":"Pedro Isaac","matricula_solicitante":"003744","email_solicitante":"pedro.isaac@strategi.in",
              "cargo_solicitante":"20220133 - Instrutor I - Horista Nível III","lotacao_solicitante":"13119 - OUTROS GASTOS COM FORMACAO - INSTRUTORIA",
              "codigo_curso_solicitante":"2024.10.44","horarios_curso":"Segunda-feira a Sexta-feira (13:00 às 17:00)","dataInicioCurso":"03/06/2024","dataFinalCurso":"26/07/2024","valor_curso_solicitante":"R$ 666","data_solicitante_hidden":"6/5/2024",
              "liderImediato":"002210","curso_solicitante":"2024.10.44 - DEPILADOR","modalidade_solicitante":"presencial","unidade_solicitante":"SENAC CENTRO","periodo_solicitante":"Noturno","justificativa_solicitante":"TESTANDO MOVE"}}',

              "process": "6wAIhJ/+34543553c2QVK/w=="
            }
```

         >parâmetro   endpoint: Obrigatório - Para iniciar um processo passar com valor "start"
          e para mover passar o valor "moveprocess".
         >parâmetro   params:   Obrigatório - (Dados do formulário)
         >parâmetro   method:   Obrigatório - *(Manter o valor "post")*
         >parâmetro   process:  Obrigatório - (ProcessId criptografado[Usar endpoint "crypto"])
### exemplo de resposta:
```json
{
   "message": "<Dados do processo>", //por exemplo: ID do processo, versão do processo, instanceId do processo, etc.
   "error": false,
   "code": 200
}
```
# Endpoint '/topdf'
### Método : POST
### drescrição:
    Passando o parâmetro "service" com o valor "bytes", irá retornar os bytes deste arquivo em multipart/form-data
    se o parâmetro "service" for passado vazio, irá colocar o arquivo PDF no GED de acordo com o pathId.
### Restrições:
    - Parâmetros adicionais não serão aceitos.
    - O parâmetro "pathId" deve conter um ID válido do GED.
    - Os parâmetros não podem ser vazios.
#### exemplo de envio Javascript:
```javascript
function createPDF(base64, nomeArquivo, documentId, fluigId) {

   // chama um endpoint no fluighub para gerar o pdf

   let endpoint = '/fluighub/rest/service/execute/topdf'
   let clientService = fluigAPI.getAuthorizeClientService();
   let data = {
      companyId: '1',
      serviceCode: 'FLUIGHUB' + '',
      endpoint: endpoint,
      method: 'post',
      timeoutService: '100',
      options: {
         encoding: 'UTF-8',
         mediaType: 'application/json'
      },
      params: {
         fileName: nomeArquivo + '',
         params: base64 + ''
      },
   }

   try {
      // multipart/form-data
      // receber os bytes do pdf
      let result = clientService.invoke(data)
      return result.getResult().getBytes()


   } catch (e) {
      throw 'Erro ao criar o PDF: ' + fluigId + " =====> Não conseguiu se conectar ao endpoint REST do FLUIGHUB:" + e + "";
   }
}
```
#### exemplo de envio Json:
```json
{
  "service": "bytes",
  "fileName": "dados2",
  "pathId": "136513",
  "folderName": "retest2",
  "params": "HTML_EM_BASE64"
}
```

    >parâmetro   service: Obrigatório - Passando com a String "bytes" irá 
     retornar os bytes deste arquivo em multipart/form-data
     se o parâmetro for passado vazio, irá colocar o arquivo PDF no GED de acordo com o pathId.
    >parâmetro   fileName:   Obrigatório - Nome para o arquivo PDF.
    >parâmetro   pathId:   Obrigatório - Id da pasta do GED no qual será criado uma subpasta
    >parâmetro   folderName:  Obrigatório - Nome da subpasta para o GED

### exemplo de resposta:
```json
//Caso com "bytes" será retornado somente os bytes do arquivo em multipart/form-data
//Caso sem "bytes" no "service":
{
   "message": "<dados_do_documento>", //por exemplo: ID do documento, versão do documento, etc.
   "error": false,
   "code": 200
}


```
# Endpoint '/crypto'
### Método : POST
### Descrição:
    Passando o parâmetro "endpoint" com o valor "crypto" deve ser passado o parâmetro "passphrase" com o valor
    a ser criptografado. 
    O parâmetro "endpoint" com o valor "decrypto" deve ser passado o parâmetro "passphrase" com o valor
    a ser decriptografado(esse valor deve ser criptografado com o parâmetro "passphrase" do endpoint "crypto").
### Restrições:
    - Parâmetros adicionais não serão aceitos.
    - valor em passphrase usando "decrypto" deve ter sido criptografado com o parâmetro "passphrase" do endpoint "crypto".
#### Exemplo de envio Javascript:
```javascript
const baseUrl = window.location.origin
const options = {
   method: 'POST',
   body: JSON.stringify({
      endpoint: 'crypto',
      passphrase: senha
   })
};

try {
   const response = await fetch(`${baseUrl}/fluighub/rest/service/execute/crypto`, options);
   const data = await response.json();

   return data.message

} catch (err) {
   FLUIGC.toast({
      title: 'Atenção!',
      message: 'Erro ao tentar se conectar com a api de criptografia.',
      type: 'warning',
      timeout: 'slow'
   });
}
}
```
#### exemplo de envio Json:
```json
{
  "endpoint": "decrypto",
  "passphrase": "6wAIhJ/+pwpLQXlc2QVK/w=="
}
```

    >parâmetro   endpoint:  pode ser determinado com "decrypto" ou "crypto".
    >parâmetro   passphrase:   Valor a ser criptografado ou decriptografado

### exemplo de resposta:
```json
{
   "message": "<passphrase_criptografada>",
   "error": false,
   "code": 200
}
```

# Endpoint '/encode'
### Método : POST
### Descrição:
    Passando o parâmetro "generic" com o valor "VALOR_PARA_ENCODAR" irá retornar o valor passado em base64.
#### exemplo de envio Json:
```json
{
  "generic": "VALOR_PARA_ENCODAR"
}
```

    >parâmetro OBRIGATÓRIO generic: String para ser retornada em base64

# Endpoint '/uploadfile'
### Método : POST
### multipart/form-data
### Descrição:
    Envia os parâmetros para o serviço de uploadfile, que irá criar um arquivo no GED de acordo com o pathId passado.
    O parâmetro "file" deve conter o arquivo a ser enviado. (blob).
### Restrições:
    - Parâmetros adicionais não serão aceitos.
    - O parâmetro "pathId" deve conter um ID válido do GED.
    - Os parâmetros não podem ser vazios.
#### Exemplo código Javascript:
```javascript
const formData = new FormData()
formData.append('file', file)
formData.append('pathId', monthFolder as string)
const currentDate = new Date()
const currentDateFormatted = currentDate.toLocaleDateString('pt-BR')
formData.append(
        'nameFile',
        fileName + ' ' + currentDateFormatted.replace(/\//g, '-') + '.pdf'
)

try {
   const url = `${baseUrl.value}/fluighub/rest/service/execute/uploadfile`
   const response = await fetch(url, {
      method: 'POST',
      body: formData
   })
   const res: any = await response.json()
   if (res.code != 200) {
      throw new Error('Erro ao carregar o arquivo')
   }
   return JSON.parse(res.message).documentId
} catch (err) {
   error.value = (err as Error).message
   return false
} finally {
   isLoading.value = false
}
}
return false
}
```
#### exemplo de envio Json:
```json
{
   "file": home/Documents/exemplo.pdf
   "pathId": 136557
   "nameFile": exemplo.pdf
}
```

    >parâmetro   file:  arquivo a ser enviado(bin).
    >parâmetro   pathId:  ID de onde será colocado o arquivo
    >parâmetro   nameFile: Nome completo do arquivo (colocar extensão)
### exemplo de resposta:
```json
{
   "message": "<dados_do_documento>", //por exemplo: ID do documento, versão do documento, etc.
   "error": false,
   "code": 200
}
```

# Endpoint '/folder'
### Método : POST
### Descrição:
    Envia os parâmetros para o serviço de folder, que irá criar uma subpasta no GED de acordo com o pathId passado.
    Caso já exista uma subpasta com o mesmo nome, será retornado o ID da pasta existente.
### Restrições:
    - Parâmetros adicionais não serão aceitos.
    - O parâmetro "pathId" deve conter um ID válido do GED.
    - Os parâmetros não podem ser vazios.
#### Exemplo de envio Javascript:
```javascript
const options = {
   foldername: foldername,
   pathId: pathId
}

try {
   const url = `${baseUrl.value}/fluighub/rest/service/execute/folder`
   const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(options)
   })
   const res: any = await response.json()
   if (res.code != 200) {
      throw new Error('Erro ao criar pastas')
   }

   return res.message // vai ser o id da pasta
} catch (err) {
   error.value = (err as Error).message
} finally {
   isLoading.value = false
}
}
```
#### exemplo de envio Json:
```json
{
	"foldername": "AM",
	"pathId" : "136557"
}
```

    >parâmetro   foldername: Nome da subpasta a ser criada (caso já exista uma com mesmo nome, será retornado o ID da pasta existente)
    >parâmetro   pathId:  ID da pasta onde será criada a subpasta

### exemplo de resposta:
```json
{
   "message": "<Id_da_pasta_criada_no_GED>",
   "error": false,
   "code": 200
}
```

# Endpoint '/mergepdf'
### Método : POST
### multipart/form-data
### Descrição:
    Envia os parâmetros para o serviço de mergepdf, que irá colocar os arquivos passados em um único arquivo PDF.
    O parâmetro "pdfFiles" deve conter em todos os arquivos a serem colocados no PDF.
### Restrições:
    - Parâmetros adicionais não serão aceitos.
    - Arquivos vazios não serão aceitos.
    - Se nenhum arquivo for passado, o serviço irá retornar um erro.
#### exemplo de envio Javascript:
```javascript
const formData = new FormData();

pdfsBytes.forEach((pdfBytes, index) => {
   const blob = new Blob([pdfBytes], { type: 'application/pdf' });
   formData.append('pdfFiles', blob);
});

try {
   const url =`${baseUrl}/fluighub/rest/service/execute/mergepdf`
   const response = await fetch(url, {
      method: 'POST',
      body: formData
   });

   if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
   }

   const mergedPdfBytes = await response.arrayBuffer();
   const blobMerged = new Blob([mergedPdfBytes], { type: 'application/pdf' });
   return blobMerged;
} catch (error) {
   console.error('Error merging PDFs:', error);
}
}
```
#### exemplo de envio Json:
```json
{
    "pdfFiles": home/Documents/exemplo.pdf
    "pdfFiles": home/Documents/exemplo2.pdf
    "pdfFiles": home/Documents/exemplo3.pdf
    "pdfFiles": home/Documents/exemplo4.pdf
}
```

    >parâmetro   pdfFiles: Colocar todos os arquivos(blob) com esse parâmetro
    
### exemplo de resposta:
```json
// Retorna os bytes do arquivo em multipart/form-data
```

# Endpoint '/uploadanexo'
### Método : POST
### multipart/form-data
### Descrição:
    Envia arquivo para uma pasta privada do usuário, para poder ser anexado a um processo.
### Restrições:
    - Parâmetros não podem ser vazios.
### Exemplo de envio Json:
```json
{
  "file": home/Documents/teste.pdf
  "fileName": exemplo.pdf
}
```

    >parâmetro   file:  arquivo a ser enviado(bin).
    >parâmetro   fileName: Nome completo do arquivo (colocar extensão), será anexado ao processo com este nome.
### exemplo de resposta:
```json
{
   "message": "{\"content\":\"OK\",\"message\":{\"message\":\"OK\",\"detail\":\"OK\",\"type\":\"INFO\",\"errorCode\":null}}",
   "error": false,
   "code": 200
}
```

# Endpoint '/attach'
### Método : POST

### Descrição:
    Após o upload do arquivo em '/uploadanexo', deve-se chamar este endpoint para anexar o arquivo a um processo. 
    Colocando o mesmo nome do arquivo que foi enviado no serviço '/uploadanexo'.


### Restrições:
    - Parâmetros não podem ser vazios.
    - Parâmetros adicionais não serão aceitos.
    - Valor do parâmetro 'taskUserId' deve ser um ID válido do usuário.
    - Valor do parâmetro 'processInstanceId' deve ser um ID válido do processo.
    - Valor do parâmetro 'attachments' deve ser um array de objetos.

### Exemplo de envio Json:
```json
{
   "processId": null,
   "version":-1,
   "managerMode":false,
   "taskUserId":"ID_DO_USUARIO_FLUIG", //exemplo: luis.silva
   "processInstanceId":
   32811 ,
   "isDigitalSigned":false,
   "selectedState":null,
   "attachments":[{
      "name":"exemplo.pdf",
      "newAttach":true,
      "description":"NOME_DO_ARQUIVO.pdf",
      "documentId":0,
      "attachedUser":"Italo Almeida",
      "attachments":[
         {
            "principal":true,
            "fileName":"exemplo.pdf"}
      ]}
   ],
   "currentMovto": null
}
```

### exemplo de resposta:
```json
{
   "message": "<dados_do_anexo>", //por exemplo: documentId do anexo, processoInstanceId do processo que foi anexado, etc.
   "error": false,
   "code": 200
}
```

# Endpoint '/deleteattach'
### Método : POST

### Descrição:
    Deletar anexo de um processo.


### Restrições:
    - Parâmetros não podem ser vazios.
    - Parâmetros adicionais não serão aceitos.
    - Valor do parâmetro 'taskUserId' deve ser um ID válido do usuário.
    - Valor do parâmetro 'processInstanceId' deve ser um ID válido do processo.

### Exemplo de envio Json:
```json
{
   "processId": null,
   "version": -1,
   "managerMode": false,
   "taskUserId": "ID_DO_USUARIO_FLUIG", //exemplo: luis.silva
   "processInstanceId": 33925,
   "isDigitalSigned": false,
   "selectedState": null,
   "attachments": [
      {
         "description": "exemplo.pdf",
         "documentId": ID_DO_ARQUIVO,
         "deleted": true
      }
   ],
   "currentMovto": null
}
```

    >parâmetro   processId:  ID do processo a ser atualizado.
    >parâmetro   version:  Versão do processo a ser atualizado.
    >parâmetro   description:  Como será o nome do arquivo excluido.
    >parâmetro   taskUserId:  ID do usuário que iniciou o processo.
    >parâmetro   processInstanceId:  ID do processo a ser atualizado.
    >parâmetro   selectedState:  Estado do processo a ser atualizado.

### exemplo de resposta:
```json
{
   "message": "<dados_do_anexo>", //por exemplo: documentId do anexo, processoInstanceId do processo que foi anexado, etc.
   "error": false,
   "code": 200
}
```

# Endpoint '/version'
### Método : GET
### Descrição:
    Retorna a versão atual do serviço FluigHub.

### exemplo de resposta:
```json
{
   "name": "<nome_do_servidor>",
   "date": "2024-08-09 11:49:52"
}
```


# Endpoint '/distancia'
### Método : POST
### Descrição:
    Retorna uma lista de origens e destinos encontrados pela API e a distancia entre esses pontos.

### Exemplo de envio Json:
```json
{
   "data": [
      {
         "origem": "Natal, RN",
         "destino": "Extremoz, RN"
      }
   ]
}
```
    - data: Array com origens e destinos 
    - origem: String com local de origem
    - destino: String com local de destino
   

### exemplo de resposta:
```json
{
   "data": [
      {
         "origem": "Natal, RN",
         "destino": "Extremoz, RN",
         "distancia": 21.905
      }
   ],
   "error": false,
   "code": 200
}
```


# Endpoint '/sunline'
### Método : GET
### Descrição:
    Endpoint para receber Json do webhook da sunline, exemplo em: 

### exemplo de resposta:
```json
{
   "name": "<nome_do_servidor>",
   "date": "2024-08-09 11:49:52"
}
```

# Alterando o Fluighub
Ao efetuar uma alteração no serviço, deve-se acrescentar o log de atualização no arquivo: 
[Arquivo de versão](UtilsDoc/updateversion.md).\
Seguindo o padrão já estabelecido com: versão atual, descrição da alteração, motivo da alteração.
Sempre escrever no início do arquivo.
### Numeração de versão:
    Alterações menores (Add: dataset, comentário...) somados no valor terceário: 2.1.*
    Alterações de funcionalidade (Add: mudar nome de dataset em pesquisa para um endpoint, arquivo em /Utils...) somados no valor secundário: 2.*.0
    Alterações de impacto geral (Mudança da estrutura do serviço, mudança de nome de endpoints, mudança de estrutura de properties) somados no valor primário: *.1.0



## Adicionando um Dataset
   Os Datasets são colocados manualmente no código do serviço,são usados para consultar os dados do dataset e acrescentados constantemente.
   Para adcionar um novo dataset, deve-se seguir os seguintes passos:
   
    1. Clonar o repositório do serviço.
    2. Abrir o arquivo pom.xml
    3. Adicionar o novo dataset no perfil da empresa que será compilado (seguir o mesmo padrão de sintaxe)



## Compilando FluigHub:
Primeiramente é necessário ter a versão correta do openjdk instalada no seu sistema(versão 11).

Para alterar a versão do java na linha de comando digite:

```
update-alternatives --config java
```

O comando acima listará todas as versões instaladas do Java. Escolha o número da versão que você deseja usar(openjdk-11) e digite o número correspondente.

### Para compilar o serviço, deve-se seguir os seguintes passos:

 1. Clonar o repositório do serviço.
 2. Atualizar o arquivo: config.properties de acordo com as configurações do serviço fluig que deseja subir[# Exemplo de properties](#exemplo-de-properties)
 3. No diretório "fluighub" execute o comando: mvn clean package -P<nome_do_perfil> 
 4. Irá gerar um arquivo fluighub.war no diretório "fluighub/target"

### Exemplo de properties:
```
##########################NOME_SERVIDOR#######################################
SCHEME=https
DOMAIN= Substituir pelo domínio do seu servidor Fluig
CONSUMER_KEY=
CONSUMER_SECRET=
ACCESS_TOKEN=
TOKEN_SECRET=
COMPANY=1
INITIAL_MINUTES=10
TOTAL_MINUTES=10
USER_FLUIG=
PASS_FLUIG=
WSDL_URL= http ou https://DOMINIO_FLUIG/webdesk/ECMDocumentService?wsdl
#Endpoints API
endpoint.dataset=true  // true para permitir o endpoint de dataset
endpoint.token=true    // true para permitir o endpoint de token e userlogin
endpoint.zipfiles=true // true para permitir o endpoint de zipfiles
endpoint.crypto=true   // true para permitir o endpoint de crypto e decrypto
endpoint.folder=true   // true para permitir o endpoint de folder
endpoint.qrcode=true // true para permitir o endpoint de qrcode
endpoint.htmltopdf=true // true para permitir o endpoint de htmltopdf
endpoint.mergepdf=true  // true para permitir o endpoint de mergepdf e UploadFile e 
endpoint.process=true // true para permitir o endpoint de AttachFile e UploadAttach
#Datasets
datasets=fluighubsebraern // usar fluighub+nome_serviço para permitir os datasets em AllowedDatasets.java (Cada servidor tem sua própria lista de datasets)
```

## Upload FluigHub em servidor:
Para uploadar o serviço FluigHub em um servidor, deve-se seguir os seguintes passos:
 1. Efetuar login no servidor Fluig desejado.
 2. Ir para Painel de Controle do fluig abrindo o menu:

![Passo1](ImgsDoc/Upload1.png)
 3. Selecionar a Central de Componentes

![Passo2](ImgsDoc/Upload2.png)

4. Clicar em Escolher Arquivo e dar Upload do .war do FluigHub.


# Executando testes:

Primeiramente é necessário ter o Cypress instalado no seu sistema.
veja como instalar o Cypress [aqui](https://docs.cypress.io/guides/getting-started/installing-cypress.html)


Para executar os testes do serviço FluigHub, deve-se seguir os seguintes passos:
 1. Clonar o repositório do serviço.
 2. Ir para o diretório "fluighub/tests"
 3. Executar o comando: npx cypress open (Resolver dependências caso não seja feito)
 4. Abrir o navegador Chrome e clicar no teste "general.cy.js"

Os testes executados criam arquivos e pastas no GED, nos diretórios com código: **123186** e **136557** e **136513**
Sempre verificar caso um componentes de teste do Fluig seja excluído ou alterado.
Sempre verificar testes com arquivos manualmente se foram enviados corretamente e em perfeito estado para o Fluig.

# Encontrar erros a partir do Log do Servidor fluig:
   As saídas de erro do Fluighub possuem o stacktrace completo que pode dificultar a leitura, alguns erros são printados com uma string em comum ("K31"). **No botão de  Hightlight
   no log do servidor, você pode alterar a cor do texto onde possui a string "K31"** para facilar a leitura.
   Além disso, os erros conhecidos possuem saídas com "throw" do Java e possuem a mensagem acompanhada do stacktrace.

# Resumo StackTrace FluigHub:
Abaixo segue um exemplo do Stacktrace do serviço FluigHub com seus modulos e respectivos funcionamentos.

### Controllers
Onde é recebido as requisições (Json, multipart/form-data, etc) e é responsável por enviar as respostas.
Também onde é feito o Parse do JSON para o model (Objeto Java). e onde o Service é chamado.
### Models
Aqui são os Objetos Java que são usados para representar os dados do serviço e facilcitar a manipulação dos dados.
### Services
Onde realmente será feito o processamento dos dados e retornar os dados para o Controller.
### Utils
Onde são feitas as funções de utilidade para o serviço normalmente utilizado em vários Services diferentes.
### oauthhub
Onde é pego os dados de autenticação do usuário para requisições de API da Totvs.
### rest/application
Onde são colocados os endpoints do serviço FluigHub.
### Configuration
Onde são colocados os parâmetros de configuração do serviço FluigHub, que ficam em config.properties.

# Documentação da Pipeline de CI/CD - API Java para Fluig

## 📚 Sumário
- [Resumo](#resumo)
- [Como Adicionar um Novo Cliente](#como-adicionar-um-novo-cliente)
- [🔐 Como adicionar as Secrets no repositório](#como-adicionar-as-secrets-no-repositório)
  - [ Secrets obrigatórias](#secrets-obrigatórias)
  - [ Formato esperado das credenciais](#formato-esperado-das-credenciais)

## Resumo

O pipeline Deploy Homolog Unique é um fluxo de trabalho do GitHub Actions projetado para automatizar o processo de login no Fluig, criação de branch, build e deploy de widgets personalizados para clientes, com base em inputs fornecidos manualmente. Ele é acionado via `workflow_dispatch` e exige três informações: o ambiente de destino (`homologação` ou `produção`), a branch base (`main` ou `release/v3.0.0`) e o identificador do cliente (ex: `sebreaam`, `doisa`). O processo começa com o job `login_fluig`, que faz checkout do repositório, instala dependências Python, extrai dados sensíveis do cliente via jq a partir de segredos codificados e executa um script que faz login na plataforma Fluig, salvando os cookies como artefato. Em seguida, o job `build` cria uma branch temporária baseada nos inputs, copia o arquivo `.properties` correspondente ao ambiente, gera o `application.info` com metadados como código do app, versão e visual, faz commit dessas alterações e compila o projeto Java com Maven, salvando o artefato `.war` para uso posterior. Por fim, o job `deploy` recupera os artefatos, lê a versão gerada, reconstrói os dados de conexão do cliente, localiza o `.war` e executa o script `upload.py` para publicar o widget no Fluig. Caso a branch base seja `main`, o processo inclui ainda a renomeação da branch para refletir a nova versão com padrão `release/<versão>`. Esse pipeline garante um deploy organizado, rastreável e customizado para múltiplos clientes e ambientes.

## Como adicionar as Secrets no repositório

Para que o processo de CI/CD funcione corretamente, você precisa cadastrar três variáveis **secretas** no repositório GitHub. Essas secrets são usadas para acessar os servidores de homologação/produção e para permitir que o GitHub manipule o repositório (push automático de builds, por exemplo).

---

### Secrets obrigatórias

| Nome da Secret           | Descrição                                                                 |
|--------------------------|---------------------------------------------------------------------------|
| `SERVIDORES_HOMOLOG_JSON`| Credenciais dos servidores de **homologação**                             |
| `SERVIDORES_PROD_JSON`   | Credenciais dos servidores de **produção**                                |
| `GH_PATH`                | Token de autenticação pessoal do GitHub (Personal Access Token - PAT)     |

---

### Formato esperado das credenciais (SERVIDORES_...)

Todas as duas variáveis (`SERVIDORES_HOMOLOG_JSON` e `SERVIDORES_PROD_JSON`) devem conter um JSON válido **em linha única**:

```json
{"strategi":{"host":"strategiconsultoria176588.fluig.cloudtotvs.com.br","port":"2450","username":"admin","password":"lcsVHVGR1IGwRQrj"},"sebraern":{"host":"fluighml.rn.sebrae.com.br","port":"443","username":"anderson.santos","password":"123456"}}
```

> 📝 *Certifique-se de que o JSON seja colado como uma única linha, sem quebras.*

---

### Como gerar o `GH_PATH` (Personal Access Token)

1. Acesse seu [perfil do GitHub](https://github.com/settings/tokens).
2. Vá em **Developer settings** > **Personal access tokens** > **Tokens (classic)**.
3. Clique em **Generate new token (classic)**.
4. Configure o token:
   - **Note**: `fluighub-token` (ou outro nome identificável)
   - **Expiration**: escolha um tempo ou deixe padrão
   - **Scopes**: selecione:
     - `repo` (acesso total ao repositório)
     - `workflow` (acesso à execução e leitura de workflows)
5. Clique em **Generate token**.
6. Copie o token gerado **imediatamente** (ele não será mostrado novamente).

---

### Como adicionar as secrets no repositório

1. Acesse seu repositório no GitHub.
2. Vá até **Settings** > **Secrets and variables** > **Actions**.
3. Clique em **New repository secret** para cada variável:
   - `SERVIDORES_HOMOLOG_JSON`
   - `SERVIDORES_PROD_JSON`
   - `GH_PATH`
4. Preencha o **Name** com o nome da variável e o **Value** com o conteúdo apropriado.
5. Clique em **Add secret**.

## Como Adicionar um Novo Cliente

Para adicionar um novo cliente ao projeto, siga os passos abaixo:

1. **Criar os arquivos `.properties` de configuração**  
   Crie dois arquivos no formato `.properties`, um para o ambiente de homologação e outro para o ambiente de produção.  
   A convenção de nomenclatura deve seguir este padrão:

   ```
   fluighub{cliente_ID}-{ambiente}.properties
   ```

   - Use `hml` para o ambiente de homologação.
   - Use `prod` para o ambiente de produção.

   **Exemplo:**
   - `fluighubsebraern-hml.properties`
   - `fluighubsebraern-prod.properties`

2. **Adicionar as credenciais do cliente nas variáveis secretas do repositório**

   - Edite o conteúdo da variável `SERVIDORES_HOMOLOG_JSON` no repositório e adicione as credenciais de **homologação** do novo cliente, seguindo o formato:

     ```json
     {"cliente_id":{"host":"host_do_servidor","port":"porta","username":"usuario","password":"senha"}}
     ```

   - Faça o mesmo com a variável `SERVIDORES_PROD_JSON`, adicionando as credenciais de **produção** do cliente.

   As variáveis devem conter **um único JSON válido em uma única linha**, sem formatação ou identação.

   **Exemplo com dois clientes:**

   ```json
   {"strategi":{"host":"strategiconsultoria176588.fluig.cloudtotvs.com.br","port":"2450","username":"admin","password":"lcsVHVGR1IGwRQrj"},"sebraern":{"host":"fluighml.rn.sebrae.com.br","port":"443","username":"anderson.santos","password":"123456"}}
   ```

3. **Adicionar o cliente no workflow do GitHub Actions**

   - Abra o arquivo `.github/workflows/ci-build-unique.yml`
   - No campo `inputs > cliente_ID > options`, adicione o ID do novo cliente como opção

     ```yaml
     cliente_ID:
      description: 'ID do cliente (mesmo cliente_ID usado para criar o .properties)'
      required: true
      type: choice
      options:
        - sebreaam
        - sebraern
        - doisa
        - strategi
        - elastri
        - novocliente  # Adicione essa linha com o novo cliente
     ```

   **Exemplo com dois clientes:**

   ```json
   {"strategi":{"host":"strategiconsultoria176588.fluig.cloudtotvs.com.br","port":"2450","username":"admin","password":"lcsVHVGR1IGwRQrj"},"sebraern":{"host":"fluighml.rn.sebrae.com.br","port":"443","username":"anderson.santos","password":"123456"}}
   ```

> ⚠️ Certifique-se de manter o conteúdo das variáveis em formato JSON válido. Cada cliente deve ter uma chave com seu ID e o objeto contendo as credenciais.

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
  `fluighub{cliente_ID}-{ambiente(hml ou prod}.properties`  
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
application.title=fluighub-sebraern
application.description=testando
application.category=SYSTEM
application.icon=icon.png
application.renderer=freemarker
developer.code=FLUIG-VSCODE-EXTENSION
developer.name=FLUIG-VSCODE-EXTENSION
developer.url=https://github.com/fluiggers/fluig-vscode-extension
application.uiwidget=true
application.mobileapp=false
application.version=3.0.0-42
view.file=view.ftl
edit.file=edit.ftl
locale.file.base.name=fluighub-sebraern
```

---

## ✅ Conclusão

Esta pipeline automatiza a entrega contínua de widgets Fluig, garantindo controle de versão, configuração por cliente, e segurança via cookies e secrets do GitHub. A modularização do login, build e deploy permite reutilização e manutenção facilitada.
