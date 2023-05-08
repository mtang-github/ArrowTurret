package us.danny.arrowturret;

public class Config {
	
	public static final String PLUGIN_NAME = "ArrowTurret";
	public static final String LORE = "Spawn a turret where you stand.";
	public static final String TURRET_PLACE_MSG = "You have placed your turret.";
	public static final String TURRET_DEATH_MSG = "Your turret has been destroyed.";
	
	public static final short COOLDOWN = 7;
	public static final short INIT_COOLDOWN = 40;
	public static final double RANGE = 40.0;
	public static final double SPEED = 1.75;
	public static final double AIM_DOWN_FROM_EYE = 0.2;
	//arrow g is 0.05 blocks/ticks^2
	//see https://minecraft.fandom.com/wiki/Entity#Motion_of_entities
	public static final double ARROW_G = 0.05;
	public static final int KNOCKBACK = 1;
	public static final double DAMAGE = 0.3;
	
	private Config() {}
}