package dk.legendebente.hungergames.events;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(!HungerGames.getInstance().handler.playerExists(player)){
            HungerGames.getInstance().handler.createPlayer(player);
        }
        event.setJoinMessage(null);
        Game game = HungerGames.getInstance().getGame("default");
        if(game.isState(Game.GameState.LOBBY) || player.isOp()){
            if(!game.getPlayersLeft().contains(player)){
                game.joinGame(player);
            }
        } else {
            player.kickPlayer(ChatHandler.format(HungerGames.getInstance().prefix + "&7Spillet er igang, du kan ikke joine!"));
        }
    }

    @EventHandler
    public void onCheck(AsyncPlayerPreLoginEvent event){
        Game game = HungerGames.getInstance().getGame("default");
        if(!game.isState(Game.GameState.LOBBY)){
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, ChatHandler.format(HungerGames.getInstance().prefix + "&7Spillet er igang, du kan ikke joine!"));
        }

    }

}
