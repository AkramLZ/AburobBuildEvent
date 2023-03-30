package dev.akraml.aburob;

import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import dev.akraml.aburob.commands.RateCommand;
import dev.akraml.aburob.data.PlayerData;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class EventTickRunnable implements Runnable {

    private final BuildEventPlugin plugin;
    public static long TICK = 0;
    public static int TIME = 3600;

    @Override
    public void run() {
        if (plugin.getEventState() == EventState.WAITING) {
            if (TICK % 20 == 0) {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = plugin.getDataManager().getData(player.getUniqueId());
                    if (playerData.getBoard() == null) continue;
                    playerData.getBoard().updateTitle(c("&6&lABUROB EVENTS"));
                    playerData.getBoard().updateLines(
                            c(""),
                            c("&6&lEVENT INFO"),
                            c("&f Event state: &eWaiting"),
                            c("&f Online players: &e" + Bukkit.getOnlinePlayers().size()),
                            c(""),
                            c("&ediscord.gg/aburob")
                    );
                }
            }
        }
        if (plugin.getEventState() == EventState.PLAYING) {
            if (TICK % 20 == 0) {
                final Date date = new Date();
                date.setHours(0);
                date.setMinutes(0);
                date.setSeconds(TIME);
                final SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                final String timeLeft = format.format(date);
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = plugin.getDataManager().getData(player.getUniqueId());
                    if (playerData.getBoard() == null) continue;
                    playerData.getBoard().updateTitle(c("&6&lABUROB EVENTS"));
                    playerData.getBoard().updateLines(
                            c(""),
                            c("&6&lEVENT INFO"),
                            c("&f Event state: &ePlaying"),
                            c("&f Time left: &e" + timeLeft),
                            c("&f Online players: &e" + Bukkit.getOnlinePlayers().size()),
                            c(""),
                            c("&6&lROUND INFO"),
                            c("&f Your rate: &e" + playerData.getRate()),
                            c(""),
                            c("&ediscord.gg/aburob")
                    );
                }
                TIME--;
                if (TIME == 0) {
                    plugin.setEventState(EventState.VOTING);
                    List<PlayerData> toVote = new ArrayList<>();
                    for (PlayerData playerData : plugin.getDataManager().getPlayers()) {
                        if (plugin.getDataManager().getRaters().containsKey(playerData.getUuid()))
                            continue;
                        toVote.add(playerData);
                    }
                    RateCommand.CURRENT_VOTING = toVote.get(0);
                    Plot plot = Plot.getPlotFromString(
                            PlotPlayer.from(Bukkit.getOnlinePlayers().stream().findFirst().get()),
                            RateCommand.CURRENT_VOTING.getName(),
                            false
                    );
                    Player randomPlayer = Bukkit.getOnlinePlayers().stream().findFirst().get();
                    while (plot == null) {
                        if (toVote.size() == 0) {
                            Bukkit.getLogger().severe("an error occurred, stopping...");
                            Bukkit.shutdown();
                            return;
                        }
                        toVote.remove(0);
                        RateCommand.CURRENT_VOTING = toVote.get(0);
                        plot = Plot.getPlotFromString(
                                PlotPlayer.from(randomPlayer),
                                RateCommand.CURRENT_VOTING.getName(),
                                false
                        );
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PlotPlayer<?> plotPlayer = PlotPlayer.from(player);
                        plot.teleportPlayer(plotPlayer, TeleportCause.PLUGIN, result -> {});
                    }
                    for (final PlayerData judge : plugin.getDataManager().getRaters().values()) {
                        judge.getRateProfile().getRateQueue().addAll(toVote);
                    }
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "&eRating " + RateCommand.CURRENT_VOTING.getName());
                }
            }
        }
        if (plugin.getEventState() == EventState.VOTING) {
            if (TICK % 20 == 0) {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = plugin.getDataManager().getData(player.getUniqueId());
                    PlotPlayer<?> plotPlayer = PlotPlayer.from(player);
                    Plot plot = plotPlayer.getCurrentPlot();
                    PlayerData targetData = null;
                    if (plot != null) {
                        targetData = plugin.getDataManager().getData(plot.getOwner());
                    }
                    if (playerData.getBoard() == null) continue;
                    playerData.getBoard().updateTitle(c("&6&lABUROB EVENTS"));
                    playerData.getBoard().updateLines(
                            c(""),
                            c("&6&lEVENT INFO"),
                            c("&f Event state: &eJudging"),
                            c("&f Online players: &e" + Bukkit.getOnlinePlayers().size()),
                            c(""),
                            c("&6&lROUND INFO"),
                            c("&f Your rate: &e" + playerData.getRate()),
                            c("&f Plot rate: &e" + (targetData == null ? "-" : targetData.getRate())),
                            c(""),
                            c("&ediscord.gg/aburob")
                    );
                }
            }
            if (plugin.getEventState() == EventState.VOTING) {
                if (TICK % 20 == 0) {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        final PlayerData playerData = plugin.getDataManager().getData(player.getUniqueId());
                        if (playerData.getBoard() == null) continue;
                        playerData.getBoard().updateTitle(c("&6&lABUROB EVENTS"));
                        playerData.getBoard().updateLines(
                                c(""),
                                c("&6&lEVENT INFO"),
                                c("&f Event state: &eEnded"),
                                c("&f Online players: &e" + Bukkit.getOnlinePlayers().size()),
                                c(""),
                                c("&f Thank you for"),
                                c("&f participating!"),
                                c(""),
                                c("&ediscord.gg/aburob")
                        );
                    }
                }
            }
        }
        TICK++;
    }

    private String c(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
