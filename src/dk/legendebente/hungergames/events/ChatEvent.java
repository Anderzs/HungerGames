package dk.legendebente.hungergames.events;

import dk.legendebente.hungergames.handlers.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        event.setCancelled(true);
        if(player.isOp() || player.hasPermission("hg.admin")){
            Bukkit.broadcastMessage(ChatHandler.format("&8[&6Staff&8] &6" + player.getName() + " &8» &e") + event.getMessage());
        } else {
            Bukkit.broadcastMessage(ChatHandler.format("&8[&7Spiller&8] &6" + player.getName() + " &8» &7") + event.getMessage());
        }
    }
}
