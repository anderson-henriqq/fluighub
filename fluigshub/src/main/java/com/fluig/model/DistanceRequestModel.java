package com.fluig.model;

import java.util.List;

public class DistanceRequestModel {
    private List<DistancePair> data;

    public List<DistancePair> getData() {
        return data;
    }

    public void setData(List<DistancePair> data) {
        this.data = data;
    }

    public static class DistancePair {
        private String origem;
        private String destino;

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
    }
}