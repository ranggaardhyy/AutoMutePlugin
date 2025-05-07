package id.boenyy.automute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDateTime;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage().toLowerCase();
        Player player = event.getPlayer();
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        
        for (String word : plugin.getBannedWords()) {
            if (msg.contains(word)) {
                handleBannedWord(event, player, word);
                return;
            }
        }
    }
    
    private void handleBannedWord(AsyncPlayerChatEvent event, Player player, String word) {
        event.setCancelled(true);
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        plugin.getLogger().info(player.getName() + " used banned word: " + word);
        
        // Get mute configuration
        FileConfiguration cfg = plugin.getConfig();
        String duration = cfg.getString("durations." + word, plugin.getDefaultDuration());
        String reason = "Using banned word: " + word;
        
        // Execute mute command
        executeMuteCommand(player, duration, reason);
        
        // Log the mute if enabled
        if (plugin.isLogEnabled()) {
            logMuteAction(player, word, duration, reason);
        }
    }
    
    private void executeMuteCommand(Player player, String duration, String reason) {
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        FileConfiguration cfg = plugin.getConfig();
        
        String cmd = cfg.getString("muteCommand")
                .replace("{player}", player.getName())
                .replace("{duration}", duration)
                .replace("{reason}", reason);
        
        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
        );
    }
    
    private void logMuteAction(Player player, String word, String duration, String reason) {
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        FileConfiguration data = plugin.getDataConfig();
        String path = "mutes." + player.getName();
        
        data.set(path + ".word", word);
        data.set(path + ".duration", duration);
        data.set(path + ".reason", reason);
        data.set(path + ".timestamp", LocalDateTime.now().toString());
        
        plugin.saveDataConfig();
    }
}
