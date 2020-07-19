package dk.legendebente.hungergames.schedulers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import dk.legendebente.api.ActionBar;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import dk.legendebente.hungergames.objects.ScoreboardC;
import dk.legendebente.hungergames.objects.Tabmanager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunningTask extends BukkitRunnable {


    Game game = HungerGames.getInstance().getGame("default");
    public int chestRefill = 11; //420 sekunder == 7 minutter
    public int deathmatch = 21; //1200 sekunder == 20 minutter
    int secondsPassed;
    private ActionBar ac;
    private static WorldBorder wb = Bukkit.getWorld("world").getWorldBorder();

    @Override
    public void run() {
        secondsPassed += 1;
        chestRefill -= 1;
        deathmatch -= 1;
        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardC.updateScoreboard(p);
        }

        if(chestRefill == 0){
            for(Hologram holo : game.getHolograms()){
                holo.insertTextLine(1, ChatHandler.format("&a&lREFILLED!"));
                holo.getLine(2).removeLine();
            }
            game.refillChests();
        } else {
            if((!game.getHolograms().isEmpty() || !game.getHolograms().equals(null)) && chestRefill > 0){
                int[] left = timeLeft(chestRefill);
                for(Hologram holo : game.getHolograms()){
                    holo.insertTextLine(1, ChatHandler.format("&7Refill: &6" + left[1] + "m, " + left[2] + "s"));
                    holo.getLine(2).removeLine();
                }
            } else if(chestRefill <= 0){
                for(Hologram holo : game.getHolograms()){
                    holo.insertTextLine(1, ChatHandler.format("&7Kisten er &arefilled"));
                    holo.getLine(2).removeLine();
                }
            }
        }

        if(deathmatch == 600 || deathmatch == 300 || deathmatch == 240 || deathmatch == 180 || deathmatch == 120 || deathmatch == 60){
            broadcastDeathmatchMinutes(deathmatch);
        }

        if(deathmatch == 30 || deathmatch == 20 || deathmatch == 10 || (deathmatch <= 5 && deathmatch > 0)){
            broadcastDeathmatchSeconds(deathmatch);
        }

        if(deathmatch == 0){
            game.startDeathmatch();
        }


    }

    public void broadcastDeathmatchMinutes(int seconds){
        int[] left = timeLeft(seconds);
        for(Player player : game.getPlayersLeft()){
           player.sendMessage(ChatHandler.format("&8[&c&l!&8] &7Deathmatch starter om: &6" + left[1] + " minut" + (left[1] == 1 ? "&7!" : "&6ter&7!")));
        }
    }

    public void broadcastDeathmatchSeconds(int seconds){
        ac = new ActionBar(ChatHandler.format("&8[&c&l!&8] &7Deathmatch starter om &6" + seconds + " sekund" + (seconds == 1 ? "&7" : "&6er")));
        for(Player player : game.getPlayersLeft()){
            if(seconds <= 5){
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 10, 1);
                ac.sendToPlayer(player);
            }
            player.sendMessage(ChatHandler.format("&8[&c&l!&8] &7Deathmatch starter om: &6" + seconds + " sekund" + (seconds == 1 ? "&7!" : "&6er&7!")));
        }
    }

    private long convertToMilli(long seconds){
        return seconds*1000;
    }

    private int[] timeLeft(long sec){
        int seconds = (int) ((convertToMilli(sec) / 1000) % 60);
        int minutes = (int) ((convertToMilli(sec) / (1000*60)) % 60);
        int hours   = (int) ((convertToMilli(sec) / (1000*60*60)) % 24);

        return new int[]{hours, minutes, seconds};
    }

    public static void initializeWB(){
        wb.setCenter(new Location(Bukkit.getWorld("world"), 1.0, 2.0, 3.0));
        wb.setSize(85);
        wb.setDamageAmount(2.5);
    }

    public static void removeWB(){
        wb.reset();
    }



}
