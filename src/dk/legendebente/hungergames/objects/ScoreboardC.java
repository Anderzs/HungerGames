package dk.legendebente.hungergames.objects;

import dk.legendebente.hungergames.HungerGames;
import dk.legendebente.hungergames.handlers.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardC {

    private static Scoreboard b = Bukkit.getScoreboardManager().getMainScoreboard();

    public static void updateScoreboard(Player player){
        Game game = HungerGames.getInstance().getGame("default");
        ScoreboardManager m = Bukkit.getScoreboardManager();
        b = m.getNewScoreboard();

        Objective o = b.registerNewObjective("Board", "");
        int[] leftTimeRefill = timeLeft(HungerGames.getInstance().runningTask.chestRefill);
        int[] leftTimeDeathmatch = timeLeft(HungerGames.getInstance().runningTask.deathmatch);
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatHandler.format("   &6&lHUNGER GAMES   "));
        Score spc1 = o.getScore("§a");
        Score name = o.getScore(ChatHandler.format("&7Spiller: &6" + player.getName()));
        Score kills = o.getScore(ChatHandler.format("&7Kills: &6" + game.getKills(player)));
        Score spc3 = o.getScore(ChatHandler.format("§d"));
        Score left = o.getScore(ChatHandler.format("&7Spillere tilbage: &6" + game.getPlayersLeft().size()));
        Score time = o.getScore(ChatHandler.format("&7Tid gået: &6" + getTime(game)[1] + "m, " + getTime(game)[0] + "s"));
        Score spc2 = o.getScore("§b");
        Score chestRefill = o.getScore(ChatHandler.format("&7Chest Refill: &6" + leftTimeRefill[1] + "m, " + leftTimeRefill[2] + "s"));
        Score deathmatch = o.getScore(ChatHandler.format("&7Deathmatch: &6" + leftTimeDeathmatch[1] + "m, " + leftTimeDeathmatch[2] + "s"));
        Score deathmatchIgang = o.getScore(ChatHandler.format("&7Deathmatch: &6Igang"));
        Score spc4 = o.getScore("§5");
        Score jagten = o.getScore(ChatHandler.format("  &7&oLavet af LegendeBente  "));

        if(leftTimeRefill[1] <= 0 && leftTimeRefill[2] <= 0){
            spc1.setScore(9);
            name.setScore(8);
            kills.setScore(7);
            spc3.setScore(6);
            left.setScore(5);
            time.setScore(4);
            spc2.setScore(3);
            if(leftTimeDeathmatch[1] <= 0 && leftTimeDeathmatch[2] <= 0){
                deathmatchIgang.setScore(2);
            } else {
                deathmatch.setScore(2);
            }

            spc4.setScore(1);
            jagten.setScore(0);
        } else {
            spc1.setScore(10);
            name.setScore(9);
            kills.setScore(8);
            spc3.setScore(7);
            left.setScore(6);
            time.setScore(5);
            spc2.setScore(4);
            if(leftTimeDeathmatch[1] <= 0 && leftTimeDeathmatch[2] <= 0){
                deathmatchIgang.setScore(3);
            } else {
                deathmatch.setScore(3);
            }
            chestRefill.setScore(2);
            spc4.setScore(1);
            jagten.setScore(0);
        }

        player.setScoreboard(b);
    }

    public static void kickScoreboard(Player player, Integer sec){
        Game game = HungerGames.getInstance().getGame("default");
        int[] left = timeLeft(sec);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective ob = board.registerNewObjective("kick", "");
        ob.setDisplaySlot(DisplaySlot.SIDEBAR);

        ob.setDisplayName(ChatHandler.format("   &6&lHUNGER GAMES   "));
        Score spc1 = ob.getScore("§1");
        Score name = ob.getScore(ChatHandler.format("&7Spiller: &6" + player.getName()));
        Score wins = ob.getScore(ChatHandler.format("&7Wins: &6" + HungerGames.getInstance().handler.getWins(player)));
        Score spc2 = ob.getScore("§2");
        Score winner = ob.getScore(ChatHandler.format("&7Vinder: &6" + game.getWinner().getName()));
        Score restart = ob.getScore(ChatHandler.format("&7Restart: &6" + left[1] + "m, " + left[2] + "s"));
        Score spc3 = ob.getScore("§3");
        Score jagten = ob.getScore(ChatHandler.format("  &7&oLavet af LegendeBente  "));

        spc1.setScore(7);
        name.setScore(6);
        wins.setScore(5);
        spc2.setScore(4);
        winner.setScore(3);
        restart.setScore(2);
        spc3.setScore(1);
        jagten.setScore(0);
        player.setScoreboard(board);
    }

    public static void lobbyScoreboard(Player player, Integer sec){
        String left = "";
        if(sec == null){
            left = "Ukendt";
        } else {
            left = sec + " sekunder";
        }
        Game game = HungerGames.getInstance().getGame("default");
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective ob = board.registerNewObjective("lobby", "");
        ob.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score spc5 = ob.getScore("§5");

        Score spc4 = ob.getScore("§1");
        ob.setDisplayName(ChatHandler.format("   &6&lHUNGER GAMES   "));
        Score spc1 = ob.getScore("§a");
        Score name = ob.getScore(ChatHandler.format("&7Spiller: &6" + player.getName()));
        Score wins = ob.getScore(ChatHandler.format("&7Wins: &6" + HungerGames.getInstance().handler.getWins(player)));
        Score spc2 = ob.getScore("§b");
        Score players = ob.getScore(ChatHandler.format("&7Spillere: &6" + game.getPlayersLeft().size() + "&8/&6" + game.getMaxPlayers()));
        Score starting = ob.getScore(ChatHandler.format("&7Starter om: &6" + left));
        Score spc3 = ob.getScore("§d");
        Score jagten = ob.getScore(ChatHandler.format("  &7&oLavet af LegendeBente  "));

        spc1.setScore(7);
        name.setScore(6);
        wins.setScore(5);
        spc2.setScore(4);
        players.setScore(3);
        starting.setScore(2);
        spc3.setScore(1);
        jagten.setScore(0);
        player.setScoreboard(board);
    }

    private static int[] getTime(Game game){
        if (game.getStarted() == null) {
            return new int[]{0, 0, 0, 0};
        }

        long diff = System.currentTimeMillis() - game.getStarted().getTime();

        int seconds = (int) (diff / 1000) % 60;
        int minutes = (int) (diff / (1000*60)) % 60;
        int hours   = (int) (diff / (1000*60*60)) % 24;
        int days = (int) ((diff / (1000*60*60*24)) % 7);
        return new int[]{seconds, minutes, hours, days};
    }

    private static long convertToMilli(long seconds){
        return seconds*1000;
    }

    private static int[] timeLeft(long sec){
        int seconds = (int) ((convertToMilli(sec) / 1000) % 60);
        int minutes = (int) ((convertToMilli(sec) / (1000*60)) % 60);
        int hours   = (int) ((convertToMilli(sec) / (1000*60*60)) % 24);

        return new int[]{hours, minutes, seconds};
    }



}
