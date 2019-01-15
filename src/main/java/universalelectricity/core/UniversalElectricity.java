package universalelectricity.core;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;

import java.io.File;

public class UniversalElectricity {

	public static final String MAJOR_VERSION = "3";
	public static final String MINOR_VERSION = "1";
	public static final String REVISION_VERSION = "0";
	public static final String BUILD_VERSION = "175";
	public static final String VERSION = "3.1.0";
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "UniversalElectricity.cfg"));
	public static double IC2_RATIO = 40.0D;
	public static double BC3_RATIO = 100.0D;
	public static double TO_IC2_RATIO;
	public static double TO_BC_RATIO;
	public static boolean isVoltageSensitive;
	public static boolean isNetworkActive;
	public static final Material machine;

	static {
		TO_IC2_RATIO = 1.0D / IC2_RATIO;
		TO_BC_RATIO = 1.0D / BC3_RATIO;
		isVoltageSensitive = false;
		isNetworkActive = false;
		machine = new Material(MapColor.ironColor);
		CONFIGURATION.load();
		IC2_RATIO = CONFIGURATION.get("Compatiblity", "IndustrialCraft Conversion Ratio", IC2_RATIO).getDouble(IC2_RATIO);
		BC3_RATIO = CONFIGURATION.get("Compatiblity", "BuildCraft Conversion Ratio", BC3_RATIO).getDouble(BC3_RATIO);
		TO_IC2_RATIO = 1.0D / IC2_RATIO;
		TO_BC_RATIO = 1.0D / BC3_RATIO;
		isVoltageSensitive = CONFIGURATION.get("Compatiblity", "Is Voltage Sensitive", isVoltageSensitive).getBoolean(isVoltageSensitive);
		isNetworkActive = CONFIGURATION.get("Compatiblity", "Is Network Active", isNetworkActive).getBoolean(isNetworkActive);
		CONFIGURATION.save();
	}
}
