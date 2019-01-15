package calclavia.lib;

import ic2.api.item.ICustomElectricItem;
import net.minecraft.item.ItemStack;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.item.ItemElectric;

public abstract class ItemUniversalElectric extends ItemElectric implements ICustomElectricItem {

	public static final float CHARGE_RATE = 0.005F;

	public ItemUniversalElectric(int id) {
		super(id);
	}

	public int charge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
		double inputElectricity = (double) amount * UniversalElectricity.IC2_RATIO;
		inputElectricity = Math.min(inputElectricity, this.getMaxJoules(itemStack) - this.getJoules(itemStack));
		if (!ignoreTransferLimit) {
			inputElectricity = Math.min(inputElectricity, this.getMaxJoules(itemStack) * 0.004999999888241291D);
		}

		if (!simulate) {
			this.setJoules(this.getJoules(itemStack) + inputElectricity, itemStack);
		}

		return (int) (inputElectricity * UniversalElectricity.TO_IC2_RATIO);
	}

	public int discharge(ItemStack itemStack, int amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
		double outputElectricity = (double) amount * UniversalElectricity.IC2_RATIO;
		outputElectricity = Math.min(outputElectricity, this.getJoules(itemStack));
		if (!ignoreTransferLimit) {
			outputElectricity = Math.min(this.getJoules(itemStack), this.getMaxJoules(itemStack) * 0.004999999888241291D);
		}

		if (!simulate) {
			this.setJoules(this.getJoules(itemStack) - outputElectricity, itemStack);
		}

		return (int) (outputElectricity * UniversalElectricity.TO_IC2_RATIO);
	}

	public boolean canUse(ItemStack itemStack, int amount) {
		return false;
	}

	public boolean canShowChargeToolTip(ItemStack itemStack) {
		return false;
	}

	public boolean canProvideEnergy(ItemStack itemStack) {
		return this.getProvideRequest(itemStack).getWatts() > 0.0D;
	}

	public int getChargedItemId(ItemStack itemStack) {
		return super.itemID;
	}

	public int getEmptyItemId(ItemStack itemStack) {
		return super.itemID;
	}

	public int getMaxCharge(ItemStack itemStack) {
		return (int) (this.getMaxJoules(itemStack) * UniversalElectricity.TO_IC2_RATIO);
	}

	public int getTier(ItemStack itemStack) {
		return 1;
	}

	public int getTransferLimit(ItemStack itemStack) {
		return (int) (this.getMaxJoules(itemStack) * 0.004999999888241291D * UniversalElectricity.TO_IC2_RATIO);
	}
}
