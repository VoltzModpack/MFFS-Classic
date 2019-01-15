package basiccomponents.client.gui;

import basiccomponents.common.container.ContainerBatteryBox;
import basiccomponents.common.tileentity.TileEntityBatteryBox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.electricity.ElectricityDisplay;

@SideOnly(Side.CLIENT)
public class GuiBatteryBox extends GuiContainer {

	private TileEntityBatteryBox tileEntity;
	private int containerWidth;
	private int containerHeight;

	public GuiBatteryBox(InventoryPlayer par1InventoryPlayer, TileEntityBatteryBox batteryBox) {
		super(new ContainerBatteryBox(par1InventoryPlayer, batteryBox));
		this.tileEntity = batteryBox;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.fontRenderer.drawString(this.tileEntity.getInvName(), 65, 6, 4210752);
		String displayJoules = ElectricityDisplay.getDisplayShort(this.tileEntity.getJoules(), ElectricityDisplay.ElectricUnit.JOULES);
		String displayMaxJoules = ElectricityDisplay.getDisplay(this.tileEntity.getMaxJoules(), ElectricityDisplay.ElectricUnit.JOULES);
		if (this.tileEntity.isDisabled()) {
			displayMaxJoules = "Disabled";
		}

		super.fontRenderer.drawString(displayJoules + " of", 98 - displayJoules.length(), 30, 4210752);
		super.fontRenderer.drawString(displayMaxJoules, 78, 40, 4210752);
		super.fontRenderer.drawString("Voltage: " + (int) this.tileEntity.getVoltage(), 90, 60, 4210752);
		super.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, super.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.mc.renderEngine.bindTexture("/mods/basiccomponents/textures/gui/battery_box.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.containerWidth = (super.width - super.xSize) / 2;
		this.containerHeight = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, super.xSize, super.ySize);
		int scale = (int) (this.tileEntity.getJoules() / this.tileEntity.getMaxJoules() * 72.0D);
		this.drawTexturedModalRect(this.containerWidth + 87, this.containerHeight + 52, 176, 0, scale, 20);
	}
}
