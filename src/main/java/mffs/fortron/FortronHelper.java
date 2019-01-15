package mffs.fortron;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FortronHelper {

	public static FluidStack LIQUID_FORTRON;

	public static FluidStack getFortron(int amount) {
		FluidStack stack = LIQUID_FORTRON.copy();
		stack.amount = amount;
		return stack;
	}

	public static int getAmount(FluidStack liquidStack) {
		return liquidStack != null ? liquidStack.amount : 0;
	}

	public static int getAmount(FluidTank fortronTank) {
		return fortronTank != null ? getAmount(fortronTank.getFluid()) : 0;
	}

}
