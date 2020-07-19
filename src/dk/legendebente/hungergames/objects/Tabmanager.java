package dk.legendebente.hungergames.objects;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class Tabmanager {

    public static void updateTablist(Player player){
        Game game = HungerGames.getInstance().getGame("default");
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;

        IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"       §6§lHUNGER GAMES       \n \"}");
        IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a(ChatHandler.format("{\"text\": \"\n     &7Status: &e" + game.getStatus() + "     \"}"));

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(tabHeader);

        try{
            Field f = packet.getClass().getDeclaredField("b");
            f.setAccessible(true);
            f.set(packet, tabFooter);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            conn.sendPacket(packet);
        }
    }

}
