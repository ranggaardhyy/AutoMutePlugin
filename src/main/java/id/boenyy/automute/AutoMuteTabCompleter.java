package id.boenyy.automute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AutoMuteTabCompleter implements TabCompleter {
    private final AutoMutePlugin plugin;

    public AutoMuteTabCompleter(AutoMutePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("automute.use")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("addword", "removeword", "setduration", "reload", "list").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "addword":
                    return Collections.emptyList();
                case "removeword":
                case "setduration":
                    return plugin.getBannedWords().stream()
                            .filter(word -> word.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setduration")) {
            // Untuk setduration, setelah pilih word -> bisa kasih saran contoh durasi
            return Arrays.asList("10s", "5m", "10m", "30m", "1h");
        }

        return Collections.emptyList();
    }
}
