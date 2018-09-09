/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viagem;

import java.util.Date;

/**
 *
 * @author askoda
 */
public class CompradoresPassagem {
    String servico;
    Date dataIda;
    Date dataVolta;
    String origem;
    String destino;
    int numPessoas;
    DadosCartao dadosCartao;
    boolean parcelado;
    
    public CompradoresPassagem(String servico, Date dataIda, Date dataVolta,
            String origem, String destino, int numPessoas,
            DadosCartao dadosCartao, boolean parcelado) {
        this.servico = servico;
        this.dataIda = dataIda;
        this.dataVolta = dataVolta;
        this.origem = origem;
        this.destino = destino;
        this.numPessoas = numPessoas;
        this.dadosCartao = dadosCartao;
        this.parcelado = parcelado;
    }
    
    @Override
    public String toString() {
        return "Serviço: " + servico + 
                ", Data de ida: " + dataIda.toString() + 
                ", Data de volta: " + dataVolta.toString() +
                ", Origem: " + origem +
                ", Destino: " + destino +
                ", Número de pessoas: " + numPessoas +
                ", Dados do cartão: (" + dadosCartao.toString() +
                "), Parcelado: " + (parcelado ? "sim" : "não");
    }

}
