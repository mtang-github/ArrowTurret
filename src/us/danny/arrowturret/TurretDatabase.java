package us.danny.arrowturret;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class TurretDatabase extends HashMap<Player, TurretData> {

	private static final long serialVersionUID = -2305581871709431702L;

	public TurretDatabase() {
		super();
	}

	public TurretDatabase(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public TurretDatabase(int initialCapacity) {
		super(initialCapacity);
	}

	public TurretDatabase(Map<? extends Player, ? extends TurretData> m) {
		super(m);
	}
	
	public boolean removeTurret(Player player) {
		TurretData turretData = remove(player);
		if(turretData != null) {
			ArmorStand turret = turretData.getTurret();
			turret.remove();
			return true;
		}
		return false;
	}
	
	public void removeAllTurrets() {
		for(Map.Entry<Player, TurretData> entry : entrySet()) {
			ArmorStand turret = entry.getValue().getTurret();
			turret.remove();
		}
	}
	
	public void replaceTurret(TurretData turretData) {
		TurretData oldTurretData = put(turretData.getPlayer(), turretData);
		if(oldTurretData != null) {
			ArmorStand turret = oldTurretData.getTurret();
			turret.remove();
		}
	}
}
