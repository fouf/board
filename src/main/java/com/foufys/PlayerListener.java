package com.foufys;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerListener implements Listener {

    private final BoardData boardData;

    PlayerListener(BoardData boardData) {
        this.boardData = boardData;
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        this.boardData.addOrUpdatePlayer(event.getPlayer());
    }

    @EventHandler
    public void playerGamemode(PlayerGameModeChangeEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        // re-add if they change to survival
        if (event.getNewGameMode() == GameMode.SURVIVAL) {
            this.boardData.addOrUpdatePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void playerSpawnEvent(PlayerSpawnLocationEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().isDead() && !event.getPlayer().isBanned()) {
            // add them to alive board
            this.boardData.addOrUpdatePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        // remove from board on ban
        if (event.getPlayer().isBanned()) {
            this.boardData.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void playerLoggedIn(PlayerLoginEvent event) {
        /* // add to board on login
        if (event.getPlayer() != null && !event.getPlayer().isDead()) {
            // add them to alive board
            this.boardData.addPlayer(event.getPlayer());
        }*/
    }

    @EventHandler
    public void playerDied(PlayerDeathEvent event) {
        // remove from board on death.
        if (event.getEntity() == null || event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = event.getEntity();
        // remove them from alive board
        this.boardData.removePlayer(player);
    }
}
