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

    PlayerListener(Logger logger) {
        this.logger = logger;
    }

    @EventHandler
    public void playerLoggedIn(PlayerLoginEvent event) {
        //logger.log(Level.INFO, "Player " + event.getPlayer().getName() + " has logged in.");
        if (!event.getPlayer().isDead()) {
            // add them to alive board
        }
    }

    @EventHandler
    public void playerDied(PlayerDeathEvent event) {
        if (event.getEntity() == null || event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player player = event.getEntity();


        // remove them from alive board

        //logger.log(Level.INFO, "Player " + event.getEntity().getName() + " has died.");
    }
}
