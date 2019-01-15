package buildcraft.api.core;

import net.minecraft.block.Block;

public class BuildCraftAPI {

	public static final int LAST_ORIGINAL_BLOCK = 122;
	public static final int LAST_ORIGINAL_ITEM = 126;
	public static final boolean[] softBlocks;

	static {
		softBlocks = new boolean[Block.blocksList.length];
	}
}
