package us.danny.arrowturret;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class TurretData {
	private final Player player;
	private final ArmorStand turret;
	private short cooldown;
	
	public TurretData(Player player, ArmorStand turret, short cooldown) {
		this.player = player;
		this.turret = turret;
		this.cooldown = cooldown;
	}

	public boolean isOnCooldown() {
		return cooldown > 0;
	}

	public void setCooldown(short cooldown) {
		this.cooldown = cooldown;
	}
	
	public void decrementCooldown() {
		--cooldown;
	}

	public Player getPlayer() {
		return player;
	}

	public ArmorStand getTurret() {
		return turret;
	}
}
