
package JavaRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author askoda
 */
public interface InterfaceServ extends Remote {
    public boolean registrarInteresse(String nomeArquivo, InterfaceCli intc, int temp) throws RemoteException;
    public void cancelarInteresse(String nomeArquivo, int id) throws RemoteException;
    public void upload(String nomeArquivo, String arquivo) throws RemoteException;
    public String consultarArquivos() throws RemoteException;
    public String download(String nomeArquivo) throws RemoteException;

}