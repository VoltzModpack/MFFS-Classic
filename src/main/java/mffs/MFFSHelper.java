package mffs;

import calclavia.lib.CalculationHelper;
import icbm.api.IBlockFrequency;
import mffs.api.IProjector;
import mffs.api.fortron.IFortronFrequency;
import mffs.api.modules.IModuleAcceptor;
import mffs.api.security.IInterdictionMatrix;
import mffs.api.security.Permission;
import mffs.fortron.FrequencyGrid;
import mffs.item.module.projector.ItemModeCustom;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import universalelectricity.core.vector.Vector3;

import java.util.*;

public class MFFSHelper {

	public static void transferFortron(IFortronFrequency transferer, Set frequencyTiles, TransferMode transferMode, int limit) {
		if (transferer != null && frequencyTiles.size() > 1) {
			int totalFortron = 0;
			int totalCapacity = 0;
			Iterator i$ = frequencyTiles.iterator();

			IFortronFrequency machine;
			while (i$.hasNext()) {
				machine = (IFortronFrequency) i$.next();
				if (machine != null) {
					totalFortron += machine.getFortronEnergy();
					totalCapacity += machine.getFortronCapacity();
				}
			}

			if (totalFortron > 0 && totalCapacity > 0) {
				int amountToSet;
				int requiredFortron;
				double capacityPercentage;
				switch (transferMode) {
					case EQUALIZE:
						i$ = frequencyTiles.iterator();

						while (i$.hasNext()) {
							machine = (IFortronFrequency) i$.next();
							if (machine != null) {
								capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
								amountToSet = (int) ((double) totalFortron * capacityPercentage);
								doTransferFortron(transferer, machine, amountToSet - machine.getFortronEnergy(), limit);
							}
						}

						return;
					case DISTRIBUTE:
						requiredFortron = totalFortron / frequencyTiles.size();
						i$ = frequencyTiles.iterator();

						while (i$.hasNext()) {
							machine = (IFortronFrequency) i$.next();
							if (machine != null) {
								doTransferFortron(transferer, machine, requiredFortron - machine.getFortronEnergy(), limit);
							}
						}

						return;
					case DRAIN:
						frequencyTiles.remove(transferer);
						i$ = frequencyTiles.iterator();

						while (i$.hasNext()) {
							machine = (IFortronFrequency) i$.next();
							if (machine != null) {
								capacityPercentage = (double) machine.getFortronCapacity() / (double) totalCapacity;
								amountToSet = (int) ((double) totalFortron * capacityPercentage);
								if (amountToSet - machine.getFortronEnergy() > 0) {
									doTransferFortron(transferer, machine, amountToSet - machine.getFortronEnergy(), limit);
								}
							}
						}

						return;
					case FILL:
						if (transferer.getFortronEnergy() < transferer.getFortronCapacity()) {
							frequencyTiles.remove(transferer);
							requiredFortron = transferer.getFortronCapacity() - transferer.getFortronEnergy();
							i$ = frequencyTiles.iterator();

							while (i$.hasNext()) {
								machine = (IFortronFrequency) i$.next();
								if (machine != null) {
									int amountToConsume = Math.min(requiredFortron, machine.getFortronEnergy());
									amountToSet = -machine.getFortronEnergy() - amountToConsume;
									if (amountToConsume > 0) {
										doTransferFortron(transferer, machine, amountToSet - machine.getFortronEnergy(), limit);
									}
								}
							}
						}
				}
			}
		}

	}

	public static void doTransferFortron(IFortronFrequency transferer, IFortronFrequency receiver, int joules, int limit) {
		if (transferer != null && receiver != null) {
			TileEntity tileEntity = (TileEntity) transferer;
			World world = tileEntity.getWorldObj();
			boolean isCamo = false;
			if (transferer instanceof IModuleAcceptor) {
				isCamo = ((IModuleAcceptor) transferer).getModuleCount(ModularForceFieldSystem.itemModuleCamouflage) > 0;
			}

			int toBeInjected;
			if (joules > 0) {
				joules = Math.min(joules, limit);
				toBeInjected = receiver.provideFortron(transferer.requestFortron(joules, false), false);
				toBeInjected = transferer.requestFortron(receiver.provideFortron(toBeInjected, true), true);
				if (world.isRemote && toBeInjected > 0 && !isCamo) {
					ModularForceFieldSystem.proxy.renderBeam(world, Vector3.add(new Vector3(tileEntity), 0.5D), Vector3.add(new Vector3((TileEntity) receiver), 0.5D), 0.6F, 0.6F, 1.0F, 20);
				}
			} else {
				joules = Math.min(Math.abs(joules), limit);
				toBeInjected = transferer.provideFortron(receiver.requestFortron(joules, false), false);
				toBeInjected = receiver.requestFortron(transferer.provideFortron(toBeInjected, true), true);
				if (world.isRemote && toBeInjected > 0 && !isCamo) {
					ModularForceFieldSystem.proxy.renderBeam(world, Vector3.add(new Vector3((TileEntity) receiver), 0.5D), Vector3.add(new Vector3(tileEntity), 0.5D), 0.6F, 0.6F, 1.0F, 20);
				}
			}
		}

	}

	public static IInterdictionMatrix getNearestInterdictionMatrix(World world, Vector3 position) {
		Iterator i$ = FrequencyGrid.instance().get().iterator();

		while (i$.hasNext()) {
			IBlockFrequency frequencyTile = (IBlockFrequency) i$.next();
			if (((TileEntity) frequencyTile).getWorldObj() == world && frequencyTile instanceof IInterdictionMatrix) {
				IInterdictionMatrix interdictionMatrix = (IInterdictionMatrix) frequencyTile;
				if (interdictionMatrix.isActive() && position.distanceTo(new Vector3((TileEntity) interdictionMatrix)) <= (double) interdictionMatrix.getActionRange()) {
					return interdictionMatrix;
				}
			}
		}

		return null;
	}

	public static boolean isPermittedByInterdictionMatrix(IInterdictionMatrix interdictionMatrix, String username, Permission... permissions) {
		if (interdictionMatrix != null && interdictionMatrix.isActive() && interdictionMatrix.getBiometricIdentifier() != null) {
			Permission[] arr$ = permissions;
			int len$ = permissions.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Permission permission = arr$[i$];
				if (!interdictionMatrix.getBiometricIdentifier().isAccessGranted(username, permission)) {
					if (interdictionMatrix.getModuleCount(ModularForceFieldSystem.itemModuleInvert, new int[0]) > 0) {
						return true;
					}

					return false;
				}
			}
		}

		return interdictionMatrix.getModuleCount(ModularForceFieldSystem.itemModuleInvert, new int[0]) <= 0;
	}

	public static List splitStringPerWord(String string, int wordsPerLine) {
		String[] words = string.split(" ");
		List lines = new ArrayList();

		for (int lineCount = 0; (double) lineCount < Math.ceil((double) ((float) words.length / (float) wordsPerLine)); ++lineCount) {
			String stringInLine = "";

			for (int i = lineCount * wordsPerLine; i < Math.min(wordsPerLine + lineCount * wordsPerLine, words.length); ++i) {
				stringInLine = stringInLine + words[i] + " ";
			}

			lines.add(stringInLine.trim());
		}

		return lines;
	}

	public static ItemStack getFirstItemBlock(TileEntity tileEntity, ItemStack itemStack) {
		return getFirstItemBlock(tileEntity, itemStack, true);
	}

	public static ItemStack getFirstItemBlock(TileEntity tileEntity, ItemStack itemStack, boolean recur) {
		int i;
		ItemStack checkStack;
		if (tileEntity instanceof IProjector) {
			int[] arr$ = ((IProjector) tileEntity).getModuleSlots();
			i = arr$.length;

			for (int i$ = 0; i$ < i; ++i$) {
				int i = arr$[i$];
				checkStack = getFirstItemBlock(i, (IProjector) tileEntity, itemStack);
				if (checkStack != null) {
					return checkStack;
				}
			}
		} else if (tileEntity instanceof IInventory) {
			IInventory inventory = (IInventory) tileEntity;

			for (i = 0; i < inventory.getSizeInventory(); ++i) {
				ItemStack checkStack = getFirstItemBlock(i, inventory, itemStack);
				if (checkStack != null) {
					return checkStack;
				}
			}
		}

		if (recur) {
			for (int i = 0; i < 6; ++i) {
				ForgeDirection direction = ForgeDirection.getOrientation(i);
				Vector3 vector = new Vector3(tileEntity);
				vector.modifyPositionFromSide(direction);
				TileEntity checkTile = vector.getTileEntity(tileEntity.getWorldObj());
				if (checkTile != null) {
					checkStack = getFirstItemBlock(checkTile, itemStack, false);
					if (checkStack != null) {
						return checkStack;
					}
				}
			}
		}

		return null;
	}

	public static ItemStack getFirstItemBlock(int i, IInventory inventory, ItemStack itemStack) {
		ItemStack checkStack = inventory.getStackInSlot(i);
		return checkStack == null || !(checkStack.getItem() instanceof ItemBlock) || itemStack != null && !checkStack.isItemEqual(itemStack) ? null : checkStack;
	}

	public static Block getFilterBlock(ItemStack itemStack) {
		if (itemStack != null && itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlockID() < Block.blocksList.length) {
			Block block = ((ItemBlock) itemStack.getItem()).field_150939_a;
			if (block.renderAsNormalBlock()) {
				return block;
			}
		}

		return null;
	}

	public static ItemStack getCamoBlock(IProjector projector, Vector3 position) {
		if (projector != null && !((TileEntity) projector).getWorldObj().isRemote && projector != null && projector.getModuleCount(ModularForceFieldSystem.itemModuleCamouflage, new int[0]) > 0) {
			if (projector.getMode() instanceof ItemModeCustom) {
				HashMap fieldMap = ((ItemModeCustom) projector.getMode()).getFieldBlockMap(projector, projector.getModeStack());
				if (fieldMap != null) {
					Vector3 fieldCenter = (new Vector3((TileEntity) projector)).add(projector.getTranslation());
					Vector3 relativePosition = position.clone().subtract(fieldCenter);
					CalculationHelper.rotateByAngle(relativePosition, (double) (-projector.getRotationYaw()), (double) (-projector.getRotationPitch()));
					int[] blockInfo = (int[]) fieldMap.get(relativePosition.round());
					if (blockInfo != null && blockInfo[0] > 0) {
						return new ItemStack(Block.blocksList[blockInfo[0]], 1, blockInfo[1]);
					}
				}
			}

			int[] arr$ = projector.getModuleSlots();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				int i = arr$[i$];
				ItemStack checkStack = projector.getStackInSlot(i);
				Block block = getFilterBlock(checkStack);
				if (block != null) {
					return checkStack;
				}
			}
		}

		return null;
	}

	public static NBTTagCompound getNBTTagCompound(ItemStack itemStack) {
		if (itemStack != null) {
			if (itemStack.getTagCompound() == null) {
				itemStack.setTagCompound(new NBTTagCompound());
			}

			return itemStack.getTagCompound();
		} else {
			return null;
		}
	}

	public static boolean hasPermission(World world, Vector3 position, Permission permission, EntityPlayer player) {
		IInterdictionMatrix interdictionMatrix = getNearestInterdictionMatrix(world, position);
		return interdictionMatrix != null ? isPermittedByInterdictionMatrix(interdictionMatrix, player.getCommandSenderName(), permission) : true;
	}

	public static boolean hasPermission(World world, Vector3 position, Action action, EntityPlayer player) {
		IInterdictionMatrix interdictionMatrix = getNearestInterdictionMatrix(world, position);
		return interdictionMatrix != null ? hasPermission(world, position, interdictionMatrix, action, player) : true;
	}

	public static boolean hasPermission(World world, Vector3 position, IInterdictionMatrix interdictionMatrix, Action action, EntityPlayer player) {
		boolean hasPermission = true;
		if (action == Action.RIGHT_CLICK_BLOCK && position.getTileEntity(world) != null && interdictionMatrix.getModuleCount(ModularForceFieldSystem.itemModuleBlockAccess, new int[0]) > 0) {
			hasPermission = false;
			if (isPermittedByInterdictionMatrix(interdictionMatrix, player.getCommandSenderName(), Permission.BLOCK_ACCESS)) {
				hasPermission = true;
			}
		}

		if (hasPermission && interdictionMatrix.getModuleCount(ModularForceFieldSystem.itemModuleBlockAlter, new int[0]) > 0 && (player.getCurrentEquippedItem() != null || action == Action.LEFT_CLICK_BLOCK)) {
			hasPermission = false;
			if (isPermittedByInterdictionMatrix(interdictionMatrix, player.getCommandSenderName(), Permission.BLOCK_ALTER)) {
				hasPermission = true;
			}
		}

		return hasPermission;
	}

}
