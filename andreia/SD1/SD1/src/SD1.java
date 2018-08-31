import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;


public class SD1 {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        /* Quando o processo é criado, é impresso um tutorial do que deve ser
            feito. Também, é requerido um número de porta para inicializar o 
            socket unicast do processo. */
        

        System.out.println("Bem vindo ao sistema de troca de arquivos!");
        System.out.println("Para usar o sistema, temos algumas instruções a " +
                "serem passadas. Todos as mensagens escritas no console " +
                "que estiverem entre parênteses são instruções sendo fornecidas " +
                "pelo sistema. Mensagens com um 'M' precedendo-as são mensagens " +
                "recebidas por multicast, e com um 'U' são as recebidas por unicast.");
        System.out.println("Espero que use com facilidade nosso sistema e aproveite!");
        System.out.println();
        
        System.out.println("(Por favor, digite um número de porta entre 1024 e" +
                " 65535 e pressione Enter)");
        
        // Lendo entrada do console
        Scanner scan;
        scan = new Scanner(System.in);
        int numPort;
        while(true) {
            if(scan.hasNext()) {
                String text= scan.nextLine();
                numPort = Integer.parseInt(text);
                
                // Garantindo que só possam ser utilizadas números de porta permitidas
                if(numPort < 1024 || numPort > 65535)
                    System.out.println("(Por favor, entre um número de porta válido.)");
                else
                    break;
            }
        }
        
        new MyProcess(numPort);         
    }
    
}
