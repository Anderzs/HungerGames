package dk.legendebente.hungergames.handlers;

import org.bukkit.ChatColor;

public class ChatHandler {

    public static String format(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
