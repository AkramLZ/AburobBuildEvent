package dev.akraml.aburob.data;

import dev.akraml.aburob.BuildEventPlugin;
import dev.akraml.aburob.json.JsonConfigAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DataManager {

    private final BuildEventPlugin plugin;
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();
    @Getter private final Map<UUID, PlayerData> raters = new HashMap<>();

    public CompletableFuture<PlayerData> loadData(final Player player) {
        if (dataMap.containsKey(player.getUniqueId()))
            return CompletableFuture.completedFuture(dataMap.get(player.getUniqueId()));
        return CompletableFuture.supplyAsync(() -> {
            final File file = new File(plugin.getDataFolder() + "/data/" + player.getUniqueId() + ".json");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                final JsonConfigAdapter jsonConfig = new JsonConfigAdapter(file);
                final PlayerData playerData = new PlayerData(player.getUniqueId(), jsonConfig);
                return playerData;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).thenApply(playerData -> {
            dataMap.put(player.getUniqueId(), playerData);
            return playerData;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public Collection<PlayerData> getPlayers() {
        return dataMap.values();
    }

    public PlayerData getData(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    public PlayerData getData(UUID uuid) {
        return dataMap.get(uuid);
    }

}
