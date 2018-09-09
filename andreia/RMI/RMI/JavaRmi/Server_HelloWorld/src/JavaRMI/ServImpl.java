/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaRMI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServImpl  extends UnicastRemoteObject implements InterfaceServ {
    // Caminho da pasta contendo os arquivos no servidor
    public static String pasta = new String("C:\\Users\\Mat\\Documents\\Engenharia da Computação\\10º periodo\\Distribuidos\\RMI\\");
    
    // Lista de clientes interessados em arquivos
    ArrayList<Interesses> listaInteresses;
    
    // Inicializa a lista de interesses
    public ServImpl() throws RemoteException {
        listaInteresses = new ArrayList<>();
    }

    // Método de registro de interesse
    @Override
    public boolean registrarInteresse(String nomeArquivo, InterfaceCli intc, int tempo) throws RemoteException {
        
        // Verifica se o cliente já registrou interesse nesse arquivo
        for(Interesses inte : listaInteresses)
            if(intc.getId() == inte.c.getId() && inte.nomeArquivo.compareTo(nomeArquivo) == 0)
                return true;
        
        // Procurando se o arquivo já existe
        File down = new File(pasta + "Download");
        File[] download = down.listFiles();
        
        for(File fileTmp : download){
            System.out.println(fileTmp.getName());
            if(nomeArquivo.equals(fileTmp.getName())) {
                // Se o arquivo já existe, notifica o cliente disso e não registra interesse
                intc.notificarEvento(nomeArquivo);
                return false;
            }
        }
        
        // Pega o tempo atual do sistema
        long millis = System.currentTimeMillis();
        int delay = tempo*1000;
        // Adiciona nome do arquivo, interface do cliente e "hora" em que o interesse será expirado
        Interesses i = new Interesses(nomeArquivo, intc, millis+delay);
        
        // Adiciona o interesse na lista
        listaInteresses.add(i);

        // Cria o timer
        Timer timer = new Timer();

        // Timer varre a lista de interesses e remove qual já está expirado
        timer.schedule(new TimerTask() {
            public void run() {
                Interesses remover = null;
                for(Interesses i : listaInteresses) {
                    long millis = System.currentTimeMillis();
                    if(i.tempoExpiracao <= millis) {
                        remover = i;
                        break;
                    }
                }
                
                if(remover != null) {
                    listaInteresses.remove(remover);
                }
                    
            }
        }, delay);
        
        return true;
    }
    
    // Cancelamento de interesse
    @Override
    public void cancelarInteresse(String nomeArquivo, int id) throws RemoteException {
        Interesses i = null;
        
        // Verifica se o interesse existe
        for(Interesses inte : listaInteresses) {
            if(id == inte.c.getId() && inte.nomeArquivo.compareTo(nomeArquivo) == 0) {
                i = inte;
                break;
            }
        }
        
        // Se o interesse não existe, não faz nada
        if(i == null)
            System.out.println("Interesse nao encontrado");
        
        // Se existe, remove interesse
        listaInteresses.remove(i);
    }
    
    // Upload de arquivos
    @Override
    public void upload(String nomeArquivo, String arquivo) throws RemoteException {
        // Fazer upload de arquivo
        try {
            // Transformando o arquivo de String para File e salvando
            File arquivoDestino = new File(pasta + "Download\\" + nomeArquivo);
            FileWriter writer = new FileWriter(arquivoDestino);
            writer.write(arquivo);
            writer.flush();
            writer.close();
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        
        // Notificar registro de interesse
        for(Interesses i : listaInteresses) 
            if(i.nomeArquivo.compareTo(nomeArquivo) == 0)
                i.c.notificarEvento(nomeArquivo);
    }
    
    // Download de arquivo
    @Override
    public String download(String nomeArquivo) throws RemoteException {
        File down = new File(pasta + "Download");
        File[] download = down.listFiles();
        boolean achou = false;
        File arquivoOrigem = null;
        
        // Verifica se o arquivo existe no servidor
        for(File fileTmp : download){
            if(nomeArquivo.equals(fileTmp.getName())) {
                achou = true;
                arquivoOrigem = fileTmp;
                break;
            }
        }
        
        // Se não achou, retorna null
        if(!achou) {
            return null;
        }
        
        try {
            // Se achou, transforma arquivo de File para String
            FileReader fis = new FileReader(arquivoOrigem);
            BufferedReader bufferedReader = new BufferedReader(fis);
            StringBuilder buffer = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) 
                buffer.append(line).append("\n");			
                            
            fis.close();
            bufferedReader.close();
            
            // Retorna o arquivo ao cliente
            return buffer.toString();
        } catch(Exception e) {
            System.out.println(e);
        }
        
        return null;
    }

    // Consulta de arquivos
    @Override
    public String consultarArquivos() throws RemoteException {

        String retorno = "";
        
        File down = new File(pasta + "Download");
        File[] download = down.listFiles();
        
        if(download.length == 0)
            return "Nenhum arquivo encontrado";
        
        // Salva os nomes dos arquivos presentes na pasta
        for(File fileTmp : download){
            retorno += "\nArquivo: " + fileTmp.getName();
        }
         // Retorna os nomes dos arquivos
        return retorno;
    }
    
    // Classe para facilitar lista de interesses
    class Interesses {
        public String nomeArquivo;
        public InterfaceCli c;
        public long tempoExpiracao;
        
        public Interesses(String s, InterfaceCli c, long t) {
            this.nomeArquivo = s;
            this.c = c;
            this.tempoExpiracao = t;
        }
    }
}
