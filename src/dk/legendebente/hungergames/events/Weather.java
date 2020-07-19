package dk.legendebente.hungergames.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class Weather implements Listener {

    @EventHandler
    public void change(WeatherChangeEvent event){
        event.setCancelled(true);
    }


}
