package com.foufys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Board extends JavaPlugin {

    public ChatColor LORD_COLOR = ChatColor.DARK_RED;
    public ChatColor LEADERS_COLOR = ChatColor.AQUA;
    public ChatColor BROTHERS_COLOR = ChatColor.BLUE;
    public ChatColor ACOLYTES_COLOR = ChatColor.GREEN;
    public ChatColor SERFS_COLOR = ChatColor.GRAY;

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
                player.sendMessage(ChatColor.RED + "--------BOARD v" + getDescription().getVersion() + "--------");
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
                        teamColor = getTeamColor(alivePlayer.TeamName);
                        player.sendMessage(teamColor + "----" + alivePlayer.TeamName);
                    }
                    Player onlineStatus = Bukkit.getPlayerExact(alivePlayer.PlayerName);

                    String name = alivePlayer.PlayerName;
                    if (alivePlayer.RPName != null && !alivePlayer.RPName.equals("") && !alivePlayer.RPName.equals("NONE")) {
                        name = alivePlayer.RPName + " [" + alivePlayer.PlayerName + "]";
                    }

                    player.sendMessage(teamColor + "------" + name + " - " + (onlineStatus == null ? ChatColor.RED + "OFFLINE" : ChatColor.DARK_GREEN + "ONLINE"));
                }
                player.sendMessage(ChatColor.RED + "--------" + boardData.GetAlivePlayers().size() + " tracked alive players --------");
            }
            return true;
        }
        return false;
    }

    public ChatColor getTeamColor(String teamName) {
        switch (teamName) {
            case "Lords":
                return LORD_COLOR;
            case "Leaders":
                return LEADERS_COLOR;
            case "Brothers":
                return BROTHERS_COLOR;
            case "Acolytes":
                return ACOLYTES_COLOR;
            case "Serfs":
                return SERFS_COLOR;
            default:
                return ChatColor.GRAY;
        }
    }
}
