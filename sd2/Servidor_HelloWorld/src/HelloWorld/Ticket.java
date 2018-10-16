/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloWorld;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author junio
 */
public class Ticket {

    public final Boolean round_trip;
    public final String origin;
    public final String destination;
    public final String departure_date;
    public final String return_date;
    public final Integer n_people;
    
    
    
    
    public Ticket(Boolean round_trip, String origin, String destination, String departure_date, String return_date, Integer n_people){
        this.round_trip = round_trip;
        this.origin = origin;
        this.destination = destination;
        this.departure_date = departure_date;
        this.return_date = return_date;
        this.n_people = n_people;
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
        
        return true;
    }
}
