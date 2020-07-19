package dk.legendebente.hungergames;

import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.commands.stdCommand;
import dk.legendebente.hungergames.events.*;
import dk.legendebente.hungergames.handlers.PlayerHandler;
import dk.legendebente.hungergames.objects.Game;
import dk.legendebente.hungergames.schedulers.GameDeathmatchTask;
import dk.legendebente.hungergames.schedulers.GameEndingTask;
import dk.legendebente.hungergames.schedulers.GameRunningTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HungerGames extends JavaPlugin {

    private static HungerGames instance;
    private Set<Game> games = new HashSet<>();
    public String prefix = "&2&lJagten &8» ";
    public PlayerHandler handler;
    public GameRunningTask runningTask;
    public GameDeathmatchTask deathmatchTask;
    public GameEndingTask endingTask;

    private Map<Player, Game> playerGameMap = new HashMap<>();

    @Override
    public void onEnable(){
        instance = this;
        if(!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
            getLogger().severe("HolographicDisplays er ikke installeret");
            getLogger().severe("Stopper plugin");
            this.setEnabled(false);
            return;
        }
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "/config.yml");
        if(!configFile.exists()){
            try{
                configFile.createNewFile();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        File playerFolder = new File(getDataFolder(), "/Stats/");
        if(!playerFolder.exists()){
            playerFolder.mkdir();
        }

        Game game = new Game(this, "default");
        boolean status = this.registerGame(game);
        if(!status){
            getLogger().warning("Kunne ikke loade spillet!");
        }

        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamage(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new FoodLevel(), this);
        getServer().getPluginManager().registerEvents(new EntitySpawn(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(), this);
        getServer().getPluginManager().registerEvents(new ChestInteract(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);
        getServer().getPluginManager().registerEvents(new Weather(), this);
        getServer().getPluginManager().registerEvents(new ChatEvent(), this);
        new stdCommand(this);
        this.handler = new PlayerHandler(this);
        this.runningTask = new GameRunningTask();
        this.deathmatchTask = new GameDeathmatchTask();
        this.endingTask = new GameEndingTask();

        Bukkit.getConsoleSender().sendMessage(ChatHandler.format(this.prefix + "&aHungerGames er startet!"));
    }

    public static HungerGames getInstance(){
        return instance;
    }

    public Game getGame(String gameName){
        for(Game game : games){
            if(game.getName().equalsIgnoreCase(gameName)){
                return game;
            }
        }
        return null;
    }

    public boolean registerGame(Game game){
        games.add(game);
        return true;
    }

    public Game getGame(Player player){
        return this.playerGameMap.get(player);
    }

    public Set<Game> getGames(){
        return games;
    }

    public void setGame(Player player, Game game){
        if(game == null){
            this.playerGameMap.remove(player);
        } else {
            this.playerGameMap.put(player, game);
        }
    }

    private Location lobbyPoint = null;
    public Location getLobbyPoint(){
        if(lobbyPoint == null){
            int x = 0;
            int y = 0;
            int z = 0;
            String world = "world";

            try{
                x = getConfig().getInt("lobby-point.x");
                y = getConfig().getInt("lobby-point.y");
                z = getConfig().getInt("lobby-point.z");
                world = getConfig().getString("lobby-point.world");
            } catch(Exception e){
                e.printStackTrace();
            }
            lobbyPoint = new Location(Bukkit.getWorld(world), x, y, z);
        }
        return lobbyPoint;
    }

}
