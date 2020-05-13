package com.foufys;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerLoginEvent;

public final class board extends JavaPlugin {

    public void boardLog(String message) {
        getLogger().info("[Board v" + getDescription().getVersion() + "] " + message);
    }

    @Override
    public void onEnable() {
        boardLog("has been enabled");
        getServer().getPluginManager().registerEvents(new PlayerListener(getLogger()), this);
    }

    @Override
    public void onDisable() {
        boardLog("has been disabled.");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("basic")) {
            return true;
        }
        return false;
    }

}
