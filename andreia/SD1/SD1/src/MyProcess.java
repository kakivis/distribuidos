import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MyProcess {
    public static String ipAddressMulticast = "228.5.6.7";
    public static String ipAddressUnicast = "192.168.100.255";
    public static int socketMulticast = 6789;
    public static int socketUnicast;
    public static InetAddress group;
    
    static boolean shouldRun = true;
    static MulticastSocket s = null;
    static Criptography criptography;
    ArrayList<InnerProcess> processes;
    ArrayList<Request> requests;
    ArrayList<MyRequests> myRequests;
    
    public static String local = "/home/askoda/Documents/P3";
    
    public MyProcess(int numPort) {
        // Lista de processos que estão rodando com ele (inclui ele mesmo)
        processes = new ArrayList<>();
        // Lista de requests de envio de arquivos
        requests = new ArrayList<>();
        // Lista de arquivos que o processo pediu
        myRequests = new ArrayList<>();
        // Número do socket que esse processo está utilizando (para comunicação unicast)
        socketUnicast = numPort;
        
        try {
            // Entrando na sala de multicast
            group = InetAddress.getByName(ipAddressMulticast);
            s = new MulticastSocket(socketMulticast);
            s.joinGroup(group);

            // Iniciando Thread que recebe em multicast
            new ReceiveMulticastThread().start();
            // Iniciando Thread que recebe em unicast
            new ReceiveUnicastThread().start();
            // Iniciando Thread que controla os comandos fornecidos pelo usuario
            new ProcessCommandsThread().start();
            
            // Gerando novo par de chaves pública e privada
            criptography = new Criptography();
            
            // Se apresentando para os processos
            sendMulticastMessage("p " + ipAddressUnicast + " " + socketUnicast +
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
                        case 'p':
                            {
                                // Retirando as informações: ip, porta, módulo e expoente
                                StringTokenizer st = new StringTokenizer(message);
                                st.nextToken();
                                String ip = st.nextToken().trim();
                                String port = st.nextToken().trim();
                                String mod = st.nextToken().trim();
                                String exp = st.nextToken().trim();
                                
                                // Adicionando o processo à lista de processos conhecidos
                                // (Inclui ele mesmo)
                                addProcess(ip, port, mod, exp);
                                int p = Integer.parseInt(port);
                                
                                // Se recebeu em multicast e não é ele mesmo, o processo
                                // ainda não o conhece, então se apresenta em unicast
                                if(ip.compareTo(ipAddressUnicast) != 0 || p != socketUnicast) {
                                    sendUnicastMessage("p " + ipAddressUnicast + " " + socketUnicast +
                                    " " + criptography.getModulusPublic() + " " +
                                    criptography.getExpoentPublic(), port);
                                }       

                                // Tornando a mensagem mais agradável ao usuário
                                message = "novo processo: IP " + ip + " porta " + port;
                                
                                break;
                            }
                        // Se for uma solicitação de envio de arquivos chegando
                        case 's':
                            {
                                // Retirando as informações: ip, porta e nome do arquivo solicitado
                                StringTokenizer st = new StringTokenizer(message);
                                st.nextToken();
                                String ip = st.nextToken().trim();
                                String port = st.nextToken().trim();
                                String name = st.nextToken().trim();
                                
                                // Adicionando a solicitação à lista que contém
                                // todas as solicitações
                                addRequest(ip, port, name);
                                
                                // Tornando a mensagem mais agradável ao usuário
                                message = "pedido do arquivo " + name + " pelo IP " +
                                        ip + " porta " + port;
                                
                                break;
                            }
                        // Caso seja uma atualização de reputação
                        case 'r':
                            {
                                // Retirando as informações: ip, porta e se é
                                // para aumentar ou diminuir a reputação
                                StringTokenizer st = new StringTokenizer(message);
                                st.nextToken();
                                String ip = st.nextToken().trim();
                                String port = st.nextToken().trim();
                                String result = st.nextToken().trim();
                                
                                // Procurando o processo
                                InnerProcess process = null;
                                for(InnerProcess p : processes) {
                                    if(p.ip.compareTo(ip) == 0 &&
                                            p.port.compareTo(port) == 0) {
                                        process = p;
                                        break;
                                    }
                                }       
                                
                                // Se o processo foi encontrado
                                if(process != null){        
                                    // Se veio 's' (sim), o envio foi bem sucedido
                                    if(result.compareTo("s") == 0)
                                        process.reputation++;
                                    // Caso contrário, não foi (nao)
                                    else
                                        process.reputation--;
                                    
                                    // Tornando a mensagem mais agradável ao usuário
                                    message = "Reputação de IP " + ip + " porta " + port + " alterada!";
                                }
                                break;
                            }
                        default:
                            break;
                    }
                    
                    // Imprimindo a mensagem ao usuário
                    System.out.println("M: " + message);
                } catch (IOException e) {
                    System.out.println(e);
                }
                
            }
            
            // Avisando que a Thread foi finalizada
            System.out.println("ReceiveMulticastThread finalizada");
        }
    }
    
    /** 
     * Thread responsável por receber mensagens enviadas em unicast
     */
    public class ReceiveUnicastThread extends Thread {
        @Override
        public void run() {
            // Enquanto o processo existir
            while(shouldRun){
                try (DatagramSocket aSocket = new DatagramSocket(socketUnicast)){
                    // Recebendo a mensagem
                    byte[] buffer = new byte[1000];
                    DatagramPacket m = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(m); 

                    String message = new String(m.getData(), "UTF-8");

                    switch (message.charAt(0)) {
                        // Se for um processo se apresentando
                        case 'p':
                            {
                                // Retirando as informações principais: ip, port, módulo e expoente
                                StringTokenizer st = new StringTokenizer(message);
                                st.nextToken();
                                String ip = st.nextToken().trim();
                                String port = st.nextToken().trim();
                                String mod = st.nextToken().trim();
                                String exp = st.nextToken().trim();
                                
                                // Adicionando o processo à lista de processos conhecidos
                                addProcess(ip, port, mod, exp);
                                
                                // Tornando a mensagem mais agradável ao usuário
                                System.out.println("U: bem-vindo, novo processo! (IP " +
                                        ip + " porta " + port + ")");
                                break;
                            }
                        // Se for um envio de arquivo
                        case 'f':
                            {
                                // Retirando as informações: nome do arquivo, ip
                                //e porta de quem está querendo enviar
                                message = message.substring(2);
                                StringTokenizer st = new StringTokenizer(message);
                                String name = st.nextToken().trim();
                                String ip = st.nextToken().trim();
                                String port = st.nextToken().trim();
                                
                                // Procurando o processo que está querendo enviar
                                InnerProcess process = null;
                                for(InnerProcess p : processes) {
                                    if(p.ip.compareTo(ip) == 0 && p.port.compareTo(port) == 0) {
                                        process = p;
                                        break;
                                    }
                                }
                                
                                // Se o processo foi encontrado, pode continuar
                                if(process != null) {
                                    Request request = null;
                                    
                                    // Procurando a request que foi aberta
                                    for(Request r : requests) {
                                        if(r.name.compareTo(name) == 0 &&
                                                r.p.ip.compareTo(ipAddressUnicast) == 0 &&
                                                Integer.parseInt(r.p.port) == socketUnicast) {
                                            request = r;
                                            break;
                                        }
                                    }
                                    
                                    // Se a request foi encontrada, pode continuar
                                    if(request != null) {
                                        boolean found = false;
                                        
                                        for(MyRequests mr : myRequests) {
                                            if(mr.request.name.compareTo(name) == 0) {
                                                // Se alguém já está tentando enviar o
                                                // arquivo, comparar reputações
                                                found = true;
                                                mr.newSender(process, message);
                                                break;
                                            }
                                        }
                                        
                                        // Se é o primeiro tentando enviar, adicionar como sender
                                        if(!found)  {
                                            myRequests.add(new MyRequests(process, request, message));
                                        }
                                    }
                                }
                                
                                // Tornando a mensagem mais agradável ao usuário
                                System.out.println("(Alguém te ofereceu um arquivo ("
                                        + name + ")!)");
                                break;
                            }
                        default:
                            System.out.println(message);
                            break;
                    }
                    
                } catch (SocketException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }
            
            }
            
            // Informando que a Thread foi finalizada
            System.out.println("ReceiveUnicastThread finalizada");
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
            
            System.out.println("(Digite 'm' para enviar mensagem " +
                    "em multicast, 'u' para enviar mensagem em unicast,");
            System.out.println("'s' para solicitar arquivos, 'e' para enviar arquivos,");
            System.out.println("'a' para aceitar arquivos ou 'tchau' para desconectar)");

            // Enquanto o processo estiver ativo
            while(shouldRun) {
                
                // Lendo qual foi o comando inserido
                if(scan.hasNext()) {
                    String text= scan.nextLine();

                    // Se foi inserido tchau, terminar com o processo
                    if(text.toLowerCase().compareTo("tchau") == 0)
                        break;
                    
                    // Se foi inserido m, enviar mensagem em multicast
                    else if(text.toLowerCase().compareTo("m") == 0) {
                        System.out.println("(Digite a mensagem)");
                        if(scan.hasNext()) {
                            String m = scan.nextLine();
                            sendMulticastMessage(m);
                        }
                    }
                    
                    // Se foi inserido u, enviar mensagem em unicast
                    else if(text.toLowerCase().compareTo("u") == 0) {
                        System.out.println("(Digite o número da porta)");
                        if(scan.hasNext()) {
                            String port = scan.nextLine();
                            System.out.println("(Digite a mensagem)");
                            if(scan.hasNext()) {
                                String m = scan.nextLine();
                                sendUnicastMessage(m, port);
                            }
                        }
                    } 
                    
                    // Se foi inserido s, solicitar envio de arquivos
                    else if(text.toLowerCase().compareTo("s") == 0) {
                        System.out.println("(Digite o nome do arquivo que deseja solicitar sem extensão)");
                        if(scan.hasNext()) {
                            String name = scan.nextLine();
                            String message = "s " + ipAddressUnicast + " " +
                                    socketUnicast + " " + name;
                            // Avisando em multicast que o processo quer aquele arquivo
                            sendMulticastMessage(message);
                        }
                    } 
                    
                    // Se foi inserido e, oferecer para enviar arquivo
                    else if(text.toLowerCase().compareTo("e") == 0) {
                        // Imprimindo a lista de requests realizadas
                        printRequests();
                        
                        // Se não houver nenhuma, não faz nada
                        if(!requests.isEmpty()) {
                            System.out.println("(Digite o número de qual requisição gostaria de atender:)");
                            String num = "0";
                            if(scan.hasNext())
                                num = scan.nextLine();

                            int n = Integer.parseInt(num);
                            Request r = requests.get(n-1);

                            System.out.println("(Deseja atender a requisição pelo arquivo " +
                                    r.name + "? 's' ou 'n')");

                            if(scan.hasNext())
                                num = scan.nextLine();

                            if(num.toLowerCase().compareTo("s") == 0) {
                                sendFile(r);
                                System.out.println("(Solicitação enviada!)");
                            }
                        }
                    } 
                    
                    // Se foi inserido a, aceitar receber arquivos
                    else if(text.toLowerCase().compareTo("a") == 0) {
                        // Se eu não tenho nenhum request atendido, não faz nada
                        if(myRequests.isEmpty()) {
                            System.out.println("(Parece que você não tem nada a receber...)");
                        } else {
                            int i = 1;
                            // Imprimindo a lista de requests atendidos
                            for(MyRequests mr : myRequests) {
                                System.out.println(i + " - " + mr.request.name);
                                i++;
                            }

                            System.out.println("(Digite o número do arquivo que deseja receber)");

                            String num = "0";
                            if(scan.hasNext())
                                num = scan.nextLine();

                            MyRequests myRequest = myRequests.get(Integer.parseInt(num)-1);

                            System.out.println("(Recebendo arquivo de IP " +
                                    myRequest.sender.ip + " port " + 
                                    myRequest.sender.port + " reputação " +
                                    myRequest.sender.reputation + ")");

                            // Recebendo o arquivo
                            receiveFile(myRequest);

                            // Confirmando se o arquivo foi recebido com sucesso,
                            // simulação de erro
                            System.out.println("(Arquivo " + myRequest.request.name + " recebido "
                            + "com sucesso? 's' ou 'n'?)");

                            if(scan.hasNext())
                                num = scan.nextLine();

                            // Preparando mensagem para enviar em multicast para
                            // atualização da reputação do processo
                            String message = "r " + myRequest.sender.ip + " " +
                                    myRequest.sender.port + " ";
                            
                            if(num.toLowerCase().compareTo("s") == 0) {
                                message = message + "s";
                            } else {
                                message = message + "n";
                            }

                            // Enviando mensagem de atualização de reputação
                            sendMulticastMessage(message);

                            Request request = myRequest.request;

                            // Removendo a request atendida das listas
                            requests.remove(request);
                            myRequests.remove(myRequests);
                        }
                    } else
                        System.out.println("(Comando não reconhecido.)");
                }
            }
            
            if(s != null)
                s.close();
            
            // Avisando que a Thread foi finalizada
            shouldRun = false;
            System.out.println("SendMessageThread finalizada");
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
    
    /** 
     * Método responsável por enviar mensagens por unicast
     * @param message
     * @param portDest 
     */
    public void sendUnicastMessage(String message, String portDest) {      
	try (DatagramSocket aSocket = new DatagramSocket()) {
            byte [] m = message.getBytes();
            
            InetAddress aHost = InetAddress.getByName(ipAddressUnicast);
            
            int serverPort = Integer.parseInt(portDest.trim());		                                                 
            DatagramPacket request = new DatagramPacket(m,  message.length(), aHost, serverPort);
            aSocket.send(request);
            
	} catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
	} catch (IOException e){
            System.out.println("IO: " + e.getMessage());
	}
        
    }
    
    /** 
     * Método para enviar aquivo por unicast
     * @param request
     */
    public void sendFile(Request request) {
        // Lendo o arquivo pelo caminho fornecido
        File arquivoOrigem = new File(local + "/" + request.name + ".txt");
        String sending = "";
        
        try {
            FileReader fis = new FileReader(arquivoOrigem);
            BufferedReader bufferedReader = new BufferedReader(fis);
            StringBuilder buffer = new StringBuilder();
            String line = "";

            // Lendo o arquivo e o transformando em uma string
            while ((line = bufferedReader.readLine()) != null) {
            	buffer.append(line).append("\n");
            }
            
            // Criptografando um "ok" para o processo provar que é ele mesmo
            String ok = criptography.criptografa("ok", criptography.key.getPrivate());
            
            // Gerando a mensagem para enviar por unicast
            sending = "f " + request.name + " " + ipAddressUnicast + " " + socketUnicast + " " +
                    ok + " " + buffer.toString();
            
            fis.close();
            bufferedReader.close();
        } catch(FileNotFoundException e) {
            System.out.println("Parece que você não tem esse arquivo...");
        } catch(Exception e) {
            System.out.println(e);
        }
        
        // Enviando a mensagem por unicast, se ela existir
        if(!sending.isEmpty())
            sendUnicastMessage(sending, request.p.port);
    }
    
    /** 
     * Método para receber o arquivo por unicast
     * @param myRequest 
     */
    public void receiveFile(MyRequests myRequest) {
        // Lendo a mensagem salva
        String message = myRequest.message;
        
        // Extraindo as informações de nome do arquivo, ip, porta e ok criptografado
        StringTokenizer st = new StringTokenizer(message);
        String name = st.nextToken().trim();
        String ip = st.nextToken().trim();
        String port = st.nextToken().trim();
        String ok = st.nextToken().trim();
                        
        message = message.substring(message.indexOf(" ")+1);
        message = message.substring(message.indexOf(" ")+1);
        message = message.substring(message.indexOf(" ")+1);
        message = message.substring(message.indexOf(" ")+1);
                        
        Key publicKey = null;
                        
        // Procurando o processo que enviou
        for(InnerProcess p : processes) {
            if(p.ip.compareTo(ip) == 0 && p.port.compareTo(port) == 0) {
                publicKey = p.publicKey;
                break;
            }
        }
                
        // Descriptografando o "ok" (mensagem padrão) que prova que o processo é ele mesmo
        if(publicKey != null)
            ok = criptography.descriptografa(ok, publicKey);
                        
        try {
            // Se o "ok" foi reconhecido, o processo é ele mesmo
            if(ok.compareTo("ok") == 0) {
                File arquivoDestino = new File(local + "/" + name + "_received"
                    + ".txt");
                FileWriter writer = new FileWriter(arquivoDestino);
                writer.write(message);
                writer.flush();
                writer.close();
            }
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    /** 
     * Adiciona novo processo na lista
     * @param ip
     * @param port
     * @param mod
     * @param exp  
     */
    public void addProcess(String ip, String port, String mod, String exp) {
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
        InnerProcess inp = new InnerProcess(ip, port, recoveredKey);
        processes.add(inp);
    }

    /** 
     * Adiciona nova request à lista
     * @param ip
     * @param port
     * @param name 
     */
    public void addRequest(String ip, String port, String name) {
        for(InnerProcess inp : processes) {
            if(inp.ip.compareTo(ip) == 0 &&
                    inp.port.compareTo(port) == 0) {
                requests.add(new Request(inp, name));
                return;
            }
        }
    }
    
    /**
     * Imprime lista de requests 
     * @return 
     */
    public boolean printRequests() {
        if(requests.isEmpty()) {
            System.out.println("Não há nenhum pedido pendente");
            return false;
        }
        
        int i = 1;
        
        System.out.println("Pedidos pendentes:");
        
        for(Request r : requests) {
            System.out.println(i + " - IP " + r.p.ip + " porta " + r.p.port +
                    " requisitou o arquivo " + r.name);
            i++;
        }
        
        return true;
    }
    
    // Classe que armazena processos existente
    public class InnerProcess {
        final String ip;
        final String port;
        final Key publicKey;
        int reputation;
        
        public InnerProcess(String ip, String port, Key pk) {
            this.ip = ip;
            this.port = port;
            this.publicKey = pk;
            reputation = 0;
        }
    }
    
    // Classe que armazena requests existentes
    public class Request {
        InnerProcess p;
        String name;
        
        public Request(InnerProcess p, String name) {
            this.name = name;
            this.p = p;
        }
    }
    
    // Classe que armazena requests que o próprio processo abriu
    public class MyRequests {
        Request request;
        InnerProcess sender;
        String message;
        
        public MyRequests(InnerProcess p, Request r, String m) {
            request = r;
            sender = p;
            message = m;
        }
        
        public void newSender(InnerProcess p, String m) {
            if(p.reputation > sender.reputation) {
                sender = p;
                message = m;
            }
        }
    }
}

