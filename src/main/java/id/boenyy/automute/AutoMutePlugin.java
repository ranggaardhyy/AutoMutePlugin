package id.boenyy.automute;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AutoMutePlugin extends JavaPlugin {

    private static AutoMutePlugin instance;
    
    private List<String> bannedWords;
    private String defaultDuration;
    private boolean logEnabled;
    
    private File dataFile;
    private FileConfiguration dataConfig;
    
    // Getters
    public List<String> getBannedWords() { return bannedWords; }
    public String getDefaultDuration() { return defaultDuration; }
    public boolean isLogEnabled() { return logEnabled; }
    public FileConfiguration getDataConfig() { return dataConfig; }
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Load configurations
        saveDefaultConfig();
        reloadSettings();
        
        // Initialize data file
        initDataFile();
        
        // Register listeners and commands
        registerHandlers();
        
        getLogger().info("AutoMute v" + getDescription().getVersion() + " enabled.");
    }
    
    private void initDataFile() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    private void registerHandlers() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getCommand("automute").setExecutor(new AutoMuteCommand());
        getCommand("automute").setTabCompleter(new AutoMuteTabCompleter(this));
    }
    
    public void reloadSettings() {
        reloadConfig();
        FileConfiguration cfg = getConfig();
        bannedWords = cfg.getStringList("bannedWords");
        defaultDuration = cfg.getString("defaultDuration", "10m");
        logEnabled = cfg.getBoolean("logEnabled", true);
    }
    
    public void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("Failed to save data.yml");
            e.printStackTrace();
        }
    }
    
    public static AutoMutePlugin getInstance() {
        return instance;
    }
}