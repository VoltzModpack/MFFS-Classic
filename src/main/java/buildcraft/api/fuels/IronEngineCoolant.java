package buildcraft.api.fuels;

import net.minecraftforge.liquids.LiquidStack;

import java.util.Iterator;
import java.util.LinkedList;

public class IronEngineCoolant {

	public static LinkedList coolants = new LinkedList();
	public final LiquidStack liquid;
	public final float coolingPerUnit;

	public static IronEngineCoolant getCoolantForLiquid(LiquidStack liquid) {
		if (liquid == null) {
			return null;
		} else if (liquid.itemID <= 0) {
			return null;
		} else {
			Iterator i$ = coolants.iterator();

			IronEngineCoolant coolant;
			do {
				if (!i$.hasNext()) {
					return null;
				}

				coolant = (IronEngineCoolant) i$.next();
			} while (!coolant.liquid.isLiquidEqual(liquid));

			return coolant;
		}
	}

	public IronEngineCoolant(LiquidStack liquid, float coolingPerUnit) {
		this.liquid = liquid;
		this.coolingPerUnit = coolingPerUnit;
	}
}
