package us.danny.arrowturret;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static us.danny.arrowturret.Config.*;

public class SpawnTurretListener implements Listener {
	
	private final TurretDatabase turretDatabase;
	
    public SpawnTurretListener(TurretDatabase turretDatabase) {
		this.turretDatabase = turretDatabase;
	}

	@EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	ItemStack item = event.getItem();
    	if(item == null) {
    		return;
    	}
    	
    	if(isValidItem(item)) {
    		Player player = event.getPlayer();
    		World world = player.getWorld();
    		Location spawnLocation = player.getEyeLocation();
    		
    		ArmorStand turret = world.spawn(spawnLocation, ArmorStand.class);
    		ItemStack helmet = new ItemStack(Material.REDSTONE_BLOCK);
    		ItemStack inHand = new ItemStack(Material.BOW);
    		turret.setHelmet(helmet);
    		turret.setItemInHand(inHand);
    		
    		TurretData newTurretData = new TurretData(player, turret, INIT_COOLDOWN);
    		turretDatabase.replaceTurret(newTurretData);
    		
    		player.sendMessage(TURRET_PLACE_MSG);
    		event.setCancelled(true);
    	}
    }
    
    private boolean isValidItem(ItemStack toTest) {
    	if(toTest.hasItemMeta()) {
    		ItemMeta itemMeta = toTest.getItemMeta();
    		if(itemMeta.hasLore()) {
    			List<String> loreList = itemMeta.getLore();
    			return loreList.contains(LORE);
    		}
    	}
    	return false;
    }
}
