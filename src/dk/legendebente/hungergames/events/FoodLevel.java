package dk.legendebente.hungergames.events;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.objects.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FoodLevel implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            Game game = HungerGames.getInstance().getGame(player);
            if(game != null) {
                if(game.isState(Game.GameState.LOBBY) || game.isState(Game.GameState.STARTING)){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 40, 2));
                }
            }
        }
    }

}
