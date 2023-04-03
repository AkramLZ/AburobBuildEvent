package dev.akraml.aburob.commands;

import dev.akraml.aburob.BuildEventPlugin;
import dev.akraml.aburob.EventState;
import dev.akraml.aburob.EventTickRunnable;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EndCommand implements CommandExecutor {

    private final BuildEventPlugin plugin;

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("event.end")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
                return true;
            }
            if (plugin.getEventState() != EventState.PLAYING) {
                player.sendMessage(ChatColor.RED + "In order to use this command, event must be started!");
                return true;
            }
            EventTickRunnable.TIME = 5;
            player.sendMessage(ChatColor.YELLOW + "Shortened event time to 5 seconds!");
            return true;
        }
        return false;
    }
}
