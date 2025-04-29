import requests
import json
import os

class UploadService:
    @staticmethod
    def get_host(server):
        return f"https://{server['host']}:{server['port']}"

    @staticmethod
    def upload_widget(server, widget_file_path):
        # Lê cookies do arquivo salvo
        try:
            with open('cookies.json', 'r') as f:
                cookies = json.load(f)
        except FileNotFoundError:
            print("Erro: cookies.json não encontrado. Execute o login primeiro.")
            exit(1)

        try:
            with open(widget_file_path, 'rb') as widget_file:
                upload_url = f"{UploadService.get_host(server)}/portal/api/rest/wcmservice/rest/product/uploadfile"

                form_data = {
                    'fileName': widget_file_path.split('/')[-1],
                    'fileDescription': 'WCM Eclipse Plugin Deploy Artifact'
                }

                files = {
                    'attachment': (widget_file_path.split('/')[-1], widget_file, 'application/java-archive')
                }

                response = requests.post(upload_url, headers={
                    'Cookie': f"JSESSIONID={cookies.get('JSESSIONID')}; JSESSIONIDSSO={cookies.get('JSESSIONIDSSO')}",
                    'Accept': 'application/json'
                }, data=form_data, files=files)

                if response.ok:
                    print("Widget enviado com sucesso.")
                    print(response.json())
                else:
                    print(f"Falha no envio do widget: {response.status_code} {response.text}")
        except FileNotFoundError:
            print(f"Erro: Arquivo não encontrado: {widget_file_path}")

if __name__ == "__main__":
    server = {
        'host': os.environ['FLUIG_HOST'],
        'port': os.environ['FLUIG_PORT'],
        'username': os.environ['FLUIG_USER'],
        'password': os.environ['FLUIG_PASS']
    }
    widget_file_path = "/home/andersonhenriq/Documentos/Workspaces/wcm/widget/testando/testando.war"

    UploadService.upload_widget(server, widget_file_path)
