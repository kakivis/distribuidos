
package JavaRMI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client_JavaRMI {

    // Caminho da pasta aonde os arquivos estão sendo salvos
    public static String pasta = new String("C:\\Users\\Mat\\Documents\\Engenharia da Computação\\10º periodo\\Distribuidos\\RMI\\Arquivos Baixados Cliente 2\\");

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException, NotBoundException, FileNotFoundException, IOException {
        // Pegando o serviço de nomes na porta 1025
        Registry servicoNomes =  LocateRegistry.getRegistry(1025);
        
        // Procurando o servidor pelo nome no serviço de nomes
        InterfaceServ s = (InterfaceServ) servicoNomes.lookup("HelloWorld");
        
        // Criando a implementação do cliente
        CliImpl c = new CliImpl(s);
        
        // Menu
        System.out.println("Digite u para upload de arquivos,");
        System.out.println("d para download de arquivos,");
        System.out.println("c para consulta de arquivos,");
        System.out.println("r para registrar interesse em um arquivo e");
        System.out.println("x para cancelar registro de interesse em um arquivo");
        
        // Lendo entrada do teclado
        Scanner scan;
        scan = new Scanner(System.in);
        while(true){
            if(scan.hasNext()){
                String text = scan.nextLine();
                
                switch (text.charAt(0)){
                    case 'u': // Upload de arquivos
                        System.out.println("Digite o nome do arquivo que deseja fazer upload");
                        if(scan.hasNext()){
                            // Recebendo nome do arquivo
                            String nomeArquivo = scan.nextLine();
                            
                            // Transforma o arquivo txt em string
                            File arquivoOrigem = new File(pasta + nomeArquivo);
                            FileReader fis = new FileReader(arquivoOrigem);
                            BufferedReader bufferedReader = new BufferedReader(fis);
                            StringBuilder buffer = new StringBuilder();
                            String line = "";
                            while ((line = bufferedReader.readLine()) != null) {
                                    buffer.append(line).append("\n");			
                            }
                            fis.close();
                            bufferedReader.close();
                            
                            // Envia o arquivo na forma de string ao servidor com o nome do arquivo
                            s.upload(nomeArquivo, buffer.toString());
                            
                            System.out.println("Upload com sucesso");
                        }

                        break;
                        
                    case 'd': // Download de arquivos
                        System.out.println("Digite o nome do arquivo que deseja fazer download");
                        if(scan.hasNext()){
                            // Pegando nome do arquivo
                            String nomeArquivo = scan.nextLine();
                            
                            // Requirindo download ao servidor
                            String file = s.download(nomeArquivo);
                            
                            // Se for nulo, não recebeu arquivo do servidor
                            if(file == null)
                                System.out.println("Arquivo não encontrado, comando cancelado");
                            else {
                                // Salvando arquivo na pasta do cliente
                                File arquivoDestino = new File(pasta + nomeArquivo);
                                FileWriter writer = new FileWriter(arquivoDestino);
                                StringBuilder buffer = new StringBuilder();
                                writer.write(buffer.toString());
                                writer.flush();
                                writer.close();

                                // Confirmação de download com sucesso
                                File down = new File(pasta);
                                File[] download = down.listFiles();
                                for(File fileTmp : download){
                                    if(nomeArquivo.equals(fileTmp.getName()))
                                        System.out.println("Download com sucesso");
                                }
                            }
                            
                        }
                                                
                        break; 
                    
                    case 'c': // Consulta de arquivos do servidor
                        System.out.println(s.consultarArquivos());
                        break;
                    
                    case 'r':
                        System.out.println("Digite o nome do arquivo que deseja registrar interesse");
                        if(scan.hasNext()){
                            // Pegando nome do arquivo
                            String nomeArquivo = scan.nextLine();
                            
                            System.out.println("Digite o tempo em segundos que deseja registrar interesse");
                            if(scan.hasNext()){
                                // Pegando tempo em segundos
                                String tempo = scan.nextLine();
                                int t = Integer.parseInt(tempo);

                                // Verificando se o registro de interesse foi um sucesso
                                boolean deuBoa = s.registrarInteresse(nomeArquivo, c, t);

                                // Gera notificação de sucesso
                                if(deuBoa)
                                    System.out.println("Interesse registrado com sucesso!");
                            }
                        }  
                        break;
                    case 'x': // Cancelamento de interesse
                        System.out.println("Digite o nome do arquivo que deseja cancelar registro de interesse");
                        if(scan.hasNext()){
                            // Nome do arquivo
                            String nomeArquivo = scan.nextLine();
                            // Cancelando o interesse
                            s.cancelarInteresse(nomeArquivo, c.getId());
                            System.out.println("Interesse removido com sucesso!");

                        }

                        break;
                        
                    default: // Outros comandos não são reconhecidos
                        System.out.println("Comando não encontrado");
                }
            }
        }  
    }
}
