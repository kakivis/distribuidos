/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloWorld;

import java.util.Map;

/**
 *
 * @author junio
 */
public class Lodge {
    public final String destination;
    public final String checkin_date;
    public final String checkout_date;
    public final Integer n_rooms;
    
    public Lodge(String destination, String checkin_date, String checkout_date, Integer n_rooms){
        this.destination = destination;
        this.checkin_date = checkin_date;
        this.checkout_date = checkout_date;
        this.n_rooms = n_rooms;
    }
    
    public boolean hash_comp(Map<String, String> hash){
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
