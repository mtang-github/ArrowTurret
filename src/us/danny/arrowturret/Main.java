package us.danny.arrowturret;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static us.danny.arrowturret.Config.*;

public class Main extends JavaPlugin {
	
	private final TurretDatabase turretDatabase;
	
	public Main() {
		turretDatabase = new TurretDatabase();
	}
	
	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		
		//SpawnTurretListener allows for placing turrets with item
		SpawnTurretListener spawnTurretListener = new SpawnTurretListener( 
				turretDatabase
		);
        pluginManager.registerEvents(spawnTurretListener, this);
        
        //PlayerDeathListener destroys turret when player dies
        PlayerDeathListener playerDeathListener = new PlayerDeathListener(
        		turretDatabase
        );
        pluginManager.registerEvents(playerDeathListener, this);
        
        //TurretUpdater shoots arrows from turrets
        TurretUpdater turretUpdater = new TurretUpdater(this, turretDatabase);
        pluginManager.registerEvents(turretUpdater, this);
		turretUpdater.runTaskTimer(this, 0, 1);
		
		System.out.println(PLUGIN_NAME + " enabled");
	}
	
	@Override
	public void onDisable() {
		//When game ends, kill all turrets
		turretDatabase.removeAllTurrets();
		System.out.println(PLUGIN_NAME + " disabled");
	}
}
