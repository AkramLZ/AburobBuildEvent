package dev.akraml.aburob.commands;

import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import dev.akraml.aburob.BuildEventPlugin;
import dev.akraml.aburob.EventState;
import dev.akraml.aburob.data.PlayerData;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class StartCommand implements CommandExecutor {

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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("event.start")) {
                player.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
                return true;
            }
            if (plugin.getEventState() != EventState.WAITING) {
                player.sendMessage(ChatColor.RED + "Event is already started!");
                return true;
            }
            plugin.setEventState(EventState.PLAYING);
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Event has been force started by " + player.getName());
            for (PlayerData playerData : plugin.getDataManager().getPlayers()) {
                if (plugin.getDataManager().getRaters().containsKey(playerData.getUuid()))
                    continue;
                Player target = Bukkit.getPlayer(playerData.getUuid());
                if (target == null)
                    continue;
                PlotPlayer<?> plotPlayer = PlotPlayer.from(target);
                if (plotPlayer ==  null)
                    continue;
                Plot plot = plotPlayer.getPlots().stream().findFirst().orElse(null);
                if (plot == null) {
                    target.performCommand("plot auto");
                } else {
                    plot.teleportPlayer(plotPlayer, TeleportCause.PLUGIN, result -> {});
                }
            }
        }
        return true;
    }
}
