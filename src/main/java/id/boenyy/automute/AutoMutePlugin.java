package id.boenyy.automute;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AutoMutePlugin extends JavaPlugin {
    private List<String> bannedWords;
    private String defaultDuration;
    private boolean logEnabled;

    private File dataFile;
    private FileConfiguration dataConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadSettings();

        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) saveResource("data.yml", false);
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("automute").setExecutor(new AutoMuteCommand(this));
        getCommand("automute").setTabCompleter(new AutoMuteTabCompleter(this));


        getLogger().info("AutoMute v" + getDescription().getVersion() + " enabled.");
    }

    public void reloadSettings() {
        reloadConfig();
        FileConfiguration cfg = getConfig();
        bannedWords = cfg.getStringList("bannedWords");
        defaultDuration = cfg.getString("defaultDuration", "10m");
        logEnabled = cfg.getBoolean("logEnabled", true);
    }

    public List<String> getBannedWords() { return bannedWords; }
    public String getDefaultDuration() { return defaultDuration; }
    public boolean isLogEnabled() { return logEnabled; }
    public FileConfiguration getDataConfig() { return dataConfig; }

    public void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("Failed to save data.yml");
            e.printStackTrace();
        }
    }
}