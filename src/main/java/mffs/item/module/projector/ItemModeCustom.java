package mffs.item.module.projector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.MFFSHelper;
import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.ICache;
import mffs.api.IFieldInteraction;
import mffs.api.IProjector;
import mffs.api.modules.IProjectorMode;
import mffs.item.mode.ItemMode;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.flag.NBTFileLoader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ItemModeCustom extends ItemMode implements ICache {

	private static final String NBT_ID = "id";
	private static final String NBT_MODE = "mode";
	private static final String NBT_POINT_1 = "point1";
	private static final String NBT_POINT_2 = "point2";
	private static final String NBT_FIELD_BLOCK_LIST = "fieldPoints";
	private static final String NBT_FIELD_BLOCK_ID = "blockID";
	private static final String NBT_FIELD_BLOCK_METADATA = "blockMetadata";
	private static final String NBT_FIELD_SIZE = "fieldSize";
	private static final String NBT_FILE_SAVE_PREFIX = "custom_mode_";
	private final HashMap cache = new HashMap();

	public ItemModeCustom(int i) {
		super(i, "modeCustom");
	}

	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
		list.add("Mode: " + (nbt.getBoolean("mode") ? "Additive" : "Substraction"));
		Vector3 point1 = Vector3.readFromNBT(nbt.getCompoundTag("point1"));
		list.add("Point 1: " + point1.intX() + ", " + point1.intY() + ", " + point1.intZ());
		Vector3 point2 = Vector3.readFromNBT(nbt.getCompoundTag("point2"));
		list.add("Point 2: " + point2.intX() + ", " + point2.intY() + ", " + point2.intZ());
		int modeID = nbt.getInteger("id");
		if (modeID > 0) {
			list.add("Mode ID: " + modeID);
			int fieldSize = nbt.getInteger("fieldSize");
			if (fieldSize > 0) {
				list.add("Field size: " + fieldSize);
			} else {
				list.add("Field not saved.");
			}
		}

		if (GuiScreen.isShiftKeyDown()) {
			super.addInformation(itemStack, par2EntityPlayer, list, par4);
		} else {
			list.add("Hold shift for more...");
		}

	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		if (!world.isRemote) {
			NBTTagCompound nbt;
			if (entityPlayer.isSneaking()) {
				nbt = MFFSHelper.getNBTTagCompound(itemStack);
				if (nbt != null) {
					Vector3 point1 = Vector3.readFromNBT(nbt.getCompoundTag("point1"));
					Vector3 point2 = Vector3.readFromNBT(nbt.getCompoundTag("point2"));
					if (nbt.hasKey("point1") && nbt.hasKey("point2") && !point1.equals(point2) && point1.distanceTo(point2) < (double) Settings.MAX_FORCE_FIELD_SCALE) {
						nbt.removeTag("point1");
						nbt.removeTag("point2");
						Vector3 midPoint = new Vector3();
						midPoint.x = (point1.x + point2.x) / 2.0D;
						midPoint.y = (point1.y + point2.y) / 2.0D;
						midPoint.z = (point1.z + point2.z) / 2.0D;
						midPoint = midPoint.floor();
						point1.subtract(midPoint);
						point2.subtract(midPoint);
						Vector3 minPoint = new Vector3(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y), Math.min(point1.z, point2.z));
						Vector3 maxPoint = new Vector3(Math.max(point1.x, point2.x), Math.max(point1.y, point2.y), Math.max(point1.z, point2.z));
						NBTTagCompound saveNBT = NBTFileLoader.loadData(this.getSaveDirectory(), "custom_mode_" + this.getModeID(itemStack));
						if (saveNBT == null) {
							saveNBT = new NBTTagCompound();
						}

						NBTTagList list;
						if (saveNBT.hasKey("fieldPoints")) {
							list = (NBTTagList) saveNBT.getTag("fieldPoints");
						} else {
							list = new NBTTagList();
						}

						for (int x = minPoint.intX(); x <= maxPoint.intX(); ++x) {
							for (int y = minPoint.intY(); y <= maxPoint.intY(); ++y) {
								for (int z = minPoint.intZ(); z <= maxPoint.intZ(); ++z) {
									Vector3 position = new Vector3((double) x, (double) y, (double) z);
									Vector3 targetCheck = Vector3.add(midPoint, position);
									int blockID = targetCheck.getBlockID(world);
									if (blockID > 0) {
										if (nbt.getBoolean("mode")) {
											NBTTagCompound vectorTag = new NBTTagCompound();
											position.writeToNBT(vectorTag);
											vectorTag.setInteger("blockID", blockID);
											vectorTag.setInteger("blockMetadata", targetCheck.getBlockMetadata(world));
											list.appendTag(vectorTag);
										} else {
											for (int i = 0; i < list.tagCount(); ++i) {
												Vector3 vector = Vector3.readFromNBT((NBTTagCompound) list.tagAt(i));
												if (vector.equals(position)) {
													list.removeTag(i);
												}
											}
										}
									}
								}
							}
						}

						saveNBT.setTag("fieldPoints", list);
						nbt.setInteger("fieldSize", list.tagCount());
						NBTFileLoader.saveData(this.getSaveDirectory(), "custom_mode_" + this.getModeID(itemStack), saveNBT);
						this.clearCache();
						entityPlayer.addChatMessage("Field structure saved.");
					}
				}
			} else {
				nbt = MFFSHelper.getNBTTagCompound(itemStack);
				if (nbt != null) {
					nbt.setBoolean("mode", !nbt.getBoolean("mode"));
					entityPlayer.addChatMessage("Changed selection mode to " + (nbt.getBoolean("mode") ? "additive" : "substraction"));
				}
			}
		}

		return itemStack;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (!world.isRemote) {
			NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
			if (nbt != null) {
				Vector3 point1 = Vector3.readFromNBT(nbt.getCompoundTag("point1"));
				if (nbt.hasKey("point1") && !point1.equals(new Vector3(0.0D, 0.0D, 0.0D))) {
					nbt.setCompoundTag("point2", (new Vector3((double) x, (double) y, (double) z)).writeToNBT(new NBTTagCompound()));
					entityPlayer.addChatMessage("Set point 2: " + x + ", " + y + ", " + z + ".");
				} else {
					nbt.setCompoundTag("point1", (new Vector3((double) x, (double) y, (double) z)).writeToNBT(new NBTTagCompound()));
					entityPlayer.addChatMessage("Set point 1: " + x + ", " + y + ", " + z + ".");
				}
			}
		}

		return true;
	}

	public int getModeID(ItemStack itemStack) {
		NBTTagCompound nbt = MFFSHelper.getNBTTagCompound(itemStack);
		int id = nbt.getInteger("id");
		if (id <= 0) {
			nbt.setInteger("id", this.getNextAvaliableID());
			id = nbt.getInteger("id");
		}

		return id;
	}

	public int getNextAvaliableID() {
		int i = 1;
		File[] arr$ = this.getSaveDirectory().listFiles();
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			File var10000 = arr$[i$];
			++i;
		}

		return i;
	}

	public File getSaveDirectory() {
		File saveDirectory = NBTFileLoader.getSaveDirectory(MinecraftServer.getServer().getFolderName());
		if (!saveDirectory.exists()) {
			saveDirectory.mkdir();
		}

		File file = new File(saveDirectory, "mffs");
		if (!file.exists()) {
			file.mkdir();
		}

		return file;
	}

	public Set getFieldBlocks(IFieldInteraction projector, ItemStack itemStack) {
		return this.getFieldBlockMap(projector, itemStack).keySet();
	}

	public HashMap getFieldBlockMap(IFieldInteraction projector, ItemStack itemStack) {
		String cacheID = "itemStack_" + itemStack.hashCode();
		if (Settings.USE_CACHE && this.cache.containsKey(cacheID) && this.cache.get(cacheID) instanceof HashMap) {
			return (HashMap) this.cache.get(cacheID);
		} else {
			float scale = (float) projector.getModuleCount(ModularForceFieldSystem.itemModuleScale, new int[0]) / 3.0F;
			HashMap fieldBlocks = new HashMap();
			if (this.getSaveDirectory() != null) {
				NBTTagCompound nbt = NBTFileLoader.loadData(this.getSaveDirectory(), "custom_mode_" + this.getModeID(itemStack));
				if (nbt != null) {
					NBTTagList nbtTagList = nbt.getTagList("fieldPoints");

					for (int i = 0; i < nbtTagList.tagCount(); ++i) {
						NBTTagCompound vectorTag = (NBTTagCompound) nbtTagList.tagAt(i);
						Vector3 position = Vector3.readFromNBT(vectorTag);
						if (scale > 0.0F) {
							position.multiply((double) scale);
						}

						int[] blockInfo = new int[]{vectorTag.getInteger("blockID"), vectorTag.getInteger("blockMetadata")};
						if (position != null) {
							fieldBlocks.put(position, blockInfo);
						}
					}
				}

				if (Settings.USE_CACHE) {
					this.cache.put(cacheID, fieldBlocks);
				}
			}

			return fieldBlocks;
		}
	}

	public Object getCache(String cacheID) {
		return this.cache.get(cacheID);
	}

	public void clearCache(String cacheID) {
		this.cache.remove(cacheID);
	}

	public void clearCache() {
		this.cache.clear();
	}

	public Set getExteriorPoints(IFieldInteraction projector) {
		return this.getFieldBlocks(projector, projector.getModeStack());
	}

	public Set getInteriorPoints(IFieldInteraction projector) {
		return this.getExteriorPoints(projector);
	}

	public boolean isInField(IFieldInteraction projector, Vector3 position) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void render(IProjector projector, double x, double y, double z, float f, long ticks) {
		IProjectorMode[] modes = new IProjectorMode[]{ModularForceFieldSystem.itemModeCube, ModularForceFieldSystem.itemModeSphere, ModularForceFieldSystem.itemModeTube, ModularForceFieldSystem.itemModePyramid};
		modes[((TileEntity) projector).worldObj.rand.nextInt(modes.length - 1)].render(projector, x, y, z, f, ticks);
	}
}
