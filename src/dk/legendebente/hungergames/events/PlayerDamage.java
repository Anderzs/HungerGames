package dk.legendebente.hungergames.events;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            Game game = HungerGames.getInstance().getGame("default");
            if(game != null){
                if(game.isState(Game.GameState.LOBBY) || game.isState(Game.GameState.STARTING) || game.isState(Game.GameState.ENDING)){
                    event.setCancelled(true);
                }
            }
        }
    }
}
