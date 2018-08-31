import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

import java.util.Scanner;
import java.util.UUID;
import java.util.StringTokenizer;

public class MyProcess {
    public static String ipAddressMulticast = "232.232.232.232";
    public static int socketMulticast = 6789;
    public static InetAddress group;
    public static UUID id;
    ArrayList<Processes> processes;
    ArrayList<Requests> requests;
    
    static boolean shouldRun = true;
    static MulticastSocket s = null;
    static Criptography criptography;

    
    public MyProcess() {

        try {
            // Entrando na sala de multicast
            group = InetAddress.getByName(ipAddressMulticast);
            s = new MulticastSocket(socketMulticast);
            s.joinGroup(group);
            id = UUID.randomUUID();

            // Iniciando Thread que recebe em multicast
            new ReceiveMulticastThread().start();
            
            // Gerando novo par de chaves pública e privada
            criptography = new Criptography();
            
            // Se apresentando para os processos
            sendMulticastMessage("j " + id +
                    " " + criptography.getModulusPublic() + " " + criptography.getExpoentPublic());
            
	} catch (SocketException e) {
            System.out.println(e);
	} catch (IOException e) {
            System.out.println(e);
        }
        
    }
    
    /** 
     * Thread responsável por receber as mensagens enviadas em multicast 
     */
    public class ReceiveMulticastThread extends Thread {
        @Override
        public void run() {
            // Enquanto o processo estiver ativo (usuário não desligar)
            while(shouldRun) {
                byte[] buffer = new byte[1000];
                
                try {
                    // Recebendo a mensagem de multicast
                    DatagramPacket m = new DatagramPacket(buffer, buffer.length);
                    s.receive(m);
                    String message = new String(m.getData());
                    
                    switch (message.charAt(0)) {
                        // Se for um processo se apresentando
                        case 'j':
                            {
                                // Retirando as informações: ip, porta, módulo e expoente
                                StringTokenizer st = new StringTokenizer(message);
                                st.nextToken();
                                String pid = st.nextToken().trim();
                                String mod = st.nextToken().trim();
                                String exp = st.nextToken().trim();
                                
                                addProcess(pid, mod, exp);
                                // Tornando a mensagem mais agradável ao usuário
                                message = "novo processo: ID " + pid;
                                
                                break;
                            }
                        case 'q':
                            {
                                // Retirando as informações: ip, porta, módulo e expoente
                                StringTokenizer st = new StringTokenizer(message);
                                st.nextToken();
                                String pid = st.nextToken().trim();
                                
                                // Procurando o processo
                                Processes process = null;
                                for(Processes p : processes) {
                                    if(p.id.toString().compareTo(pid) == 0) {
                                        process = p;
                                        break;
                                    }
                                }
                                
                                // Se o processo foi encontrado
                                if(process != null){        
                                    processes.remove(process);
                                }
                                
                                message = "processo finalizado: ID " + pid;
                                
                                break;
                            }
                        default:
                            break;
                    }
                    
                    // Imprimindo a mensagem ao usuário
                    System.out.println("log: " + message);
                } catch (IOException e) {
                    System.out.println(e);
                }
                
            }
            
            // Avisando que a Thread foi finalizada
            System.out.println("ReceiveMulticastThread finalizada");
        }
    }
    
    
    /** 
     * Thread responsável por processar os comandos digitados pelo usuário
     */
    public class ProcessCommandsThread extends Thread {
        @Override
        public void run() {
            Scanner scan;
            scan = new Scanner(System.in);

            // Enquanto o processo estiver ativo
            while(shouldRun) {
                
                // Lendo qual foi o comando inserido
                if(scan.hasNext()) {
                    String text= scan.nextLine();

                    // Se foi inserido comando de quit(/q), terminar com o processo e notificar saída
                    if(text.toLowerCase().compareTo("/q") == 0){
                        sendMulticastMessage("q " + id );
                        break;
                    }
                    
                    else{
                        System.out.println("digite um comando válido.");
                    }
                }
            }
            
            if(s != null)
                s.close();
            
            // Avisando que a Thread foi finalizada
            shouldRun = false;
            System.out.println("processo "+id+" finalizado");
        }
    }
    
    /** 
     * Método responsável por enviar as mensagens por multicast
     * @param text 
     */
    public static void sendMulticastMessage(String text) {
        try {
            byte [] m = text.getBytes();
                        
            DatagramPacket messageOut =
                new DatagramPacket(m, m.length, group, socketMulticast);
            s.send(messageOut);  
                
        } catch (IOException e){System.out.println("IO: " + e.getMessage());}        
    }
    
    public class Processes {
        final UUID id;
        final String state;
        final Key publicKey;
        
        public Processes(String id, String state, Key pk) {
            this.id = UUID.fromString(id);
            this.state = state;
            this.publicKey = pk;
        }
    }
    
    public void addProcess(String id, String mod, String exp) {
        Key recoveredKey = null;
        try {
            // Recria a chave pública através do módulo e expoente
            BigInteger m = new BigInteger(mod);
            BigInteger e = new BigInteger(exp);
            
            KeyFactory kf = KeyFactory.getInstance("RSA");
            recoveredKey = kf.generatePublic(new RSAPublicKeySpec(m, e));
            
        } catch(Exception e) {
            System.out.println(e);
        }

        if(recoveredKey == null) return;
        
        // Cria o novo processo da lista e o adiciona
        Processes p = new Processes(id, "RELEASED", recoveredKey);
        processes.add(p);
    }
    
    public class Requests {
        Processes p;
        
        public Requests(Processes p, String name) {
            this.p = p;
        }
    }
}

