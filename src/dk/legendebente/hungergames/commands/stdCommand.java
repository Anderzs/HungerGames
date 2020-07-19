package dk.legendebente.hungergames.commands;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import dk.legendebente.hungergames.objects.SupplyDrop;
import dk.legendebente.hungergames.objects.Tabmanager;
import dk.legendebente.hungergames.schedulers.GameRunTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class stdCommand implements CommandExecutor {

    private HungerGames instance;
    private String[] spillere;
    public stdCommand(HungerGames instance){
        this.instance = instance;
        instance.getCommand("hungergames").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("hungergames") || label.equalsIgnoreCase("hg")){
            if(!sender.hasPermission("hg.admin")){
                sender.sendMessage(ChatHandler.format(instance.prefix + "&cDu har ikke adgang til dette!"));
                return true;
            }
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatHandler.format(instance.prefix + "&7Du kan ikke udføre commands i console!"));
                return true;
            }
            Player player = (Player) sender;
            if(args.length <= 0){
                sendCommandList(player);
                return true;
            }

            if(args[0].equalsIgnoreCase("setspawn")){
                Location loc = player.getLocation();
                setLobbySpawn(loc);
                player.sendMessage(ChatHandler.format(instance.prefix + "&aDu har sat lobby spawn!"));
                player.sendMessage(ChatHandler.format(instance.prefix + "&7Lokation: &cX: " + loc.getX() + ", Y: " + loc.getY() + ", Z: " + loc.getY()));
                return true;
            } else if(args[0].equalsIgnoreCase("setpoint")){
                Location loc = player.getLocation();
                setArenaPoint(player, loc, Integer.parseInt(args[1]));
            } else if(args[0].equalsIgnoreCase("additem")){
                ItemStack item = player.getItemInHand();
                if(args[1].equalsIgnoreCase("tier1")){
                    addItem(1, item, Integer.parseInt( args[2]));
                } else if(args[1].equalsIgnoreCase("tier2")){
                    addItem(2, item, Integer.parseInt( args[2]));
                } else if(args[1].equalsIgnoreCase("tier3")){
                    addItem(3, item, Integer.parseInt( args[2]));
                }

                player.sendMessage(ChatHandler.format(instance.prefix + "&7Du har tilføjet en item"));
            } else if(args[0].equalsIgnoreCase("reload")){
                reloadConfig();
                player.sendMessage(ChatHandler.format(instance.prefix + "&7Du har reloadet config"));
            } else if(args[0].equalsIgnoreCase("list")){
                Game game = HungerGames.getInstance().getGame("default");
                int i = 0;
                if(game.getPlayersLeft() == null){
                    player.sendMessage(ChatHandler.format(instance.prefix + "&7Der ingen spillere!"));
                    return true;
                }
                player.sendMessage(ChatHandler.format(instance.prefix + "&7Spillere:"));
                for(Player p : game.getPlayersLeft()){
                    player.sendMessage(ChatHandler.format(" &8&l* &6" + p.getName()));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("forcestart")){
                Game game = HungerGames.getInstance().getGame("default");
                player.sendMessage(ChatHandler.format(instance.prefix + "&7Du har startet spillet!"));

                new GameRunTask(game).runTaskTimer(HungerGames.getInstance(), 0, 20);

            } else if(args[0].equalsIgnoreCase("start")){
                Game game = HungerGames.getInstance().getGame("default");
                player.sendMessage(ChatHandler.format(instance.prefix + "&7Du har startet nedtælling!"));
                game.setState(Game.GameState.STARTING);
                //game.sendToAll("&8[&c&l!&8] &7Spillet starter om &e30 sekunder&7...");
                game.setStatus("Starter spillet");
                for(Player p : game.getPlayersLeft()){
                    Tabmanager.updateTablist(p);
                }
                game.startCountdown();
            } else if(args[0].equalsIgnoreCase("stop")){
                Game game = HungerGames.getInstance().getGame("default");
                if(game.stopGame()){
                    player.sendMessage(ChatHandler.format(instance.prefix + "&7Du har stoppet spillet!"));
                } else {
                    player.sendMessage(ChatHandler.format(instance.prefix + "&7Kunne ikke stoppe spillet!"));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("hologram")){
                Game game = HungerGames.getInstance().getGame("default");
                for(Hologram holo : game.getHolograms()){
                    player.sendMessage(holo.toString());
                }
            } else if(args[0].equalsIgnoreCase("supply")){
                SupplyDrop drop = new SupplyDrop(player.getLocation().add(0, 0, 0));
                ItemStack[] contents = new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD)};
                drop.setContents(contents);
                drop.sendToAll();
                drop.spawnSupplyDrop();
            }

            return true;
        }

        return true;
    }

    public void reloadConfig(){
        File defFile = new File(instance.getDataFolder(), "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(defFile);
        try {
            config.load(defFile);
            config.save(defFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void addItem(int tier, ItemStack item, int id){
        File defFile = new File(instance.getDataFolder(), "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(defFile);
        config.set("Spil.default.items.tier." + tier + "." + id, item);
        try {
            config.save(defFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setArenaPoint(Player player, Location location, int id){
        File defFile = new File(instance.getDataFolder(), "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(defFile);

        config.set("Spil.default.spawnPoints." + id + ".x", location.getX());
        config.set("Spil.default.spawnPoints." + id + ".y", location.getY());
        config.set("Spil.default.spawnPoints." + id + ".z", location.getZ());
        try {
            config.save(defFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage(ChatHandler.format(instance.prefix + "&7Du har sat et arena lokation!"));

    }

    public void setLobbySpawn(Location location){
        File defFile = new File(instance.getDataFolder(), "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(defFile);
        config.set("Spil.default.displayName", "Test Spil");
        config.set("Spil.default.maxPlayers", 30);
        config.set("Spil.default.minPlayers", 10);
        config.set("Spil.default.worldName", location.getWorld().getName());
        config.set("Spil.default.lobbyPoint.x", location.getX());
        config.set("Spil.default.lobbyPoint.y", location.getY());
        config.set("Spil.default.lobbyPoint.z", location.getZ());
        try {
            config.save(defFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommandList(Player player){
        player.sendMessage(ChatHandler.format(instance.prefix + "&aTilgængelige kommandoer: "));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg forcestart &8| &6Forcestart hungergames"));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg start &8| &6Starter hungergames"));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg stop &8| &6Stopper spillet, hvis igang"));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg setspawn &8| &6Sæt lobby spawn"));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg setpoint <id> &8| &6Sæt spawnpoints i arenaen"));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg additem <tier(id)> <id> &8| &6Tilføj item til chest"));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg list &8| &6Liste over spillere i en variabel"));
        player.sendMessage(ChatHandler.format(" &8&l* &7/hg reload &8| &6Reload config"));
    }
}
