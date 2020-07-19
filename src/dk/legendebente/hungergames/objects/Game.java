package dk.legendebente.hungergames.objects;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dk.legendebente.api.Title.Title;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.schedulers.GameCountdownTask;
import dk.legendebente.hungergames.schedulers.GameRunningTask;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class Game {

    //Basis config options
    private String displayName;
    private String gameName = "default";
    private int maxPlayers;
    private int minPlayers;
    private World world;
    private List<Location> spawnPoints;
    private Location lobbyPoint;
    private List<ItemStack> tier1_items;
    private List<ItemStack> tier2_items;
    private List<ItemStack> tier3_items;

    //Aktiv spil info
    private List<Player> players;
    private Set<Player> spectators;
    private Map<Player, Location> playerToSpawnPoint = new HashMap<>();
    public Map<Player, Integer> kills = new HashMap<>();
    private GameState gameState = GameState.LOBBY;
    private Set<Chest> opened;
    private Set<Chest> supplyDrops;
    private HashMap<Chest, Integer> chestTier;
    private boolean movementFrozen = false;
    private Date started;
    private String status;
    private Set<Hologram> holograms;
    private Player winner;

    //File info
    private File configFile;
    private FileConfiguration config;

    private HungerGames instance;
    public Game(HungerGames instance, String gameName){
        configFile = new File(instance.getDataFolder(), "/config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        this.displayName = config.getString("Spil." + gameName + ".displayName");
        this.maxPlayers = config.getInt("Spil." + gameName + ".maxPlayers");
        this.minPlayers = config.getInt("Spil." + gameName + ".minPlayers");

        this.world = Bukkit.getWorld(config.getString("Spil." + gameName + ".worldName"));
        world.setStorm(false);
        world.setTime(6000);

        try{
            double x = config.getDouble("Spil." + gameName + ".lobbyPoint.x");
            double y = config.getDouble("Spil." + gameName + ".lobbyPoint.y");
            double z = config.getDouble("Spil." + gameName + ".lobbyPoint.z");
            System.out.println(world.getName() + " "+  x + " " + y + " " + z);
            this.lobbyPoint = new Location(world, x, y, z);

        } catch(Exception e){
            e.printStackTrace();
        }

        this.spawnPoints = new ArrayList<>();

        for(String point : config.getConfigurationSection("Spil." + gameName + ".spawnPoints").getKeys(false)){
            try{
                double x = config.getDouble("Spil." + gameName + ".spawnPoints." + point + ".x");
                double y = config.getDouble("Spil." + gameName + ".spawnPoints." + point + ".y");
                double z = config.getDouble("Spil." + gameName + ".spawnPoints." + point + ".z");
                Location loc = new Location(world, x, y, z);
                spawnPoints.add(loc);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        this.opened = new HashSet<>();

        this.tier1_items = new ArrayList<>();
        this.tier2_items = new ArrayList<>();
        this.tier3_items = new ArrayList<>();

        if(!(config.getConfigurationSection("Spil.default.items.tier") == null)){
            for(String id : config.getConfigurationSection("Spil.default.items.tier.2").getKeys(false)){
                try{
                    ItemStack item = config.getItemStack("Spil.default.items.tier.2." + id);
                    this.tier2_items.add(item);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        if(!(config.getConfigurationSection("Spil.default.items.tier") == null)){
            for(String id : config.getConfigurationSection("Spil.default.items.tier.1").getKeys(false)){
                try{
                    ItemStack item = config.getItemStack("Spil.default.items.tier.1." + id);
                    this.tier1_items.add(item);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        if(!(config.getConfigurationSection("Spil.default.items.tier") == null)){
            for(String id : config.getConfigurationSection("Spil.default.items.tier.3").getKeys(false)){
                try{
                    ItemStack item = config.getItemStack("Spil.default.items.tier.3." + id);
                    this.tier3_items.add(item);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        this.players = new ArrayList<>();
        this.spectators = new HashSet<>();
        this.status = "Venter på spillere";
        this.holograms = new HashSet<>();
        this.chestTier = new HashMap<>();
    }

    public boolean joinGame(Player player){
        if(isState(GameState.LOBBY)){
            if(this.players.size() >= this.maxPlayers){
                player.sendMessage(ChatHandler.format(instance.prefix + "&cKunne ikke joine, da spillet er fuldt."));
                return false;
            }

            this.players.add(player);
            this.kills.put(player, 0);
            sendToAll("&8[&c&l!&8] &a" + player.getName() + " &7er joined! &8(&e" + players.size() + "&7/&e" + maxPlayers + "&8)");

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(player.getMaxHealth());
            for(Player p : players){
                ScoreboardC.lobbyScoreboard(p, null);
                Tabmanager.updateTablist(p);
            }
            //TabManager.setTabPrefix();

            configFile = new File(HungerGames.getInstance().getDataFolder(), "/config.yml");
            config = YamlConfiguration.loadConfiguration(configFile);

            player.teleport(new Location(Bukkit.getWorld(config.getString("Spil.default.worldName")), config.getDouble("Spil.default.lobbyPoint.x"), config.getDouble("Spil.default.lobbyPoint.y"), config.getDouble("Spil.default.lobbyPoint.z")));
            if(players.size() == this.minPlayers && !isState(GameState.STARTING)){
                setState(GameState.STARTING);
                //sendToAll("&8[&c&l!&8] &7Spillet starter om &e30 sekunder&7...");
                this.status = "Starter spillet";
                for(Player p : players){
                    Tabmanager.updateTablist(p);
                }
                startCountdown();
            }

            HungerGames.getInstance().setGame(player, this);
            return true;
        } else {
            player.sendMessage(ChatHandler.format( "&8[&c&l!&8] &7Du er blevet spectator"));
            HungerGames.getInstance().setGame(player, this);
            activateSpectatorSettings(player);
            return true;
        }
    }

    public void activateSpectatorSettings(Player player){
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SPECTATOR);
        Title spec = new Title();
        spec.send(player, ChatHandler.format("&6&lSPECTATOR"), ChatHandler.format("&7Du er blevet spectator!"), 10, 20, 10);
        switchToSpectator(player);
    }

    public void switchToSpectator(Player player){
        this.players.remove(player);
        getSpectators().add(player);
    }

    public void assignSpawnPositions(){
        int id = 0;
        for(Player player : players){
            try{
                playerToSpawnPoint.put(player, spawnPoints.get(id));
                player.teleport(spawnPoints.get(id));
                id += 1;
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(player.getMaxHealth());
                player.removePotionEffect(PotionEffectType.SATURATION);
            } catch(IndexOutOfBoundsException ex){
                HungerGames.getInstance().getLogger().severe("Kunne ikke sætte alle spawns!!");
            }
        }
    }

    public Set<Player> getSpectators(){
        return spectators;
    }

    public void startCountdown(){
        new GameCountdownTask(this).runTaskTimer(HungerGames.getInstance(), 0, 20);
    }

    public void setMovementFrozen(boolean movementFrozen){
        this.movementFrozen = movementFrozen;
    }

    public void sendToAll(String msg){
        for(Player player : players){
            player.sendMessage(ChatHandler.format(msg));
        }
    }

    public boolean stopGame(){
        if(this.isState(GameState.LOBBY) || this.isState(GameState.STARTING)){
            return false;
        }
        setStatus("Venter på spillere");
        setState(GameState.LOBBY);
        setMovementFrozen(false);
        Bukkit.getScheduler().cancelTasks(HungerGames.getInstance());
        for(Player p : Bukkit.getOnlinePlayers()){
            p.teleport(new Location(Bukkit.getWorld(config.getString("Spil.default.worldName")), config.getDouble("Spil.default.lobbyPoint.x"), config.getDouble("Spil.default.lobbyPoint.y"), config.getDouble("Spil.default.lobbyPoint.z")));
            p.sendMessage(ChatHandler.format("&8[&c&l!&8] &cSpillet er blevet stoppet!"));
            p.setGameMode(GameMode.ADVENTURE);
            p.setHealth(p.getMaxHealth());
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            ScoreboardC.lobbyScoreboard(p, null);
            Tabmanager.updateTablist(p);
        }
        for(Player player : players){
            player.sendMessage(ChatHandler.format(HungerGames.getInstance().prefix + "&7Du er blevet fjernet fra spillet!"));
        }
        players.clear();
        removeHolograms();
        this.opened.clear();
        HungerGames.getInstance().runningTask.removeWB();
        GameRunningTask.removeWB();

        return true;
    }

    public void startDeathmatch(){
        int id = 0;
        for(Player player : getPlayersLeft()){
            try{
                player.teleport(playerToSpawnPoint.get(player));
                id += 1;
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                Tabmanager.updateTablist(player);
            } catch(IndexOutOfBoundsException ex){
                HungerGames.getInstance().getLogger().severe("Kunne ikke sætte alle spawns!!");
            }
        }
        setMovementFrozen(true);
        setState(GameState.DEATHMATCH);
        setStatus("&eDeathmatch");
        Bukkit.getWorld("world").setTime(14000);
        HungerGames.getInstance().deathmatchTask.runTaskTimer(HungerGames.getInstance(), 0, 20);
        HungerGames.getInstance().runningTask.initializeWB();
    }

    public void removeHolograms(){
        for(Hologram holo : holograms){
            if(!holo.isDeleted()){
                holo.delete();
            }
        }
        holograms.clear();
    }

    public void refillChests(){
        sendToAll("&8[&c&l!&8] &7Chests er blevet refilled!");
        getOpened().clear();
    }

    public Set<Chest> getOpened(){
        return this.opened;
    }

    public List<ItemStack> getTier1_items(){
        return tier1_items;
    }

    public List<ItemStack> getTier2_items(){
        return tier2_items;
    }

    public List<ItemStack> getTier3_items(){
        return tier3_items;
    }

    public int getKills(Player player){
        return kills.get(player);
    }

    public void setStarted(Date date){
        this.started = date;
    }

    public Date getStarted(){
        return this.started;
    }

    public int getMaxPlayers(){
        return this.maxPlayers;
    }

    public int getMinPlayers(){
        return this.minPlayers;
    }

    public List<Player> getPlayersLeft(){
        return this.players;
    }

    public void removePlayersLeft(Player player){
        this.players.remove(player);
    }

    public boolean isMovementFrozen(){
        return movementFrozen;
    }

    public boolean isState(GameState state){
        return getGameState() == state;
    }

    public GameState getGameState(){
        return this.gameState;
    }

    public void setState(GameState state){
        this.gameState = state;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public String getName(){
        return this.gameName;
    }

    public HashMap<Chest, Integer> getChestTier(){
        return this.chestTier;
    }

    public Player getPlayer(Player target){
        for(Player player : players){
            if(player.equals(target)){
                return player;
            }
        }
        return null;
    }

    public String getStatus(){
        return this.status;
    }

    public void setStatus(String status){
        this.status = status;
        for(Player p : Bukkit.getOnlinePlayers()){
            Tabmanager.updateTablist(p);
        }
    }

    public Set<Hologram> getHolograms(){
        return this.holograms;
    }

    public Set<Chest> getSupplyDrops(){
        return this.supplyDrops;
    }

    public File getConfigFile(){
        return new File(HungerGames.getInstance().getDataFolder(), "/config.yml");
    }

    public Player getWinner(){
        return this.winner;
    }

    public void setWinner(Player winner){
        this.winner = winner;
    }


    public enum GameState {
        LOBBY, STARTING, ACTIVE, DEATHMATCH, ENDING
    }

}
