package mffs.item;

import mffs.MFFSHelper;
import mffs.ModularForceFieldSystem;
import mffs.api.card.ICardLink;
import mffs.api.fortron.IFortronFrequency;
import mffs.api.security.Permission;
import mffs.fortron.FrequencyGrid;
import mffs.item.card.ItemCardFrequency;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.vector.Vector3;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ItemRemoteController extends ItemCardFrequency implements ICardLink {

	public ItemRemoteController(int id) {
		super("remoteController", id);
	}

	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b) {
		super.addInformation(itemStack, player, list, b);
		Vector3 position = this.getLink(itemStack);
		if (position != null) {
			int blockId = position.getBlockID(player.worldObj);
			if (Block.blocksList[blockId] != null) {
				list.add("Linked with: " + Block.blocksList[blockId].getLocalizedName());
				list.add(position.intX() + ", " + position.intY() + ", " + position.intZ());
				return;
			}
		}

		list.add("Not linked.");
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			if (!world.isRemote) {
				Vector3 vector = new Vector3((double) x, (double) y, (double) z);
				this.setLink(itemStack, vector);
				if (Block.blocksList[vector.getBlockID(world)] != null) {
					player.addChatMessage("Linked remote to position: " + x + ", " + y + ", " + z + " with block: " + Block.blocksList[vector.getBlockID(world)].getLocalizedName());
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		if (!entityPlayer.isSneaking()) {
			Vector3 position = this.getLink(itemStack);
			if (position != null) {
				int blockId = position.getBlockID(world);
				if (Block.blocksList[blockId] != null) {
					Chunk chunk = world.getChunkFromBlockCoords(position.intX(), position.intZ());
					if (chunk != null && chunk.isChunkLoaded && (MFFSHelper.hasPermission(world, position, Action.RIGHT_CLICK_BLOCK, entityPlayer) || MFFSHelper.hasPermission(world, position, Permission.REMOTE_CONTROL, entityPlayer))) {
						double requiredEnergy = Vector3.distance(new Vector3(entityPlayer), position) * 10.0D;
						int receivedEnergy = 0;
						Set fortronTiles = FrequencyGrid.instance().getFortronTiles(world, new Vector3(entityPlayer), 50, this.getFrequency(itemStack));
						Iterator i$ = fortronTiles.iterator();

						while (i$.hasNext()) {
							IFortronFrequency fortronTile = (IFortronFrequency) i$.next();
							int consumedEnergy = fortronTile.requestFortron((int) Math.ceil(requiredEnergy / (double) fortronTiles.size()), true);
							if (consumedEnergy > 0) {
								if (world.isRemote) {
									ModularForceFieldSystem.proxy.renderBeam(world, (new Vector3(entityPlayer)).add(new Vector3(0.0D, (double) entityPlayer.getEyeHeight() - 0.2D, 0.0D)), (new Vector3((TileEntity) fortronTile)).add(0.5D), 0.6F, 0.6F, 1.0F, 20);
								}

								receivedEnergy += consumedEnergy;
							}

							if ((double) receivedEnergy >= requiredEnergy) {
								try {
									Block.blocksList[blockId].onBlockActivated(world, position.intX(), position.intY(), position.intZ(), entityPlayer, 0, 0.0F, 0.0F, 0.0F);
								} catch (Exception var15) {
									var15.printStackTrace();
								}

								return itemStack;
							}
						}

						if (!world.isRemote) {
							entityPlayer.addChatMessage("Unable to harness " + ElectricityDisplay.getDisplay(requiredEnergy, ElectricityDisplay.ElectricUnit.JOULES) + " from the Fortron field.");
						}
					}
				}
			}
		}

		return itemStack;
	}

	public void setLink(ItemStack itemStack, Vector3 position) {
		NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
		nbt.setCompoundTag("position", position.writeToNBT(new NBTTagCompound()));
	}

	public Vector3 getLink(ItemStack itemStack) {
		NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
		return Vector3.readFromNBT(nbt.getCompoundTag("position"));
	}
}
