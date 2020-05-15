package com.foufys;

import com.Alvaeron.api.RPEngineAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;

public class BoardData {

    public class AlivePlayer implements java.lang.Comparable<AlivePlayer> {
        public String PlayerName;
        public String RPName;
        public String TeamName;

        AlivePlayer() {

        }
        AlivePlayer(String playerName, String rpName, String teamName) {
            this.PlayerName = playerName;
            this.RPName = rpName;
            this.TeamName = teamName;
        }

        @Override
        public int compareTo(AlivePlayer other) {
            Map<String, Integer> teamValues = new HashMap<>();
            teamValues.put("Lords", 5);
            teamValues.put("Leaders", 4);
            teamValues.put("Brothers", 3);
            teamValues.put("Acolytes", 2);
            teamValues.put("Serfs", 1);

            int onlineSort = 0;
            if (this.PlayerName != null && other.PlayerName != null) { // online sorting
                Player onlineStatus1 = Bukkit.getPlayerExact(this.PlayerName);
                Player onlineStatus2 = Bukkit.getPlayerExact(other.PlayerName);

                if ((onlineStatus1 != null && onlineStatus2 == null) || (onlineStatus1 == null && onlineStatus2 != null)) {
                    onlineSort = onlineStatus1 == null ? 1 : -1;
                }
            }

            if (!StringUtils.isEmpty(this.TeamName) && !StringUtils.isEmpty(other.TeamName) && teamValues.containsKey(this.TeamName) && teamValues.containsKey(other.TeamName)) {
                int a = teamValues.get(this.TeamName);
                int b = teamValues.get(other.TeamName);
                if (a == b) {
                    return onlineSort;
                }
                return (b - a);
            } else if (!StringUtils.isEmpty(this.TeamName) && !StringUtils.isEmpty(other.TeamName)) {
                // sort teams alphabetically then by online status for unknown teams
                if (this.TeamName.equals(other.TeamName)) {
                    return onlineSort;
                }
                return this.TeamName.compareTo(other.TeamName);
            } else {
                if (!StringUtils.isEmpty(this.TeamName) && StringUtils.isEmpty(other.TeamName)) {
                    return -1;
                } else if (StringUtils.isEmpty(this.TeamName) && !StringUtils.isEmpty(other.TeamName)) {
                    return 1;
                } else {
                    return onlineSort;
                }
            }
        }

        @Override
        public String toString() {
            if (this.PlayerName != null) {
                return "----- " + this.PlayerName;
            }
            return "";
        }
    }

    private final File boardFolder;
    private final File dataFile;

    private final JSONParser jsonParser = new JSONParser();

    private final List<AlivePlayer> alivePlayers;

    Plugin plugin = Board.getPlugin(Board.class);

    public BoardData() {

        this.alivePlayers = new ArrayList<AlivePlayer>();

        this.dataFile = new File("./plugins/Board/", "board-data.json");
        this.boardFolder = new File("./plugins/Board");

        plugin.getLogger().log(Level.INFO, "Checking board data files...");
        this.initializeDataFile();
    }

    public void initializeDataFile() {
        if (!this.dataFile.exists()) {
            if (!this.boardFolder.exists()) {
                try {
                    if (this.boardFolder.mkdir()) {
                        try {
                            if (dataFile.createNewFile()) {
                                plugin.getLogger().log(Level.INFO, "Board data files created.");
                            } else {
                                plugin.getLogger().log(Level.SEVERE, "Could not create board data file. No data will be saved.");
                            }
                        } catch (Exception e) {
                            plugin.getLogger().log(Level.SEVERE, "Could not create board data file, no data will be saved: " + e.getMessage());
                        }
                    } else {
                        plugin.getLogger().log(Level.WARNING, "Could not create board data folder. No data will be saved.");
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not create board data folder, no data will be saved: " + e.getMessage());
                }
            }
        } else {
            plugin.getLogger().log(Level.INFO, "Board data files OK.");
        }
    }

    public List<AlivePlayer> GetAlivePlayers() {
        Collections.sort(this.alivePlayers);
        return this.alivePlayers;
    }

    public void addOrUpdatePlayer(Player player) {
        if (player.isDead() || player.isBanned()) {
            return;
        }

        String rpName = "";
        try {
            rpName = RPEngineAPI.getRpName(player.getName());
        } catch (Exception e) {
            rpName = "";
        }

        Team playerTeam = getPlayerTeam(player);
        String teamName = "";
        if (playerTeam != null) {
            teamName = playerTeam.getName();
        }


        if (alreadyAlive(player)) {
            // just update, if they already exist
            for (AlivePlayer alivePlayer : alivePlayers) {
                if (player.getName().equals(alivePlayer.PlayerName)) {
                    alivePlayer.TeamName = teamName;
                    if (!StringUtils.isEmpty(rpName)) {
                        alivePlayer.RPName = rpName;
                    }
                }
            }
            return;
        }

        this.alivePlayers.add(new AlivePlayer(player.getName(), rpName, teamName));
        plugin.getLogger().log(Level.INFO, "Player " + player.getName() + " added to the board.");
    }
    public void removePlayer(Player player) {
        if (player != null && player.getName() != null) {
            alivePlayers.removeIf(alivePlayer -> alivePlayer != null && alivePlayer.PlayerName != null && alivePlayer.PlayerName.equals(player.getName()));
            plugin.getLogger().log(Level.INFO, "Player " + player.getName() + " removed from the board.");
        }
    }

    private Team getPlayerTeam(Player player) {
        Set<Team> teams = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeams();
        for (Team team : teams) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }

    private boolean alreadyAlive(Player player) {
        for (AlivePlayer alivePlayer : alivePlayers) {
            if (player.getName().equals(alivePlayer.PlayerName)) {
                return true;
            }
        }
        return false;
    }

    public void saveData() {
        JSONArray playerJSONArray = new JSONArray();
        for (AlivePlayer alivePlayer : this.alivePlayers) {
            JSONObject playerJSONObject = new JSONObject();
            playerJSONObject.put("PlayerName", alivePlayer.PlayerName);
            playerJSONObject.put("TeamName", alivePlayer.TeamName);
            playerJSONObject.put("RPName", alivePlayer.RPName);
            playerJSONArray.add(playerJSONObject);
        }

        try {
            FileWriter dataFileWriter = new FileWriter(this.dataFile);
            dataFileWriter.write(playerJSONArray.toJSONString());
            dataFileWriter.close();
            plugin.getLogger().log(Level.INFO, "Successfully saved board player data.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not save board player data: " + e.getMessage());
        }
    }

    public void loadData() {
        try {
            Object object = jsonParser.parse(new FileReader(this.dataFile));
            JSONArray playerJSONArray = (JSONArray) object;
            for (Object o : playerJSONArray) {
                JSONObject playerJSONObject = (JSONObject) o;
                AlivePlayer player = new AlivePlayer();

                player.PlayerName = (String) playerJSONObject.get("PlayerName");
                player.TeamName = (String) playerJSONObject.get("TeamName");
                player.RPName = (String) playerJSONObject.get("RPName");
                this.alivePlayers.add(player);
            }

            plugin.getLogger().log(Level.WARNING, "Successfully loaded " + playerJSONArray.size() + " players");

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not load board player data: " + e.getMessage());
        }
    }

}
