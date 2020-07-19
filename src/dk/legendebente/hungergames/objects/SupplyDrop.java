package dk.legendebente.hungergames.objects;

import dk.legendebente.hungergames.handlers.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;


public class SupplyDrop {

    //Supplydrop variabler
    private Location location;
    private Chest chest;
    private ItemStack[] contents;

    public SupplyDrop(Location location){
        this.location = location;
    }

    public Location getLocation(){
        return this.location;
    }

    public void sendToAll(){
        Bukkit.getServer().broadcastMessage(ChatHandler.format("&8[&c&l!&8] &6Supply drop &7er landet!"));
        Bukkit.getServer().broadcastMessage(ChatHandler.format("&8[&c&l!&8] &7Koordinater: &6X: " + (int) getLocation().getX() + ", Y: " + (int) getLocation().getY() + ", Z:" + (int) getLocation().getZ()));
    }

    public void spawnSupplyDrop(){
        Bukkit.getWorld("world").getBlockAt(location).setType(Material.CHEST);
        Chest chest1 = (Chest) location.getBlock().getState();
        chest1.getBlockInventory().setContents(getContents());
    }

    public void setContents(ItemStack[] contents){
        this.contents = contents;
    }

    public ItemStack[] getContents(){
        return this.contents;
    }


}
