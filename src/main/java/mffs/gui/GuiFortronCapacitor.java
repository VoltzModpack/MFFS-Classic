package mffs.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import mffs.base.GuiBase;
import mffs.base.TileEntityBase;
import mffs.container.ContainerFortronCapacitor;
import mffs.gui.button.GuiButtonPressTransferMode;
import mffs.tileentity.TileEntityFortronCapacitor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.network.PacketManager;

public class GuiFortronCapacitor extends GuiBase {

	private TileEntityFortronCapacitor tileEntity;

	public GuiFortronCapacitor(EntityPlayer player, TileEntityFortronCapacitor tileentity) {
		super(new ContainerFortronCapacitor(player, tileentity), tileentity);
		this.tileEntity = tileentity;
	}

	public void initGui() {
		super.textFieldPos = new Vector2(50.0D, 76.0D);
		super.initGui();
		super.buttonList.add(new GuiButtonPressTransferMode(1, super.width / 2 + 15, super.height / 2 - 37, this, this.tileEntity));
	}

	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.fontRenderer.drawString(this.tileEntity.getInvName(), super.xSize / 2 - super.fontRenderer.getStringWidth(this.tileEntity.getInvName()) / 2, 6, 4210752);
		GL11.glPushMatrix();
		GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
		this.drawTextWithTooltip("upgrade", -95, 140, x, y);
		GL11.glPopMatrix();
		this.drawTextWithTooltip("linkedDevice", "%1: " + this.tileEntity.getLinkedDevices().size(), 8, 28, x, y);
		this.drawTextWithTooltip("transmissionRate", "%1: " + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getTransmissionRate(), ElectricityDisplay.ElectricUnit.JOULES), 8, 40, x, y);
		this.drawTextWithTooltip("range", "%1: " + this.tileEntity.getTransmissionRange(), 8, 52, x, y);
		this.drawTextWithTooltip("frequency", "%1:", 8, 63, x, y);
		super.textFieldFrequency.drawTextBox();
		this.drawTextWithTooltip("fortron", "%1:", 8, 95, x, y);
		super.fontRenderer.drawString(ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronEnergy(), ElectricityDisplay.ElectricUnit.JOULES) + "/" + ElectricityDisplay.getDisplayShort((double) this.tileEntity.getFortronCapacity(), ElectricityDisplay.ElectricUnit.JOULES), 8, 105, 4210752);
		super.drawGuiContainerForegroundLayer(x, y);
	}

	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		super.drawGuiContainerBackgroundLayer(f, x, y);
		this.drawSlot(153, 46);
		this.drawSlot(153, 66);
		this.drawSlot(153, 86);
		this.drawSlot(8, 73);
		this.drawSlot(26, 73);
		this.drawForce(8, 115, Math.min((float) this.tileEntity.getFortronEnergy() / (float) this.tileEntity.getFortronCapacity(), 1.0F));
	}

	protected void actionPerformed(GuiButton guibutton) {
		super.actionPerformed(guibutton);
		if (guibutton.id == 1) {
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", this.tileEntity, TileEntityBase.TilePacketType.TOGGLE_MODE.ordinal()));
		}

	}
}
