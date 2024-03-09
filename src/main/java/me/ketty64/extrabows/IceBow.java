package me.ketty64.extrabows;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class IceBow implements Listener {
    private JavaPlugin plugin;

    public IceBow(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("§3§lIceBow");
        List<String> lore = new ArrayList<>();
        lore.add("This Bow creates ice trails when fired");
        meta.setLore(lore);

        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        bow.setItemMeta(meta);
        return bow;
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getBow().getItemMeta().getDisplayName().equals("§3§lIceBow")) {
                Arrow arrow = (Arrow) event.getProjectile();
                World world = player.getWorld();
                world.playSound(arrow.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5f, 1.0f);
                new IceBowTrail(arrow, 20).runTaskTimer(plugin, 0L, 1L);
            }
        }
    }

    private static class IceBowTrail extends BukkitRunnable {
        private final Arrow arrow;
        private final int maxDistance;
        private final Location startLoc;
        private int distanceTraveled;

        public IceBowTrail(Arrow arrow, int maxDistance) {
            this.arrow = arrow;
            this.maxDistance = maxDistance;
            this.startLoc = arrow.getLocation().add(arrow.getVelocity().normalize());
            this.distanceTraveled = 0;
        }

        @Override
        public void run() {
            if (arrow.isOnGround() || arrow.isDead() || distanceTraveled >= maxDistance) {
                this.cancel();
                return;
            }
            Location loc = arrow.getLocation();
            if (loc.distance(startLoc) >= 1.0) {
                Vector direction = arrow.getVelocity().normalize();
                Location nextLoc = startLoc.clone();
                for (int i = 0; i < distanceTraveled; i++) {
                    nextLoc.add(direction);
                    if (nextLoc.getBlock().getType() != Material.ICE) {
                        nextLoc.getBlock().setType(Material.ICE);
                        nextLoc.getWorld().playSound(nextLoc, Sound.BLOCK_GLASS_BREAK, 0.1f, 1.0f);
                        nextLoc.getWorld().spawnParticle(Particle.SNOWBALL, nextLoc, 1, 0, 0, 0, 0);
                        nextLoc.getWorld().spawnParticle(Particle.CRIT_MAGIC, nextLoc, 1, 0, 0, 0, 0);
                    }
                }
                distanceTraveled++;
            }
        }
    }
}