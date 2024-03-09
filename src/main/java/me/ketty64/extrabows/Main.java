package me.ketty64.extrabows;



import me.ketty64.extrabows.IceBow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private IceBow iceBow;

    @Override
    public void onEnable() {
        getLogger().info("@ ExtraBows plugin enabled.");
        iceBow = new IceBow(this);
        getServer().getPluginManager().registerEvents(iceBow, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("@ ExtraBows plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("extrabows") || cmd.getName().equalsIgnoreCase("eb")) {
            if (args.length >= 1 && args[0].equalsIgnoreCase("icebow")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.getInventory().addItem(iceBow.getBow());
                    player.sendMessage("§bYou have received an §3§lIceBow!");
                    return true;
                } else {
                    sender.sendMessage("This command can only be executed by a player.");
                    return true;
                }
            } else {
                sender.sendMessage("§bRunning §9§lExtraBows 1.0 §bby §9§lketty64");
                return true;
            }
        }
        return false;
    }
}