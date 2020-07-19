package dk.legendebente.hungergames.events;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        Game game = HungerGames.getInstance().getGame("default");
        if(game != null){
            if(!game.isState(Game.GameState.LOBBY) && !game.isState(Game.GameState.STARTING)){
                if(game.getPlayersLeft().contains(player)){
                    game.getPlayersLeft().remove(player);
                    Bukkit.broadcastMessage(ChatHandler.format("&8[&c&l!&8] &c" + player.getName() + " &7er smuttet! &8- &6" + game.getPlayersLeft().size() + " &6spillere tilbage"));
                } else {
                    Bukkit.broadcastMessage(ChatHandler.format("&8[&c-&8] &c" + player.getName()));
                }
            } else {
                if(game.getPlayersLeft().contains(player)){
                    game.getPlayersLeft().remove(player);
                }
                Bukkit.broadcastMessage(ChatHandler.format("&8[&c-&8] &c" + player.getName()));
            }
        }
    }

}
