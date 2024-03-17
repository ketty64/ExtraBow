package me.ketty64.extrabows;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class HealingBow implements Listener {
    private final JavaPlugin plugin;
    private double healAmount;
    private final String bowName;

    public HealingBow(JavaPlugin plugin) {
        this.plugin = plugin;
        this.bowName = "§a§lHealingBow";
        reloadConfigValues();
        this.healAmount = plugin.getConfig().getDouble("healing-bow.heal-amount", 10.0);
        this.healAmount = this.healAmount;
    }
    public void reloadConfigValues() {
        healAmount = plugin.getConfig().getDouble("healing-bow.heal-amount", 10.0);
    }

    public ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName(bowName);
        List<String> lore = List.of("This Bow heals entities on impact");
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
        if (bowItem == null || !bowItem.hasItemMeta() || !bowItem.getItemMeta().hasDisplayName() || !bowItem.getItemMeta().getDisplayName().equals(bowName))
            return;

        Arrow arrow = (Arrow) event.getProjectile();
        arrow.setShooter(player);
        arrow.setCritical(true); // Render the arrow as critical for visibility
        arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
        arrow.setDamage(0); // Set damage to 0 so entities won't take damage

        new HealingBowEffect(arrow).runTaskTimer(plugin, 1, 1); // Start task immediately and run it every tick
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                event.setCancelled(true); // Cancel damage if the arrow was shot by a player
            }
        }
    }

    private class HealingBowEffect extends BukkitRunnable {
        private final Arrow arrow;

        public HealingBowEffect(Arrow arrow) {
            this.arrow = arrow;
        }

        @Override
        public void run() {
            if (arrow.isDead()) {
                cancel();
                return;
            }

            for (Entity entity : arrow.getNearbyEntities(1, 1, 1)) {
                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    double maxHealth = livingEntity.getMaxHealth();
                    double newHealth = Math.min(maxHealth, livingEntity.getHealth() + (healAmount));
                    livingEntity.setHealth(newHealth);
                    arrow.remove();
                    cancel();
                    return;
                }
            }
        }
    }
}

