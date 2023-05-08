package us.danny.arrowturret;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static us.danny.arrowturret.Config.*;

public class TurretUpdater extends BukkitRunnable implements Listener{
	
	private final JavaPlugin plugin;
	private final TurretDatabase turretDatabase;
	private final Map<Player, Vector> playerVelocities;

	public TurretUpdater(JavaPlugin plugin, TurretDatabase turretDatabase) {
		this.plugin = plugin;
		this.turretDatabase = turretDatabase;
		playerVelocities = new HashMap<>();
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Vector oldVector = event.getFrom().toVector();
		Vector newVector = event.getTo().toVector();
		Vector playerVelocity = newVector.subtract(oldVector);
		playerVelocities.put(event.getPlayer(), playerVelocity);
	}

	@Override
	public void run() {
		updateAllTurrets();
	}
	
	private void updateAllTurrets() {
		for(Map.Entry<Player, TurretData> entry : turretDatabase.entrySet()) {
			updateTurret(entry.getValue());
		}
	}
	
	private void updateTurret(TurretData turretData) {
		//if the turret is dead, remove from database and message player
		ArmorStand turret = turretData.getTurret();
		if(turret.isDead()) {
			turretDatabase.removeTurret(turretData.getPlayer());
			turretData.getPlayer().sendMessage(TURRET_DEATH_MSG);
			return;
		}
		//if the turret is falling, do nothing
		if(!turret.isOnGround()) {
			return;
		}
		//if turret is active, fire if off cooldown
		if(turretData.isOnCooldown()) {
			turretData.decrementCooldown();
		}
		else {
			shootArrow(turretData);
			turretData.setCooldown(COOLDOWN);
		}
	}
	
	private void shootArrow(TurretData turretData) {
		Player player = turretData.getPlayer();
		ArmorStand turret = turretData.getTurret();
		Location originLocation = turret.getEyeLocation();
		
		PlayerDistTuple playerDistTuple = findClosestEnemyPlayer(
				originLocation, 
				player
		);
		if(playerDistTuple == null) {
			return;
		}
		
		double distanceToClosestEnemyPlayer = playerDistTuple.distance;
		if(distanceToClosestEnemyPlayer > RANGE) {
			return;
		}
		
		Player closestEnemyPlayer = playerDistTuple.player;
		Location enemyLocation = closestEnemyPlayer.getEyeLocation();
		enemyLocation.setY(enemyLocation.getY() - AIM_DOWN_FROM_EYE);
		
		Vector originVector = originLocation.toVector();
		Vector enemyVector = enemyLocation.toVector();
		
		//calculate arrow vector
		//vertical velocity
		double horizontalDist = calculateHorizontalDist(originVector, enemyVector);
		double flightTime = horizontalDist / SPEED;
		double verticalDist = enemyVector.getY() - originVector.getY();
		double gravityDist = 0.5 * ARROW_G * flightTime * flightTime;
		double yVel = (verticalDist + gravityDist) / flightTime;
		//horizontal velocity
		Vector enemyMovementVector = playerVelocities.get(closestEnemyPlayer);
		enemyMovementVector.setY(0);
		enemyMovementVector.multiply(flightTime);
		Vector enemyPredictionVector = new Vector().copy(enemyVector);
		enemyPredictionVector.add(enemyMovementVector);
		Vector attackVector = new Vector().copy(enemyPredictionVector);
		attackVector.subtract(originVector);
		double theta = Math.atan2(attackVector.getZ(), attackVector.getX());
		double xVel = SPEED * Math.cos(theta);
		double zVel = SPEED * Math.sin(theta);
		
		Vector velocity = new Vector(xVel, yVel, zVel);
		
		//spawn arrow
		Arrow arrow = turret.launchProjectile(Arrow.class, velocity);
    	arrow.setOp(false);
    	arrow.setBounce(false);
    	arrow.setShooter(turret);
    	arrow.setVelocity(velocity);
    	arrow.setFireTicks(0);
    	arrow.setKnockbackStrength(KNOCKBACK);
    	arrow.setCritical(false);
    	arrow.spigot().setDamage(DAMAGE);
	}
	
	//may return null if no player found
	private PlayerDistTuple findClosestEnemyPlayer(Location location, Player player) {
		Player closestPlayer = null;
		double distanceToClosestPlayer = Double.MAX_VALUE;
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if(p != player && isValidPlayer(p)){
        	//if(isValidPlayer(p)) {
                double distance = location.distance(p.getLocation());
                if(distance < distanceToClosestPlayer){
                    distanceToClosestPlayer = distance;
                    closestPlayer = p;
                }
            }
        }
        
        if(closestPlayer == null) {
        	return null;
        }
        return new PlayerDistTuple(closestPlayer, distanceToClosestPlayer);
	}
	
	private boolean isValidPlayer(Player p){
		GameMode gamemode = p.getGameMode();
	    return !p.isDead() && 
	    		(gamemode == GameMode.SURVIVAL || gamemode == GameMode.ADVENTURE);
	}
	
	private double calculateHorizontalDist(Vector a, Vector b) {
		double xSquared = Math.pow(a.getX() - b.getX(), 2);
		double zSquared = Math.pow(a.getZ() - b.getZ(), 2);
		return Math.sqrt(xSquared + zSquared);
	}
	
	private static class PlayerDistTuple{
		public final Player player;
		public final double distance;
		
		public PlayerDistTuple(Player player, double distance) {
			this.player = player;
			this.distance = distance;
		}
	}
}
