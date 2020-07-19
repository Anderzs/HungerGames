package dk.legendebente.hungergames.events;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        Game game = HungerGames.getInstance().getGame(player);
        if(game != null){
            if(game.isMovementFrozen()){
                if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()){
                    player.teleport(event.getFrom());
                }
            }
        }
    }

}
