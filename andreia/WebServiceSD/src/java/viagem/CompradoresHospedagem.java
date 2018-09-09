
package viagem;

import java.util.Date;

/**
 *
 * @author askoda
 */
public class CompradoresHospedagem {
    String destino;
    Date dataEntrada;
    Date dataSaida;
    int numQuartos;
    DadosCartao dadosCartao;
    boolean parcelado;
    
    public CompradoresHospedagem(String destino, Date dataEntrada, Date dataSaida,
            int numQuartos, DadosCartao dadosCartao, boolean parcelado) {
        this.destino = destino;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.numQuartos = numQuartos;
        this.dadosCartao = dadosCartao;
        this.parcelado = parcelado;
    }
    
    @Override
    public String toString() {
        return "Destino: " + destino + 
                ", Data de Entrada: " + dataEntrada.toString() +
                ", Data de Saída: " + dataSaida.toString() +
                ", Número de quartos: " + numQuartos + 
                ", Dados do cartão: " + dadosCartao.toString() +
                ", Parcelado: " + (parcelado ? "sim" : "não");
    }
}
