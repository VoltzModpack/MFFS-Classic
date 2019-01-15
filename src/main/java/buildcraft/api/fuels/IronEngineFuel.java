package buildcraft.api.fuels;

import net.minecraftforge.liquids.LiquidStack;

import java.util.Iterator;
import java.util.LinkedList;

public class IronEngineFuel {

	public static LinkedList fuels = new LinkedList();
	public final LiquidStack liquid;
	public final float powerPerCycle;
	public final int totalBurningTime;

	public static IronEngineFuel getFuelForLiquid(LiquidStack liquid) {
		if (liquid == null) {
			return null;
		} else if (liquid.itemID <= 0) {
			return null;
		} else {
			Iterator i$ = fuels.iterator();

			IronEngineFuel fuel;
			do {
				if (!i$.hasNext()) {
					return null;
				}

				fuel = (IronEngineFuel) i$.next();
			} while (!fuel.liquid.isLiquidEqual(liquid));

			return fuel;
		}
	}

	public IronEngineFuel(int liquidId, float powerPerCycle, int totalBurningTime) {
		this(new LiquidStack(liquidId, 1000, 0), powerPerCycle, totalBurningTime);
	}

	public IronEngineFuel(LiquidStack liquid, float powerPerCycle, int totalBurningTime) {
		this.liquid = liquid;
		this.powerPerCycle = powerPerCycle;
		this.totalBurningTime = totalBurningTime;
	}
}
