package dev.akraml.aburob.commands;

import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import dev.akraml.aburob.BuildEventPlugin;
import dev.akraml.aburob.EventState;
import dev.akraml.aburob.data.PlayerData;
import dev.akraml.aburob.data.Rate;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class RateCommand implements CommandExecutor {

    private final BuildEventPlugin plugin;
    public static PlayerData CURRENT_VOTING = null;

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
            if (!player.hasPermission("event.rate")) {
                player.sendMessage(ChatColor.RED + "You are not allowed to rate!");
                return true;
            }
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /rate <rate>");
                return true;
            }
            if (plugin.getEventState() != EventState.VOTING) {
                player.sendMessage(ChatColor.RED + "You can only rate during judging period!");
                return true;
            }
            final PlayerData playerData = plugin.getDataManager().getData(player);
            if (playerData == null) {
                player.sendMessage(ChatColor.RED + "Seems like your data is not loaded correctly!");
                return true;
            }
            final PlotPlayer<?> plotPlayer = PlotPlayer.from(player);
            final Plot plot = plotPlayer.getCurrentPlot();
            if (plot == null || plot.getOwner() == null) {
                player.sendMessage(ChatColor.RED + "You are not in a valid plot!");
                return true;
            }
            final PlayerData target = plugin.getDataManager().getData(plot.getOwner());
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Seems like plot owner is not loaded!");
                return true;
            }
            Double rate = parseDouble(args[0]);
            if (rate == null) {
                player.sendMessage(ChatColor.RED + "Please provide a valid double!");
                return true;
            }
            if (rate < 0 || rate > 10) {
                player.sendMessage(ChatColor.RED + "Rate must be between 0 and 10!");
                return true;
            }
            Rate rate1 = new Rate(player.getName());
            rate1.setRate(rate);
            target.addRate(rate1);
            player.sendMessage(ChatColor.YELLOW + "Ratted this plot with " + rate + "/10");
            playerData.getRateProfile().getRateQueue().remove(CURRENT_VOTING);
            boolean isDone = true;
            boolean allEmpty = true;
            for (PlayerData judge : plugin.getDataManager().getRaters().values()) {
                if (judge.getRateProfile().getRateQueue().contains(CURRENT_VOTING)) {
                    isDone = false;
                }
                if (judge.getRateProfile().getRateQueue().size() > 0) {
                    allEmpty = false;
                }
            }
            if (isDone) {
                if (allEmpty) {
                    plugin.setEventState(EventState.ENDING);
                } else {
                    CURRENT_VOTING = playerData.getRateProfile().getRateQueue().get(0);
                    Plot plot1 = Plot.getPlotFromString(
                            PlotPlayer.from(Bukkit.getOnlinePlayers().stream().findFirst().get()),
                            RateCommand.CURRENT_VOTING.getName(),
                            false
                    );
                    Player randomPlayer = Bukkit.getOnlinePlayers().stream().findFirst().get();
                    while (plot1 == null) {
                        for (PlayerData judge : plugin.getDataManager().getRaters().values()) {
                            judge.getRateProfile().getRateQueue().remove(0);
                        }
                        RateCommand.CURRENT_VOTING = playerData.getRateProfile().getRateQueue().get(0);
                        plot1 = Plot.getPlotFromString(
                                PlotPlayer.from(randomPlayer),
                                RateCommand.CURRENT_VOTING.getName(),
                                false
                        );
                    }
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        PlotPlayer<?> plotPlayer1 = PlotPlayer.from(player1);
                        plot1.teleportPlayer(plotPlayer1, TeleportCause.PLUGIN, result -> {});
                    }
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "&eRating " + RateCommand.CURRENT_VOTING.getName());
                }
            }
        }
        return true;
    }

    public Double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception exception) {
            return null;
        }
    }
}
