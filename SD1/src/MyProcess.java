import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.Scanner;
import java.util.UUID;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class MyProcess {
    public static String ipAddressMulticast = "232.232.232.232";
    public static int socketMulticast = 6789;
    public static InetAddress group;
    public static UUID id;// id "universal" dos processos
    public static int state0 = 0; // Controle de estado dos recursos(0 para RELEASED, 1 para WANTED, 2 para HELD)
    public static int state1 = 0;// Controle de estado dos recursos(0 para RELEASED, 1 para WANTED, 2 para HELD)
    ArrayList<Processes> processes = new ArrayList<>(); //Lista de processos
    ArrayList<String> positive_response_ids = new ArrayList<>(); //id dos processos com respostas positivas ao request
    ArrayList<String> negative_response_ids = new ArrayList<>();//id dos processos com respostas negativas ao request
    
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
            sendMulticastMessage("j " + id.toString() +
                    " " + criptography.getModulusPublic() + " " + criptography.getExpoentPublic());
            
            new ProcessCommandsThread().start();
            
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
                        /*
                        As mensagens podem ter as seguintes estruturas:
                        j id key_mod key_exp
                        q id
                        r resource_num id
                        f resource_num id resp_criptografada
                        */
                        case 'j':
                            // Retirando as informações: id, modulo e expoente 
                            StringTokenizer st = new StringTokenizer(message);
                            st.nextToken();
                            String pid = st.nextToken().trim();
                            String mod = st.nextToken().trim();
                            String exp = st.nextToken().trim();
                                                            
                            // Procurando o processo
                            Processes process = null;
                            for(Processes p : processes) {
                                if(p.id.toString().compareTo(pid) == 0) {
                                    process = p;
                                    break;
                                }
                            }
                                
                            // Se o processo foi encontrado
                            if(process != null ){  
                                message = "";
                            } else if(pid.compareTo(id.toString()) == 0) {
                                message = "";
                            } else { //só adiciona o processo se não for o próprio e ainda não estiver na lista
                                addProcess(pid, mod, exp);
                                message = "novo processo: ID " + pid;
                                
                                // Se apresentando ao novo processo
                                sendMulticastMessage("j " + id +
                                    " " + criptography.getModulusPublic() + " " + criptography.getExpoentPublic());
                            }
                            break;
                        // Se for um processo saindo
                        case 'q':
                            // Retirando as informações: ip
                            st = new StringTokenizer(message);
                            st.nextToken();
                            pid = st.nextToken().trim();
                                
                            // Procurando o processo
                            process = null;
                            for(Processes p : processes) {
                                if(p.id.toString().compareTo(pid) == 0) {
                                    process = p;
                                    break;
                                }
                            }
                                
                            // Se o processo foi encontrado, remove da lista
                            if(process != null){        
                                processes.remove(process);
                                message = "processo finalizado: ID " + pid;
                            } else {
                                message = "processo não encontrado: " + pid;
                            }
                               
                            break;
                        case 'r':
                            // Retirando as informações: id de quem enviou e número do recurso requisitado
                            st = new StringTokenizer(message);
                            st.nextToken();
                            int recNo = Integer.parseInt(st.nextToken().trim());
                            pid = st.nextToken().trim();
                            
                            if(pid.compareTo(id.toString()) == 0) {
                                message = "Requisitando recurso "+recNo;
                                break;
                            }
                            
                            if(recNo == 0) {
                                String feedback = "f " + recNo + " " + id + " " + criptography.criptografa("y", criptography.key.getPrivate());
                                
                                if(state0 != 0) { // Se não está RELEASED, diz que o recurso não está livre
                                    feedback = "f " + recNo + " " + id + " " + criptography.criptografa("n", criptography.key.getPrivate());
                                }
                                
                                sendMulticastMessage(feedback);
                                message = "Processo " + pid + " requisitou o recurso " + recNo;
                            } else if(recNo == 1) {
                                String feedback = "f " + recNo + " " + id + " " + criptography.criptografa("y", criptography.key.getPrivate());
                                
                                if(state1 != 0) { // Se não está RELEASED, diz que o recurso não está livre
                                    feedback = "f " + recNo + " " + id + " " + criptography.criptografa("n", criptography.key.getPrivate());
                                }
                                
                                sendMulticastMessage(feedback);
                                message = "Processo " + pid + " requisitou o recurso " + recNo;
                            } else {
                                message = "Recebido número de recurso inválido";
                            }
                            
                            break;
                        case 'f':
                            // Retirando as informações: id de quem respondeu o resquest e número do recurso
                            st = new StringTokenizer(message);
                            st.nextToken();
                            recNo = Integer.parseInt(st.nextToken().trim());
                            pid = st.nextToken().trim();
                            String resp = st.nextToken().trim();
                            if(pid.compareTo(id.toString()) == 0) {
                                message = ""; //ignora proprio feedback
                                break;
                            }
                            message = "Mensagem de feedback não aguardada";
                            
                            if((recNo == 0 && state0 == 1) ||(recNo == 1 && state1 == 1)) {
                                Key k = criptography.key.getPublic();
                                for(Processes p : processes) {
                                    if(p.id.toString().compareTo(pid) == 0) {
                                        k = p.publicKey;
                                        break;
                                    }
                                }
                                
                                String respD = criptography.descriptografa(resp, k);
                                
                                if(respD.compareTo("y") == 0){
                                    if (negative_response_ids.contains(pid)){
                                        negative_response_ids.remove(pid);
                                    }
                                    if (!positive_response_ids.contains(pid)){
                                        positive_response_ids.add(pid);
                                    }
                                    message = "Feedback do processo "+ pid+ " foi positivo";
                                    
                                } else if (respD.compareTo("n") == 0){
                                    if (positive_response_ids.contains(pid)){
                                        positive_response_ids.remove(pid);
                                    }
                                    if (!negative_response_ids.contains(pid)){
                                        negative_response_ids.add(pid);
                                    }
                                    message = "Feedback do processo "+ pid+ " foi negativo";
                                }
                                
                            }
                            
                            break;
                        default:
                            break;
                    }
                    
                    // Imprimindo a mensagem ao usuário
                    if(message.compareTo("") != 0)
                        System.out.println("LOG: " + message);
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
                System.out.println("Digite r para requisitar um recurso, l para liberar um recurso e q para sair");
                
                // Lendo qual foi o comando inserido
                if(scan.hasNext()) {
                    String text= scan.nextLine();

                    // Se foi inserido comando de quit(q), terminar com o processo e notificar saída
                    if(text.toLowerCase().compareTo("q") == 0){
                        sendMulticastMessage("q " + id );
                        shouldRun = false;
                        break;
                    } else if(text.toLowerCase().compareTo("r") == 0) {//Comando de requisição(r), pergunta qual recurso e envia mensagem
                        System.out.println("Digite o número do recurso que você quer solicitar (0 ou 1)");
                        String recurso = "";
                        String message = "";
                        if(scan.hasNext()) {
                            recurso = scan.nextLine();
                            if(recurso.compareTo("0") == 0) {
                                state0 = 1;
                                message = "r 0 " + id.toString();
                                // Setar timer esperando por resposta
                            } else if(recurso.compareTo("1") == 0) {
                                state1 = 1;
                                message = "r 1 " + id.toString();
                            } else {
                                System.out.println("Número de recurso inválido");
                                break;
                            }
                        }
                        
                        int delay = 10*1000;

                        // Cria o timer
                        Timer timer = new Timer();
                        
                        // Timer varre a lista de processos e remove quem não respondeu
                        timer.schedule(new TimerTask() {
                            public void run() {
                                System.out.println("Timer ativou: Atualizando lista de processos");
                                for(Iterator<Processes> iter = processes.iterator(); iter.hasNext();) {
                                    Processes p = iter.next();
                                    if(!positive_response_ids.contains(p.id.toString()) && !negative_response_ids.contains(p.id.toString())) {
                                        iter.remove();
                                    }
                                }
                            }
                        }, delay);

                        sendMulticastMessage(message); //envia requisição
                        int i;
                        for(i =0; i < 1000000; i++); //pequeno delay para ajustar a ordem dos prints
                        
                        System.out.println("Esperando todos os feedbacks serem positivos...");
                        
                        while(true){//Espera todos os processos responderem positivamente
                            if (positive_response_ids.size() == processes.size()){
                                break;
                            }
                            System.out.print("");
                            if(positive_response_ids.size() + negative_response_ids.size() == processes.size() && timer != null) {//Se todos responderam, desliga timer
                                timer.cancel();
                                timer.purge();
                                timer = null;
                            }
                        }
                        positive_response_ids.clear();
                        negative_response_ids.clear();
                        
                        if(timer != null) {
                            timer.cancel();
                            timer.purge();
                        }
                        System.out.println("Recurso adquirido!");
                        
                        if(recurso.compareTo("0") == 0) {//Muda estado de WANTED para HELD
                            state0 = 2;
                        } else if(recurso.compareTo("1") == 0) {
                            state1 = 2;
                        }
                        
                    } else if(text.toLowerCase().compareTo("l") == 0){//Comando de liberar recurso, Checa se está em HELD no recurso e então envia mensagem de feedback positivo caso alguém esteja aguardando
                        System.out.println("Digite o número do recurso que você quer liberar (0 ou 1)");
                        if(scan.hasNext()) {
                            String recurso = scan.nextLine();
                            if(recurso.compareTo("0") == 0 && state0 == 2) {
                                state0 = 0;
                                System.out.println("Recurso liberado!");
                                String message = "f " + "0" + " " + id + " " + criptography.criptografa("y", criptography.key.getPrivate());
                                sendMulticastMessage(message);
                            } else if(recurso.compareTo("1") == 0 && state1 == 2) {
                                state1 = 0;
                                System.out.println("Recurso liberado!");
                                String message = "f " + "1" + " " + id + " " + criptography.criptografa("y", criptography.key.getPrivate());
                                sendMulticastMessage(message);
                            } else {
                                System.out.println("Você não pode liberar este recurso");
                            }
                        }
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
        final Key publicKey;
        
        public Processes(String id, Key pk) {
            this.id = UUID.fromString(id);
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
        Processes p = new Processes(id, recoveredKey);
        processes.add(p);
    }
}

