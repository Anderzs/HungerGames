package dk.legendebente.hungergames.handlers;

import dk.legendebente.hungergames.HungerGames;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerHandler {

    public PlayerHandler(HungerGames instance){

    }

    private File playerFile;
    private FileConfiguration config;

    public int getWins(Player player){
        playerFile = getPlayerFile(player);
        config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getInt("Spiller.Wins");
    }

    public void setWins(Player player, int wins){
        playerFile = getPlayerFile(player);
        config = YamlConfiguration.loadConfiguration(playerFile);
        config.set("Spiller.Wins", wins);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(Player player){
        if(getPlayerFile(player).exists()){
            return true;
        } else {
            return false;
        }
    }

    public void createPlayer(Player player){
        try {
            getPlayerFile(player).createNewFile();
            playerFile = getPlayerFile(player);
            config = YamlConfiguration.loadConfiguration(playerFile);
            config.set("Spiller.Wins", 0);
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private File getPlayerFile(Player player){
        return new File(HungerGames.getInstance().getDataFolder(), "/Stats/" + player.getUniqueId().toString() + ".yml");
    }

}
