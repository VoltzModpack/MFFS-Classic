package ic2.api.crops;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public abstract class CropCard {

	@SideOnly(Side.CLIENT)
	protected Icon[] textures;

	public abstract String name();

	public String discoveredBy() {
		return "Alblaka";
	}

	public String desc(int i) {
		String[] att = this.attributes();
		if (att != null && att.length != 0) {
			String s;
			if (i == 0) {
				s = att[0];
				if (att.length >= 2) {
					s = s + ", " + att[1];
					if (att.length >= 3) {
						s = s + ",";
					}
				}

				return s;
			} else if (att.length < 3) {
				return "";
			} else {
				s = att[2];
				if (att.length >= 4) {
					s = s + ", " + att[3];
				}

				return s;
			}
		} else {
			return "";
		}
	}

	public abstract int tier();

	public abstract int stat(int var1);

	public abstract String[] attributes();

	public abstract int maxSize();

	@SideOnly(Side.CLIENT)
	public void registerSprites(IconRegister iconRegister) {
		this.textures = new Icon[this.maxSize()];

		for (int i = 1; i <= this.textures.length; ++i) {
			this.textures[i - 1] = iconRegister.registerIcon("ic2:crop/blockCrop." + this.name() + "." + i);
		}

	}

	@SideOnly(Side.CLIENT)
	public Icon getSprite(ICropTile crop) {
		return crop.getSize() > 0 && crop.getSize() <= this.textures.length ? this.textures[crop.getSize() - 1] : null;
	}

	public String getTextureFile() {
		return "/ic2/sprites/crops_0.png";
	}

	public int growthDuration(ICropTile crop) {
		return this.tier() * 200;
	}

	public abstract boolean canGrow(ICropTile var1);

	public int weightInfluences(ICropTile crop, float humidity, float nutrients, float air) {
		return (int) (humidity + nutrients + air);
	}

	public boolean canCross(ICropTile crop) {
		return crop.getSize() >= 3;
	}

	public boolean rightclick(ICropTile crop, EntityPlayer player) {
		return crop.harvest(true);
	}

	public abstract boolean canBeHarvested(ICropTile var1);

	public float dropGainChance() {
		float base = 1.0F;

		for (int i = 0; i < this.tier(); ++i) {
			base = (float) ((double) base * 0.95D);
		}

		return base;
	}

	public abstract ItemStack getGain(ICropTile var1);

	public byte getSizeAfterHarvest(ICropTile crop) {
		return 1;
	}

	public boolean leftclick(ICropTile crop, EntityPlayer player) {
		return crop.pick(true);
	}

	public float dropSeedChance(ICropTile crop) {
		if (crop.getSize() == 1) {
			return 0.0F;
		} else {
			float base = 0.5F;
			if (crop.getSize() == 2) {
				base /= 2.0F;
			}

			for (int i = 0; i < this.tier(); ++i) {
				base = (float) ((double) base * 0.8D);
			}

			return base;
		}
	}

	public ItemStack getSeeds(ICropTile crop) {
		return crop.generateSeeds(crop.getID(), crop.getGrowth(), crop.getGain(), crop.getResistance(), crop.getScanLevel());
	}

	public void onNeighbourChange(ICropTile crop) {
	}

	public int emitRedstone(ICropTile crop) {
		return 0;
	}

	public void onBlockDestroyed(ICropTile crop) {
	}

	public int getEmittedLight(ICropTile crop) {
		return 0;
	}

	public boolean onEntityCollision(ICropTile crop, Entity entity) {
		return entity instanceof EntityLiving ? ((EntityLiving) entity).isSprinting() : false;
	}

	public void tick(ICropTile crop) {
	}

	public boolean isWeed(ICropTile crop) {
		return crop.getSize() >= 2 && (crop.getID() == 0 || crop.getGrowth() >= 24);
	}

	public final int getId() {
		return Crops.instance.getIdFor(this);
	}
}
