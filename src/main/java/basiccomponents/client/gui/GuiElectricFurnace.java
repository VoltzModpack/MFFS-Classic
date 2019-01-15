package basiccomponents.client.gui;

import basiccomponents.common.container.ContainerElectricFurnace;
import basiccomponents.common.tileentity.TileEntityElectricFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.electricity.ElectricityDisplay;

@SideOnly(Side.CLIENT)
public class GuiElectricFurnace extends GuiContainer {

	private TileEntityElectricFurnace tileEntity;
	private int containerWidth;
	private int containerHeight;

	public GuiElectricFurnace(InventoryPlayer par1InventoryPlayer, TileEntityElectricFurnace tileEntity) {
		super(new ContainerElectricFurnace(par1InventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.fontRenderer.drawString(this.tileEntity.getInvName(), 45, 6, 4210752);
		super.fontRenderer.drawString("Smelting:", 10, 28, 4210752);
		super.fontRenderer.drawString("Battery:", 10, 53, 4210752);
		String displayText = "";
		if (this.tileEntity.isDisabled()) {
			displayText = "Disabled!";
		} else if (this.tileEntity.processTicks > 0) {
			displayText = "Smelting";
		} else {
			displayText = "Idle";
		}

		super.fontRenderer.drawString("Status: " + displayText, 82, 45, 4210752);
		super.fontRenderer.drawString(ElectricityDisplay.getDisplay(10000.0D, ElectricityDisplay.ElectricUnit.WATT), 82, 56, 4210752);
		super.fontRenderer.drawString(ElectricityDisplay.getDisplay(this.tileEntity.getVoltage(), ElectricityDisplay.ElectricUnit.VOLTAGE), 82, 68, 4210752);
		super.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, super.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.mc.renderEngine.bindTexture("/mods/basiccomponents/textures/gui/electric_furnace.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.containerWidth = (super.width - super.xSize) / 2;
		this.containerHeight = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, super.xSize, super.ySize);
		if (this.tileEntity.processTicks > 0) {
			int scale = (int) ((double) this.tileEntity.processTicks / 130.0D * 23.0D);
			this.drawTexturedModalRect(this.containerWidth + 77, this.containerHeight + 24, 176, 0, 23 - scale, 20);
		}

	}
}
