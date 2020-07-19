package dk.legendebente.hungergames.schedulers;

import dk.legendebente.api.ActionBar;
import dk.legendebente.api.Title.Title;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import dk.legendebente.hungergames.objects.ScoreboardC;
import dk.legendebente.hungergames.objects.Tabmanager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class GameRunTask extends BukkitRunnable {

    private Game game;
    private int startIn = 10;
    private Title title;
    private ActionBar ac;
    private static WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();

    public GameRunTask(Game game){
        this.title = new Title();
        this.game = game;
        this.game.setState(Game.GameState.STARTING);
        this.game.assignSpawnPositions();
        this.game.sendToAll("&8[&c&l!&8] &7Du er blevet teleporteret");
        this.game.sendToAll("&8[&c&l!&8] &7Spillet starter om: &6" + this.startIn + " &6sekunder&7...");
        this.game.setMovementFrozen(true);
    }

    @Override
    public void run() {
        if(startIn <= 1){
            ac = new ActionBar(ChatHandler.format("&8[&c&l!&8] &7Spillet er startet"));
            this.cancel();
            this.game.setState(Game.GameState.ACTIVE);
            this.game.sendToAll("&8[&c&l!&8] &aSpillet er startet!");
            Date nu = new Date();
            this.game.setStarted(nu);
            this.game.setMovementFrozen(false);
            this.game.setStatus("Spillet er igang");
            for(Player p : this.game.getPlayersLeft()){
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 1);
                ac.sendToPlayer(p);
                title.send(p, ChatHandler.format("&6&lHUNGER GAMES"), ChatHandler.format("&7Spillet er startet, held og lykke!"), 0, 10, 0);
            }
            this.setWorldBorder();
            //Bukkit.broadcastMessage("Loadede world border");
            HungerGames.getInstance().runningTask.runTaskTimer(HungerGames.getInstance(), 0, 20);
        } else {
            startIn -= 1;
            ac = new ActionBar(ChatHandler.format("&8[&c&l!&8] &7Spillet starter om &6" + startIn + " sekund" + (startIn == 1 ? "&7" : "&6er")));
            if(startIn <= 10 || startIn <= 5){
                for(Player player : game.getPlayersLeft()){
                    if(startIn <= 5){
                        title.send(player, ChatHandler.format("&6&lHUNGER GAMES"), ChatHandler.format("&7Starter om &6" + startIn + " sekund" + (startIn == 1 ? "&7" : "&6er")), 0, 20, 0);
                    }
                    player.playSound(player.getLocation(), Sound.NOTE_STICKS, 10, 1);
                    ac.sendToPlayer(player);
                }
                if(startIn == 10 || startIn <= 5){
                    this.game.sendToAll("&8[&c&l!&8] &7Spillet starter om &6" + startIn + " sekund" + (startIn == 1 ? "&7!" : "&6er&7!"));
                }
            }
        }
    }

    private static void setWorldBorder(){
        wb.setCenter(new Location(Bukkit.getWorld("world"), 0.0, 2.0, 0.0));
        wb.setSize(615);
        wb.setDamageAmount(2.5);
    }


    private static void removeWB(){
        wb.reset();
    }
}
