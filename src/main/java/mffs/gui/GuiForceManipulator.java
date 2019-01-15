package mffs.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import mffs.base.GuiBase;
import mffs.base.TileEntityBase;
import mffs.container.ContainerForceManipulator;
import mffs.gui.button.GuiIcon;
import mffs.tileentity.TileEntityForceManipulator;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.vector.Region2;

public class GuiForceManipulator extends GuiBase {

	private TileEntityForceManipulator tileEntity;

	public GuiForceManipulator(EntityPlayer player, TileEntityForceManipulator tileEntity) {
		super(new ContainerForceManipulator(player, tileEntity), tileEntity);
		this.tileEntity = tileEntity;
	}

	public void initGui() {
		super.textFieldPos = new Vector2(111.0D, 93.0D);
		super.initGui();
		super.buttonList.add(new GuiButton(1, super.width / 2 - 60, super.height / 2 - 22, 40, 20, "Reset"));
		super.buttonList.add(new GuiIcon(2, super.width / 2 - 82, super.height / 2 - 82, new ItemStack[]{null, new ItemStack(Item.redstone), new ItemStack(Block.blockRedstone)}));
		super.buttonList.add(new GuiIcon(3, super.width / 2 - 82, super.height / 2 - 60, new ItemStack[]{null, new ItemStack(Block.anvil)}));
		super.tooltips.put(new Region2(new Vector2(117.0D, 44.0D), (new Vector2(117.0D, 44.0D)).add(18.0D)), "Mode");
		super.tooltips.put(new Region2(new Vector2(90.0D, 17.0D), (new Vector2(90.0D, 17.0D)).add(18.0D)), "Up");
		super.tooltips.put(new Region2(new Vector2(144.0D, 17.0D), (new Vector2(144.0D, 17.0D)).add(18.0D)), "Up");
		super.tooltips.put(new Region2(new Vector2(90.0D, 71.0D), (new Vector2(90.0D, 71.0D)).add(18.0D)), "Down");
		super.tooltips.put(new Region2(new Vector2(144.0D, 71.0D), (new Vector2(144.0D, 71.0D)).add(18.0D)), "Down");
		super.tooltips.put(new Region2(new Vector2(108.0D, 17.0D), (new Vector2(108.0D, 17.0D)).add(18.0D)), "Front");
		super.tooltips.put(new Region2(new Vector2(126.0D, 17.0D), (new Vector2(126.0D, 17.0D)).add(18.0D)), "Front");
		super.tooltips.put(new Region2(new Vector2(108.0D, 71.0D), (new Vector2(108.0D, 71.0D)).add(18.0D)), "Back");
		super.tooltips.put(new Region2(new Vector2(126.0D, 71.0D), (new Vector2(126.0D, 71.0D)).add(18.0D)), "Back");
		super.tooltips.put(new Region2(new Vector2(90.0D, 35.0D), (new Vector2(108.0D, 35.0D)).add(18.0D)), "Left");
		super.tooltips.put(new Region2(new Vector2(90.0D, 53.0D), (new Vector2(108.0D, 53.0D)).add(18.0D)), "Left");
		super.tooltips.put(new Region2(new Vector2(144.0D, 35.0D), (new Vector2(144.0D, 35.0D)).add(18.0D)), "Right");
		super.tooltips.put(new Region2(new Vector2(144.0D, 53.0D), (new Vector2(144.0D, 53.0D)).add(18.0D)), "Right");
	}

	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.fontRenderer.drawString(this.tileEntity.getInvName(), super.xSize / 2 - super.fontRenderer.getStringWidth(this.tileEntity.getInvName()) / 2, 6, 4210752);
		GL11.glPushMatrix();
		GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
		super.fontRenderer.drawString(this.tileEntity.getDirection(this.tileEntity.worldObj, this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord).name(), -100, 10, 4210752);
		GL11.glPopMatrix();
		super.fontRenderer.drawString("Anchor:", 30, 60, 4210752);
		if (this.tileEntity.anchor != null) {
			super.fontRenderer.drawString(this.tileEntity.anchor.intX() + ", " + this.tileEntity.anchor.intY() + ", " + this.tileEntity.anchor.intZ(), 30, 72, 4210752);
		}

		super.textFieldFrequency.drawTextBox();
		this.drawTextWithTooltip("fortron", "%1: " + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronEnergy(), ElectricityDisplay.ElectricUnit.JOULES) + "/" + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronCapacity(), ElectricityDisplay.ElectricUnit.JOULES), 8, 110, x, y);
		super.fontRenderer.drawString("§4-" + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronCost(), ElectricityDisplay.ElectricUnit.JOULES), 120, 121, 4210752);
		super.drawGuiContainerForegroundLayer(x, y);
	}

	public void updateScreen() {
		super.updateScreen();
		((GuiIcon) super.buttonList.get(2)).setIndex(this.tileEntity.displayMode);
		((GuiIcon) super.buttonList.get(3)).setIndex(this.tileEntity.doAnchor ? 1 : 0);
	}

	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		super.drawGuiContainerBackgroundLayer(f, x, y);
		this.drawSlot(72, 90);
		this.drawSlot(90, 90);
		this.drawSlot(117, 44, GuiBase.SlotType.NONE, 1.0F, 0.4F, 0.4F);

		int xSlot;
		int ySlot;
		for (xSlot = 0; xSlot < 4; ++xSlot) {
			for (ySlot = 0; ySlot < 4; ++ySlot) {
				if ((xSlot != 1 || ySlot != 1) && (xSlot != 2 || ySlot != 2) && (xSlot != 1 || ySlot != 2) && (xSlot != 2 || ySlot != 1)) {
					GuiBase.SlotType type = GuiBase.SlotType.NONE;
					if (xSlot == 0 && ySlot == 0) {
						type = GuiBase.SlotType.ARR_UP_LEFT;
					} else if (xSlot == 0 && ySlot == 3) {
						type = GuiBase.SlotType.ARR_DOWN_LEFT;
					} else if (xSlot == 3 && ySlot == 0) {
						type = GuiBase.SlotType.ARR_UP_RIGHT;
					} else if (xSlot == 3 && ySlot == 3) {
						type = GuiBase.SlotType.ARR_DOWN_RIGHT;
					} else if (ySlot == 0) {
						type = GuiBase.SlotType.ARR_UP;
					} else if (ySlot == 3) {
						type = GuiBase.SlotType.ARR_DOWN;
					} else if (xSlot == 0) {
						type = GuiBase.SlotType.ARR_LEFT;
					} else if (xSlot == 3) {
						type = GuiBase.SlotType.ARR_RIGHT;
					}

					this.drawSlot(90 + 18 * xSlot, 17 + 18 * ySlot, type);
				}
			}
		}

		for (xSlot = 0; xSlot < 3; ++xSlot) {
			for (ySlot = 0; ySlot < 2; ++ySlot) {
				this.drawSlot(30 + 18 * xSlot, 18 + 18 * ySlot);
			}
		}

		this.drawForce(8, 120, Math.min((float) this.tileEntity.getFortronEnergy() / (float) this.tileEntity.getFortronCapacity(), 1.0F));
	}

	protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
		if (guiButton.id == 1) {
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", (TileEntity) super.frequencyTile, TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()));
		} else if (guiButton.id == 2) {
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", (TileEntity) super.frequencyTile, TileEntityBase.TilePacketType.TOGGLE_MODE_2.ordinal()));
		} else if (guiButton.id == 3) {
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", (TileEntity) super.frequencyTile, TileEntityBase.TilePacketType.TOGGLE_MODE_3.ordinal()));
		}

	}
}
