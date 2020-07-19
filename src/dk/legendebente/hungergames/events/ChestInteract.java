package dk.legendebente.hungergames.events;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Random;

public class ChestInteract implements Listener {

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event){
        Player player = event.getPlayer();

        Game game = HungerGames.getInstance().getGame("default");
        if(game != null){
            if(game.isState(Game.GameState.LOBBY) || game.isState(Game.GameState.STARTING)){
                event.setCancelled(true);
                return;
            }

            handle(event, game);
        }
    }

    private void handle(PlayerInteractEvent event, Game game){
        if(event.hasBlock() && event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Chest){
            Chest chest = (Chest) event.getClickedBlock().getState();

            if(game.getOpened().contains(chest)){
                return;
            }
            chest.getInventory().clear();
            int tier = 100;
            String tierN = "";
            if((!game.getChestTier().isEmpty()) && !(game.getOpened().contains(chest)) && !(game.getChestTier().get(chest) == null)){
                tier = game.getChestTier().get(chest);
            } else {
                tier = new Random().nextInt(99) + 1;
            }

            if(tier <= 15){
                tierN = "&c&lTIER 1";
            } else if(tier <= 50){
                tierN = "&e&lTIER 2";
            } else{
                tierN = "&7&lTIER 3";
            }
            if(game.getChestTier().get(chest) != null){

            } else {
                game.getChestTier().put(chest, tier);
            }

            int toFill;
            int randomSlot;
            while(getItems(chest.getBlockInventory()) < 4){
                toFill = new Random().nextInt(5);
                for(int x = 0; x < toFill; x++){
                    randomSlot = new Random().nextInt(chest.getInventory().getSize());
                    if(tier <= 15){
                        int selected = new Random().nextInt(game.getTier1_items().size());
                        chest.getBlockInventory().setItem(randomSlot, game.getTier1_items().get(selected));
                    } else if(tier <= 50){
                        int selected = new Random().nextInt(game.getTier2_items().size());
                        chest.getBlockInventory().setItem(randomSlot, game.getTier2_items().get(selected));
                    } else {
                        int selected = new Random().nextInt(game.getTier3_items().size());
                        chest.getBlockInventory().setItem(randomSlot, game.getTier3_items().get(selected));
                    }
                }
            }

            if(!game.getChestTier().isEmpty()){
                if(game.getChestTier().get(chest) != null){
                    createHologram(chest, tierN);
                }
            }

            game.getOpened().add(chest);
        }
    }

    public void createHologram(Chest chest, String tierN){
        Game game = HungerGames.getInstance().getGame("default");
        Location loc = new Location(chest.getLocation().getWorld(), chest.getLocation().getX(), chest.getY(), chest.getZ());
        Hologram holo = HologramsAPI.createHologram(HungerGames.getInstance(), loc.add(0.5, 2.5, 0.5));
        holo.appendTextLine(ChatHandler.format(tierN));
        holo.appendTextLine(ChatHandler.format("&7Loader..."));
        game.getHolograms().add(holo);
    }

    public boolean isInvEmpty(Inventory inv){
        for(ItemStack item : inv.getContents()){
            if(item != null){
                return false;
            }
        }
        return true;
    }

    public int getItems(Inventory inv){
        int count = 0;
        for(ItemStack item : inv.getContents()){
            if(item != null){
                count += 1;
            }
        }

        return count;
    }

}
