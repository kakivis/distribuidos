/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloWorld;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author junio
 */
public class Combo {

    public final Boolean round_trip;
    public final String origin;
    public final String destination;
    public final String departure_date;
    public final String return_date;
    public final Integer n_people;
    public final Integer n_rooms;
    public final String checkin_date;
    public final String checkout_date;
    public Ticket ticket = null;
    public Lodge lodge = null;
    
    public Combo(Boolean round_trip, String origin, String destination, String departure_date, String return_date, Integer n_people, Integer n_rooms, String checkin_date, String checkout_date){
        this.round_trip = round_trip;
        this.origin = origin;
        this.destination = destination;
        this.departure_date = departure_date;
        this.return_date = return_date;
        this.n_people = n_people;
        this.n_rooms = n_rooms;
        this.checkin_date = checkin_date;
        this.checkout_date = checkout_date;
    }
    
    public boolean set_ticket(Ticket t){
        if(Objects.equals(t.departure_date, this.departure_date) && Objects.equals(t.round_trip, this.round_trip) && Objects.equals(t.origin, this.origin) && Objects.equals(t.destination, this.destination) && Objects.equals(t.return_date, this.return_date) && Objects.equals(t.n_people, this.n_people)){
            this.ticket = t;
            return true;
        }
        return false;
    }
    
    public boolean set_lodge(Lodge l){
        if(Objects.equals(l.destination, this.destination) && Objects.equals(l.checkin_date, this.checkin_date) && Objects.equals(l.checkout_date, this.checkout_date) && Objects.equals(l.n_rooms, this.n_rooms)){ 
            this.lodge = l;
            return true;
        }
        return false;
    }
    
    public Boolean check_existance(){
        return (this.lodge != null && this.ticket != null);
    }
    
    public boolean hash_comp(Map<String, String> hash){
        if (!(hash.get("round_trip").equals("s") && round_trip.equals(true)))
            return false;
        if (!(hash.get("origin").equals(origin)))
            return false;
        if(!(hash.get("destination").equals(destination)))
            return false;
        if(!(hash.get("departure_date").equals(departure_date)))
            return false;
        if(!(hash.get("return_date").equals(return_date)))
            return false;
        if(!(hash.get("n_people").equals(n_people.toString())))
            return false;
        if(!(hash.get("destination").equals(destination)))
            return false;
        if(!(hash.get("checkin_date").equals(checkin_date)))
            return false;
        if(!(hash.get("checkout_date").equals(checkout_date)))
            return false;
        if(!(hash.get("n_rooms").equals(n_rooms.toString())))
            return false;
        
        return true;
    }
}
