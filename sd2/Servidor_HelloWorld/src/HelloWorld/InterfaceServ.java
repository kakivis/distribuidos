/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloWorld;

import java.rmi.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author a1717456
 */
public interface InterfaceServ extends Remote {
    
    public String[] get_tickets() throws RemoteException;

    public String[] get_combos() throws RemoteException;

    public String[] get_lodges() throws RemoteException;
    
    public String set_interest(InterfaceCli cli, String type, Map hash) throws RemoteException;
    
    public String[] get_interests(InterfaceCli cli) throws RemoteException;
    
    public String remove_interest(InterfaceCli cli, int index) throws RemoteException;
    
    public String buy_ticket(int index) throws RemoteException;
    
    public String buy_lodge(int index) throws RemoteException;
    
    public String buy_combo(int index) throws RemoteException;
    
}
