package me.ketty64.extrabows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
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

public class ExplosiveBow implements Listener {
    private final JavaPlugin plugin;

    public ExplosiveBow(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("§c§lExplosiveBow");
        List<String> lore = new ArrayList<>();
        lore.add("This Bow creates explosions on impact");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        bow.setItemMeta(meta);
        return bow;
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        ItemStack bowItem = event.getBow();
        if (bowItem == null || !bowItem.hasItemMeta() || !bowItem.getItemMeta().hasDisplayName() || !bowItem.getItemMeta().getDisplayName().equals("§c§lExplosiveBow"))
            return;

        Arrow arrow = (Arrow) event.getProjectile();
        arrow.setShooter(player);
        arrow.setCritical(true); // Render the arrow as critical for visibility
        arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
        new ExplosiveBowExplosion(arrow).runTaskTimer(plugin, 1, 1); // Start task immediately and run it every tick
    }

    private static class ExplosiveBowExplosion extends BukkitRunnable {
        private final Arrow arrow;
        private static final int MAX_DISTANCE = 200; // Increase the maximum distance checked
        private int ticksLived = 0;

        public ExplosiveBowExplosion(Arrow arrow) {
            this.arrow = arrow;
        }

        @Override
        public void run() {
            if (arrow.isDead() || ticksLived++ >= MAX_DISTANCE) {
                this.cancel();
                return;
            }

            if (arrow.isOnGround() || arrow.isInBlock()) {
                arrow.getWorld().createExplosion(arrow.getLocation(), 4.0f); // Create an explosion at the arrow's location
                arrow.remove();
                cancel();
                return;
            }

            for (Entity entity : arrow.getNearbyEntities(2, 2, 2)) { // Increase the size of entity search
                if (entity instanceof LivingEntity && entity != arrow.getShooter()) {
                    arrow.getWorld().createExplosion(arrow.getLocation(), 4.0f); // Create an explosion at the arrow's location
                    arrow.remove();
                    cancel();
                    return;
                }
            }

            // If the arrow has traveled an excessive distance without collisions, create an explosion anyway
            if (ticksLived >= MAX_DISTANCE) {
                arrow.getWorld().createExplosion(arrow.getLocation(), 4.0f); // Create an explosion at the arrow's location
                arrow.remove();
                cancel();
                return;
            }
        }
    }
}
