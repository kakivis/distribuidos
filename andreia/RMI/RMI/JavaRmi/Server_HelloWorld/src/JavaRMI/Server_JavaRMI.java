
package JavaRMI;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author askoda
 */
public class Server_JavaRMI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        // Criando a implementação dos métodos do servidor
        ServImpl s = new ServImpl();
        // Criando o registro na porta 1025
        Registry server = LocateRegistry.createRegistry(1025);
        // Associando o servidor ao nome no serviço de nomes
        server.bind("HelloWorld", s);
    }
    
}
