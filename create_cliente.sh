CLIENTE=melo
RELEASE=release/v2.0

# Cria branch do cliente baseada em versão
git checkout -b cliente-$CLIENTE $RELEASE

# Cria branches de ambiente
git checkout -b cliente-$CLIENTE-hml
git checkout cliente-$CLIENTE
git checkout -b cliente-$CLIENTE-prod
