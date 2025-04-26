package id.boenyy.automute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.LocalDateTime;

public class ChatListener implements Listener {
    private final AutoMutePlugin plugin;

    public ChatListener(AutoMutePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage().toLowerCase();
        Player player = event.getPlayer();

        for (String word : plugin.getBannedWords()) {
            if (msg.contains(word)) {
                event.setCancelled(true);
                plugin.getLogger().info(player.getName() + " used banned word: " + word);

                FileConfiguration cfg = plugin.getConfig();
                String duration = cfg.getString("durations." + word, plugin.getDefaultDuration());
                String reason = "Using banned word: " + word;
                String cmd = String.format("advancedban:mute %s %s %s", player.getName(), duration, reason);

                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
                );

                if (plugin.isLogEnabled()) {
                    FileConfiguration data = plugin.getDataConfig();
                    String path = "mutes." + player.getName();
                    data.set(path + ".word", word);
                    data.set(path + ".duration", duration);
                    data.set(path + ".reason", reason);
                    data.set(path + ".timestamp", LocalDateTime.now().toString());
                    plugin.saveDataConfig();
                }
                return;
            }
        }
    }
}
