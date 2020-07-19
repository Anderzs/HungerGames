package dk.legendebente.hungergames.schedulers;

import dk.legendebente.api.ActionBar;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import dk.legendebente.hungergames.objects.ScoreboardC;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountdownTask extends BukkitRunnable {

    private int time = 31;
    private Game game;
    ActionBar ac;

    public GameCountdownTask(Game game){
        this.game = game;
    }

    @Override
    public void run() {
        time -= 1;
        ac = new ActionBar(ChatHandler.format("&8[&c&l!&8] &7Teleportering starter om &6" + time + " sekund" + (time == 1 ? "&7!" : "&6er!")));

        if(time == 0){
            cancel();
            for(Player p : game.getPlayersLeft()){
                ScoreboardC.updateScoreboard(p);
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 5, 1);
            }
            new GameRunTask(game).runTaskTimer(HungerGames.getInstance(), 0, 20);
        } else {
            for(Player p : game.getPlayersLeft()){
                ScoreboardC.lobbyScoreboard(p, time);
                ac.sendToPlayer(p);
            }
            if(time == 30 || time == 25 || time == 20 || time == 15 || time == 10 || time <= 5){
                if(time <= 5){
                    for(Player p : game.getPlayersLeft()){
                        p.playSound(p.getLocation(), Sound.NOTE_PLING, 5, 1);
                    }
                }
                game.sendToAll("&8[&c&l!&8] &7Teleportering starter om &6" + time + " sekund" + (time == 1 ? "&7!" : "&6er&7!"));
            }
        }
    }
}
