package dk.legendebente.hungergames.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawn implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent event){
        if(!event.getEntityType().equals(EntityType.DROPPED_ITEM)){
            event.setCancelled(true);
        }
    }

}
