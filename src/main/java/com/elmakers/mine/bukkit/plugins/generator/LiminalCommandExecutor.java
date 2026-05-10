package com.elmakers.mine.bukkit.plugins.generator;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public class LiminalCommandExecutor implements TabExecutor {
    private final LiminalWorldPlugin plugin;

    public LiminalCommandExecutor(LiminalWorldPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommand("liminal").setExecutor(this);
        plugin.getCommand("liminal").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 0) {
            showUsage(sender);
            return true;
        }

        String subCommand = args[0];
        switch (subCommand) {
            case "go":
                processGoCommand(sender, args[1]);
                break;
            default:
                showUsage(sender);
        }
        return true;
    }

    private void processGoCommand(CommandSender sender, String level) {
        if (!checkPlayer(sender)) {
            return;
        }
        Player player = (Player)sender;
        if (!plugin.sendToLevel(player, level)) {
            sender.sendMessage(ChatColor.RED + "Unable to load world " + level);
        }
    }

    private boolean checkPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        }

        sender.sendMessage(ChatColor.RED + "This command may only be used in-game");
        return false;
    }

    private void showUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage: /liminal go <pools|ocean>");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 1) {
            return List.of("go");
        }
        return List.of("pools", "ocean");
    }
}
