package com.foufys;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerListener implements Listener {

    private final Logger logger;
    private final BoardData boardData;

    PlayerListener(Logger logger, BoardData boardData) {
        this.logger = logger;
        this.boardData = boardData;
    }

    @EventHandler
    public void playerLoggedIn(PlayerLoginEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().isDead()) {
            // add them to alive board
            this.boardData.addPlayer(event.getPlayer());
            this.logger.log(Level.INFO, "Player " + event.getPlayer().getName() + " has logged in. Updating board.");
        }
    }

    @EventHandler
    public void playerDied(PlayerDeathEvent event) {
        if (event.getEntity() == null || event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = event.getEntity();
        // remove them from alive board
        this.boardData.removePlayer(player);
        this.logger.log(Level.INFO, "Player " + event.getEntity().getName() + " has died. Removing from board.");
    }
}
