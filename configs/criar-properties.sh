#!/bin/bash

# Lista de clientes
clientes=(
  "agroneli"
  "azevedosetravassos"
  "bn"
  "bsm"
  "concrejato"
  "doisa"
  "elastri"
  "geleng"
  "motric"
  "rdg"
  "sebreaam"
  "sebraepb"
  "sebraern"
  "senac"
  "strategi"
)

# Loop por cada cliente
for cliente in "${clientes[@]}"; do
  arquivo_hml="fluighub${cliente}-hml.properties"
  arquivo_prod="fluighub${cliente}-prod.properties"

  # Criar arquivo HML se não existir
  if [ ! -f "$arquivo_hml" ]; then
    touch "$arquivo_hml"
    echo "Criado: $arquivo_hml"
  else
    echo "Já existe: $arquivo_hml (não alterado)"
  fi

  # Criar arquivo PROD se não existir
  if [ ! -f "$arquivo_prod" ]; then
    touch "$arquivo_prod"
    echo "Criado: $arquivo_prod"
  else
    echo "Já existe: $arquivo_prod (não alterado)"
  fi
done
