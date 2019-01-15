package mffs.base;

import cpw.mods.fml.common.network.PacketDispatcher;
import icbm.api.IBlockFrequency;
import mffs.MFFSHelper;
import mffs.api.IBiometricIdentifierLink;
import mffs.gui.button.GuiIcon;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.vector.Region2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class GuiBase extends GuiContainer {

	private static final int METER_X = 54;
	public static final int METER_HEIGHT = 49;
	public static final int METER_WIDTH = 14;
	public static final int METER_END = 68;
	protected GuiTextField textFieldFrequency;
	protected Vector2 textFieldPos;
	public String tooltip;
	protected int containerWidth;
	protected int containerHeight;
	protected IBlockFrequency frequencyTile;
	protected HashMap tooltips;

	public GuiBase(Container container) {
		super(container);
		this.textFieldPos = new Vector2();
		this.tooltip = "";
		this.tooltips = new HashMap();
		super.ySize = 217;
	}

	public GuiBase(Container container, IBlockFrequency frequencyTile) {
		this(container);
		this.frequencyTile = frequencyTile;
	}

	public void initGui() {
		super.initGui();
		super.buttonList.clear();
		super.buttonList.add(new GuiIcon(0, super.width / 2 - 82, super.height / 2 - 104, new ItemStack[]{new ItemStack(Block.torchRedstoneIdle), new ItemStack(Block.torchRedstoneActive)}));
		Keyboard.enableRepeatEvents(true);
		if (this.frequencyTile != null) {
			this.textFieldFrequency = new GuiTextField(super.fontRenderer, this.textFieldPos.intX(), this.textFieldPos.intY(), 50, 12);
			this.textFieldFrequency.setMaxStringLength(6);
			this.textFieldFrequency.setText(this.frequencyTile.getFrequency() + "");
		}

	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
		if (this.textFieldFrequency != null) {
			this.textFieldFrequency.textboxKeyTyped(par1, par2);

			try {
				int newFrequency = Math.max(0, Integer.parseInt(this.textFieldFrequency.getText()));
				this.frequencyTile.setFrequency(newFrequency);
				this.textFieldFrequency.setText(this.frequencyTile.getFrequency() + "");
				PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", (TileEntity) this.frequencyTile, TileEntityBase.TilePacketType.FREQUENCY.ordinal(), this.frequencyTile.getFrequency()));
			} catch (NumberFormatException var4) {
			}
		}

	}

	protected void actionPerformed(GuiButton guiButton) {
		super.actionPerformed(guiButton);
		if (this.frequencyTile != null && guiButton.id == 0) {
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket("MFFS", (TileEntity) this.frequencyTile, TileEntityBase.TilePacketType.TOGGLE_ACTIVATION.ordinal()));
		}

	}

	public void updateScreen() {
		super.updateScreen();
		if (this.textFieldFrequency != null && !this.textFieldFrequency.isFocused()) {
			this.textFieldFrequency.setText(this.frequencyTile.getFrequency() + "");
		}

		if (this.frequencyTile instanceof TileEntityBase && super.buttonList.size() > 0 && super.buttonList.get(0) != null) {
			((GuiIcon) super.buttonList.get(0)).setIndex(((TileEntityBase) this.frequencyTile).isActive() ? 1 : 0);
		}

	}

	protected void mouseClicked(int x, int y, int par3) {
		super.mouseClicked(x, y, par3);
		if (this.textFieldFrequency != null) {
			this.textFieldFrequency.mouseClicked(x - this.containerWidth, y - this.containerHeight, par3);
		}

	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (this.textFieldFrequency != null && this.isPointInRegion(this.textFieldPos.intX(), this.textFieldPos.intY(), this.textFieldFrequency.getWidth(), 12, mouseX, mouseY)) {
			this.tooltip = TranslationHelper.getLocal("gui.frequency.tooltip");
		}

		Iterator it = this.tooltips.entrySet().iterator();

		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			if (((Region2) entry.getKey()).isIn(new Vector2((double) (mouseX - super.guiLeft), (double) (mouseY - super.guiTop)))) {
				this.tooltip = (String) entry.getValue();
				break;
			}
		}

		if (this.tooltip != null && this.tooltip != "") {
			this.drawTooltip(mouseX - super.guiLeft, mouseY - super.guiTop, (String[]) MFFSHelper.splitStringPerWord(this.tooltip, 5).toArray(new String[0]));
		}

		this.tooltip = "";
	}

	protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
		this.containerWidth = (super.width - super.xSize) / 2;
		this.containerHeight = (super.height - super.ySize) / 2;
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_base.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, super.xSize, super.ySize);
		if (this.frequencyTile instanceof IBiometricIdentifierLink) {
			this.drawBulb(167, 4, ((IBiometricIdentifierLink) this.frequencyTile).getBiometricIdentifier() != null);
		}

	}

	protected void drawBulb(int x, int y, boolean isOn) {
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (isOn) {
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 0, 6, 6);
		} else {
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 161, 4, 6, 6);
		}

	}

	protected void drawSlot(int x, int y, ItemStack itemStack) {
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);
		this.drawItemStack(itemStack, this.containerWidth + x, this.containerHeight + y);
	}

	protected void drawItemStack(ItemStack itemStack, int x, int y) {
		++x;
		++y;
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		GuiContainer.itemRenderer.renderItemAndEffectIntoGUI(super.fontRenderer, super.mc.renderEngine, itemStack, x, y);
	}

	protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY) {
		this.drawTextWithTooltip(textName, format, x, y, mouseX, mouseY, 4210752);
	}

	protected void drawTextWithTooltip(String textName, String format, int x, int y, int mouseX, int mouseY, int color) {
		String name = TranslationHelper.getLocal("gui." + textName + ".name");
		String text = format.replaceAll("%1", name);
		super.fontRenderer.drawString(text, x, y, color);
		String tooltip = TranslationHelper.getLocal("gui." + textName + ".tooltip");
		if (tooltip != null && tooltip != "" && this.isPointInRegion(x, y, (int) ((double) text.length() * 4.8D), 12, mouseX, mouseY)) {
			this.tooltip = tooltip;
		}

	}

	protected void drawTextWithTooltip(String textName, int x, int y, int mouseX, int mouseY) {
		this.drawTextWithTooltip(textName, "%1", x, y, mouseX, mouseY);
	}

	protected void drawSlot(int x, int y, GuiBase.SlotType type, float r, float g, float b) {
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		GL11.glColor4f(r, g, b, 1.0F);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);
		if (type != GuiBase.SlotType.NONE) {
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18 * type.ordinal(), 18, 18);
		}

	}

	protected void drawSlot(int x, int y, GuiBase.SlotType type) {
		this.drawSlot(x, y, type, 1.0F, 1.0F, 1.0F);
	}

	protected void drawSlot(int x, int y) {
		this.drawSlot(x, y, GuiBase.SlotType.NONE);
	}

	protected void drawBar(int x, int y, float scale) {
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 0, 22, 15);
		if (scale > 0.0F) {
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 15, 22 - (int) (scale * 22.0F), 15);
		}

	}

	protected void drawForce(int x, int y, float scale) {
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);
		if (scale > 0.0F) {
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 11, (int) (scale * 107.0F), 11);
		}

	}

	protected void drawElectricity(int x, int y, float scale) {
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 0, 107, 11);
		if (scale > 0.0F) {
			this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 54, 22, (int) (scale * 107.0F), 11);
		}

	}

	protected void drawMeter(int x, int y, float scale, LiquidStack liquidStack) {
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 0, 14, 49);
		this.displayGauge(this.containerWidth + x, this.containerHeight + y, 0, 0, (int) (48.0F * scale), liquidStack);
		super.mc.renderEngine.bindTexture("/mods/mffs/textures/gui/gui_components.png");
		this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 40, 98, 14, 49);
	}

	public void drawTooltip(int x, int y, String... toolTips) {
		if (!GuiScreen.isShiftKeyDown()) {
			GL11.glDisable(32826);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(2896);
			GL11.glDisable(2929);
			if (toolTips != null) {
				int var5 = 0;

				int var6;
				int var7;
				for (var6 = 0; var6 < toolTips.length; ++var6) {
					var7 = super.fontRenderer.getStringWidth(toolTips[var6]);
					if (var7 > var5) {
						var5 = var7;
					}
				}

				var6 = x + 12;
				var7 = y - 12;
				int var9 = 8;
				if (toolTips.length > 1) {
					var9 += 2 + (toolTips.length - 1) * 10;
				}

				if (super.guiTop + var7 + var9 + 6 > super.height) {
					var7 = super.height - var9 - super.guiTop - 6;
				}

				super.zLevel = 300.0F;
				int var10 = -267386864;
				this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
				this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
				this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
				this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
				this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
				int var11 = 1347420415;
				int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
				this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
				this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
				this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
				this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

				for (int var13 = 0; var13 < toolTips.length; ++var13) {
					String var14 = toolTips[var13];
					super.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
					var7 += 10;
				}

				super.zLevel = 0.0F;
				GL11.glEnable(2929);
				GL11.glEnable(2896);
				RenderHelper.enableGUIStandardItemLighting();
				GL11.glEnable(32826);
			}
		}

	}

	protected void displayGauge(int x, int y, int line, int col, int scale, LiquidStack liquidStack) {
		int liquidId = liquidStack.itemID;
		int liquidMeta = liquidStack.itemMeta;
		int liquidImgIndex = 0;
		if (liquidId > 0) {
			int imgLine = liquidImgIndex / 16;
			int imgColumn = liquidImgIndex - imgLine * 16;
			int start = 0;

			int a;
			do {
				int a = false;
				if (scale > 16) {
					a = 16;
					scale -= 16;
				} else {
					a = scale;
					scale = 0;
				}

				this.drawTexturedModalRect(x + col, y + line + 58 - a - start, imgColumn * 16, imgLine * 16 + (16 - a), 16, 16 - (16 - a));
				start += 16;
			} while (a != 0 && scale != 0);

		}
	}

	public static enum SlotType {
		NONE,
		BATTERY,
		LIQUID,
		ARR_UP,
		ARR_DOWN,
		ARR_LEFT,
		ARR_RIGHT,
		ARR_UP_RIGHT,
		ARR_UP_LEFT,
		ARR_DOWN_LEFT,
		ARR_DOWN_RIGHT;
	}
}
