
package viagem;

import java.util.Date;

/**
 *
 * @author askoda
 */
public class Hospedagem {
    String local;
    Date data;
    int numQuartos;
    
    public Hospedagem(String l, Date d, int n) {
        local = l;
        data = d;
        numQuartos = n;
    }
    
    
    
    @Override
    public String toString() {
        return "Local: " + local + ", Número de quartos disponíveis: " + numQuartos +
                ", Data: " + data.toString();
    }
}
