/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viagem;

/**
 *
 * @author askoda
 */
public class DadosCartao {
    String numCartao;
    String dataVencimento;
    String nome;
    String codigoSeguranca;
    
    public DadosCartao(String n, String d, String nome, String c) {
        numCartao = n;
        dataVencimento = d;
        this.nome = nome;
        codigoSeguranca = c;
    }
    
    @Override
    public String toString() {
        return "Número do cartão: " + numCartao +
                ", Data de vencimento: " + dataVencimento +
                ", Nome do proprietário: " + nome +
                ", Código de segurança: " + codigoSeguranca;
    }
}
