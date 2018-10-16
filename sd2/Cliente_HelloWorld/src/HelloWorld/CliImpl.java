package HelloWorld;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author junio
 */
public class CliImpl extends UnicastRemoteObject implements InterfaceCli {

    /**
     *
     */
    public static InterfaceServ server;
    public static UUID id;

    public CliImpl(InterfaceServ s) throws RemoteException{
        server = s;
        id = UUID.randomUUID();
    }
    
    public static void print_menu(){
        System.out.println("Digite a opção desejada:");
        System.out.println("1. Consultar/Comprar passagens");
        System.out.println("2. Consultar/Comprar hospedagens");
        System.out.println("3. Consultar/Comprar pacotes");
        System.out.println("4. Registrar interesse em evento");
        System.out.println("5. Consultar/Remover interesses em eventos");  
    }
    
    public static void print_tickets(String[] tickets_string){
        System.out.println("Lista de Passagens:");
        System.out.println("|Ida e volta         |Origem              |Destino             |Data da ida       |Data da volta       |Numero de pessoas   ");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        if(tickets_string == null){
            System.out.println("Não existem passagens disponíveis no momento.");
            return;
        }
        for(String t : tickets_string){
            System.out.println(t);
        }
    }
    
    
    public static void print_lodges(String[] lodges_string){
        System.out.println("Lista de Hospedagem:");
        System.out.println("|Destino             |Data de checkin     |Data de checkout    |Numero de quartos   ");
        System.out.println("------------------------------------------------------------------------------------");
        if(lodges_string == null){
            System.out.println("Não existem hospedagens disponíveis no momento.");
            return;
        }
        
        for(String l : lodges_string){
            System.out.println(l);
        }
    }
    
    public static void print_combos(String[] combos_string){
        System.out.println("Lista de Pacotes:");
        System.out.println("|Ida e volta    |Origem         |Destino        |Data da ida  |Data da volta  |Numero de pessoas|Numero de quartos|Data de checkin|Data de checkout|Tem passagem?|Tem hospedagem");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        if(combos_string == null){
            System.out.println("Não existem pacotes disponíveis no momento.");
            return;
        }
        
        for(String c : combos_string){
            System.out.println(c);
        }
    }
    
    /**
     *
     * @return
     */
    public static Map get_ticket_info(){
        Map<String, String> hash = new HashMap<>();
        
        System.out.println("Insira os dados da passagem desejada.");
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Ida e volta? (s ou n)");
        String round_trip = scan.nextLine();
        hash.put("round_trip", round_trip);
        System.out.println("Insira o local de origem:");
        String origin = scan.nextLine();
        hash.put("origin", origin);
        System.out.println("Insira o local de destino");
        String destination = scan.nextLine();
        hash.put("destination", destination);
        System.out.println("Insira a data de partida");
        String departure_date = scan.nextLine();
        hash.put("departure_date", departure_date);
        String return_date;
        if ("s".equals(round_trip)){
            System.out.println("Insira a data de retorno");
            return_date = scan.nextLine();
        } else {
            return_date = "00/00/0000";
        }
        hash.put("return_date", return_date);
        System.out.println("Quantas pessoas?");
        String n_people = scan.nextLine();
        hash.put("n_people", n_people);
        
        return hash;
    }
    
    /**
     *
     * @return
     */
    public static Map get_lodge_info(){
        Map<String, String> hash = new HashMap<>();
        
        System.out.println("Insira os dados da hospedagem desejada.");
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Insira o local de destino");
        String destination = scan.nextLine();
        hash.put("destination", destination);
        System.out.println("Insira a data de check in");
        String checkin_date = scan.nextLine();
        hash.put("checkin_date", checkin_date);
        System.out.println("Insira a data de check out");
        String checkout_date = scan.nextLine();
        hash.put("checkout_date", checkout_date);
        System.out.println("Quantos quartos?");
        String n_rooms = scan.nextLine();
        hash.put("n_rooms", n_rooms);
        
        return hash;
    }
    
    /**
     *
     * @return
     */
    public static Map get_combo_info(){
        Map<String, String> hash = new HashMap<>();
        
        System.out.println("Insira os dados do pacote desejado:");
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Ida e volta? (s ou n)");
        String round_trip = scan.nextLine();
        hash.put("round_trip", round_trip);
        System.out.println("Insira o local de origem:");
        String origin = scan.nextLine();
        hash.put("origin", origin);
        System.out.println("Insira o local de destino");
        String destination = scan.nextLine();
        hash.put("destination", destination);
        System.out.println("Insira a data de partida");
        String departure_date = scan.nextLine();
        hash.put("departure_date", departure_date);
        String return_date;
        if ("s".equals(round_trip)){
            System.out.println("Insira a data de retorno");
            return_date = scan.nextLine();
        } else {
            return_date = "00/00/0000";
        }
        hash.put("return_date", return_date);
        System.out.println("Quantas pessoas?");
        String n_people = scan.nextLine();
        hash.put("n_people", n_people);
        System.out.println("Insira a data de check in");
        String checkin_date = scan.nextLine();
        hash.put("checkin_date", checkin_date);
        System.out.println("Insira a data de check out");
        String checkout_date = scan.nextLine();
        hash.put("checkout_date", checkout_date);
        System.out.println("Quantos quartos?");
        String n_rooms = scan.nextLine();
        hash.put("n_rooms", n_rooms);
        
        return hash;
    }
    
    @Override
    public void notify_cli(String interest)  throws RemoteException {
        System.out.println();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!NOTIFICAÇÃO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("O evento a seguir acabou de ser liberado para compra!:");
        System.out.println("|Tipo do evento |Dados do evento(campo:valor)");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(interest);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println();
    }
    
    public void handle_set_interest() throws RemoteException{
        
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Selecione o tipo de evento desejado.");
        System.out.println("1. Passagem");
        System.out.println("2. Hospedagem");
        System.out.println("3. Pacote");
        if(scan.hasNext()){
            String text = scan.nextLine();
            switch(text.charAt(0)){
                case '1':
                    Map<String, String> info_t = get_ticket_info();
                    System.out.println(server.set_interest(this, "Passagem", info_t));
                    
                    break;
                    
                case '2':
                    Map<String, String> info_l = get_lodge_info();
                    System.out.println(server.set_interest(this, "Hospedagem", info_l));
                    
                    break;
                    
                case '3':
                    Map<String, String> info_c = get_combo_info();
                    System.out.println(server.set_interest(this, "Pacote", info_c));
                    
                    break;
                    
            }
        }
    }
    
    public static void print_interests(String[] interests){
        System.out.println("Lista de Interesses:");
        System.out.println("|Tipo do evento |Dados do evento(campo:valor)");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        if(interests == null){
            System.out.println("Não existem interesses registrados até o momento.");
            return;
        }
        
        for(String i : interests){
            System.out.println(i);
        }
    }
    
    public void handle_get_interests() throws RemoteException{
        Scanner scan;
        scan = new Scanner(System.in);
        String[] interests = server.get_interests(this);
        print_interests(interests);
        System.out.println();
        if(interests != null){
            System.out.println("Insira o índice(1..n) do interesse que você deseja remover ou '0' para voltar.");
            try {
                int index = Integer.parseInt(scan.nextLine());
                if( 0 < index && index < interests.length + 1){
                    System.out.println(server.remove_interest(this, index - 1));
                } else if (index != 0){
                    System.out.println("Indice inválido");
                }
            } catch (NumberFormatException nfe){
                System.out.println("Indice deve ser um valor numérico");
            }
        }        
    }

    
    public void start_menu() throws RemoteException{
        print_menu();
        // Lendo entrada do teclado
        Scanner scan;
        scan = new Scanner(System.in);
        while(true){
            if(scan.hasNext()){
                String text = scan.nextLine();
                
                switch (text.charAt(0)){
                    case '1': // Consultar/Comprar passagens
                        System.out.println();
                        System.out.println();
                        String[] tickets = server.get_tickets();
                        print_tickets(tickets);
                        System.out.println();
                        if(tickets != null){
                            System.out.println("Insira o índice(1..n) da hospedagem que você deseja comprar ou '0' para voltar.");
                            try {
                                int index = Integer.parseInt(scan.nextLine());
                                if( 0 < index && index < tickets.length + 1 ){
                                    System.out.println(server.buy_ticket(index - 1));
                                } else if(index != 0) {
                                    System.out.println("Indice inválido");
                                }
                            } catch (NumberFormatException nfe){
                                System.out.println("Indice deve ser um valor numérico.");
                                
                            }
                        }
                        System.out.println();
                        print_menu();
                        break;
                        
                    case '2': // Consultar/Comprar hospedagens
                        System.out.println();
                        System.out.println();
                        String[] lodges = server.get_lodges();
                        print_lodges(lodges);
                        System.out.println();
                        if(lodges != null){
                            System.out.println("Insira o índice(1..n) da hospedagem que você deseja comprar ou '0' para voltar.");
                            try {
                                int index = Integer.parseInt(scan.nextLine());
                                if( 0 < index && index < lodges.length + 1){
                                    System.out.println(server.buy_lodge(index - 1));
                                } else if (index != 0){
                                    System.out.println("Indice inválido");
                                }                            
                            } catch (NumberFormatException nfe){
                                System.out.println("Indice deve ser um valor numérico");
                            }
                        }
                        System.out.println();
                        print_menu();
                        break; 
                    
                    case '3': // Consultar/Comprar pacotes
                        System.out.println();
                        System.out.println();
                        String[] combos = server.get_combos();
                        print_combos(combos);
                        System.out.println();
                        if(combos != null){
                            System.out.println("Insira o índice(1..n) do pacote que você deseja comprar ou '0' para voltar.");
                            try {
                                int index = Integer.parseInt(scan.nextLine());
                                if( 0 < index && index < combos.length + 1){
                                    System.out.println(server.buy_combo(index - 1));
                                } else if (index != 0){
                                    System.out.println("Indice inválido");
                                }
                            } catch (NumberFormatException nfe){
                                System.out.println("Indice deve ser um valor numérico");
                            }
                            
                        }
                        System.out.println();
                        print_menu();
                        break;
                    
                    case '4': //Registrar interesse em evento
                        handle_set_interest();
                        
                        System.out.println();
                        print_menu();
                        break;
//                  
                    case '5': //Consultar/Remover interesses em eventos
                        handle_get_interests();
                        
                        System.out.println();
                        print_menu();
                        break;
                    
                    default: // Outros comandos não são reconhecidos
                        System.out.println("Comando não reconhecido");
                        print_menu();
                }
            }
        }
    }
    
}
