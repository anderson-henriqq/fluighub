CLIENTE=fluigsebraern
RELEASE=main

# Cria branch do cliente baseada em versão
git checkout $CLIENTE $RELEASE

# Cria branches de ambiente
git checkout $CLIENTE-prod
