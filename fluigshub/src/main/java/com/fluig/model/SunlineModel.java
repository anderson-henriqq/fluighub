package com.fluig.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SunlineModel {
    private Long id;

    @SerializedName("Status")
    private String status;

    @SerializedName("Data")
    private String data;


    @SerializedName("Nome")
    private String nome;

    @SerializedName("UltimoNome")
    private String ultimoNome;

    @SerializedName("CidadeDeReferencia")
    private String cidadeDeReferencia;

    @SerializedName("Adultos")
    private int adultos;

    @SerializedName("Criancas")
    private int criancas;

    @SerializedName("Bebes")
    private int bebes;

    @SerializedName("Empresa")
    private Empresa empresa;

    @SerializedName("CentroCusto")
    private String centroCusto;

    @SerializedName("Solicitante")
    private String solicitante;

    @SerializedName("EmailSolicitante")
    private String emailSolicitante;

    @SerializedName("TelefoneSolicitante")
    private String telefoneSolicitante;

    @SerializedName("PoliticaViagem")
    private PoliticaViagem politicaViagem;

    @SerializedName("Observacoes")
    private List<Observacao> observacoes;

    @SerializedName("RemarksBackOffice")
    private List<RemarksBackOffice> remarksBackOffice;

    @SerializedName("Justificativas")
    private List<Justificativa> justificativas;

    @SerializedName("Motivo")
    private String motivo;

    @SerializedName("Passageiros")
    private List<Passageiro> passageiros;

    @SerializedName("ReservasAereo")
    private List<ReservaAereo> reservasAereo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }



    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUltimoNome() { return ultimoNome; }
    public void setUltimoNome(String ultimoNome) { this.ultimoNome = ultimoNome; }



    public String getCidadeDeReferencia() { return cidadeDeReferencia; }
    public void setCidadeDeReferencia(String cidadeDeReferencia) { this.cidadeDeReferencia = cidadeDeReferencia; }

    public int getAdultos() { return adultos; }
    public void setAdultos(int adultos) { this.adultos = adultos; }

    public int getCriancas() { return criancas; }
    public void setCriancas(int criancas) { this.criancas = criancas; }

    public int getBebes() { return bebes; }
    public void setBebes(int bebes) { this.bebes = bebes; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public String getCentroCusto() { return centroCusto; }
    public void setCentroCusto(String centroCusto) { this.centroCusto = centroCusto; }

    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }

    public String getEmailSolicitante() { return emailSolicitante; }
    public void setEmailSolicitante(String emailSolicitante) { this.emailSolicitante = emailSolicitante; }

    public String getTelefoneSolicitante() { return telefoneSolicitante; }
    public void setTelefoneSolicitante(String telefoneSolicitante) { this.telefoneSolicitante = telefoneSolicitante; }

    public PoliticaViagem getPoliticaViagem() { return politicaViagem; }
    public void setPoliticaViagem(PoliticaViagem politicaViagem) { this.politicaViagem = politicaViagem; }

    public List<Observacao> getObservacoes() { return observacoes; }
    public void setObservacoes(List<Observacao> observacoes) { this.observacoes = observacoes; }

    public List<RemarksBackOffice> getRemarksBackOffice() { return remarksBackOffice; }
    public void setRemarksBackOffice(List<RemarksBackOffice> remarksBackOffice) { this.remarksBackOffice = remarksBackOffice; }

    public List<Justificativa> getJustificativas() { return justificativas; }
    public void setJustificativas(List<Justificativa> justificativas) { this.justificativas = justificativas; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public List<Passageiro> getPassageiros() { return passageiros; }
    public void setPassageiros(List<Passageiro> passageiros) { this.passageiros = passageiros; }

    public List<ReservaAereo> getReservasAereo() { return reservasAereo; }

    public String getNomePassageiro() {
        return this.nome;
    }

    public String getSobrenomePassageiro() {
        return this.ultimoNome;
    }

    public String getTelefone() {
        return this.telefoneSolicitante;
    }

    public String getAeroportoOrigem() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            ReservaAereo reserva = this.reservasAereo.get(0);
            if (reserva.getTrechosMaisBaratos() != null && !reserva.getTrechosMaisBaratos().isEmpty()) {
                TrechoMaisBarato trecho = reserva.getTrechosMaisBaratos().get(0);
                if (trecho.getAeroportoOrigem() != null) {
                    return trecho.getAeroportoOrigem().getDescricaoAeroporto();
                }
            }
        }
        return null;
    }

    public String getAeroportoDestino() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            ReservaAereo reserva = this.reservasAereo.get(0);
            if (reserva.getTrechosMaisBaratos() != null && !reserva.getTrechosMaisBaratos().isEmpty()) {
                TrechoMaisBarato trecho = reserva.getTrechosMaisBaratos().get(0);
                if (trecho.getAeroportoDestino() != null) {
                    return trecho.getAeroportoDestino().getDescricaoAeroporto();
                }
            }
        }
        return null;
    }

    public String getLocalizador() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            return this.reservasAereo.get(0).getLocalizador();
        }
        return null;
    }

    public String getDataPartida() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            ReservaAereo reserva = this.reservasAereo.get(0);
            if (reserva.getTrechosMaisBaratos() != null && !reserva.getTrechosMaisBaratos().isEmpty()) {
                TrechoMaisBarato trecho = reserva.getTrechosMaisBaratos().get(0);
                if (trecho.getDataPartida() != null) {
                    return trecho.getDataPartida();
                }
            }
        }
        return null;
    }

    public String getSistema() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            return this.reservasAereo.get(0).getSistema();
        }
        return null;
    }


    public String getFormaPagamento() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            return this.reservasAereo.get(0).getFormaPagamento();
        }
        return null;
    }

    public String getNumeroBilhete() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            List<Bilhete> bilhetes = this.reservasAereo.get(0).getBilhetes();
            if (bilhetes != null && !bilhetes.isEmpty()) {
                return bilhetes.get(0).getNumero();
            }
        }
        return null;
    }

    public String getTipoPax() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            List<Tarifacao> tarifacao = this.reservasAereo.get(0).getTarifacao();
            if (tarifacao != null && !tarifacao.isEmpty()) {
                return tarifacao.get(0).getTipoPax();
            }
        }
        return null;
    }

    public double getTarifa() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            List<Tarifacao> tarifacao = this.reservasAereo.get(0).getTarifacao();
            if (tarifacao != null && !tarifacao.isEmpty()) {
                return tarifacao.get(0).getTarifa();
            }
        }
        return 0.0;
    }

    public double getTaxaEmbarque() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            List<Tarifacao> tarifacao = this.reservasAereo.get(0).getTarifacao();
            if (tarifacao != null && !tarifacao.isEmpty()) {
                return tarifacao.get(0).getTaxaEmbarque();
            }
        }
        return 0.0;
    }

    public double getTaxaServico() {
        // Assumindo que a taxa de serviço não está diretamente disponível no modelo.
        // Você pode precisar ajustar isso dependendo de onde essa informação está armazenada.
        return 0.0;
    }

    public double getTotal() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            List<Tarifacao> tarifacao = this.reservasAereo.get(0).getTarifacao();
            if (tarifacao != null && !tarifacao.isEmpty()) {
                return tarifacao.get(0).getTotal();
            }
        }
        return 0.0;
    }

    public String getNumeroVooIda() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            List<TrechoMaisBarato> trechos = this.reservasAereo.get(0).getTrechosMaisBaratos();
            if (trechos != null && !trechos.isEmpty()) {
                return trechos.get(0).getNumeroVoo();
            }
        }
        return null;
    }

    public String getNumeroVooVolta() {
        if (this.reservasAereo != null && !this.reservasAereo.isEmpty()) {
            List<TrechoMaisBarato> trechos = this.reservasAereo.get(0).getTrechosMaisBaratos();
            if (trechos != null && trechos.size() > 1) {
                return trechos.get(1).getNumeroVoo();
            }
        }
        return null;
    }
    // Getters e Setters

    public static class Empresa {
        private Long id;
        @SerializedName("EmpresaPai")
        private Empresa empresaPai;
        private String nomeFantasia;
        private String email;
        private String telefone;
        private String cnpj;
        private Configuracao configuracao;

        @Override
        public String toString() {
            return "Empresa{" +
                    "id=" + id +
                    ", empresaPai=" + (empresaPai != null ? nomeFantasia : "N/A") +
                    ", nomeFantasia='" + nomeFantasia + '\'' +
                    ", email='" + email + '\'' +
                    ", telefone='" + telefone + '\'' +
                    ", cnpj='" + cnpj + '\'' +
                    ", configuracao=" + (configuracao != null ? configuracao.toString() : "N/A") +
                    '}';
        }
    }

    public static class Configuracao {
        private String imagemCabecalho;
        private String imagemEmail;
        private boolean ocultarInformacoesComprovante;
        private boolean escondeFormaPgtCompEmissao;
        private boolean taxasUnicoCampo;
        private boolean exibirDetalhamentoComprovante;
        private boolean ocultaValoresTarifamento;
        private boolean desabilitarComparativo;

        @Override
        public String toString() {
            return "Configuracao{" +
                    "imagemCabecalho='" + imagemCabecalho + '\'' +
                    ", imagemEmail='" + imagemEmail + '\'' +
                    ", ocultarInformacoesComprovante=" + ocultarInformacoesComprovante +
                    ", escondeFormaPgtCompEmissao=" + escondeFormaPgtCompEmissao +
                    ", taxasUnicoCampo=" + taxasUnicoCampo +
                    ", exibirDetalhamentoComprovante=" + exibirDetalhamentoComprovante +
                    ", ocultaValoresTarifamento=" + ocultaValoresTarifamento +
                    ", desabilitarComparativo=" + desabilitarComparativo +
                    '}';
        }
    }

    public static class PoliticaViagem {
        private String nome;
        private String antecedencia;
        private String aereo;
        private String hotel;
        private String carro;

        @Override
        public String toString() {
            return "PoliticaViagem{" +
                    "nome='" + nome + '\'' +
                    ", antecedencia='" + antecedencia + '\'' +
                    ", aereo='" + aereo + '\'' +
                    ", hotel='" + hotel + '\'' +
                    ", carro='" + carro + '\'' +
                    '}';
        }
    }

    public static class Observacao {
        private String mensagem;

        @Override
        public String toString() {
            return "Observacao{" +
                    "mensagem='" + mensagem + '\'' +
                    '}';
        }
    }

    public static class RemarksBackOffice {
        private String identificador;
        private String valor;

        @Override
        public String toString() {
            return "RemarksBackOffice{" +
                    "identificador='" + identificador + '\'' +
                    ", valor='" + valor + '\'' +
                    '}';
        }
    }

    public static class Justificativa {
        private String valor;
        private String tipo;

        @Override
        public String toString() {
            return "Justificativa{" +
                    "valor='" + valor + '\'' +
                    ", tipo='" + tipo + '\'' +
                    '}';
        }
    }

    public static class Passageiro {
        private Long id;
        @SerializedName("Nome")
        private String nome;
        @SerializedName("Sobrenome")
        private String sobrenome;
        @SerializedName("FaixaEtaria")
        private String faixaEtaria;
        private String emailPassageiro;
        private boolean pip;
        private boolean vip;

        @Override
        public String toString() {
            return "Passageiro{" +
                    "id=" + id +
                    ", nome='" + nome + '\'' +
                    ", sobrenome='" + sobrenome + '\'' +
                    ", faixaEtaria='" + faixaEtaria + '\'' +
                    ", emailPassageiro='" + emailPassageiro + '\'' +
                    ", pip=" + pip +
                    ", vip=" + vip +
                    '}';
        }
    }

    public static class ReservaAereo {
        @SerializedName("Localizador")
        private String localizador;
        @SerializedName("Sistema")
        private String sistema;
        @SerializedName("Cancelado")
        private boolean cancelado;
        @SerializedName("Data")
        private String data;
        private String dataValidade;
        private String status;
        @SerializedName("FormaPagamento")
        private String formaPagamento;
        @SerializedName("Bilhetes")
        private List<Bilhete> bilhetes;
        @SerializedName("TrechosMaisBaratos")
        private List<TrechoMaisBarato> trechosMaisBaratos;
        @SerializedName("Tarifacao")
        private List<Tarifacao> tarifacao;
        @SerializedName("Emissoes")
        private List<Emissao> emissoes;

        @Override
        public String toString() {
            return "ReservaAereo{" +
                    "localizador='" + localizador + '\'' +
                    ", sistema='" + sistema + '\'' +
                    ", cancelado=" + cancelado +
                    ", data=" + data +
                    ", dataValidade=" + dataValidade +
                    ", status='" + status + '\'' +
                    ", formaPagamento='" + formaPagamento + '\'' +
                    ", bilhetes=" + bilhetes +
                    ", trechosMaisBaratos=" + trechosMaisBaratos +
                    ", tarifacao=" + tarifacao +
                    ", emissoes=" + emissoes +
                    '}';
        }

        public List<Bilhete> getBilhetes() {
            return bilhetes;
        }

        public List<TrechoMaisBarato> getTrechosMaisBaratos() {
            return trechosMaisBaratos;
        }

        public String getLocalizador() {
            return localizador;
        }

        public String getSistema() {
            return sistema;
        }

        public String getFormaPagamento() {
            return formaPagamento;
        }

        public List<Tarifacao> getTarifacao() {
            return tarifacao;
        }

        public String getDataReserva() { return data; };
    }

    public static class Bilhete {
        @SerializedName("Numero")
        private String numero;
        @SerializedName("Passageiro")
        private String passageiro;

        @Override
        public String toString() {
            return "Bilhete{" +
                    "numero='" + numero + '\'' +
                    ", passageiro='" + passageiro + '\'' +
                    '}';
        }

        public String getNumero() {
            return numero;
        }


    }

    public static class TrechoMaisBarato {
        private Long id;
        @SerializedName("NumeroVoo")
        private String numeroVoo;
        private String ciaAerea;
        @SerializedName("DescricaoCiaAerea")
        private String descricaoCiaAerea;
        @SerializedName("DataPartida")
        private String dataPartida;
        @SerializedName("AeroportoOrigem")
        private Aeroporto aeroportoOrigem;
        private String dataChegada;
        @SerializedName("AeroportoDestino")
        private Aeroporto aeroportoDestino;
        @SerializedName("Duracao")
        private String duracao;
        private double tarifa;

        @Override
        public String toString() {
            return "TrechoMaisBarato{" +
                    "id=" + id +
                    ", numeroVoo='" + numeroVoo + '\'' +
                    ", ciaAerea='" + ciaAerea + '\'' +
                    ", descricaoCiaAerea='" + descricaoCiaAerea + '\'' +
                    ", dataPartida=" + dataPartida +
                    ", aeroportoOrigem=" + aeroportoOrigem +
                    ", dataChegada=" + dataChegada +
                    ", aeroportoDestino=" + aeroportoDestino +
                    ", duracao='" + duracao + '\'' +
                    ", tarifa=" + tarifa +
                    '}';
        }


        public Aeroporto getAeroportoDestino() {
            return aeroportoDestino;
        }

        public Aeroporto getAeroportoOrigem() {
            return aeroportoOrigem;
        }

        public String getNumeroVoo() {
            return numeroVoo;
        }

        public String getDataPartida() {return dataPartida;}
    }

    public static class Aeroporto {
        private String iataAeroporto;
        private String descricaoAeroporto;
        @SerializedName("DescricaoCurta")
        private String descricaoCurta;
        private String cidade;

        @Override
        public String toString() {
            return "Aeroporto{" +
                    "iataAeroporto='" + iataAeroporto + '\'' +
                    ", descricaoAeroporto='" + descricaoAeroporto + '\'' +
                    ", descricaoCurta='" + descricaoCurta + '\'' +
                    ", cidade='" + cidade + '\'' +
                    '}';
        }

        public String getDescricaoAeroporto() {
            return descricaoCurta;
        }
    }

    public static class Tarifacao {
        @SerializedName("TipoPax")
        private String tipoPax;
        @SerializedName("QuantidadePorPax")
        private int quantidadePorPax;
        @SerializedName("Tarifa")
        private double tarifa;
        @SerializedName("TaxaEmbarque")
        private double taxaEmbarque;
        @SerializedName("Total")
        private double total;

        @Override
        public String toString() {
            return "Tarifacao{" +
                    "tipoPax='" + tipoPax + '\'' +
                    ", quantidadePorPax=" + quantidadePorPax +
                    ", tarifa=" + tarifa +
                    ", taxaEmbarque=" + taxaEmbarque +
                    ", total=" + total +
                    '}';
        }

        public double getTarifa() {
            return tarifa;
        }

        public double getTaxaEmbarque() {
            return taxaEmbarque;
        }

        public double getTotal() {
            return total;
        }

        public String getTipoPax() {
            return tipoPax;
        }
    }

    public static class Emissao {
        private String data;

        @Override
        public String toString() {
            return "Emissao{" +
                    "data=" + data +
                    '}';
        }
    }
}
