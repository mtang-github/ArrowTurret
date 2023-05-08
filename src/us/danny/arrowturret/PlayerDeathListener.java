package us.danny.arrowturret;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
	
	private final TurretDatabase turretDatabase;
	
    public PlayerDeathListener(TurretDatabase turretDatabase) {
		this.turretDatabase = turretDatabase;
	}
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	Player player = (Player)event.getEntity();
    	turretDatabase.removeTurret(player);
    }
}
