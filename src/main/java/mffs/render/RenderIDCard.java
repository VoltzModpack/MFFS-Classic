package mffs.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.ModularForceFieldSystem;
import mffs.api.card.ICardIdentification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector2;

@SideOnly(Side.CLIENT)
public class RenderIDCard implements IItemRenderer {

	private Minecraft mc = Minecraft.getMinecraft();

	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		if (itemStack.getItem() instanceof ICardIdentification) {
			ICardIdentification card = (ICardIdentification) itemStack.getItem();
			GL11.glPushMatrix();
			GL11.glDisable(2884);
			this.transform(type);
			this.renderItemIcon(ModularForceFieldSystem.itemCardID.getIcon(itemStack, 0));
			if (type != ItemRenderType.INVENTORY) {
				GL11.glTranslatef(0.0F, 0.0F, -5.0E-4F);
			}

			this.renderPlayerFace(this.getSkin(card.getUsername(itemStack)));
			if (type != ItemRenderType.INVENTORY) {
				GL11.glTranslatef(0.0F, 0.0F, 0.002F);
				this.renderItemIcon(ModularForceFieldSystem.itemCardID.getIcon(itemStack, 0));
			}

			GL11.glEnable(2884);
			GL11.glPopMatrix();
		}

	}

	private void transform(ItemRenderType type) {
		float scale = 0.0625F;
		if (type != ItemRenderType.INVENTORY) {
			GL11.glScalef(scale, -scale, -scale);
			GL11.glTranslatef(20.0F, -16.0F, 0.0F);
			GL11.glRotatef(180.0F, 1.0F, 1.0F, 0.0F);
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
		}

		if (type == ItemRenderType.ENTITY) {
			GL11.glTranslatef(20.0F, 0.0F, 0.0F);
			GL11.glRotatef((float) Minecraft.getSystemTime() / 12.0F % 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-8.0F, 0.0F, 0.0F);
			GL11.glTranslated(0.0D, 2.0D * Math.sin((double) Minecraft.getSystemTime() / 512.0D % 360.0D), 0.0D);
		}

	}

	private int getSkin(String name) {
		try {
			String skin = "http://skins.minecraft.net/MinecraftSkins/" + name + ".png";
			Minecraft mc = Minecraft.getMinecraft();
			if (!mc.renderEngine.hasImageData(skin)) {
				mc.renderEngine.obtainImageData(skin, new ImageBufferDownload());
			}

			return mc.renderEngine.getTextureForDownloadableImage(skin, "/mob/char.png");
		} catch (Exception var4) {
			var4.printStackTrace();
			return 0;
		}
	}

	private void renderPlayerFace(int texID) {
		Vector2 translation = new Vector2(9.0D, 5.0D);
		int xSize = 4;
		int ySize = 4;
		int topLX = translation.intX();
		int topRX = translation.intX() + xSize;
		int botLX = translation.intX();
		int botRX = translation.intX() + xSize;
		int topLY = translation.intY();
		int topRY = translation.intY();
		int botLY = translation.intY() + ySize;
		int botRY = translation.intY() + ySize;
		GL11.glBindTexture(3553, texID);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(7);
		GL11.glTexCoord2f(0.125F, 0.25F);
		GL11.glVertex2f((float) topLX, (float) topLY);
		GL11.glTexCoord2f(0.125F, 0.5F);
		GL11.glVertex2f((float) botLX, (float) botLY);
		GL11.glTexCoord2f(0.25F, 0.5F);
		GL11.glVertex2f((float) botRX, (float) botRY);
		GL11.glTexCoord2f(0.25F, 0.25F);
		GL11.glVertex2f((float) topRX, (float) topRY);
		GL11.glEnd();
		GL11.glBegin(7);
		GL11.glTexCoord2f(0.625F, 0.25F);
		GL11.glVertex2f((float) topLX, (float) topLY);
		GL11.glTexCoord2f(0.625F, 0.5F);
		GL11.glVertex2f((float) botLX, (float) botLY);
		GL11.glTexCoord2f(0.75F, 0.5F);
		GL11.glVertex2f((float) botRX, (float) botRY);
		GL11.glTexCoord2f(0.75F, 0.25F);
		GL11.glVertex2f((float) topRX, (float) topRY);
		GL11.glEnd();
	}

	private void renderItemIcon(Icon icon) {
		Minecraft.getMinecraft().renderEngine.bindTexture("/gui/items.png");
		GL11.glBegin(7);
		GL11.glTexCoord2f(icon.getMinU(), icon.getMinV());
		GL11.glVertex2f(0.0F, 0.0F);
		GL11.glTexCoord2f(icon.getMinU(), icon.getMaxV());
		GL11.glVertex2f(0.0F, 16.0F);
		GL11.glTexCoord2f(icon.getMaxU(), icon.getMaxV());
		GL11.glVertex2f(16.0F, 16.0F);
		GL11.glTexCoord2f(icon.getMaxU(), icon.getMinV());
		GL11.glVertex2f(16.0F, 0.0F);
		GL11.glEnd();
	}

	private void renderItem3D(EntityLiving par1EntityLiving, ItemStack par2ItemStack, int par3) {
		Icon icon = par1EntityLiving.getItemIcon(par2ItemStack, par3);
		if (icon == null) {
			GL11.glPopMatrix();
		} else {
			if (par2ItemStack.getItemSpriteNumber() == 0) {
				this.mc.renderEngine.bindTexture("/terrain.png");
			} else {
				this.mc.renderEngine.bindTexture("/gui/items.png");
			}

			Tessellator tessellator = Tessellator.instance;
			float f = icon.getMinU();
			float f1 = icon.getMaxU();
			float f2 = icon.getMinV();
			float f3 = icon.getMaxV();
			float f4 = 0.0F;
			float f5 = 0.3F;
			GL11.glEnable(32826);
			GL11.glTranslatef(-f4, -f5, 0.0F);
			float f6 = 1.5F;
			GL11.glScalef(f6, f6, f6);
			GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
			ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);
			if (par2ItemStack != null && par2ItemStack.hasEffect() && par3 == 0) {
				GL11.glDepthFunc(514);
				GL11.glDisable(2896);
				this.mc.renderEngine.bindTexture("%blur%/misc/glint.png");
				GL11.glEnable(3042);
				GL11.glBlendFunc(768, 1);
				float f7 = 0.76F;
				GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
				GL11.glMatrixMode(5890);
				GL11.glPushMatrix();
				float f8 = 0.125F;
				GL11.glScalef(f8, f8, f8);
				float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
				GL11.glTranslatef(f9, 0.0F, 0.0F);
				GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScalef(f8, f8, f8);
				f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
				GL11.glTranslatef(-f9, 0.0F, 0.0F);
				GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				GL11.glMatrixMode(5888);
				GL11.glDisable(3042);
				GL11.glEnable(2896);
				GL11.glDepthFunc(515);
			}

			GL11.glDisable(32826);
		}
	}

	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}
}
