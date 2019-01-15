package mffs;

import basiccomponents.common.BasicComponents;
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mffs.base.BlockBase;
import mffs.base.BlockMachine;
import mffs.base.ItemBase;
import mffs.block.*;
import mffs.card.ItemCard;
import mffs.fortron.FortronHelper;
import mffs.fortron.FrequencyGrid;
import mffs.item.ItemRemoteController;
import mffs.item.card.ItemCardFrequency;
import mffs.item.card.ItemCardID;
import mffs.item.card.ItemCardInfinite;
import mffs.item.card.ItemCardLink;
import mffs.item.mode.ItemMode;
import mffs.item.mode.ItemModeCube;
import mffs.item.mode.ItemModeSphere;
import mffs.item.mode.ItemModeTube;
import mffs.item.module.ItemModule;
import mffs.item.module.interdiction.*;
import mffs.item.module.projector.*;
import mffs.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.modstats.ModstatInfo;
import org.modstats.Modstats;
import universalelectricity.prefab.CustomDamageSource;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.TranslationHelper;

import java.util.Arrays;
import java.util.logging.Logger;

@Mod(
	modid = "MFFS",
	name = "Modular Force Field System",
	version = "3.1.0",
	useMetadata = true
)
//@NetworkMod(
//	clientSideRequired = true,
//	channels = {"MFFS"},
//	packetHandler = PacketManager.class
//)
//@ModstatInfo(
//	prefix = "mffs"
//)
public class ModularForceFieldSystem {

	public static final String CHANNEL = "MFFS";
	public static final String ID = "MFFS";
	public static final String NAME = "Modular Force Field System";
	public static final String PREFIX = "mffs:";
	public static final String MAJOR_VERSION = "3";
	public static final String MINOR_VERSION = "1";
	public static final String REVISION_VERSION = "0";
	public static final String VERSION = "3.1.0";
	public static final String BUILD_VERSION = "175";
	@Instance("MFFS")
	public static ModularForceFieldSystem instance;
	@Metadata("MFFS")
	public static ModMetadata metadata;
	@SidedProxy(
		clientSide = "mffs.ClientProxy",
		serverSide = "mffs.CommonProxy"
	)
	public static CommonProxy proxy;
	public static final Logger LOGGER = Logger.getLogger("Modular Force Field System");
	public static final String RESOURCE_DIRECTORY = "/mods/mffs/";
	public static final String LANGUAGE_DIRECTORY = "/mods/mffs/languages/";
	public static final String TEXTURE_DIRECTORY = "/mods/mffs/textures/";
	public static final String BLOCK_DIRECTORY = "/mods/mffs/textures/blocks/";
	public static final String ITEM_DIRECTORY = "/mods/mffs/textures/items/";
	public static final String MODEL_DIRECTORY = "/mods/mffs/textures/models/";
	public static final String GUI_DIRECTORY = "/mods/mffs/textures/gui/";
	public static final String GUI_BASE_DIRECTORY = "/mods/mffs/textures/gui/gui_base.png";
	public static final String GUI_COMPONENTS = "/mods/mffs/textures/gui/gui_components.png";
	public static final String GUI_BUTTON = "/mods/mffs/textures/gui/gui_button.png";
	public static BlockMachine blockCoercionDeriver;
	public static BlockMachine blockFortronCapacitor;
	public static BlockMachine blockForceFieldProjector;
	public static BlockMachine blockBiometricIdentifier;
	public static BlockMachine blockInterdictionMatrix;
	public static BlockMachine blockForceManipulator;
	public static BlockBase blockForceField;
	public static Item itemFortron;
	public static Item itemRemoteController;
	public static Item itemFocusMatix;
	public static ItemCard itemCardBlank;
	public static ItemCard itemCardInfinite;
	public static ItemCard itemCardFrequency;
	public static ItemCard itemCardID;
	public static ItemCard itemCardLink;
	public static ItemMode itemModeCube;
	public static ItemMode itemModeSphere;
	public static ItemMode itemModeTube;
	public static ItemMode itemModeCylinder;
	public static ItemMode itemModePyramid;
	public static ItemMode itemModeCustom;
	public static ItemModule itemModule;
	public static ItemModule itemModuleSpeed;
	public static ItemModule itemModuleCapacity;
	public static ItemModule itemModuleTranslate;
	public static ItemModule itemModuleScale;
	public static ItemModule itemModuleRotate;
	public static ItemModule itemModuleCollection;
	public static ItemModule itemModuleInvert;
	public static ItemModule itemModuleSilence;
	public static ItemModule itemModuleFusion;
	public static ItemModule itemModuleManipulator;
	public static ItemModule itemModuleCamouflage;
	public static ItemModule itemModuleDisintegration;
	public static ItemModule itemModuleShock;
	public static ItemModule itemModuleGlow;
	public static ItemModule itemModuleSponge;
	public static ItemModule itemModuleStablize;
	public static ItemModule itemModuleAntiHostile;
	public static ItemModule itemModuleAntiFriendly;
	public static ItemModule itemModuleAntiPersonnel;
	public static ItemModule itemModuleConfiscate;
	public static ItemModule itemModuleWarn;
	public static ItemModule itemModuleBlockAccess;
	public static ItemModule itemModuleBlockAlter;
	public static ItemModule itemModuleAntiSpawn;
	public static DamageSource damagefieldShock = (new CustomDamageSource("fieldShock")).setDamageBypassesArmor();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER.setParent(FMLLog.getLogger());
		Modstats.instance().getReporter().registerMod(this);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
		MinecraftForge.EVENT_BUS.register(new SubscribeEventHandler());
		Settings.load();
		Settings.CONFIGURATION.load();
		blockForceField = new BlockForceField(Settings.getNextBlockID());
		blockCoercionDeriver = new BlockCoercionDeriver(Settings.getNextBlockID());
		blockFortronCapacitor = new BlockFortronCapacitor(Settings.getNextBlockID());
		blockForceFieldProjector = new BlockForceFieldProjector(Settings.getNextBlockID());
		blockBiometricIdentifier = new BlockBiometricIdentifier(Settings.getNextBlockID());
		blockInterdictionMatrix = new BlockInterdictionMatrix(Settings.getNextBlockID());
		blockForceManipulator = new BlockForceManipulator(Settings.getNextBlockID());
		itemRemoteController = new ItemRemoteController(Settings.getNextItemID());
		itemFocusMatix = new ItemBase(Settings.getNextItemID(), "focusMatrix");
		itemModeCube = new ItemModeCube(Settings.getNextItemID());
		itemModeSphere = new ItemModeSphere(Settings.getNextItemID());
		itemModeTube = new ItemModeTube(Settings.getNextItemID());
		itemModePyramid = new ItemModePyramid(Settings.getNextItemID());
		itemModeCylinder = new ItemModeCylinder(Settings.getNextItemID());
		itemModeCustom = new ItemModeCustom(Settings.getNextItemID());
		itemModuleTranslate = (new ItemModule(Settings.getNextItemID(), "moduleTranslate")).setCost(1.6F);
		itemModuleScale = (new ItemModule(Settings.getNextItemID(), "moduleScale")).setCost(1.2F);
		itemModuleRotate = (new ItemModule(Settings.getNextItemID(), "moduleRotate")).setCost(0.1F);
		itemModuleSpeed = (new ItemModule(Settings.getNextItemID(), "moduleSpeed")).setCost(1.0F);
		itemModuleCapacity = (new ItemModule(Settings.getNextItemID(), "moduleCapacity")).setCost(0.5F);
		itemModuleFusion = new ItemModuleFusion(Settings.getNextItemID());
		itemModuleManipulator = new ItemModuleManipulator(Settings.getNextItemID());
		itemModuleCamouflage = (new ItemModule(Settings.getNextItemID(), "moduleCamouflage")).setCost(1.5F).setMaxStackSize(1);
		itemModuleDisintegration = new ItemModuleDisintegration(Settings.getNextItemID());
		itemModuleShock = new ItemModuleShock(Settings.getNextItemID());
		itemModuleGlow = new ItemModule(Settings.getNextItemID(), "moduleGlow");
		itemModuleSponge = new ItemModuleSponge(Settings.getNextItemID());
		itemModuleStablize = new ItemModuleStablize(Settings.getNextItemID());
		itemModuleAntiFriendly = new ItemModuleAntiFriendly(Settings.getNextItemID());
		itemModuleAntiHostile = new ItemModuleAntiHostile(Settings.getNextItemID());
		itemModuleAntiPersonnel = new ItemModuleAntiPersonnel(Settings.getNextItemID());
		itemModuleConfiscate = new ItemModuleConfiscate(Settings.getNextItemID());
		itemModuleWarn = new ItemModuleWarn(Settings.getNextItemID());
		itemModuleBlockAccess = (new ItemModuleInterdictionMatrix(Settings.getNextItemID(), "moduleBlockAccess")).setCost(10.0F);
		itemModuleBlockAlter = (new ItemModuleInterdictionMatrix(Settings.getNextItemID(), "moduleBlockAlter")).setCost(15.0F);
		itemModuleAntiSpawn = (new ItemModuleInterdictionMatrix(Settings.getNextItemID(), "moduleAntiSpawn")).setCost(10.0F);
		itemCardBlank = new ItemCard(Settings.getNextItemID(), "cardBlank");
		itemCardFrequency = new ItemCardFrequency(Settings.getNextItemID());
		itemCardLink = new ItemCardLink(Settings.getNextItemID());
		itemCardID = new ItemCardID(Settings.getNextItemID());
		itemCardInfinite = new ItemCardInfinite(Settings.getNextItemID());
		itemFortron = (new ItemBase(Settings.getNextItemID(), "fortron")).setCreativeTab((CreativeTabs) null);

		FortronHelper.FLUID_FORTRON = new Fluid("Fortron");
		FortronHelper.LIQUID_FORTRON = new FluidStack(FortronHelper.FLUID_FORTRON, 0);
		//FluidRegistry.registerFluid(new Fluid("Fortron"));new FluidStack(itemFortron, 0);
		itemModuleCollection = (new ItemModule(Settings.getNextItemID(), "moduleCollection")).setMaxStackSize(1).setCost(15.0F);
		itemModuleInvert = (new ItemModule(Settings.getNextItemID(), "moduleInvert")).setMaxStackSize(1).setCost(15.0F);
		itemModuleSilence = (new ItemModule(Settings.getNextItemID(), "moduleSilence")).setMaxStackSize(1).setCost(1.0F);
		Settings.CONFIGURATION.save();
		GameRegistry.registerBlock(blockForceField, blockForceField.getUnlocalizedName());
		GameRegistry.registerBlock(blockCoercionDeriver, blockCoercionDeriver.getUnlocalizedName());
		GameRegistry.registerBlock(blockFortronCapacitor, blockFortronCapacitor.getUnlocalizedName());
		GameRegistry.registerBlock(blockForceFieldProjector, blockForceFieldProjector.getUnlocalizedName());
		GameRegistry.registerBlock(blockBiometricIdentifier, blockBiometricIdentifier.getUnlocalizedName());
		GameRegistry.registerBlock(blockInterdictionMatrix, blockInterdictionMatrix.getUnlocalizedName());
		GameRegistry.registerBlock(blockForceManipulator, blockForceManipulator.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityForceField.class, blockForceField.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityCoercionDeriver.class, blockCoercionDeriver.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityFortronCapacitor.class, blockFortronCapacitor.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityForceFieldProjector.class, blockForceFieldProjector.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityBiometricIdentifier.class, blockBiometricIdentifier.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityInterdictionMatrix.class, blockInterdictionMatrix.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityForceManipulator.class, blockForceManipulator.getUnlocalizedName());
		proxy.preInit();
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {
		BasicComponents.register(this, "MFFS");
		BasicComponents.requestItem("ingotSteel", 0);
		BasicComponents.requestItem("dustSteel", 0);
		BasicComponents.requestItem("ingotCopper", 0);
		BasicComponents.requestBlock("oreCopper", 0);
		BasicComponents.requestBlock("copperWire", 0);
		BasicComponents.requestItem("wrench", 0);
		LOGGER.fine("Language(s) Loaded: " + TranslationHelper.loadLanguages("/mods/mffs/languages/", new String[]{"en_US", "zh_CN", "de_DE"}));
		metadata.modId = "MFFS";
		metadata.name = "Modular Force Field System";
		metadata.description = "Modular Force Field System is a mod that adds force fields, high tech machinery and defensive measures to Minecraft.";
		metadata.url = "http://www.universalelectricity.com/mffs/";
		metadata.logoFile = "/mffs_logo.png";
		metadata.version = "3.1.0.175";
		metadata.authorList = Arrays.asList("Calclavia");
		metadata.credits = "Please visit the website.";
		metadata.autogenerated = false;
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		UniversalRecipes.init();
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemFocusMatix, 9), new Object[]{"RMR", "MDM", "RMR", 'M', "ingotSteel", 'D', Items.diamond, 'R', Items.redstone}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRemoteController), new Object[]{"WWW", "MCM", "MCM", 'W', "calclavia:WIRE", 'C', UniversalRecipes.BATTERY, 'M', "ingotSteel"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCoercionDeriver), new Object[]{"M M", "MFM", "MCM", 'C', UniversalRecipes.BATTERY, 'M', "ingotSteel", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockFortronCapacitor), new Object[]{"MFM", "FCF", "MFM", 'D', Items.diamond, 'C', UniversalRecipes.BATTERY, 'F', itemFocusMatix, 'M', "ingotSteel"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockForceFieldProjector), new Object[]{" D ", "FFF", "MCM", 'D', Items.diamond, 'C', UniversalRecipes.BATTERY, 'F', itemFocusMatix, 'M', "ingotSteel"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBiometricIdentifier), new Object[]{"FMF", "MCM", "FMF", 'C', itemCardBlank, 'M', "ingotSteel", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockInterdictionMatrix), new Object[]{"SSS", "FFF", "FEF", 'S', itemModuleShock, 'E', Blocks.ender_chest, 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockForceManipulator), new Object[]{"F F", "FMF", "F F", 'F', itemFocusMatix, 'M', "calclavia:MOTOR"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardBlank), new Object[]{"PPP", "PMP", "PPP", 'P', Items.paper, 'M', "ingotSteel"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardLink), new Object[]{"BWB", 'B', itemCardBlank, 'W', "calclavia:WIRE"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardFrequency), new Object[]{"WBW", 'B', itemCardBlank, 'W', "calclavia:WIRE"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardID), new Object[]{"RBR", 'B', itemCardBlank, 'R', Items.redstone}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeSphere), new Object[]{" F ", "FFF", " F ", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeCube), new Object[]{"FFF", "FFF", "FFF", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeTube), new Object[]{"FFF", "   ", "FFF", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModePyramid), new Object[]{"F  ", "FF ", "FFF", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeCylinder), new Object[]{"S", "S", "S", 'S', itemModeSphere}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeCustom), new Object[]{" C ", "TFP", " S ", 'S', itemModeSphere, 'C', itemModeCube, 'T', itemModeTube, 'P', itemModePyramid, 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemModeCustom), new Object[]{new ItemStack(itemModeCustom)}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleSpeed, 2), new Object[]{"FFF", "RRR", "FFF", 'F', itemFocusMatix, 'R', Items.redstone}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleCapacity, 2), new Object[]{"FCF", 'F', itemFocusMatix, 'C', UniversalRecipes.BATTERY}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleShock), new Object[]{"FWF", 'F', itemFocusMatix, 'W', "calclavia:WIRE"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleSponge), new Object[]{"BBB", "BFB", "BBB", 'F', itemFocusMatix, 'B', Items.water_bucket}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleDisintegration), new Object[]{" W ", "FBF", " W ", 'F', itemFocusMatix, 'W', "calclavia:WIRE", 'B', UniversalRecipes.BATTERY}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleManipulator), new Object[]{"F", " ", "F", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleCamouflage), new Object[]{"WFW", "FWF", "WFW", 'F', itemFocusMatix, 'W', new ItemStack(Block.cloth, 1, 32767)}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleFusion), new Object[]{"FJF", 'F', itemFocusMatix, 'J', itemModuleShock}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleScale, 2), new Object[]{"FRF", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleTranslate, 2), new Object[]{"FSF", 'F', itemFocusMatix, 'S', itemModuleScale}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleRotate, 4), new Object[]{"F  ", " F ", "  F", 'F', itemFocusMatix}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleGlow, 4), new Object[]{"GGG", "GFG", "GGG", 'F', itemFocusMatix, 'G', Blocks.glowstone}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleStablize), new Object[]{"FDF", "PSA", "FDF", 'F', itemFocusMatix, 'P', Items.diamond_pickaxe, 'S', Items.diamond_shovel, 'A', Items.diamond_axe, 'D', Items.diamond}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleCollection), new Object[]{"F F", " H ", "F F", 'F', itemFocusMatix, 'H', Blocks.hopper}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleInvert), new Object[]{"L", "F", "L", 'F', itemFocusMatix, 'L', Blocks.lapis_block}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleSilence), new Object[]{" N ", "NFN", " N ", 'F', itemFocusMatix, 'N', Blocks.noteblock}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiHostile), new Object[]{" R ", "GFB", " S ", 'F', itemFocusMatix, 'G', Items.gunpowder, 'R', Items.rotten_flesh, 'B', Items.bone, 'S', Items.ghast_tear}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiFriendly), new Object[]{" R ", "GFB", " S ", 'F', itemFocusMatix, 'G', Items.cooked_porkchop, 'R', new ItemStack(Blocks.wool, 1, 32767), 'B', Items.leather, 'S', Items.slime_ball}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiPersonnel), new Object[]{"BFG", 'F', itemFocusMatix, 'B', itemModuleAntiHostile, 'G', itemModuleAntiFriendly}));
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleConfiscate), new Object[]{"PEP", "EFE", "PEP", 'F', itemFocusMatix, 'E', Items.ender_eye, 'P', Items.ender_pearl}), Settings.CONFIGURATION, true);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleWarn), new Object[]{"NFN", 'F', itemFocusMatix, 'N', Blocks.noteblock}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleBlockAccess), new Object[]{" C ", "BFB", " C ", 'F', itemFocusMatix, 'B', Blocks.iron_block, 'C', Blocks.chest}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleBlockAlter), new Object[]{" G ", "GFG", " G ", 'F', itemModuleBlockAccess, 'G', Blocks.gold_block}));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiSpawn), new Object[]{" H ", "G G", " H ", 'H', itemModuleAntiHostile, 'G', itemModuleAntiFriendly}));
		proxy.init();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent evt) {
		FrequencyGrid.reinitiate();
	}

}
