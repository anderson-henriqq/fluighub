CLIENTE=sebraern
BASE=fluighub
CLIENTE=$BASE$CLIENTE
RELEASE=main


# Cria branch do cliente baseada em versão
git checkout $CLIENTE 

# Cria branches de ambiente
git checkout $CLIENTE-prod
