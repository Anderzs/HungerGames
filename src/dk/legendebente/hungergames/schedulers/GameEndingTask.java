package dk.legendebente.hungergames.schedulers;

import dk.legendebente.api.ActionBar;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import dk.legendebente.hungergames.objects.ScoreboardC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class GameEndingTask extends BukkitRunnable {

    private Game game = HungerGames.getInstance().getGame("default");
    private File configFile = game.getConfigFile();
    private FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    private ActionBar ac;
    int time = 10;
    int kickTime = 120;

    @Override
    public void run() {
        time -= 1;
        kickTime -= 1;
        if(time == 0){
            HungerGames.getInstance().runningTask.removeWB();
            Location loc = new Location(Bukkit.getWorld(config.getString("Spil.default.worldName")), config.getDouble("Spil.default.lobbyPoint.x"), config.getDouble("Spil.default.lobbyPoint.y"), config.getDouble("Spil.default.lobbyPoint.z"));
            for(Player p : Bukkit.getOnlinePlayers()){
                p.teleport(loc);
                p.sendMessage(ChatHandler.format(HungerGames.getInstance().prefix + "&7Du er blevet teleporteret til lobbyen"));
                ScoreboardC.kickScoreboard(p, kickTime);
            }
            game.setStatus("Venter på spillere");
        } else if(time < 0){
            for(Player p : Bukkit.getOnlinePlayers()){
                ScoreboardC.kickScoreboard(p, kickTime);
            }
        }
        if(kickTime == 60 || kickTime == 30 || kickTime == 20 || kickTime == 10 || kickTime <= 5){
            ac = new ActionBar(ChatHandler.format("&8[&c&l!&8] &7Serveren genstarter om &6" + kickTime + " sekund" + (kickTime == 1 ? "" : "er")));
            ac.sendToAll();
            Bukkit.broadcastMessage(ChatHandler.format("&8[&c&l!&8] &7Serveren genstarter om &6" + kickTime + " sekund" + (kickTime == 1 ? "" : "er")));
        }

        if(kickTime == 0){
            for(Player p : Bukkit.getOnlinePlayers()){
                p.kickPlayer(ChatHandler.format(HungerGames.getInstance().prefix + "&7Tak for din deltagelse \n &7Kig på vores discord om der kommer flere games!"));
            }
            this.cancel();
            stopServer();
        }

    }

    boolean stopServer(){
        HungerGames.getInstance().getLogger().info("[HungerGames] Stopper serveren...");
        try{
            HungerGames.getInstance().getServer().dispatchCommand(HungerGames.getInstance().getServer().getConsoleSender(), "save-all");
            HungerGames.getInstance().getServer().dispatchCommand(HungerGames.getInstance().getServer().getConsoleSender(), "restart");
        }  catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
