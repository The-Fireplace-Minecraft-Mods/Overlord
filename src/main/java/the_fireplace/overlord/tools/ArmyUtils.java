package the_fireplace.overlord.tools;

import static the_fireplace.overlord.Overlord.proxy;

/**
 * @author The_Fireplace
 */
public final class ArmyUtils {
	public static String getAttackModeString(byte b) {
		switch (b) {
			case 0:
				return proxy.translateToLocal("skeleton.mode.passive");
			case 2:
				return proxy.translateToLocal("skeleton.mode.aggressive");
			case 1:
			default:
				return proxy.translateToLocal("skeleton.mode.defensive");
		}
	}

	public static String getMovementModeString(byte b) {
		switch (b) {
			case 0:
				return proxy.translateToLocal("skeleton.mode.stationed");
			case 2:
				return proxy.translateToLocal("skeleton.mode.base");
			case 1:
			default:
				return proxy.translateToLocal("skeleton.mode.follower");
		}
	}
}
