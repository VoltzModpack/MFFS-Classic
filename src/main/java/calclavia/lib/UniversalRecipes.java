package calclavia.lib;

import cpw.mods.fml.common.FMLLog;
import ic2.api.item.Items;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class UniversalRecipes {

	private static final String PREFIX = "calclavia:";
	public static final String PRIMARY_METAL = "ingotSteel";
	public static final String PRIMARY_PLATE = "plateSteel";
	public static final String SECONDARY_METAL = "ingotBronze";
	public static final String SECONDARY_PLATE = "plateBronze";
	public static final String CIRCUIT_T1 = "calclavia:CIRCUIT_T1";
	public static final String CIRCUIT_T2 = "calclavia:CIRCUIT_T2";
	public static final String CIRCUIT_T3 = "calclavia:CIRCUIT_T3";
	public static String ADVANCED_BATTERY = "calclavia:ADVANCED_BATTERY";
	public static String BATTERY = "calclavia:BATTERY";
	public static String BATTERY_BOX = "calclavia:BATTERY_BOX";
	public static final String WRENCH = "calclavia:WRENCH";
	public static final String WIRE = "calclavia:WIRE";
	public static final String MOTOR = "calclavia:MOTOR";
	public static boolean isInit = false;

	public static void init() {
		if (!isInit) {
			register("calclavia:CIRCUIT_T1", "circuitBasic", Items.getItem("electronicCircuit"), new ItemStack(Block.torchRedstoneIdle));
			register("calclavia:CIRCUIT_T2", "circuitAdvanced", Items.getItem("advancedCircuit"), new ItemStack(Item.redstoneRepeater));
			register("calclavia:CIRCUIT_T3", "circuitElite", Items.getItem("iridiumPlate"), new ItemStack(Block.redstoneComparatorIdle));
			register(ADVANCED_BATTERY, "advancedBattery", Items.getItem("energyCrystal"), "battery", new ItemStack(Item.redstoneRepeater));
			register(BATTERY, "battery", Items.getItem("reBattery"), new ItemStack(Item.redstoneRepeater));
			register(BATTERY_BOX, "batteryBox", Items.getItem("batBox"), new ItemStack(Block.blockGold));
			register("calclavia:WRENCH", "wrench", Items.getItem("wrench"), new ItemStack(Item.axeIron));
			register("calclavia:WIRE", "copperWire", "copperCableBlock", new ItemStack(Item.redstone));
			register("calclavia:MOTOR", "motor", Items.getItem("generator"), new ItemStack(Block.pistonBase));
			isInit = true;
		}

	}

	public static void register(String name, Object... possiblities) {
		Object[] arr$ = possiblities;
		int len$ = possiblities.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Object possiblity = arr$[i$];
			if (possiblity instanceof ItemStack) {
				if (registerItemStacksToDictionary(name, (ItemStack) possiblity)) {
					break;
				}
			} else if (possiblity instanceof String) {
				if (registerItemStacksToDictionary(name, (String) possiblity)) {
					break;
				}
			} else {
				FMLLog.severe("Universal Recipes: Error Registering " + name, new Object[0]);
			}
		}

	}

	public static boolean registerItemStacksToDictionary(String name, List itemStacks) {
		boolean returnValue = false;
		if (itemStacks != null && itemStacks.size() > 0) {
			Iterator i$ = itemStacks.iterator();

			while (i$.hasNext()) {
				ItemStack stack = (ItemStack) i$.next();
				if (stack != null) {
					OreDictionary.registerOre(name, stack);
					returnValue = true;
				}
			}
		}

		return returnValue;
	}

	public static boolean registerItemStacksToDictionary(String name, ItemStack... itemStacks) {
		return registerItemStacksToDictionary(name, Arrays.asList(itemStacks));
	}

	public static boolean registerItemStacksToDictionary(String name, String stackName) {
		return registerItemStacksToDictionary(name, (List) OreDictionary.getOres(stackName));
	}
}
