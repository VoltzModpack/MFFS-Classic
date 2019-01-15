package basiccomponents.client.gui;

import basiccomponents.common.container.ContainerCoalGenerator;
import basiccomponents.common.tileentity.TileEntityCoalGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.electricity.ElectricityDisplay;

@SideOnly(Side.CLIENT)
public class GuiCoalGenerator extends GuiContainer {

	private TileEntityCoalGenerator tileEntity;
	private int containerWidth;
	private int containerHeight;

	public GuiCoalGenerator(InventoryPlayer par1InventoryPlayer, TileEntityCoalGenerator tileEntity) {
		super(new ContainerCoalGenerator(par1InventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.fontRenderer.drawString(this.tileEntity.getInvName(), 55, 6, 4210752);
		super.fontRenderer.drawString("Generating", 90, 33, 4210752);
		String displayText = "";
		if (this.tileEntity.isDisabled()) {
			displayText = "Disabled";
		} else if (this.tileEntity.generateWatts <= 0.0D) {
			displayText = "Not Generating";
		} else if (this.tileEntity.generateWatts < 100.0D) {
			displayText = "Hull Heat: " + (int) (this.tileEntity.generateWatts / 100.0D * 100.0D) + "%";
		} else {
			displayText = ElectricityDisplay.getDisplay(this.tileEntity.generateWatts, ElectricityDisplay.ElectricUnit.WATT);
		}

		super.fontRenderer.drawString(displayText, (int) (100.0D - (double) displayText.length() * 1.25D), 45, 4210752);
		super.fontRenderer.drawString("Voltage: " + (int) this.tileEntity.getVoltage(), 85, 60, 4210752);
		super.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, super.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		super.mc.renderEngine.bindTexture("/mods/basiccomponents/textures/gui/coal_generator.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.containerWidth = (super.width - super.xSize) / 2;
		this.containerHeight = (super.height - super.ySize) / 2;
		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, super.xSize, super.ySize);
	}
}
