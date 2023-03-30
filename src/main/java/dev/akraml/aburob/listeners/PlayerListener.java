package dev.akraml.aburob.listeners;

import dev.akraml.aburob.BuildEventPlugin;
import dev.akraml.aburob.EventState;
import dev.akraml.aburob.data.RateProfile;
import fr.mrmicky.fastboard.FastBoard;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final BuildEventPlugin plugin;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        event.setJoinMessage(null);
        plugin.getDataManager().loadData(player).thenAccept(playerData -> {
            playerData.setName(player.getName());
            playerData.setBoard(new FastBoard(player));
            if (player.hasPermission("event.rate")) {
                if (!plugin.getDataManager().getRaters().containsKey(player.getUniqueId())) {
                    plugin.getDataManager().getRaters().put(player.getUniqueId(), playerData);
                }
                if (playerData.getRateProfile() == null)
                    playerData.setRateProfile(new RateProfile(playerData));
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getEventState() != EventState.PLAYING && !player.hasPermission("event.build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getEventState() != EventState.PLAYING && !player.hasPermission("event.build")) {
            event.setCancelled(true);
        }
    }

}
