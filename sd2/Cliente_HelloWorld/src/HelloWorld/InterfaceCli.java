/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloWorld;

import java.rmi.*;

/**
 *
 * @author a1717456
 */
public interface InterfaceCli  extends Remote{

    /**
     *
     * @param interest
     * @throws RemoteException
     */
    public void notify_cli(String interest) throws RemoteException;
}
