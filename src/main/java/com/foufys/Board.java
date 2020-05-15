package com.foufys;

import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class Board extends JavaPlugin {

    private BoardData boardData;
    private final int PAGE_SIZE = 15;

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

            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    page = 1;
                }
            }

            if (page <= 0) {
                page = 1;
            }
            List<BoardData.AlivePlayer> alivePlayers = boardData.GetAlivePlayers();
            int maxPage = (alivePlayers.size() + PAGE_SIZE - 1) / PAGE_SIZE;

            if (page > maxPage) {
                page = maxPage;
            }

            int offset = (page - 1) * PAGE_SIZE;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.YELLOW + "--------Board v" + getDescription().getVersion() + " - " + boardData.GetAlivePlayers().size() + " tracked alive players" + "--------");
                String currentTeam = "";
                ChatColor teamColor = ChatColor.WHITE;


                int lowerOffset = offset;
                if (lowerOffset < 0) {
                    lowerOffset = 0;
                }

                int upperOffset = offset + PAGE_SIZE;
                if (upperOffset > alivePlayers.size()) {
                    upperOffset = alivePlayers.size();
                }

                List<BoardData.AlivePlayer> pagedPlayers = alivePlayers.subList(lowerOffset, upperOffset);
                for (BoardData.AlivePlayer alivePlayer : pagedPlayers) {
                    if (alivePlayer == null || alivePlayer.PlayerName == null) {
                        continue;
                    }
                    if (StringUtils.isEmpty(alivePlayer.PlayerName)) {
                        continue;
                    }

                    String alivePlayerTeam = alivePlayer.TeamName == null || alivePlayer.TeamName.equals("") ? "NO TEAM" : alivePlayer.TeamName;

                    if (!currentTeam.equals(alivePlayerTeam)) {
                        currentTeam = alivePlayerTeam;

                        teamColor = ChatColor.WHITE;
                        Set<Team> teams = getServer().getScoreboardManager().getMainScoreboard().getTeams();
                        for (Team team : teams) {
                            if (team.getName().equals(alivePlayerTeam)) {
                                teamColor = team.getColor();
                            }
                        }

                        player.sendMessage(teamColor + "--" + alivePlayerTeam);
                    }

                    Player onlineStatus = Bukkit.getPlayerExact(alivePlayer.PlayerName);

                    String name = alivePlayer.PlayerName;
                    if (alivePlayer.RPName != null && !alivePlayer.RPName.equals("") && !alivePlayer.RPName.equals("NONE")) {
                        name = alivePlayer.RPName + " [" + alivePlayer.PlayerName + "]";
                    }
                    TextComponent onlineIndicator = new TextComponent("█");
                    onlineIndicator.setColor(onlineStatus == null ? ChatColor.RED.asBungee() : ChatColor.DARK_GREEN.asBungee());
                    if (onlineStatus != null) {
                        onlineIndicator.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bird " + alivePlayer.PlayerName + " "));
                        onlineIndicator.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click to send a bird" ).create()));
                    }
                    TextComponent playerName = new TextComponent(name);
                    playerName.setColor(teamColor.asBungee());
                    onlineIndicator.addExtra(playerName);

                    player.spigot().sendMessage(onlineIndicator);
                }

                int previousPage = page - 1;
                previousPage = Math.max(previousPage, 1);

                int nextPage = page + 1;
                nextPage = Math.min(nextPage, maxPage);

                TextComponent paginationButtons = new TextComponent();

                TextComponent separator = new TextComponent("--------");
                separator.setColor(ChatColor.YELLOW.asBungee());

                TextComponent previousButton = new TextComponent(" <<<<<<<< ");
                previousButton.setColor(ChatColor.DARK_RED.asBungee());
                previousButton.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Previous page" ).create()));
                previousButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/board " + previousPage));

                TextComponent pageInfo = new TextComponent("");
                pageInfo.addExtra("Page ");
                pageInfo.addExtra(page + "/" + maxPage);

                TextComponent nextButton = new TextComponent(" >>>>>>>> ");
                nextButton.setColor(ChatColor.DARK_GREEN.asBungee());
                nextButton.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Next page" ).create()));
                nextButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/board " + nextPage));

                paginationButtons.addExtra(separator);
                paginationButtons.addExtra(previousButton);
                paginationButtons.addExtra(pageInfo);
                paginationButtons.addExtra(nextButton);
                paginationButtons.addExtra(separator);

                player.spigot().sendMessage(paginationButtons);
            }
            return true;
        }
        return false;
    }
}
