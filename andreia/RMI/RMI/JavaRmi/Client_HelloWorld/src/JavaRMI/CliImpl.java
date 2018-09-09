/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaRMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

/**
 *
 * @author askoda
 */
public class CliImpl extends UnicastRemoteObject implements InterfaceCli {
    private int id;
    
    public CliImpl(InterfaceServ s) throws RemoteException {
        // Criando um ID para o servidor reconhecer o cliente na hora do cancelamento de interesse
        Random randomGenerator = new Random();
        id = randomGenerator.nextInt(1000000);
    }
    
    @Override
    public int getId() throws RemoteException {
        return id;
    }

    @Override
    public void notificarEvento(String mensagem) throws RemoteException {
        // Notifica um evento: arquivo chegou no servidor
        System.out.println("Notificação: O arquivo " + mensagem + " chegou no servidor!");
    }



}
