package dk.legendebente.hungergames.schedulers;

import dk.legendebente.api.ActionBar;
import dk.legendebente.api.Title.Title;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameDeathmatchTask extends BukkitRunnable {

    private int frozenTime = 11;
    private Game game = HungerGames.getInstance().getGame("default");
    private ActionBar ac;


    @Override
    public void run() {
        frozenTime -= 1;
        if(frozenTime <= 5 && frozenTime > 0){
            Title t = new Title();
            for (Player p : game.getPlayersLeft()){
                t.send(p, ChatHandler.format("&6&lDEATHMATCH"), ChatHandler.format("&7Starter om &6" + frozenTime + " sekund" + (frozenTime == 1 ? "&7" : "&6er")), 0, 20, 0);
            }
        } else if(frozenTime == 0){
            Title t = new Title();
            for (Player p : game.getPlayersLeft()){
                t.send(p, ChatHandler.format("&6&lDEATHMATCH"), ChatHandler.format("&7Deathmatch er startet!"), 0, 10, 0);
            }
            game.setMovementFrozen(false);
        }
    }

    private void broadcastSecondsLeft(int seconds){
        for(Player player : game.getPlayersLeft()){
            player.sendMessage(ChatHandler.format("&8[&c&l!&8] &7Deathmatch starter om &6" + seconds + " sekund" + (frozenTime == 1 ? "&7" : "&6er")));
        }
    }


}
