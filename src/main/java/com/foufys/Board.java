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

import java.util.logging.Level;

public final class Board extends JavaPlugin {

    public net.md_5.bungee.api.ChatColor LORD_COLOR = net.md_5.bungee.api.ChatColor.DARK_RED;
    public net.md_5.bungee.api.ChatColor LEADERS_COLOR = net.md_5.bungee.api.ChatColor.AQUA;
    public net.md_5.bungee.api.ChatColor BROTHERS_COLOR = net.md_5.bungee.api.ChatColor.BLUE;
    public net.md_5.bungee.api.ChatColor ACOLYTES_COLOR = net.md_5.bungee.api.ChatColor.GREEN;
    public net.md_5.bungee.api.ChatColor SERFS_COLOR = net.md_5.bungee.api.ChatColor.GRAY;

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
                net.md_5.bungee.api.ChatColor teamColor = net.md_5.bungee.api.ChatColor.WHITE;
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
                        player.sendMessage(teamColor + "--" + alivePlayer.TeamName);
                    }
                    Player onlineStatus = Bukkit.getPlayerExact(alivePlayer.PlayerName);

                    String name = alivePlayer.PlayerName;
                    if (alivePlayer.RPName != null && !alivePlayer.RPName.equals("") && !alivePlayer.RPName.equals("NONE")) {
                        name = alivePlayer.RPName + " [" + alivePlayer.PlayerName + "]";
                    }
                    TextComponent onlineIndicator = new TextComponent("█");
                    onlineIndicator.setColor(onlineStatus == null ? net.md_5.bungee.api.ChatColor.RED : net.md_5.bungee.api.ChatColor.DARK_GREEN);
                    if (onlineStatus != null) {
                        onlineIndicator.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bird " + alivePlayer.PlayerName));
                        onlineIndicator.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click to send a bird" ).create()));
                    }
                    TextComponent playerName = new TextComponent(name);
                    playerName.setColor(teamColor);
                    onlineIndicator.addExtra(playerName);

                    player.spigot().sendMessage(onlineIndicator);
                }
                player.sendMessage(ChatColor.YELLOW + "--------" + boardData.GetAlivePlayers().size() + " tracked alive players --------");
            }
            return true;
        }
        return false;
    }

    public net.md_5.bungee.api.ChatColor getTeamColor(String teamName) {
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
                return net.md_5.bungee.api.ChatColor.GRAY;
        }
    }
}
