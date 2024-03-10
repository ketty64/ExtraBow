package me.ketty64.extrabows;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements CommandExecutor {

    private IceBow iceBow;
    private LightningBow lightningBow;

    @Override
    public void onEnable() {
        getLogger().info("@ ExtraBows plugin enabled.");
        iceBow = new IceBow(this);
        lightningBow = new LightningBow(this);
        getServer().getPluginManager().registerEvents(iceBow, this);
        getServer().getPluginManager().registerEvents(lightningBow, this);

        // Registra il comando 'extrabows' con il plugin
        this.getCommand("extrabows").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("extrabows")) {
            if (args.length == 0) {
                player.sendMessage("§bRunning §9§lExtraBows 1.0 §bby §9§lketty64");
                return true;
            } else if (args.length == 1) {
                String commandArg = args[0].toLowerCase();
                if (commandArg.equals("icebow")) {
                    player.getInventory().addItem(iceBow.getBow());
                    player.sendMessage("§bYou have received an §3§lIceBow!");
                    return true;
                } else if (commandArg.equals("lightningbow")) {
                    player.getInventory().addItem(lightningBow.getBow());
                    player.sendMessage("§bYou have received a §e§lLightningBow!");
                    return true;
                } else {
                    player.sendMessage("§3§lExtrabows: §cinvalid command!");
                    return true;
                }
            }
        }
        return false;
    }




@Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("extrabows") || cmd.getName().equalsIgnoreCase("eb")) {
            if (args.length == 1) {
                completions.add("icebow");
                completions.add("lightningbow");
            }
        }
        return completions;
    }
}
