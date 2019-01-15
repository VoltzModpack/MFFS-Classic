package mffs.gui.button;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiIcon extends GuiButton {

	public static RenderItem itemRenderer = new RenderItem();
	public ItemStack[] itemStacks;
	private int index = 0;

	public GuiIcon(int par1, int par2, int par3, ItemStack... itemStacks) {
		super(par1, par2, par3, 20, 20, "");
		this.itemStacks = itemStacks;
	}

	public void setIndex(int i) {
		if (i >= 0 && i < this.itemStacks.length) {
			this.index = i;
		}

	}

	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		super.drawButton(par1Minecraft, par2, par3);
		if (super.drawButton && this.itemStacks[this.index] != null) {
			int yDisplacement = 2;
			if (this.itemStacks[this.index].itemID != Block.torchRedstoneIdle.blockID && this.itemStacks[this.index].itemID != Block.torchRedstoneActive.blockID) {
				if (this.itemStacks[this.index].getItem() instanceof ItemBlock) {
					yDisplacement = 3;
				}
			} else {
				yDisplacement = 0;
			}

			this.drawItemStack(this.itemStacks[this.index], super.xPosition, super.yPosition + yDisplacement);
		}

	}

	protected void drawItemStack(ItemStack itemStack, int x, int y) {
		x += 2;
		--y;
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontRenderer = mc.fontRenderer;
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		super.zLevel = 500.0F;
		itemRenderer.zLevel = 500.0F;
		itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, itemStack, x, y);
		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemStack, x, y);
		super.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
		RenderHelper.disableStandardItemLighting();
	}
}
