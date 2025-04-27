package id.boenyy.automute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class AutoMuteCommand implements CommandExecutor {
    private final AutoMutePlugin plugin;

    public AutoMuteCommand(AutoMutePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§6§lAutoMute Commands:");
            sender.sendMessage("§e/automute addword <word> §7- Add a banned word");
            sender.sendMessage("§e/automute removeword <word> §7- Remove a banned word");
            sender.sendMessage("§e/automute setduration <word> <duration> §7- Set mute duration for a word");
            sender.sendMessage("§e/automute list §7- Show list of banned word");
            sender.sendMessage("§e/automute reload §7- Reload plugin configuration");
            sender.sendMessage("§7Plugin by §bBoenyy");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addword":
                if (args.length != 2) {
                    sender.sendMessage("§eUsage: /automute addword <word>");
                    return true;
                }
                addWord(sender, args[1]);
                break;

            case "removeword":
                if (args.length != 2) {
                    sender.sendMessage("§eUsage: /automute removeword <word>");
                    return true;
                }
                removeWord(sender, args[1]);
                break;

            case "setduration":
                if (args.length != 3) {
                    sender.sendMessage("§eUsage: /automute setduration <word> <duration>");
                    return true;
                }
                setDuration(sender, args[1], args[2]);
                break;

            case "list":
                listBannedWords(sender);
                break;

            case "reload":
                plugin.reloadSettings();
                sender.sendMessage("§aConfiguration reloaded.");
                break;

            default:
                sender.sendMessage("§cUnknown subcommand.");
        }
        return true;
    }

    private void addWord(CommandSender sender, String word) {
        FileConfiguration cfg = plugin.getConfig();
        List<String> list = cfg.getStringList("bannedWords");
        if (list.contains(word)) {
            sender.sendMessage("§cWord already banned.");
            return;
        }
        list.add(word);
        cfg.set("bannedWords", list);
        plugin.saveConfig();
        plugin.reloadSettings();
        sender.sendMessage("§aBanned word added: " + word);
    }

    private void listBannedWords(CommandSender sender) {
        List<String> list = plugin.getBannedWords();
        if (list.isEmpty()) {
            sender.sendMessage("§cNo banned words found.");
            return;
        }

        FileConfiguration cfg = plugin.getConfig();

        sender.sendMessage("§8===== §bAutoMute Banned Words §8=====");
        sender.sendMessage("§7Creator: §fBoenyy");
        sender.sendMessage("§7Total Banned Words: §f" + list.size());
        sender.sendMessage("§7List:");
        for (String word : list) {
            String duration = cfg.getString("durations." + word);
            if (duration == null) {
                duration = "default";
            }
            sender.sendMessage(" §f- " + word + " (§e" + duration + "§f)");
        }
        sender.sendMessage("§8==========================");
    }

    private void removeWord(CommandSender sender, String word) {
        FileConfiguration cfg = plugin.getConfig();
        List<String> list = cfg.getStringList("bannedWords");
        if (!list.remove(word)) {
            sender.sendMessage("§cWord not found.");
            return;
        }
        cfg.set("durations." + word, null);
        cfg.set("bannedWords", list);
        plugin.saveConfig();
        plugin.reloadSettings();
        sender.sendMessage("§aBanned word removed: " + word);
    }

    private void setDuration(CommandSender sender, String word, String duration) {
        FileConfiguration cfg = plugin.getConfig();
        List<String> list = cfg.getStringList("bannedWords");
        if (!list.contains(word)) {
            sender.sendMessage("§cWord not banned: " + word);
            return;
        }
        cfg.set("durations." + word, duration);
        plugin.saveConfig();
        plugin.reloadSettings();
        sender.sendMessage("§aDuration for '" + word + "' set to: " + duration);
    }
}