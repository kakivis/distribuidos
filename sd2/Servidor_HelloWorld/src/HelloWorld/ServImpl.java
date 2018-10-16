package HelloWorld;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;


public class ServImpl extends UnicastRemoteObject implements InterfaceServ {
    
    public static ArrayList<Ticket> tickets = new ArrayList<>();
    public static ArrayList<Lodge> lodges = new ArrayList<>();
    public static ArrayList<Combo> combos = new ArrayList<>();
    public static ArrayList<Interest> interests = new ArrayList<>();
    
    public static void print_menu(){
        System.out.println("Digite a opção desejada:");
        System.out.println("1. Criar passagem");
        System.out.println("2. Criar hospedagem");
        System.out.println("3. Criar pacote");
        System.out.println("4. Visualizar passagens");
        System.out.println("5. Visualizar hospedagens");
        System.out.println("6. Visualizar pacotes");
        System.out.println("7. Visualizar interesses");        
    }
    
    public static void create_ticket() throws RemoteException{
        System.out.println("Criando passagem:");
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Ida e volta? (s ou n)");
        String text = scan.nextLine();
        Boolean round_trip = text.charAt(0) == 's';
        System.out.println("Insira o local de origem:");
        String origin = scan.nextLine();
        System.out.println("Insira o local de destino");
        String destination = scan.nextLine();
        System.out.println("Insira a data de partida");
        String departure_date = scan.nextLine();
        String return_date;
        if (round_trip){
            System.out.println("Insira a data de retorno");
            return_date = scan.nextLine();
        } else {
            return_date = "00/00/0000";
        }
        System.out.println("Quantas pessoas?");
        Integer n_people = Integer.parseInt(scan.nextLine());
        
        Ticket t = new Ticket(round_trip, origin, destination, departure_date, return_date, n_people);
        tickets.add(t);
        
        for(Interest i : interests){
            if(i.type.equals("Passagem")){
                if(t.hash_comp(i.h)){
                    i.cli.notify_cli(interest_to_s(i));
                }
            }
        }
        
        for(Combo c : combos){
            if(!c.check_existance()){
               if(c.set_ticket(t)){
                for(Interest i : interests){
                     if(i.type.equals("Pacote")){
                         if(c.hash_comp(i.h) && c.check_existance()){
                             i.cli.notify_cli(interest_to_s(i));
                         }
                     }
                 }    
               }
            }
        }
        
    }
    
    
    public static void create_lodge() throws RemoteException{
        System.out.println("Criando hospedagem:");
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Insira o local de destino");
        String destination = scan.nextLine();
        System.out.println("Insira a data de check in");
        String checkin_date = scan.nextLine();
        System.out.println("Insira a data de check out");
        String checkout_date = scan.nextLine();
        System.out.println("Quantos quartos?");
        Integer n_rooms = Integer.parseInt(scan.nextLine());
        
        Lodge l = new Lodge(destination, checkin_date, checkout_date, n_rooms);
        lodges.add(l);
        
        for(Interest i : interests){
            if(i.type.equals("Hospedagem")){
                if(l.hash_comp(i.h)){
                    i.cli.notify_cli(interest_to_s(i));
                }
            }
        }
        
        for(Combo c : combos){
            if(!c.check_existance()){
               if(c.set_lodge(l)){
                for(Interest i : interests){
                     if(i.type.equals("Pacote")){
                         if(c.hash_comp(i.h) && c.check_existance()){
                             i.cli.notify_cli(interest_to_s(i));
                         }
                     }
                 }    
               }
            }
        }
    }
    
    
    public static void create_combo() throws RemoteException{
        System.out.println("Criando pacote:");
        Scanner scan;
        scan = new Scanner(System.in);
        System.out.println("Passagem de ida e volta? (s ou n)");
        String text = scan.nextLine();
        Boolean round_trip = text.charAt(0) == 's';
        System.out.println("Insira o local de origem:");
        String origin = scan.nextLine();
        System.out.println("Insira o local de destino");
        String destination = scan.nextLine();
        System.out.println("Insira a data de partida");
        String departure_date = scan.nextLine();
        String return_date;
        if (round_trip){
            System.out.println("Insira a data de retorno");
            return_date = scan.nextLine();
        } else {
            return_date = "00/00/0000";
        }
        System.out.println("Quantas pessoas?");
        Integer n_people = Integer.parseInt(scan.nextLine());
        System.out.println("Quantos quartos?");
        Integer n_rooms = Integer.parseInt(scan.nextLine());
        System.out.println("Insira a data do checkin");
        String checkin_date = scan.nextLine();
        System.out.println("Insira a data do checkout");
        String checkout_date = scan.nextLine();
        
        Combo c = new Combo(round_trip, origin, destination, departure_date, return_date, n_people, n_rooms,checkin_date, checkout_date);
        combos.add(c);
        
        for(Ticket t : tickets){
            if(c.ticket == null){
               c.set_ticket(t);
            }
        }
        
        for(Lodge l : lodges){
            if(c.lodge == null){
                c.set_lodge(l);
            }
        }
        
        for(Interest i : interests){
            if(i.type.equals("Pacote")){
                if(c.hash_comp(i.h) && c.check_existance()){
                    i.cli.notify_cli(interest_to_s(i));
                }
            }
        }
    }
    
    public static void print_tickets(){
        System.out.println("Lista de Passagens:");
        System.out.println("|Ida e volta         |Origem              |Destino             |Data da ida       |Data da volta       |Numero de pessoas   ");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        
        for(Ticket t : tickets){
            System.out.print("|" + t.round_trip.toString());
            int n = 20 - t.round_trip.toString().length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + t.origin);
            n = 20 - t.origin.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + t.destination);
            n = 20 - t.destination.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + t.departure_date);
            n = 20 - t.departure_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + t.return_date);
            n = 20 - t.return_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + t.n_people.toString());
            n = 20 - t.n_people.toString().length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    
    public static void print_lodges(){
        System.out.println("Lista de Hospedagem:");
        System.out.println("|Destino             |Data de checkin     |Data de checkout    |Numero de quartos   ");
        System.out.println("------------------------------------------------------------------------------------");
        
        for(Lodge l : lodges){
            System.out.print("|" + l.destination);
            int n = 20 - l.destination.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + l.checkin_date);
            n = 20 - l.checkin_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + l.checkout_date);
            n = 20 - l.checkout_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + l.n_rooms.toString());
            n = 20 - l.n_rooms.toString().length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    public static void print_combos(){
        System.out.println("Lista de Pacotes:");
        System.out.println("|Ida e volta    |Origem         |Destino        |Data da ida  |Data da volta  |Numero de pessoas|Numero de quartos|Data de checkin|Data de checkout|Tem passagem?|Tem hospedagem");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        
        for(Combo c : combos){
            System.out.print("|" + c.round_trip.toString());
            int n = 15 - c.round_trip.toString().length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.origin);
            n = 15 - c.origin.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.destination);
            n = 15 - c.destination.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.departure_date);
            n = 13 - c.departure_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.return_date);
            n = 15 - c.return_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.n_people.toString());
            n = 17 - c.n_people.toString().length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.n_rooms.toString());
            n = 17 - c.n_rooms.toString().length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.checkin_date);
            n = 15 - c.checkin_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.print("|" + c.checkout_date);
            n = 16 - c.checkout_date.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            
            boolean foo = c.ticket != null;
            String bar = Boolean.toString(foo);
            System.out.print("|" + bar);
            n = 13 - bar.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            foo = c.lodge != null;
            bar = Boolean.toString(foo);            
            System.out.print("|" + bar);
            n = 13 - bar.length();
            for(int i=0;i<n;i++){
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    

    public ServImpl() throws RemoteException{
        
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
                    case '1': // Criando passagem
                        create_ticket();
                        print_menu();
                        break;
                        
                    case '2': // Criando hospedagem
                        create_lodge();
                        
                        print_menu();
                        break; 
                    
                    case '3': // Criando pacote
                        create_combo();
                        
                        print_menu();
                        break;
                    
                    case '4': //listar passagens
                        print_tickets();
                        
                        print_menu();
                        break;
//                  
                    case '5': //listar hospedagens
                        print_lodges();
                        
                        print_menu();
                        break;
                    
                    case '6': //listar pacotes
                        print_combos();
                        
                        print_menu();
                        break;
                        
                    case '7':
                        print_interests();
                        
                        print_menu();
                        break;
                    
                        
                    default: // Outros comandos não são reconhecidos
                        System.out.println("Comando não reconhecido");
                        print_menu();
                }
            }
        }
    }
    
    /**
     *
     * @param index
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized final String buy_ticket(int index) throws RemoteException{
        for(Combo c : combos){
            if(c.ticket.equals(tickets.get(index))){
                c.ticket = null;
            }
        }
        if(tickets.remove(index) != null){
            return "Compra da passagem foi efetuada.";
        } else {
            return "Houve um problema com a compra da passagem.";
        }
    }
    
    /**
     *
     * @param index
     * @return
     * @throws RemoteException
     */
    @Override
    public final String buy_lodge(int index) throws RemoteException{
        for(Combo c : combos){
            if(c.lodge.equals(lodges.get(index))){
                c.lodge = null;
            }
        }
        if(lodges.remove(index) != null){
            return "Compra da hospedagem foi efetuada.";
        } else {
            return "Houve um problema com a compra da hospedagem.";
        }
    }
    
    /**
     *
     * @param index
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized final String buy_combo(int index) throws RemoteException{
        if(combos.remove(index) != null){
            return "Compra do pacote foi efetuada.";
        } else {
            return "Houve um problema com a compra do pacote.";
        }
    }
    
    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized final String[] get_tickets() throws RemoteException {
        int aux = tickets.size();
        if(aux == 0){
            return null;
        }
        String[] tickets_string = new String[aux];
        
        for(int i = 0; i < aux; i++){
            tickets_string[i] = ticket_to_s(tickets.get(i));
        }
        return tickets_string;
    }
    
    /**
     *
     * @param t
     * @return
     * @throws RemoteException
     */
    public final String ticket_to_s(Ticket t) throws RemoteException {
        String ticket_string = "|" + t.round_trip.toString();
        int aux = 21 - ticket_string.length();
        for(int i = 0; i < aux; i++){
            ticket_string = ticket_string + " ";
        }
        ticket_string = ticket_string + "|" + t.origin;
        aux = 42 - ticket_string.length();
        for(int i = 0; i < aux; i++){
            ticket_string = ticket_string + " ";
        }
        ticket_string = ticket_string + "|" + t.destination;
        aux = 63 - ticket_string.length();
        for(int i = 0; i < aux; i++){
            ticket_string = ticket_string + " ";
        }
        ticket_string = ticket_string + "|" + t.departure_date;
        aux = 82 - ticket_string.length();
        for(int i = 0; i < aux; i++){
            ticket_string = ticket_string + " ";
        }
        ticket_string = ticket_string + "|" + t.return_date;
        aux = 105 - ticket_string.length();
        for(int i = 0; i < aux; i++){
            ticket_string = ticket_string + " ";
        }
        ticket_string = ticket_string + "|" + t.n_people.toString();
        
        return ticket_string;
    }
    
    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public final String[] get_lodges() throws RemoteException {
        int aux = lodges.size();
        if(aux == 0){
            return null;
        }
        String[] lodges_string = new String[aux];
        
        for(int i = 0; i < aux; i++){
            lodges_string[i] = lodge_to_s(lodges.get(i));
        }
        
        return lodges_string;
    }
    
    public final String lodge_to_s(Lodge l){
        String lodge_string = "|" + l.destination;
        int aux = 21 - lodge_string.length();
        for(int i = 0; i < aux; i++){
            lodge_string = lodge_string + " ";
        }
        lodge_string = lodge_string + "|" + l.checkin_date;
        aux = 42 - lodge_string.length();
        for(int i = 0; i < aux; i++){
            lodge_string = lodge_string + " ";
        }
        lodge_string = lodge_string + "|" + l.checkout_date;
        aux = 63 - lodge_string.length();
        for(int i = 0; i < aux; i++){
            lodge_string = lodge_string + " ";
        }
        lodge_string = lodge_string + "|" + l.n_rooms.toString();
        
        return lodge_string;
    }
    
    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public final String[] get_combos() throws RemoteException {
        int aux = combos.size();
        if(aux == 0){
            return null;
        }
        String[] combos_string = new String[aux];
        
        for(int i = 0; i < aux; i++){
            combos_string[i] = combo_to_s(combos.get(i));
        }
        
        return combos_string;
    }
    
    public final String combo_to_s(Combo c){
        String combo_string = "|" + c.round_trip.toString();
        int aux = 16 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.origin;
        aux = 32 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.destination;
        aux = 48 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.departure_date;
        aux = 62 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.return_date;
        aux = 78 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.n_people.toString();
        aux = 96 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.n_rooms.toString();
        aux = 114 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.checkin_date;
        aux = 130 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        combo_string = combo_string + "|" + c.checkout_date;
        aux = 147 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        boolean foo = c.ticket != null;
        String bar = Boolean.toString(foo);
        combo_string = combo_string + "|" + bar;
        aux = 161 - combo_string.length();
        for(int i = 0; i < aux; i++){
            combo_string = combo_string + " ";
        }
        foo = c.lodge != null;
        bar = Boolean.toString(foo);
        combo_string = combo_string + "|" + bar;
        
        return combo_string;
    }
    
    /**
     *
     * @param cli
     * @param type
     * @param hash
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized String set_interest(InterfaceCli cli, String type, Map hash) throws RemoteException {
        Interest i = new Interest(cli, type, hash);
        interests.add(i);
        return "Interesse registrado com sucesso.";
    }
    
    public static String interest_to_s(Interest i){
        String interest_string = "|" + i.type;
        int aux = 16 - interest_string.length();
        for(int j = 0; j < aux; j++){
            interest_string = interest_string + " ";
        }

        for (Map.Entry<String, String> pair : i.h.entrySet()) {
            interest_string =interest_string + "| " + pair.getKey()+":"+pair.getValue();
        }        
        return interest_string;
    }
    
    public ArrayList<Interest> interests_by_cli_id(InterfaceCli cli){
        ArrayList<Interest> result = new ArrayList<>();
        for(Interest i : interests){
            if(i.cli.equals(cli)){
                result.add(i);
            }
        }
        return result;
    }
    
    /**
     *
     * @param cli
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
    public String[] get_interests(InterfaceCli cli) throws RemoteException {
        ArrayList<Interest> cli_int = interests_by_cli_id(cli);
        int aux = cli_int.size();
        if(aux == 0){
            return null;
        }
        String[] interests_string = new String[aux];
        
        for(int i = 0; i < aux; i++){
            interests_string[i] = interest_to_s(cli_int.get(i));
        }
        
        return interests_string;
        
    }
    
    /**
     *
     * @param cli
     * @param index
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized String remove_interest(InterfaceCli cli, int index) throws RemoteException {
        ArrayList<Interest> cli_int = interests_by_cli_id(cli);
        Interest r = cli_int.get(index);
        interests.remove(r);
        return "Interesse removido com sucesso.";
    }
    
    public void print_interests(){
        int aux = interests.size();
        if(aux == 0){
            return;
        }
        String[] interests_string = new String[aux];
        
        for(int i = 0; i < aux; i++){
            interests_string[i] = interest_to_s(interests.get(i));
        }
        System.out.println("Lista de Interesses:");
        System.out.println("|Tipo do evento |Dados do evento(campo:valor)");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        if(interests_string == null){
            System.out.println("Não existem interesses registrados até o momento.");
            return;
        }
        
        for(String i : interests_string){
            System.out.println(i);
        }
    }
    
    // Classe para facilitar lista de interesses
    public class Interest {
        public InterfaceCli cli;
        public String type;
        public Map<String, String> h = new HashMap<>();
        
        public Interest(InterfaceCli cli, String type, Map hash) {
            this.cli = cli;
            this.type = type;
            this.h = hash;                    
        }
    }
    
}
