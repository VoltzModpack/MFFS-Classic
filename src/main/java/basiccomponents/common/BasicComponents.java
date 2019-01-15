package basiccomponents.common;

import basiccomponents.client.RenderCopperWire;
import basiccomponents.common.block.BlockBase;
import basiccomponents.common.block.BlockBasicMachine;
import basiccomponents.common.block.BlockCopperWire;
import basiccomponents.common.item.*;
import basiccomponents.common.tileentity.TileEntityBatteryBox;
import basiccomponents.common.tileentity.TileEntityCoalGenerator;
import basiccomponents.common.tileentity.TileEntityCopperWire;
import basiccomponents.common.tileentity.TileEntityElectricFurnace;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class BasicComponents {

	public static final String NAME = "Basic Components";
	public static String CHANNEL = "";
	public static final String RESOURCE_PATH = "/mods/basiccomponents/";
	public static CommonProxy proxy;
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "BasicComponents.cfg"));
	public static final String TEXTURE_DIRECTORY = "/mods/basiccomponents/textures/";
	public static final String GUI_DIRECTORY = "/mods/basiccomponents/textures/gui/";
	public static final String BLOCK_TEXTURE_DIRECTORY = "/mods/basiccomponents/textures/blocks/";
	public static final String ITEM_TEXTURE_DIRECTORY = "/mods/basiccomponents/textures/items/";
	public static final String MODEL_TEXTURE_DIRECTORY = "/mods/basiccomponents/textures/models/";
	public static final String TEXTURE_NAME_PREFIX = "basiccomponents:";
	public static final String LANGUAGE_PATH = "/mods/basiccomponents/languages/";
	private static final String[] LANGUAGES_SUPPORTED = new String[]{"en_US", "zh_CN", "es_ES", "it_IT", "nl_NL", "de_DE"};
	public static final int BLOCK_ID_PREFIX = 3970;
	public static final int ITEM_ID_PREFIX = 13970;
	public static Block blockOreCopper;
	public static final int idOreCopper = 3970;
	public static Block blockOreTin;
	public static final int idOreTin = 3971;
	public static Block blockCopperWire;
	public static final int idCopperWire = 3972;
	public static Block blockMachine;
	public static final int idMachine = 3973;
	public static Item itemBattery;
	public static final int idBattery = 13970;
	public static Item itemInfiniteBattery;
	public static final int idInfiniteBattery = 13971;
	public static Item itemWrench;
	public static final int idWrench = 13972;
	public static Item itemMotor;
	public static final int idMotor = 13973;
	public static Item itemCircuitBasic;
	public static final int idCircuitBasic = 13974;
	public static Item itemCircuitAdvanced;
	public static final int idCircuitAdvanced = 13975;
	public static Item itemCircuitElite;
	public static final int idCircuitElite = 13976;
	public static Item itemPlateCopper;
	public static final int idPlateCopper = 13977;
	public static Item itemPlateTin;
	public static final int idPlateTin = 13978;
	public static Item itemPlateBronze;
	public static final int idPlateBronze = 13979;
	public static Item itemPlateSteel;
	public static final int idPlateSteel = 13980;
	public static Item itemPlateIron;
	public static final int idPlateIron = 13981;
	public static Item itemPlateGold;
	public static final int idPlateGold = 13982;
	public static Item itemIngotCopper;
	public static final int idIngotCopper = 13983;
	public static Item itemIngotTin;
	public static final int idIngotTin = 13984;
	public static Item itemIngotSteel;
	public static final int idIngotSteel = 13985;
	public static Item itemIngotBronze;
	public static final int idIngotBronze = 13986;
	public static Item itemDustSteel;
	public static final int idDustSteel = 13987;
	public static Item itemDustBronze;
	public static final int idDustBronze = 13988;
	public static OreGenBase generationOreCopper;
	public static OreGenBase generationOreTin;
	public static boolean INITIALIZED = false;
	private static boolean registeredTileEntities = false;
	public static final ArrayList bcDependants = new ArrayList();
	private static int NEXT_BLOCK_ID = 3970;
	private static int NEXT_ITEM_ID = 13970;

	public static void init() {
		if (!INITIALIZED) {
			System.out.println("Basic Components Loaded: " + TranslationHelper.loadLanguages("/mods/basiccomponents/languages/", LANGUAGES_SUPPORTED) + " Languages.");
			INITIALIZED = true;
		}

	}

	public static Item requireItem(String name, int id) {
		init();

		try {
			Field field = ReflectionHelper.findField(BasicComponents.class, new String[]{"item" + Character.toUpperCase(name.charAt(0)) + name.substring(1)});
			Item f = (Item) field.get((Object) null);
			Field idField = ReflectionHelper.findField(BasicComponents.class, new String[]{"id" + Character.toUpperCase(name.charAt(0)) + name.substring(1)});
			id = id <= 0 ? (Integer) idField.get((Object) null) : id;
			if (f == null) {
				CONFIGURATION.load();
				Item item;
				if (name.contains("ingot")) {
					field.set((Object) null, new ItemIngot(name, id));
				} else if (name.contains("plate")) {
					field.set((Object) null, new ItemPlate(name, id));
					item = (Item) field.get((Object) null);
					String ingotName = name.replaceAll("plate", "ingot");
					RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"!!", "!!", '!', ingotName}), CONFIGURATION, true);
					Item itemIngot = null;
					if (OreDictionary.getOres(ingotName).size() > 0) {
						itemIngot = ((ItemStack) OreDictionary.getOres(ingotName).get(0)).getItem();
					}

					if (name.equals("plateIron")) {
						itemIngot = Item.ingotIron;
					} else if (name.equals("plateGold")) {
						itemIngot = Item.ingotGold;
					}

					if (itemIngot != null) {
						RecipeHelper.addRecipe(new ShapelessOreRecipe(new ItemStack(itemIngot, 4), new Object[]{item}), CONFIGURATION, true);
						RecipeHelper.addRecipe(new ShapelessOreRecipe(item, new Object[]{new ItemStack(itemIngot), new ItemStack(itemIngot), new ItemStack(itemIngot), new ItemStack(itemIngot)}), CONFIGURATION, true);
					}
				} else if (name.contains("dust")) {
					field.set((Object) null, (new ItemBase(name, id)).setCreativeTab(CreativeTabs.tabMaterials));
					item = (Item) field.get((Object) null);
					if (name.equals("dustBronze")) {
						RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"!#!", '!', "ingotCopper", '#', "ingotTin"}), CONFIGURATION, true);
						if (OreDictionary.getOres("ingotBronze").size() > 0) {
							GameRegistry.addSmelting(item.itemID, (ItemStack) OreDictionary.getOres("ingotBronze").get(0), 0.6F);
						}
					} else if (name.equals("dustSteel")) {
						RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{" C ", "CIC", " C ", 'I', Item.ingotIron, 'C', Item.coal}), CONFIGURATION, true);
						if (OreDictionary.getOres("ingotSteel").size() > 0) {
							GameRegistry.addSmelting(item.itemID, (ItemStack) OreDictionary.getOres("ingotSteel").get(0), 0.8F);
						}
					}
				} else if (name.equals("wrench")) {
					field.set((Object) null, new ItemWrench(id));
					item = (Item) field.get((Object) null);
					if (OreDictionary.getOres("ingotSteel").size() > 0) {
						RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{" S ", " SS", "S  ", 'S', "ingotSteel"}), CONFIGURATION, true);
					} else {
						RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{" S ", " SS", "S  ", 'S', Item.ingotIron}), CONFIGURATION, true);
					}
				} else if (name.equals("battery")) {
					field.set((Object) null, new ItemBattery(name, id));
					RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(itemBattery), new Object[]{" T ", "TRT", "TCT", 'T', "ingotTin", 'R', Item.redstone, 'C', Item.coal}), CONFIGURATION, true);
					OreDictionary.registerOre(name, ElectricItemHelper.getUncharged(itemBattery));
				} else if (name.equals("infiniteBattery")) {
					itemInfiniteBattery = new ItemInfiniteBattery(name, id);
					OreDictionary.registerOre(name, ElectricItemHelper.getUncharged(itemInfiniteBattery));
				} else {
					field.set((Object) null, (new ItemBase(name, id)).setCreativeTab(CreativeTabs.tabMaterials));
					item = (Item) field.get((Object) null);
					if (name.equals("circuitBasic")) {
						if (OreDictionary.getOres("copperWire").size() > 0) {
							RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"!#!", "#@#", "!#!", '@', "plateBronze", '#', Item.redstone, '!', "copperWire"}), CONFIGURATION, true);
							RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"!#!", "#@#", "!#!", '@', "plateSteel", '#', Item.redstone, '!', "copperWire"}), CONFIGURATION, true);
						} else {
							RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"!#!", "#@#", "!#!", '@', "plateBronze", '#', Item.redstone, '!', Block.redstoneComparatorIdle}), CONFIGURATION, true);
							RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"!#!", "#@#", "!#!", '@', "plateSteel", '#', Item.redstone, '!', Block.redstoneComparatorIdle}), CONFIGURATION, true);
						}
					} else if (name.equals("circuitAdvanced")) {
						RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"@@@", "#?#", "@@@", '@', Item.redstone, '?', Item.diamond, '#', "circuitBasic"}), CONFIGURATION, true);
					} else if (name.equals("circuitElite")) {
						RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"@@@", "?#?", "@@@", '@', Item.ingotGold, '?', "circuitAdvanced", '#', Block.blockLapis}), CONFIGURATION, true);
					} else if (name.equals("motor")) {
						if (OreDictionary.getOres("copperWire").size() > 0) {
							RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"@!@", "!#!", "@!@", '!', "ingotSteel", '#', Item.ingotIron, '@', "copperWire"}), CONFIGURATION, true);
						} else {
							RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(item), new Object[]{"@!@", "!#!", "@!@", '!', "ingotSteel", '#', Item.ingotIron, '@', Block.redstoneComparatorIdle}), CONFIGURATION, true);
						}
					}
				}

				item = (Item) field.get((Object) null);
				OreDictionary.registerOre(name, item);
				CONFIGURATION.save();
				FMLLog.info("Basic Components: Successfully requested item: " + name, new Object[0]);
				return item;
			} else {
				return f;
			}
		} catch (Exception var8) {
			FMLLog.severe("Basic Components: Failed to require item: " + name, new Object[0]);
			var8.printStackTrace();
			return null;
		}
	}

	public static Item requestItem(String name, int id) {
		if (OreDictionary.getOres(name).size() > 0 || name.equals("wrench") && Loader.isModLoaded("BuildCraft|Core")) {
			FMLLog.info("Basic Components: " + name + " already exists in Ore Dictionary, using the ore instead.", new Object[0]);
			return OreDictionary.getOres(name).size() > 0 ? ((ItemStack) OreDictionary.getOres(name).get(0)).getItem() : null;
		} else {
			return requireItem(name, id);
		}
	}

	public static Block requireBlock(String name, int id) {
		init();

		try {
			Field field = ReflectionHelper.findField(BasicComponents.class, new String[]{"block" + Character.toUpperCase(name.charAt(0)) + name.substring(1)});
			Block f = (Block) field.get((Object) null);
			Field idField = ReflectionHelper.findField(BasicComponents.class, new String[]{"id" + Character.toUpperCase(name.charAt(0)) + name.substring(1)});
			id = id <= 0 ? (Integer) idField.get((Object) null) : id;
			if (f == null) {
				CONFIGURATION.load();
				Block block;
				if (name.equals("copperWire")) {
					field.set((Object) null, new BlockCopperWire(id));
					GameRegistry.registerBlock((Block) field.get((Object) null), ItemBlockCopperWire.class, name);
					if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
						try {
							registerCopperWireRenderer();
						} catch (Exception var8) {
							FMLLog.severe("Basic Components copper wire registry error!", new Object[0]);
							var8.printStackTrace();
						}
					}

					GameRegistry.registerTileEntity(TileEntityCopperWire.class, "copperWire");
					RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(blockCopperWire, 6), new Object[]{"WWW", "CCC", "WWW", 'W', Block.cloth, 'C', "ingotCopper"}), CONFIGURATION, true);
					UniversalElectricity.isNetworkActive = true;
				} else if (name.contains("ore")) {
					field.set((Object) null, new BlockBase(name, id));
					block = (Block) field.get((Object) null);
					GameRegistry.registerBlock(block, name);
					String ingotName = name.replaceAll("ore", "ingot");
					if (OreDictionary.getOres(ingotName).size() > 0) {
						GameRegistry.addSmelting(block.blockID, (ItemStack) OreDictionary.getOres(ingotName).get(0), 0.6F);
					}

					Field generationField = ReflectionHelper.findField(BasicComponents.class, new String[]{"generation" + Character.toUpperCase(name.charAt(0)) + name.substring(1)});
					generationField.set((Object) null, (new OreGenReplaceStone(name, name, new ItemStack(block), 60, 22, 4)).enable(CONFIGURATION));
					OreGenerator.addOre((OreGenReplaceStone) generationField.get((Object) null));
				}

				block = (Block) field.get((Object) null);
				OreDictionary.registerOre(name, block);
				CONFIGURATION.save();
				FMLLog.info("Basic Components: Successfully requested block: " + name, new Object[0]);
				return block;
			} else {
				return f;
			}
		} catch (Exception var9) {
			FMLLog.severe("Basic Components: Failed to require block: " + name, new Object[0]);
			var9.printStackTrace();
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerCopperWireRenderer() throws Exception {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCopperWire.class, new RenderCopperWire());
	}

	public static Block requestBlock(String name, int id) {
		if (OreDictionary.getOres(name).size() <= 0) {
			return requireBlock(name, id);
		} else {
			FMLLog.info("Basic Components: " + name + " already exists in Ore Dictionary, using the ore instead.", new Object[0]);
			return ((ItemStack) OreDictionary.getOres(name).get(0)).getItem() instanceof ItemBlock ? Block.blocksList[((ItemBlock) ((ItemStack) OreDictionary.getOres(name).get(0)).getItem()).getBlockID()] : null;
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static Item requireBattery(int id) {
		return requestItem("battery", id);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static Item requireInfiniteBattery(int id) {
		return requestItem("infiniteBattery", id);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static ItemStack requireMachines(int id) {
		return requireMachines((Object) null, id);
	}

	public static ItemStack requireMachines(Object mod, int id) {
		if (blockMachine == null) {
			id = id <= 0 ? 3973 : id;
			CONFIGURATION.load();
			blockMachine = new BlockBasicMachine(CONFIGURATION.getBlock("Basic Machine", id).getInt(id), 0);
			GameRegistry.registerBlock(blockMachine, ItemBlockBasicMachine.class, "Basic Machine");
			OreDictionary.registerOre("coalGenerator", ((BlockBasicMachine) blockMachine).getCoalGenerator());
			OreDictionary.registerOre("batteryBox", ((BlockBasicMachine) blockMachine).getBatteryBox());
			OreDictionary.registerOre("electricFurnace", ((BlockBasicMachine) blockMachine).getElectricFurnace());
			RecipeHelper.addRecipe(new ShapedOreRecipe((ItemStack) OreDictionary.getOres("batteryBox").get(0), new Object[]{"SSS", "BBB", "SSS", 'B', "battery", 'S', "ingotSteel"}), CONFIGURATION, true);
			RecipeHelper.addRecipe(new ShapedOreRecipe((ItemStack) OreDictionary.getOres("coalGenerator").get(0), new Object[]{"MMM", "MOM", "MCM", 'M', "ingotSteel", 'C', "motor", 'O', Block.furnaceIdle}), CONFIGURATION, true);
			RecipeHelper.addRecipe(new ShapedOreRecipe((ItemStack) OreDictionary.getOres("coalGenerator").get(0), new Object[]{"MMM", "MOM", "MCM", 'M', "ingotBronze", 'C', "motor", 'O', Block.furnaceIdle}), CONFIGURATION, true);
			RecipeHelper.addRecipe(new ShapedOreRecipe((ItemStack) OreDictionary.getOres("electricFurnace").get(0), new Object[]{"SSS", "SCS", "SMS", 'S', "ingotSteel", 'C', "circuitAdvanced", 'M', "motor"}), CONFIGURATION, true);
			CONFIGURATION.save();
		}

		if (mod != null) {
			bcDependants.add(mod);
			NetworkRegistry.instance().registerGuiHandler(mod, new BCGuiHandler());
		}

		return new ItemStack(blockMachine);
	}

	public static void registerTileEntities() {
		if (!registeredTileEntities) {
			GameRegistry.registerTileEntity(TileEntityBatteryBox.class, "UEBatteryBox");
			GameRegistry.registerTileEntity(TileEntityCoalGenerator.class, "UECoalGenerator");
			GameRegistry.registerTileEntity(TileEntityElectricFurnace.class, "UEElectricFurnace");
			registeredTileEntities = true;
		}

	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static void register(Object mod, String channel) {
		register(channel);
	}

	public static void register(String channel) {
		CHANNEL = channel;
		if (itemDustBronze != null && OreDictionary.getOres("ingotBronze").size() > 0) {
			GameRegistry.addSmelting(itemDustBronze.itemID, (ItemStack) OreDictionary.getOres("ingotBronze").get(0), 0.6F);
		}

		if (itemDustSteel != null && OreDictionary.getOres("ingotSteel").size() > 0) {
			GameRegistry.addSmelting(itemDustSteel.itemID, (ItemStack) OreDictionary.getOres("ingotSteel").get(0), 0.6F);
		}

		if (blockOreCopper != null) {
			GameRegistry.addSmelting(blockOreCopper.blockID, (ItemStack) OreDictionary.getOres("ingotCopper").get(0), 0.7F);
		}

		if (blockOreTin != null) {
			GameRegistry.addSmelting(blockOreTin.blockID, (ItemStack) OreDictionary.getOres("ingotTin").get(0), 0.7F);
		}

	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static void requestAll() {
		requestAll((Object) null);
	}

	public static void requestAll(Object mod) {
		requestItem("ingotCopper", 0);
		requestItem("ingotTin", 0);
		requestBlock("oreCopper", 0);
		requestBlock("oreTin", 0);
		requestItem("ingotSteel", 0);
		requestItem("dustSteel", 0);
		requestItem("plateSteel", 0);
		requestItem("ingotBronze", 0);
		requestItem("dustBronze", 0);
		requestItem("plateBronze", 0);
		requestItem("plateCopper", 0);
		requestItem("plateTin", 0);
		requestItem("plateIron", 0);
		requestItem("plateGold", 0);
		requestBlock("copperWire", 0);
		requestItem("circuitBasic", 0);
		requestItem("circuitAdvanced", 0);
		requestItem("circuitElite", 0);
		requestItem("motor", 0);
		requestItem("wrench", 0);
		requestItem("battery", 0);
		requestItem("infiniteBattery", 0);
		requireMachines(mod, 0);
	}

	public static Object getFirstDependant() {
		return bcDependants.size() > 0 ? bcDependants.get(0) : null;
	}
}
