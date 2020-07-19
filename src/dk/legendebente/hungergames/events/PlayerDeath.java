package dk.legendebente.hungergames.events;

import dk.legendebente.api.Title.Title;
import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import dk.legendebente.hungergames.objects.Game;
import dk.legendebente.hungergames.objects.ScoreboardC;
import dk.legendebente.hungergames.objects.Tabmanager;
import javafx.scene.control.Tab;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerDeath implements Listener {

    private Game game = HungerGames.getInstance().getGame("default");
    private Title title = new Title();

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if(event.getEntity() instanceof Player && !(event.getEntity().getKiller() instanceof Player)){
            playerKilledByUnknown(event.getEntity());
        }

        for(Player p : game.getPlayersLeft()){
            if(game != null){
                if(game.isState(Game.GameState.LOBBY)){
                    ScoreboardC.lobbyScoreboard(p, null);
                } else {
                    ScoreboardC.updateScoreboard(p);
                }
            }
            Tabmanager.updateTablist(p);
        }

        if(game != null){ ;
            handle(event, game);
        }
    }

    public void handle(PlayerDeathEvent event, Game game){
        Player player = event.getEntity();

        if(!game.isState(Game.GameState.ACTIVE) && !game.isState(Game.GameState.DEATHMATCH)){
            return;
        }
        String killer = "";
        if(event.getEntity().getKiller().equals(null)){
            killer = "at logge ud";
        } else {
            killer = event.getEntity().getKiller().getName();
            game.kills.put(event.getEntity().getKiller(), game.kills.get(event.getEntity().getKiller()) + 1);
        }
        event.setDeathMessage(null);
        game.activateSpectatorSettings(player);

        if(game.getPlayersLeft().size() <= 1 && !(game.isState(Game.GameState.LOBBY)) && (!game.isState(Game.GameState.STARTING))){
            playerWonGame(game.getPlayersLeft().get(0));
        } else {
            Bukkit.broadcastMessage(ChatHandler.format("&8[&c&l!&8] &c" + player.getName() + " &7er blevet dræbt af &c" + killer + "&7! &8- &e" + (game.getPlayersLeft().size() - 1) + " &6spillere tilbage"));
            //event.setDeathMessage(ChatHandler.format("&8[&c&l!&8] &c" + player.getName() + " &7er blevet dræbt af &c" + killer + "&7! &8- &e" + (game.getPlayersLeft().size() - 1) + " &6spillere tilbage"));
            game.activateSpectatorSettings(player);
            updateScoreboard();
        }
    }

    public void updateScoreboard(){
        Game game = HungerGames.getInstance().getGame("default");
        for(Player p : game.getPlayersLeft()){
            ScoreboardC.updateScoreboard(p);
        }
    }

    public void updateTablist(){
        Game game = HungerGames.getInstance().getGame("default");
        for(Player p : game.getPlayersLeft()){
            Tabmanager.updateTablist(p);
        }
    }

    public void playerWonGame(Player player){
        try{
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 10, 1);
            }
            Player winner = player;
            game.setWinner(winner);
            HungerGames.getInstance().handler.setWins(winner, HungerGames.getInstance().handler.getWins(winner) + 1);
            title.send(winner, ChatHandler.format("&6&lVICTORY"), ChatHandler.format("&7Du har vundet &6&lHUNGER GAMES&7!"), 10, 30, 10);
            Bukkit.broadcastMessage(ChatHandler.format("&8[&c&l!&8] &7Spilleren &a" + winner.getName() + " &7har vundet &6&lHUNGER GAMES&7!"));
            game.setState(Game.GameState.ENDING);
            updateScoreboard();
            updateTablist();
            Bukkit.getScheduler().cancelTasks(HungerGames.getInstance());
            HungerGames.getInstance().endingTask.runTaskTimer(HungerGames.getInstance(), 0, 20);
        } catch(IndexOutOfBoundsException ex) {}
    }

    public void playerKilledByUnknown(Player player){
        game.activateSpectatorSettings(player);
        Bukkit.broadcastMessage(ChatHandler.format("&8[&c&l!&8] &c" + player.getName() + " &7er blevet dræbt &caf sig selv&7! &8- &e" + (game.getPlayersLeft().size()) + " &6spillere tilbage"));
        updateScoreboard();
    }

}
