package id.boenyy.automute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class AutoMuteCommand implements CommandExecutor {

    private static final String PREFIX = AutoMutePlugin.getInstance().getConfig().getString("prefix");
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addword":
                if (validateArgs(sender, args, 2, "§eUsage: /automute addword <word>")) {
                    addWord(sender, args[1]);
                }
                break;

            case "removeword":
                if (validateArgs(sender, args, 2, "§eUsage: /automute removeword <word>")) {
                    removeWord(sender, args[1]);
                }
                break;

            case "setduration":
                if (validateArgs(sender, args, 3, "§eUsage: /automute setduration <word> <duration>")) {
                    setDuration(sender, args[1], args[2]);
                }
                break;

            case "list":
                listBannedWords(sender);
                break;

            case "reload":
                AutoMutePlugin.getInstance().reloadSettings();
                sender.sendMessage(PREFIX + "§aConfiguration reloaded.");
                break;

            default:
                sender.sendMessage(PREFIX + "§cUnknown subcommand.");
        }
        return true;
    }
    
    private boolean validateArgs(CommandSender sender, String[] args, int required, String usage) {
        if (args.length != required) {
            sender.sendMessage(PREFIX + usage);
            return false;
        }
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6§lAutoMute Commands:");
        sender.sendMessage("§e/automute addword <word> §7- Add a banned word");
        sender.sendMessage("§e/automute removeword <word> §7- Remove a banned word");
        sender.sendMessage("§e/automute setduration <word> <duration> §7- Set mute duration for a word");
        sender.sendMessage("§e/automute list §7- Show list of banned words");
        sender.sendMessage("§e/automute reload §7- Reload plugin configuration");
        sender.sendMessage("§7Plugin by §bBoenyy");
    }

    private void addWord(CommandSender sender, String word) {
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        FileConfiguration cfg = plugin.getConfig();
        List<String> list = cfg.getStringList("bannedWords");
        
        if (list.contains(word)) {
            sender.sendMessage(PREFIX + "§cWord already banned.");
            return;
        }
        
        list.add(word);
        cfg.set("bannedWords", list);
        plugin.saveConfig();
        plugin.reloadSettings();
        sender.sendMessage(PREFIX + "§aBanned word added: " + word);
    }

    private void listBannedWords(CommandSender sender) {
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        List<String> list = plugin.getBannedWords();
        
        if (list.isEmpty()) {
            sender.sendMessage(PREFIX + "§cNo banned words found.");
            return;
        }

        FileConfiguration cfg = plugin.getConfig();

        sender.sendMessage("§8===== §bAutoMute Banned Words §8=====");
        sender.sendMessage("§7Creator: §fBoenyy");
        sender.sendMessage("§7Total Banned Words: §f" + list.size());
        sender.sendMessage("§7List:");
        
        for (String word : list) {
            String duration = cfg.getString("durations." + word, "default");
            sender.sendMessage(" §f- " + word + " (§e" + duration + "§f)");
        }
        
        sender.sendMessage("§8==========================");
    }

    private void removeWord(CommandSender sender, String word) {
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        FileConfiguration cfg = plugin.getConfig();
        List<String> list = cfg.getStringList("bannedWords");
        
        if (!list.remove(word)) {
            sender.sendMessage(PREFIX + "§cWord not found.");
            return;
        }
        
        cfg.set("durations." + word, null);
        cfg.set("bannedWords", list);
        plugin.saveConfig();
        plugin.reloadSettings();
        sender.sendMessage(PREFIX + "§aBanned word removed: " + word);
    }

    private void setDuration(CommandSender sender, String word, String duration) {
        AutoMutePlugin plugin = AutoMutePlugin.getInstance();
        FileConfiguration cfg = plugin.getConfig();
        List<String> list = cfg.getStringList("bannedWords");
        
        if (!list.contains(word)) {
            sender.sendMessage(PREFIX + "§cWord not banned: " + word);
            return;
        }
        
        cfg.set("durations." + word, duration);
        plugin.saveConfig();
        plugin.reloadSettings();
        sender.sendMessage(PREFIX + "§aDuration for '" + word + "' set to: " + duration);
    }
}