package universalelectricity.prefab.multiblock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;

import java.util.Random;

public class BlockMulti extends BlockContainer {

	public String textureName = null;
	public String channel = "";

	public BlockMulti() {
		super(UniversalElectricity.machine);
		this.setHardness(0.8F);
		this.setUnlocalizedName("multiBlock");
	}

	public BlockMulti setChannel(String channel) {
		this.channel = channel;
		return this;
	}

	public BlockMulti setTextureName(String name) {
		this.textureName = name;
		return this;
	}

	public void makeFakeBlock(World worldObj, Vector3 position, Vector3 mainBlock) {
		worldObj.setBlock(position.intX(), position.intY(), position.intZ(), super.blockID);
		((TileEntityMulti) worldObj.getTileEntity(position.intX(), position.intY(), position.intZ())).setMainBlock(mainBlock);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		if (this.textureName != null) {
			super.blockIcon = iconRegister.registerIcon(this.textureName);
		} else {
			super.registerIcons(iconRegister);
		}

	}

	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityMulti) {
			((TileEntityMulti) tileEntity).onBlockRemoval();
		}

		super.breakBlock(world, x, y, z, par5, par6);
	}

	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		TileEntityMulti tileEntity = (TileEntityMulti) par1World.getTileEntity(x, y, z);
		return tileEntity.onBlockActivated(par1World, x, y, z, par5EntityPlayer);
	}

	public int quantityDropped(Random par1Random) {
		return 0;
	}

	public int getRenderType() {
		return -1;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityMulti(this.channel);
	}

	public ItemStack getPickBlock(MovingObjectPosition target, World par1World, int x, int y, int z) {
		TileEntity tileEntity = par1World.getTileEntity(x, y, z);
		Vector3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;
		if (mainBlockPosition != null) {
			int mainBlockID = par1World.getBlockId(mainBlockPosition.intX(), mainBlockPosition.intY(), mainBlockPosition.intZ());
			if (mainBlockID > 0) {
				return Block.blocksList[mainBlockID].getPickBlock(target, par1World, mainBlockPosition.intX(), mainBlockPosition.intY(), mainBlockPosition.intZ());
			}
		}

		return null;
	}
}
