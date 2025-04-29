import requests
import json
import os

class LoginService:
    @staticmethod
    def get_host(server):
        return f"https://{server['host']}:{server['port']}"

    @staticmethod
    def login_and_get_cookies(server):
        login_url = f"{LoginService.get_host(server)}/portal/api/servlet/login.do"
        login_data = {
            'j_username': server['username'],
            'j_password': server['password']
        }
        response = requests.post(login_url, data=login_data, headers={
            'Content-Type': 'application/x-www-form-urlencoded'
        })

        if response.ok:
            cookies = response.cookies.get_dict()
            if LoginService.is_valid_cookies(cookies, server):
                # Salva cookies num arquivo para serem usados depois
                with open('cookies.json', 'w') as f:
                    json.dump(cookies, f)
                print("Login bem-sucedido e cookies salvos.")
            else:
                print("Login falhou: cookies inválidos.")
                exit(1)
        else:
            print(f"Erro no login: {response.status_code} {response.text}")
            exit(1)

    @staticmethod
    def is_valid_cookies(cookies_cached, server):
        ping_url = f"{LoginService.get_host(server)}/portal/p/api/servlet/ping"
        response = requests.post(ping_url, cookies=cookies_cached)
        return response.ok and 'pong' in response.text

if __name__ == "__main__":
    server = {
    'host': os.environ['FLUIG_HOST'],
    'port': os.environ['FLUIG_PORT'],
    'username': os.environ['FLUIG_USER'],
    'password': os.environ['FLUIG_PASS']
}
    LoginService.login_and_get_cookies(server)
