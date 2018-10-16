/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloWorld;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author a1717456
 */
public class Cliente_HelloWorld {

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     */
    public static void main(String[] args) throws RemoteException, NotBoundException {
         Registry referenciaServicoNomes = LocateRegistry.getRegistry(1024);
         InterfaceServ server = (InterfaceServ) referenciaServicoNomes.lookup("servidor");
         CliImpl client = new CliImpl(server);
         client.start_menu();
    }
    
}
