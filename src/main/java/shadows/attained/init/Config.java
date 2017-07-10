package shadows.attained.init;

import net.minecraftforge.common.config.Configuration;
import shadows.attained.proxy.CommonProxy;

public class Config {

	public static int dropChance = 18; // 1/n for Essence drops.
	public static boolean allowBonemeal = false;
	public static int revertChance = 6; // 1/n for soil reversion.
	public static int creatorRadius = 2;
	public static boolean revertToDirt = true;

	public static void syncConfig() {
		try {
			CommonProxy.config.load();
			dropChance = CommonProxy.config.get(Configuration.CATEGORY_GENERAL, "Drop Chance", "18", "The 1/n chance for Essence to drop from mobs.").getInt();
			allowBonemeal = CommonProxy.config.get(Configuration.CATEGORY_GENERAL, "Allow Bonemeal", false, "If bonemealing Attained Plants is allowed").getBoolean();
			revertChance = CommonProxy.config.get(Configuration.CATEGORY_GENERAL, "Revert Chance", 6, "The 1/n chance for soil to revert after a bulb grows. Set to 0 to disable.").getInt();
			creatorRadius = CommonProxy.config.get(Configuration.CATEGORY_GENERAL, "Creator Radius", 2, "The radius of the soil creator.").getInt();
			revertToDirt = CommonProxy.config.get(Configuration.CATEGORY_GENERAL, "Revert to dirt", true, "If Vitalized Soil has a chance to revert to dirt upon growth.  Uses \"Revert Chance\" as its chance.").getBoolean();

		} catch (Exception e) {
		} finally {
			if (CommonProxy.config.hasChanged())
				CommonProxy.config.save();
		}
	}

}