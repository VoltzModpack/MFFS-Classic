package basiccomponents.common.block;

import basiccomponents.common.BasicComponents;
import basiccomponents.common.tileentity.TileEntityBatteryBox;
import basiccomponents.common.tileentity.TileEntityCoalGenerator;
import basiccomponents.common.tileentity.TileEntityElectricFurnace;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.block.BlockAdvanced;

import java.util.List;
import java.util.Random;

public class BlockBasicMachine extends BlockAdvanced {

	public static final int COAL_GENERATOR_METADATA = 0;
	public static final int BATTERY_BOX_METADATA = 4;
	public static final int ELECTRIC_FURNACE_METADATA = 8;
	private Icon iconMachineSide;
	private Icon iconInput;
	private Icon iconOutput;
	private Icon iconCoalGenerator;
	private Icon iconBatteryBox;
	private Icon iconElectricFurnace;

	public BlockBasicMachine(int id, int textureIndex) {
		super(id, UniversalElectricity.machine);
		this.setUnlocalizedName("basiccomponents:bcMachine");
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setStepSound(Block.soundMetalFootstep);
	}

	public void registerIcons(IconRegister par1IconRegister) {
		super.blockIcon = par1IconRegister.registerIcon("basiccomponents:machine");
		this.iconInput = par1IconRegister.registerIcon("basiccomponents:machine_input");
		this.iconOutput = par1IconRegister.registerIcon("basiccomponents:machine_output");
		this.iconMachineSide = par1IconRegister.registerIcon("basiccomponents:machine_side");
		this.iconCoalGenerator = par1IconRegister.registerIcon("basiccomponents:coalGenerator");
		this.iconBatteryBox = par1IconRegister.registerIcon("basiccomponents:batteryBox");
		this.iconElectricFurnace = par1IconRegister.registerIcon("basiccomponents:electricFurnace");
	}

	public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random) {
		TileEntity tile = par1World.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityCoalGenerator) {
			TileEntityCoalGenerator tileEntity = (TileEntityCoalGenerator) tile;
			if (tileEntity.generateWatts > 0.0D) {
				int metadata = par1World.getBlockMetadata(x, y, z);
				float var7 = (float) x + 0.5F;
				float var8 = (float) y + 0.0F + par5Random.nextFloat() * 6.0F / 16.0F;
				float var9 = (float) z + 0.5F;
				float var10 = 0.52F;
				float var11 = par5Random.nextFloat() * 0.6F - 0.3F;
				if (metadata == 3) {
					par1World.spawnParticle("smoke", (double) (var7 - var10), (double) var8, (double) (var9 + var11), 0.0D, 0.0D, 0.0D);
					par1World.spawnParticle("flame", (double) (var7 - var10), (double) var8, (double) (var9 + var11), 0.0D, 0.0D, 0.0D);
				} else if (metadata == 2) {
					par1World.spawnParticle("smoke", (double) (var7 + var10), (double) var8, (double) (var9 + var11), 0.0D, 0.0D, 0.0D);
					par1World.spawnParticle("flame", (double) (var7 + var10), (double) var8, (double) (var9 + var11), 0.0D, 0.0D, 0.0D);
				} else if (metadata == 1) {
					par1World.spawnParticle("smoke", (double) (var7 + var11), (double) var8, (double) (var9 - var10), 0.0D, 0.0D, 0.0D);
					par1World.spawnParticle("flame", (double) (var7 + var11), (double) var8, (double) (var9 - var10), 0.0D, 0.0D, 0.0D);
				} else if (metadata == 0) {
					par1World.spawnParticle("smoke", (double) (var7 + var11), (double) var8, (double) (var9 + var10), 0.0D, 0.0D, 0.0D);
					par1World.spawnParticle("flame", (double) (var7 + var11), (double) var8, (double) (var9 + var10), 0.0D, 0.0D, 0.0D);
				}
			}
		}

	}

	public Icon getIcon(int side, int metadata) {
		if (side != 0 && side != 1) {
			if (metadata >= 8) {
				metadata -= 8;
				if (side == metadata + 2) {
					return this.iconInput;
				}

				if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
					return this.iconElectricFurnace;
				}
			} else {
				if (metadata >= 4) {
					metadata -= 4;
					if (side == metadata + 2) {
						return this.iconOutput;
					}

					if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
						return this.iconInput;
					}

					return this.iconBatteryBox;
				}

				if (side == metadata + 2) {
					return this.iconOutput;
				}

				if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal()) {
					return this.iconCoalGenerator;
				}
			}

			return this.iconMachineSide;
		} else {
			return super.blockIcon;
		}
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving, ItemStack itemStack) {
		int metadata = world.getBlockMetadata(x, y, z);
		int angle = MathHelper.floor_double((double) (entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int change = 0;
		switch (angle) {
			case 0:
				change = 1;
				break;
			case 1:
				change = 2;
				break;
			case 2:
				change = 0;
				break;
			case 3:
				change = 3;
		}

		if (metadata >= 8) {
			world.setBlockMetadataWithNotify(x, y, z, 8 + change, 3);
		} else if (metadata >= 4) {
			switch (angle) {
				case 0:
					change = 3;
					break;
				case 1:
					change = 1;
					break;
				case 2:
					change = 2;
					break;
				case 3:
					change = 0;
			}

			world.setBlockMetadataWithNotify(x, y, z, 4 + change, 3);
		} else {
			world.setBlockMetadataWithNotify(x, y, z, 0 + change, 3);
		}

	}

	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ) {
		int metadata = par1World.getBlockMetadata(x, y, z);
		int original = metadata;
		int change = 0;
		if (metadata >= 8) {
			original = metadata - 8;
		} else if (metadata >= 4) {
			original = metadata - 4;
		}

		switch (original) {
			case 0:
				change = 3;
				break;
			case 1:
				change = 2;
				break;
			case 2:
				change = 0;
				break;
			case 3:
				change = 1;
		}

		if (metadata >= 8) {
			change += 8;
		} else if (metadata >= 4) {
			change += 4;
		}

		par1World.setBlockMetadataWithNotify(x, y, z, change, 3);
		return true;
	}

	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ) {
		int metadata = par1World.getBlockMetadata(x, y, z);
		if (!par1World.isRemote) {
			if (metadata >= 8) {
				par5EntityPlayer.openGui(BasicComponents.getFirstDependant(), -1, par1World, x, y, z);
				return true;
			} else if (metadata >= 4) {
				par5EntityPlayer.openGui(BasicComponents.getFirstDependant(), -1, par1World, x, y, z);
				return true;
			} else {
				par5EntityPlayer.openGui(BasicComponents.getFirstDependant(), -1, par1World, x, y, z);
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public TileEntity createTileEntity(World world, int metadata) {
		if (metadata >= 8) {
			return new TileEntityElectricFurnace();
		} else {
			return (TileEntity) (metadata >= 4 ? new TileEntityBatteryBox() : new TileEntityCoalGenerator());
		}
	}

	public ItemStack getCoalGenerator() {
		return new ItemStack(super.blockID, 1, 0);
	}

	public ItemStack getBatteryBox() {
		return new ItemStack(super.blockID, 1, 4);
	}

	public ItemStack getElectricFurnace() {
		return new ItemStack(super.blockID, 1, 8);
	}

	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		par3List.add(this.getCoalGenerator());
		par3List.add(this.getBatteryBox());
		par3List.add(this.getElectricFurnace());
	}

	public int damageDropped(int metadata) {
		if (metadata >= 8) {
			return 8;
		} else {
			return metadata >= 4 ? 4 : 0;
		}
	}

	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int id = this.idPicked(world, x, y, z);
		if (id == 0) {
			return null;
		} else {
			Item item = Item.itemsList[id];
			if (item == null) {
				return null;
			} else {
				int metadata = this.getDamageValue(world, x, y, z);
				return new ItemStack(id, 1, metadata);
			}
		}
	}
}
