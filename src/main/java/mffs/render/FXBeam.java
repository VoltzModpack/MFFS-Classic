package mffs.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector3;

@SideOnly(Side.CLIENT)
public class FXBeam extends EntityFX {

	double movX = 0.0D;
	double movY = 0.0D;
	double movZ = 0.0D;
	private float length = 0.0F;
	private float rotYaw = 0.0F;
	private float rotPitch = 0.0F;
	private float prevYaw = 0.0F;
	private float prevPitch = 0.0F;
	private Vector3 target = new Vector3();
	private float endModifier = 1.0F;
	private boolean reverse = false;
	private boolean pulse = true;
	private int rotationSpeed = 20;
	private float prevSize = 0.0F;

	public FXBeam(World par1World, Vector3 position, Vector3 target, float red, float green, float blue, int age) {
		super(par1World, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
		this.setRGB(red, green, blue);
		this.setSize(0.02F, 0.02F);
		super.noClip = true;
		super.motionX = 0.0D;
		super.motionY = 0.0D;
		super.motionZ = 0.0D;
		this.target = target;
		float xd = (float) (super.posX - this.target.x);
		float yd = (float) (super.posY - this.target.y);
		float zd = (float) (super.posZ - this.target.z);
		this.length = (float) (new Vector3(this)).distanceTo(this.target);
		double var7 = (double) MathHelper.sqrt_double((double) (xd * xd + zd * zd));
		this.rotYaw = (float) (Math.atan2((double) xd, (double) zd) * 180.0D / 3.141592653589793D);
		this.rotPitch = (float) (Math.atan2((double) yd, var7) * 180.0D / 3.141592653589793D);
		this.prevYaw = this.rotYaw;
		this.prevPitch = this.rotPitch;
		super.particleMaxAge = age;
		EntityLiving renderentity = Minecraft.getMinecraft().renderViewEntity;
		int visibleDistance = 50;
		if (!Minecraft.getMinecraft().gameSettings.fancyGraphics) {
			visibleDistance = 25;
		}

		if (renderentity.getDistance(super.posX, super.posY, super.posZ) > (double) visibleDistance) {
			super.particleMaxAge = 0;
		}

	}

	public void onUpdate() {
		super.prevPosX = super.posX;
		super.prevPosY = super.posY;
		super.prevPosZ = super.posZ;
		this.prevYaw = this.rotYaw;
		this.prevPitch = this.rotPitch;
		float xd = (float) (super.posX - this.target.x);
		float yd = (float) (super.posY - this.target.y);
		float zd = (float) (super.posZ - this.target.z);
		this.length = MathHelper.sqrt_float(xd * xd + yd * yd + zd * zd);
		double var7 = (double) MathHelper.sqrt_double((double) (xd * xd + zd * zd));
		this.rotYaw = (float) (Math.atan2((double) xd, (double) zd) * 180.0D / 3.141592653589793D);
		this.rotPitch = (float) (Math.atan2((double) yd, var7) * 180.0D / 3.141592653589793D);
		if (super.particleAge++ >= super.particleMaxAge) {
			this.setDead();
		}

	}

	public void setRGB(float r, float g, float b) {
		super.particleRed = r;
		super.particleGreen = g;
		super.particleBlue = b;
	}

	public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
		tessellator.draw();
		GL11.glPushMatrix();
		float var9 = 1.0F;
		float slide = (float) super.worldObj.getTotalWorldTime();
		float rot = (float) (super.worldObj.provider.getWorldTime() % (long) (360 / this.rotationSpeed) * (long) this.rotationSpeed) + (float) this.rotationSpeed * f;
		float size = 1.0F;
		if (this.pulse) {
			size = Math.min((float) super.particleAge / 4.0F, 1.0F);
			size = this.prevSize + (size - this.prevSize) * f;
		}

		float op = 0.5F;
		if (this.pulse && super.particleMaxAge - super.particleAge <= 4) {
			op = 0.5F - (float) (4 - (super.particleMaxAge - super.particleAge)) * 0.1F;
		}

		Minecraft.getMinecraft().renderEngine.bindTexture("/mods/mffs/textures/blocks/fortron.png");
		GL11.glTexParameterf(3553, 10242, 10497.0F);
		GL11.glTexParameterf(3553, 10243, 10497.0F);
		GL11.glDisable(2884);
		float var11 = slide + f;
		if (this.reverse) {
			var11 *= -1.0F;
		}

		float var12 = -var11 * 0.2F - (float) MathHelper.floor_float(-var11 * 0.1F);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 1);
		GL11.glDepthMask(false);
		float xx = (float) (super.prevPosX + (super.posX - super.prevPosX) * (double) f - EntityFX.interpPosX);
		float yy = (float) (super.prevPosY + (super.posY - super.prevPosY) * (double) f - EntityFX.interpPosY);
		float zz = (float) (super.prevPosZ + (super.posZ - super.prevPosZ) * (double) f - EntityFX.interpPosZ);
		GL11.glTranslated((double) xx, (double) yy, (double) zz);
		float ry = this.prevYaw + (this.rotYaw - this.prevYaw) * f;
		float rp = this.prevPitch + (this.rotPitch - this.prevPitch) * f;
		GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(180.0F + ry, 0.0F, 0.0F, -1.0F);
		GL11.glRotatef(rp, 1.0F, 0.0F, 0.0F);
		double var44 = -0.15D * (double) size;
		double var17 = 0.15D * (double) size;
		double var44b = -0.15D * (double) size * (double) this.endModifier;
		double var17b = 0.15D * (double) size * (double) this.endModifier;
		GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);

		for (int t = 0; t < 3; ++t) {
			double var29 = (double) (this.length * size * var9);
			double var31 = 0.0D;
			double var33 = 1.0D;
			double var35 = (double) (-1.0F + var12 + (float) t / 3.0F);
			double var37 = (double) (this.length * size * var9) + var35;
			GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
			tessellator.startDrawingQuads();
			tessellator.setBrightness(200);
			tessellator.setColorRGBA_F(super.particleRed, super.particleGreen, super.particleBlue, op);
			tessellator.addVertexWithUV(var44b, var29, 0.0D, var33, var37);
			tessellator.addVertexWithUV(var44, 0.0D, 0.0D, var33, var35);
			tessellator.addVertexWithUV(var17, 0.0D, 0.0D, var31, var35);
			tessellator.addVertexWithUV(var17b, var29, 0.0D, var31, var37);
			tessellator.draw();
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(true);
		GL11.glDisable(3042);
		GL11.glEnable(2884);
		GL11.glPopMatrix();
		tessellator.startDrawingQuads();
		this.prevSize = size;
		Minecraft.getMinecraft().renderEngine.bindTexture("/particles.png");
	}
}
