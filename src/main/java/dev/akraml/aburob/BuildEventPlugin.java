package dev.akraml.aburob;

import dev.akraml.aburob.commands.RateCommand;
import dev.akraml.aburob.commands.StartCommand;
import dev.akraml.aburob.data.DataManager;
import dev.akraml.aburob.listeners.PlayerListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

/**
 * @author AkramL
 */
public class BuildEventPlugin extends JavaPlugin {

    @Getter @Setter
    private EventState eventState = EventState.WAITING;
    @Getter private DataManager dataManager;

    @Override
    public void onEnable() {
        // Create players data folder
        getDataFolder().mkdirs();
        File dataFile = new File(getDataFolder() + "/data");
        dataFile.mkdirs();
        dataManager = new DataManager(this);
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        // Create task
        getServer().getScheduler().runTaskTimerAsynchronously(this, new EventTickRunnable(this), 0L, 1L);
        Objects.requireNonNull(getCommand("rate")).setExecutor(new RateCommand(this));
        Objects.requireNonNull(getCommand("start")).setExecutor(new StartCommand(this));
    }

}
