package ic2.api.tile;

import net.minecraft.block.Block;

import java.util.HashSet;
import java.util.Set;

public final class ExplosionWhitelist {

	private static Set whitelist = new HashSet();

	public static void addWhitelistedBlock(Block block) {
		whitelist.add(block);
	}

	public static void removeWhitelistedBlock(Block block) {
		whitelist.remove(block);
	}

	public static boolean isBlockWhitelisted(Block block) {
		return whitelist.contains(block);
	}
}
