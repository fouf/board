package com.foufys;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.Set;
import java.util.logging.Level;

public final class Board extends JavaPlugin {

    private BoardData boardData;

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "has been enabled. Loading board data...");
        this.boardData = new BoardData();
        this.boardData.loadData();
        getServer().getPluginManager().registerEvents(new PlayerListener(this.boardData), this);
    }

    @Override
    public void onDisable() {
        this.boardData.saveData();
        getLogger().log(Level.INFO, "has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("board")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.YELLOW + "--------BOARD v" + getDescription().getVersion() + "--------");
                String currentTeam = "";
                ChatColor teamColor = ChatColor.WHITE;
                for (BoardData.AlivePlayer alivePlayer : boardData.GetAlivePlayers()) {
                    if (alivePlayer == null || alivePlayer.PlayerName == null) {
                        continue;
                    }
                    if (alivePlayer.PlayerName.equals("")) {
                        continue;
                    }

                    if (currentTeam.equals("") || !currentTeam.equals(alivePlayer.TeamName)) {
                        currentTeam = alivePlayer.TeamName;

                        teamColor = ChatColor.WHITE;
                        Set<Team> teams = getServer().getScoreboardManager().getMainScoreboard().getTeams();
                        for (Team team : teams) {
                            if (team.getName().equals(alivePlayer.TeamName)) {
                                teamColor = team.getColor();
                            }
                        }

                        player.sendMessage(teamColor + "--" + alivePlayer.TeamName);
                    }
                    Player onlineStatus = Bukkit.getPlayerExact(alivePlayer.PlayerName);

                    String name = alivePlayer.PlayerName;
                    if (alivePlayer.RPName != null && !alivePlayer.RPName.equals("") && !alivePlayer.RPName.equals("NONE")) {
                        name = alivePlayer.RPName + " [" + alivePlayer.PlayerName + "]";
                    }
                    TextComponent onlineIndicator = new TextComponent("█");
                    onlineIndicator.setColor(onlineStatus == null ? ChatColor.RED.asBungee() : ChatColor.DARK_GREEN.asBungee());
                    if (onlineStatus != null) {
                        onlineIndicator.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bird " + alivePlayer.PlayerName));
                        onlineIndicator.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click to send a bird" ).create()));
                    }
                    TextComponent playerName = new TextComponent(name);
                    playerName.setColor(teamColor.asBungee());
                    onlineIndicator.addExtra(playerName);

                    player.spigot().sendMessage(onlineIndicator);
                }
                player.sendMessage(ChatColor.YELLOW + "--------" + boardData.GetAlivePlayers().size() + " tracked alive players --------");
            }
            return true;
        }
        return false;
    }
}
