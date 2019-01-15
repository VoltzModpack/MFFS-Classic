package mffs.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import mffs.base.GuiBase;
import mffs.base.TileEntityBase;
import mffs.container.ContainerInterdictionMatrix;
import mffs.tileentity.TileEntityInterdictionMatrix;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.network.PacketManager;

public class GuiInterdictionMatrix extends GuiBase {

	private TileEntityInterdictionMatrix tileEntity;

	public GuiInterdictionMatrix(EntityPlayer player, TileEntityInterdictionMatrix tileEntity) {
		super(new ContainerInterdictionMatrix(player, tileEntity), tileEntity);
		this.tileEntity = tileEntity;
	}

	public void initGui() {
		super.textFieldPos = new Vector2(110.0D, 91.0D);
		super.initGui();
		super.buttonList.add(new GuiButton(1, super.width / 2 - 80, super.height / 2 - 65, 50, 20, "Banned"));
	}

	protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
		if (guiButton.id == 1) {
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", this.tileEntity, TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()));
		}

	}

	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.fontRenderer.drawString(this.tileEntity.getInvName(), super.xSize / 2 - super.fontRenderer.getStringWidth(this.tileEntity.getInvName()) / 2, 6, 4210752);
		this.drawTextWithTooltip("warn", "%1: " + this.tileEntity.getWarningRange(), 35, 19, x, y);
		this.drawTextWithTooltip("action", "%1: " + this.tileEntity.getActionRange(), 100, 19, x, y);
		this.drawTextWithTooltip("filterMode", "%1:", 9, 32, x, y);
		if (!this.tileEntity.isBanMode()) {
			if (super.buttonList.get(1) instanceof GuiButton) {
				((GuiButton) super.buttonList.get(1)).displayString = "Allowed";
			}
		} else if (super.buttonList.get(1) instanceof GuiButton) {
			((GuiButton) super.buttonList.get(1)).displayString = "Banned";
		}

		this.drawTextWithTooltip("frequency", "%1:", 8, 93, x, y);
		super.textFieldFrequency.drawTextBox();
		this.drawTextWithTooltip("fortron", "%1: " + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronEnergy(), ElectricityDisplay.ElectricUnit.JOULES) + "/" + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronCapacity(), ElectricityDisplay.ElectricUnit.JOULES), 8, 110, x, y);
		super.fontRenderer.drawString("§4-" + ElectricityDisplay.getDisplayShort((double) (this.tileEntity.getFortronCost() * 20), ElectricityDisplay.ElectricUnit.JOULES), 120, 121, 4210752);
		super.drawGuiContainerForegroundLayer(x, y);
	}

	protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
		super.drawGuiContainerBackgroundLayer(var1, x, y);

		int var4;
		for (var4 = 0; var4 < 2; ++var4) {
			for (int var4 = 0; var4 < 4; ++var4) {
				this.drawSlot(98 + var4 * 18, 30 + var4 * 18);
			}
		}

		for (var4 = 0; var4 < 9; ++var4) {
			if (this.tileEntity.isBanMode()) {
				this.drawSlot(8 + var4 * 18, 68, GuiBase.SlotType.NONE, 1.0F, 0.8F, 0.8F);
			} else {
				this.drawSlot(8 + var4 * 18, 68, GuiBase.SlotType.NONE, 0.8F, 1.0F, 0.8F);
			}
		}

		this.drawSlot(68, 88);
		this.drawSlot(86, 88);
		this.drawForce(8, 120, Math.min((float) this.tileEntity.getFortronEnergy() / (float) this.tileEntity.getFortronCapacity(), 1.0F));
	}
}
