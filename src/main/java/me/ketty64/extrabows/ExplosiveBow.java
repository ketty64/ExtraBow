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
    private float explosionRadius;
    private int maxDistance;

    public ExplosiveBow(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfigValues();

    }

    public void reloadConfigValues() {
        explosionRadius = (float) plugin.getConfig().getDouble("explosive-bow.explosion-radius", 4.0);
        maxDistance = plugin.getConfig().getInt("explosive-bow.max-distance", 200);
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
        arrow.setCritical(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
        new ExplosiveBowExplosion(arrow).runTaskTimer(plugin, 1, 1);
    }

    private class ExplosiveBowExplosion extends BukkitRunnable {
        private final Arrow arrow;
        private int ticksLived = 0;

        public ExplosiveBowExplosion(Arrow arrow) {
            this.arrow = arrow;
        }

        @Override
        public void run() {
            if (arrow.isDead() || ticksLived++ >= maxDistance) {
                this.cancel();
                return;
            }

            if (arrow.isOnGround() || arrow.isInBlock()) {
                arrow.getWorld().createExplosion(arrow.getLocation(), explosionRadius); // Create an explosion at the arrow's location
                arrow.remove();
                cancel();
                return;
            }

            for (Entity entity : arrow.getNearbyEntities(2, 2, 2)) {
                if (entity instanceof LivingEntity && entity != arrow.getShooter()) {
                    arrow.getWorld().createExplosion(arrow.getLocation(), explosionRadius); // Create an explosion at the arrow's location
                    arrow.remove();
                    cancel();
                    return;
                }
            }

            // If the arrow has traveled an excessive distance without collisions, create an explosion anyway
            if (ticksLived >= maxDistance) {
                arrow.getWorld().createExplosion(arrow.getLocation(), explosionRadius); // Create an explosion at the arrow's location
                arrow.remove();
                cancel();
            }
        }
    }
}
