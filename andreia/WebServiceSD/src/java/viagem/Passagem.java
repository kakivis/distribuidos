
package viagem;

import java.util.Date;

/**
 *
 * @author askoda
 */
public class Passagem {
    String origem;
    String destino;
    Date dataIda;
    int numAssentos;
    
    public Passagem(String o, String d, Date i, int n) {
        origem = o;
        destino = d;
        dataIda = i;
        numAssentos = n;
    }
    
    public boolean compraPassagem(int numCompras) {
        if(numAssentos - numCompras >= 0) {
            numAssentos -= numCompras;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Origem: " + origem + ", Destino: " + destino +
                ", Número de assentos disponíveis: " + numAssentos +
                ", Data de Ida: " + dataIda.toString();
    }
}
