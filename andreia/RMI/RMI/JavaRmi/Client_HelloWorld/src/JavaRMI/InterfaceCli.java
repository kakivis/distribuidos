
package JavaRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author askoda
 */
public interface InterfaceCli extends Remote {
    public int getId() throws RemoteException;
    public void notificarEvento(String mensagem) throws RemoteException;
}
