/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloWorld;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 *
 * @author a1717456
 */
public class Servidor_HelloWorld {
    
    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     * @throws java.rmi.AlreadyBoundException
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        Registry referenciaServicoNomes = LocateRegistry.createRegistry(1024);
        ServImpl server = new ServImpl();
        referenciaServicoNomes.bind("servidor", (InterfaceServ)server);
        server.start_menu();
    }
    
}
