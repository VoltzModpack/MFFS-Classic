package mffs.gui.button;

import mffs.base.GuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.TranslationHelper;

public class GuiButtonPress extends GuiButton {

	protected Vector2 offset;
	public boolean stuck;
	private GuiBase mainGui;

	public GuiButtonPress(int id, int x, int y, Vector2 offset, GuiBase mainGui, String name) {
		super(id, x, y, 18, 18, name);
		this.offset = new Vector2();
		this.stuck = false;
		this.offset = offset;
		this.mainGui = mainGui;
	}

	public GuiButtonPress(int id, int x, int y, Vector2 offset, GuiBase mainGui) {
		this(id, x, y, offset, mainGui, "");
	}

	public GuiButtonPress(int id, int x, int y, Vector2 offset) {
		this(id, x, y, offset, (GuiBase) null, "");
	}

	public GuiButtonPress(int id, int x, int y) {
		this(id, x, y, new Vector2());
	}

	public void drawButton(Minecraft minecraft, int x, int y) {
		if (super.drawButton) {
			Minecraft.getMinecraft().renderEngine.bindTexture("/mods/mffs/textures/gui/gui_button.png");
			if (this.stuck) {
				GL11.glColor4f(0.6F, 0.6F, 0.6F, 1.0F);
			} else if (this.isPointInRegion(super.xPosition, super.yPosition, super.width, super.height, x, y)) {
				GL11.glColor4f(0.85F, 0.85F, 0.85F, 1.0F);
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

			this.drawTexturedModalRect(super.xPosition, super.yPosition, this.offset.intX(), this.offset.intY(), super.width, super.height);
			this.mouseDragged(minecraft, x, y);
		}

	}

	protected void mouseDragged(Minecraft minecraft, int x, int y) {
		if (this.mainGui != null && super.displayString != null && super.displayString.length() > 0 && this.isPointInRegion(super.xPosition, super.yPosition, super.width, super.height, x, y)) {
			String title = TranslationHelper.getLocal("gui." + super.displayString + ".name");
			this.mainGui.tooltip = TranslationHelper.getLocal("gui." + super.displayString + ".tooltip");
			if (title != null && title.length() > 0) {
				this.mainGui.tooltip = title + ": " + this.mainGui.tooltip;
			}
		}

	}

	protected boolean isPointInRegion(int x, int y, int width, int height, int checkX, int checkY) {
		int var7 = 0;
		int var8 = 0;
		checkX -= var7;
		checkY -= var8;
		return checkX >= x - 1 && checkX < x + width + 1 && checkY >= y - 1 && checkY < y + height + 1;
	}
}
