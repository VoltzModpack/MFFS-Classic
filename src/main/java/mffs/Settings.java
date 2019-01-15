package mffs;

import cpw.mods.fml.common.Loader;
import mffs.api.Blacklist;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Settings {

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "Modular Force Field System.cfg"));
	public static final int BLOCK_ID_PREFIX = 1680;
	public static final int ITEM_ID_PREFIX = 11130;
	private static int NEXT_BLOCK_ID = 1680;
	private static int NEXT_ITEM_ID = 11130;
	public static int MAX_FORCE_FIELDS_PER_TICK = 1000;
	public static int MAX_FORCE_FIELD_SCALE = 200;
	public static boolean INTERACT_CREATIVE = true;
	public static boolean LOAD_CHUNKS = true;
	public static boolean OP_OVERRIDE = true;
	public static boolean USE_CACHE = true;
	public static boolean ENABLE_ELECTRICITY = true;
	public static boolean CONSERVE_PACKETS = true;
	public static boolean HIGH_GRAPHICS = true;
	public static int INTERDICTION_MURDER_ENERGY = 0;
	public static final int MAX_FREQUENCY_DIGITS = 6;
	public static boolean ENABLE_MANIPULATOR = true;

	public static int getNextBlockID() {
		++NEXT_BLOCK_ID;
		return NEXT_BLOCK_ID;
	}

	public static int getNextItemID() {
		++NEXT_ITEM_ID;
		return NEXT_ITEM_ID;
	}

	public static void load() {
		CONFIGURATION.load();
		ENABLE_MANIPULATOR = CONFIGURATION.get("general", "Enable Force Manipulator", ENABLE_MANIPULATOR).getBoolean(ENABLE_MANIPULATOR);
		Property propFieldScale = CONFIGURATION.get("general", "Max Force Field Scale", MAX_FORCE_FIELD_SCALE);
		MAX_FORCE_FIELD_SCALE = propFieldScale.getInt(MAX_FORCE_FIELD_SCALE);
		Property propInterdiction = CONFIGURATION.get("general", "Interdiction Murder Fortron Consumption", INTERDICTION_MURDER_ENERGY);
		INTERDICTION_MURDER_ENERGY = propInterdiction.getInt(INTERDICTION_MURDER_ENERGY);
		Property propCreative = CONFIGURATION.get("general", "Effect Creative Players", INTERACT_CREATIVE);
		propCreative.comment = "Should the interdiction matrix interact with creative players?.";
		INTERACT_CREATIVE = propCreative.getBoolean(INTERACT_CREATIVE);
		Property propChunkLoading = CONFIGURATION.get("general", "Load Chunks", LOAD_CHUNKS);
		propChunkLoading.comment = "Set this to false to turn off the MFFS Chunkloading capabilities.";
		LOAD_CHUNKS = propChunkLoading.getBoolean(LOAD_CHUNKS);
		Property propOpOverride = CONFIGURATION.get("general", "Op Override", OP_OVERRIDE);
		propOpOverride.comment = "Allow the operator(s) to override security measures created by MFFS?";
		OP_OVERRIDE = propOpOverride.getBoolean(OP_OVERRIDE);
		Property propUseCache = CONFIGURATION.get("general", "Use Cache", USE_CACHE);
		propUseCache.comment = "Cache allows temporary data saving to decrease calculations required.";
		USE_CACHE = propUseCache.getBoolean(USE_CACHE);
		Property maxFFGenPerTick = CONFIGURATION.get("general", "Field Calculation Per Tick", MAX_FORCE_FIELDS_PER_TICK);
		maxFFGenPerTick.comment = "How many force field blocks can be generated per tick? Less reduces lag.";
		MAX_FORCE_FIELDS_PER_TICK = maxFFGenPerTick.getInt(MAX_FORCE_FIELDS_PER_TICK);
		Property useElectricity = CONFIGURATION.get("general", "Require Electricity?", ENABLE_ELECTRICITY);
		useElectricity.comment = "Turning this to false will make MFFS run without electricity or energy systems required. Great for vanilla!";
		ENABLE_ELECTRICITY = useElectricity.getBoolean(ENABLE_ELECTRICITY);
		Property conservePackets = CONFIGURATION.get("general", "Conserve Packets?", CONSERVE_PACKETS);
		conservePackets.comment = "Turning this to false will enable better client side packet and updates but in the cost of more packets sent.";
		CONSERVE_PACKETS = conservePackets.getBoolean(CONSERVE_PACKETS);
		Property highGraphics = CONFIGURATION.get("general", "High Graphics", HIGH_GRAPHICS);
		highGraphics.comment = "Turning this to false will reduce rendering and client side packet graphical packets.";
		CONSERVE_PACKETS = highGraphics.getBoolean(HIGH_GRAPHICS);
		Property forceManipulatorBlacklist = CONFIGURATION.get("general", "Force Manipulator Blacklist", "");
		highGraphics.comment = "Put a list of block IDs to be not-moved by the force manipulator. Separate by commas, no space.";
		String blackListString = forceManipulatorBlacklist.getString();
		String blockIDString;
		int blockID;
		if (blackListString != null) {
			String[] arr$ = blackListString.split(",");
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				blockIDString = arr$[i$];
				if (blockIDString != null && !blockIDString.isEmpty()) {
					try {
						blockID = Integer.parseInt(blockIDString);
						Blacklist.forceManipulationBlacklist.add(Block.blocksList[blockID]);
					} catch (Exception var23) {
						ModularForceFieldSystem.LOGGER.severe("Invalid block blacklist ID!");
						var23.printStackTrace();
					}
				}
			}
		}

		Property blacklist1 = CONFIGURATION.get("general", "Stabilization Blacklist", "");
		String blackListString1 = blacklist1.getString();
		int blockID;
		if (blackListString1 != null) {
			String[] arr$ = blackListString1.split(",");
			int len$ = arr$.length;

			for (blockID = 0; blockID < len$; ++blockID) {
				String blockIDString = arr$[blockID];
				if (blockIDString != null && !blockIDString.isEmpty()) {
					try {
						blockID = Integer.parseInt(blockIDString);
						Blacklist.stabilizationBlacklist.add(Block.blocksList[blockID]);
					} catch (Exception var22) {
						ModularForceFieldSystem.LOGGER.severe("Invalid block blacklist ID!");
						var22.printStackTrace();
					}
				}
			}
		}

		Property blacklist2 = CONFIGURATION.get("general", "Disintegration Blacklist", "");
		blockIDString = blacklist1.getString();
		if (blockIDString != null) {
			String[] arr$ = blockIDString.split(",");
			int len$ = arr$.length;

			for (blockID = 0; blockID < len$; ++blockID) {
				String blockIDString = arr$[blockID];
				if (blockIDString != null && !blockIDString.isEmpty()) {
					try {
						int blockID = Integer.parseInt(blockIDString);
						Blacklist.disintegrationBlacklist.add(Block.blocksList[blockID]);
					} catch (Exception var21) {
						ModularForceFieldSystem.LOGGER.severe("Invalid block blacklist ID!");
						var21.printStackTrace();
					}
				}
			}
		}

		Blacklist.stabilizationBlacklist.add(Blocks.water);
		Blacklist.stabilizationBlacklist.add(Blocks.flowing_water);
		Blacklist.stabilizationBlacklist.add(Blocks.lava);
		Blacklist.stabilizationBlacklist.add(Blocks.flowing_lava);

		Blacklist.disintegrationBlacklist.add(Blocks.water);
		Blacklist.disintegrationBlacklist.add(Blocks.flowing_water);
		Blacklist.disintegrationBlacklist.add(Blocks.lava);
		Blacklist.stabilizationBlacklist.add(Blocks.flowing_lava);

		Blacklist.forceManipulationBlacklist.add(Blocks.bedrock);
		Blacklist.forceManipulationBlacklist.add(ModularForceFieldSystem.blockForceField);
		CONFIGURATION.save();
	}
}
