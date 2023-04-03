package dev.akraml.aburob.commands;

import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.util.query.PlotQuery;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                System.out.println("done");
                if (allEmpty) {
                    System.out.println("all empty");
                    plugin.setEventState(EventState.ENDING);
                    // Get top winners
                    List<PlayerData> toSort = new ArrayList<>(plugin.getDataManager().getPlayers());
                    toSort.removeAll(plugin.getDataManager().getRaters().values());
                    toSort.sort(Comparator.comparing(PlayerData::getRate));
                    Bukkit.broadcastMessage("§6§lEVENT ENDED! §fWinners are:");
                    int i = 0;
                    if (toSort.size() <= 5) {
                        for (PlayerData playerData1 : toSort) {
                            i++;
                            Bukkit.broadcastMessage("§e§l " + i + ". §f" + playerData1.getName() + " with "
                                    + new DecimalFormat("##.##").format(playerData1.getRate()) + "/10");
                        }
                    } else {
                        for (i = 0; i < 5; i++) {
                            PlayerData playerData1 = toSort.get(i);
                            Bukkit.broadcastMessage("§e§l " + (i + 1) + ". §f" + playerData1.getName() + " with "
                                    + new DecimalFormat("##.##").format(playerData1.getRate()) + "/10");
                        }
                    }
                } else {
                    System.out.println("not all empty");
                    CURRENT_VOTING = playerData.getRateProfile().getRateQueue().get(0);
                    Plot plot1 = PlotQuery.newQuery()
                            .thatPasses(plot2 -> plot2.getOwner() != null
                                    && plot2.getOwner().equals(RateCommand.CURRENT_VOTING.getUuid()))
                            .asList().stream().findFirst().orElse(null);
                    while (plot1 == null) {
                        for (PlayerData judge : plugin.getDataManager().getRaters().values()) {
                            judge.getRateProfile().getRateQueue().remove(0);
                        }
                        RateCommand.CURRENT_VOTING = playerData.getRateProfile().getRateQueue().get(0);
                        plot1 = PlotQuery.newQuery()
                                .thatPasses(plot2 -> plot2.getOwner() != null
                                        && plot2.getOwner().equals(RateCommand.CURRENT_VOTING.getUuid()))
                                .asList().stream().findFirst().orElse(null);
                    }
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        PlotPlayer<?> plotPlayer1 = PlotPlayer.from(player1);
                        plot1.teleportPlayer(plotPlayer1, TeleportCause.PLUGIN, result -> {});
                    }
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Rating " + RateCommand.CURRENT_VOTING.getName());
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
