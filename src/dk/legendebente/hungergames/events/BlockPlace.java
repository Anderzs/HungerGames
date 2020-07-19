package dk.legendebente.hungergames.events;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    private Game game = HungerGames.getInstance().getGame("default");

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(!game.isState(Game.GameState.LOBBY)){
            event.setCancelled(true);
        }
    }

}
