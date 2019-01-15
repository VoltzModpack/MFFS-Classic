package mffs.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import mffs.base.GuiBase;
import mffs.base.TileEntityBase;
import mffs.container.ContainerCoercionDeriver;
import mffs.tileentity.TileEntityCoercionDeriver;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.network.PacketManager;

public class GuiCoercionDeriver extends GuiBase {

	private TileEntityCoercionDeriver tileEntity;

	public GuiCoercionDeriver(EntityPlayer player, TileEntityCoercionDeriver tileentity) {
		super(new ContainerCoercionDeriver(player, tileentity), tileentity);
		this.tileEntity = tileentity;
	}

	public void initGui() {
		super.textFieldPos = new Vector2(30.0D, 43.0D);
		super.initGui();
		super.buttonList.add(new GuiButton(1, super.width / 2 - 10, super.height / 2 - 28, 58, 20, "Derive"));
	}

	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.fontRenderer.drawString(this.tileEntity.getInvName(), super.xSize / 2 - super.fontRenderer.getStringWidth(this.tileEntity.getInvName()) / 2, 6, 4210752);
		this.drawTextWithTooltip("frequency", "%1:", 8, 30, x, y);
		super.textFieldFrequency.drawTextBox();
		GL11.glPushMatrix();
		GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
		this.drawTextWithTooltip("upgrade", -95, 140, x, y);
		GL11.glPopMatrix();
		if (super.buttonList.get(1) instanceof GuiButton) {
			if (!this.tileEntity.isInversed) {
				((GuiButton) super.buttonList.get(1)).displayString = "Derive";
			} else {
				((GuiButton) super.buttonList.get(1)).displayString = "Integrate";
			}
		}

		super.fontRenderer.drawString(1000.0D * UniversalElectricity.TO_BC_RATIO + " MJ/s", 85, 30, 4210752);
		super.fontRenderer.drawString(1000.0D * UniversalElectricity.TO_IC2_RATIO + " EU/s", 85, 40, 4210752);
		super.fontRenderer.drawString(ElectricityDisplay.getDisplayShort(1000.0D, ElectricityDisplay.ElectricUnit.WATT), 85, 50, 4210752);
		super.fontRenderer.drawString(ElectricityDisplay.getDisplayShort(this.tileEntity.getVoltage(), ElectricityDisplay.ElectricUnit.VOLTAGE), 85, 60, 4210752);
		this.drawTextWithTooltip("progress", "%1: " + (this.tileEntity.isActive() ? "Running" : "Idle"), 8, 70, x, y);
		this.drawTextWithTooltip("fortron", "%1: " + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronEnergy(), ElectricityDisplay.ElectricUnit.JOULES), 8, 105, x, y);
		super.fontRenderer.drawString("§2+" + ElectricityDisplay.getDisplayShort((double) (this.tileEntity.getProductionRate() * 20), ElectricityDisplay.ElectricUnit.JOULES), 120, 117, 4210752);
		super.drawGuiContainerForegroundLayer(x, y);
	}

	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		super.drawGuiContainerBackgroundLayer(f, x, y);
		this.drawSlot(153, 46);
		this.drawSlot(153, 66);
		this.drawSlot(153, 86);
		this.drawSlot(8, 40);
		this.drawSlot(8, 82, GuiBase.SlotType.BATTERY);
		this.drawSlot(28, 82);
		this.drawBar(50, 84, 1.0F);
		this.drawForce(8, 115, (float) this.tileEntity.getFortronEnergy() / (float) this.tileEntity.getFortronCapacity());
	}

	protected void actionPerformed(GuiButton guibutton) {
		super.actionPerformed(guibutton);
		if (guibutton.id == 1) {
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", (TileEntity) super.frequencyTile, TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()));
		}

	}
}
