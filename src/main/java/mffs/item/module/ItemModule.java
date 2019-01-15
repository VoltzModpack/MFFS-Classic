package mffs.item.module;

import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import mffs.api.modules.IModule;
import mffs.base.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import java.util.List;
import java.util.Set;

public class ItemModule extends ItemBase implements IModule {

	private float fortronCost = 0.5F;

	public ItemModule(int id, String name) {
		super(id, name);
	}

	public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
		info.add("Fortron: " + this.getFortronCost(1.0F));
		super.addInformation(itemStack, player, info, b);
	}

	public void onCalculate(IFieldInteraction projector, Set position) {
	}

	public boolean onProject(IProjector projector, Set fields) {
		return false;
	}

	public int onProject(IProjector projector, Vector3 position) {
		return 0;
	}

	public boolean onCollideWithForceField(World world, int x, int y, int z, Entity entity, ItemStack moduleStack) {
		return false;
	}

	public ItemModule setCost(float cost) {
		this.fortronCost = cost;
		return this;
	}

	public ItemModule setMaxStackSize(int par1) {
		super.setMaxStackSize(par1);
		return this;
	}

	public float getFortronCost(float amplifier) {
		return this.fortronCost * amplifier;
	}
}
