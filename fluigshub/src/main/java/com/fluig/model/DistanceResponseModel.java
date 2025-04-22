package com.fluig.model;

public class DistanceResponseModel {
    private String origem;
    private String destino;
    private Double distancia; // Distância em km

    public DistanceResponseModel(String origem, String destino, Double distancia) {
        this.origem = origem;
        this.destino = destino;
        this.distancia = distancia;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
}