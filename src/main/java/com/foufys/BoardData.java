package com.foufys;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoardData {

    public class AlivePlayer {
        public String PlayerName;
        public String Title;
    }

    private final Logger logger;

    private final File boardFolder;
    private final File dataFile;

    private JSONObject json;
    private JSONParser jsonParser = new JSONParser();

    private List<Player> alivePlayers;

    public BoardData(Logger logger) {
        this.logger = logger;

        this.dataFile = new File("./plugins/Board/", "board-data.json");
        this.boardFolder = new File("./plugins/Board");

        logger.log(Level.INFO, "Checking board data files...");
        this.initializeConfig();
    }

    public void initializeConfig() {
        if (!this.dataFile.exists()) {
            if (!this.boardFolder.exists()) {
                try {
                    if (this.boardFolder.mkdir()) {
                        try {
                            if (dataFile.createNewFile()) {
                                logger.log(Level.INFO, "Board data files created.");
                            } else {
                                logger.log(Level.SEVERE, "Could not create board data file. No data will be saved.");
                            }
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Could not create board data file, no data will be saved: " + e.getMessage());
                        }
                    } else {
                        logger.log(Level.WARNING, "Could not create board data folder. No data will be saved.");
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Could not create board data folder, no data will be saved: " + e.getMessage());
                }
            }
        } else {
            logger.log(Level.INFO, "Board data files OK.");
        }
    }


    public void addPlayer(Player player) {
        Team team = player.getScoreboard().getPlayerTeam(player);
    }
    public void removePlayer(Player player) {

    }

    public void saveJson() {

    }

    public void loadJson() {

    }

}
