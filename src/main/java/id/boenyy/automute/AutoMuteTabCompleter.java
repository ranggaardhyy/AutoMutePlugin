package id.boenyy.automute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AutoMuteTabCompleter implements TabCompleter {
    private static final List<String> SUBCOMMANDS = Arrays.asList("addword", "removeword", "setduration", "reload", "list");
    private static final List<String> DURATION_EXAMPLES = Arrays.asList("10s", "5m", "10m", "30m", "1h");
    
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
            return filterStartingWith(SUBCOMMANDS, args[0]);
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "removeword":
                case "setduration":
                    return filterStartingWith(plugin.getBannedWords(), args[1]);
                case "addword":
                default:
                    return Collections.emptyList();
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setduration")) {
            return DURATION_EXAMPLES;
        }

        return Collections.emptyList();
    }
    
    private List<String> filterStartingWith(List<String> options, String prefix) {
        return options.stream()
                .filter(s -> s.startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
