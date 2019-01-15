package mffs.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelForceManipulator extends ModelBase {

	ModelRenderer ElectrodePillar;
	ModelRenderer ElectrodeBase;
	ModelRenderer ElectrodeNode;
	ModelRenderer WallBottom;
	ModelRenderer WallFront;
	ModelRenderer WallBack;
	ModelRenderer WallLeft;
	ModelRenderer WallRight;
	ModelRenderer WallTop;

	public ModelForceManipulator() {
		super.textureWidth = 128;
		super.textureHeight = 128;
		this.ElectrodePillar = new ModelRenderer(this, 0, 32);
		this.ElectrodePillar.addBox(0.0F, 0.0F, 0.0F, 3, 3, 3);
		this.ElectrodePillar.setRotationPoint(-1.5F, 19.0F, -1.5F);
		this.ElectrodePillar.setTextureSize(128, 128);
		this.ElectrodePillar.mirror = true;
		this.setRotation(this.ElectrodePillar, 0.0F, 0.0F, 0.0F);
		this.ElectrodeBase = new ModelRenderer(this, 0, 39);
		this.ElectrodeBase.addBox(0.0F, 0.0F, 0.0F, 7, 2, 7);
		this.ElectrodeBase.setRotationPoint(-3.5F, 21.5F, -3.5F);
		this.ElectrodeBase.setTextureSize(128, 128);
		this.ElectrodeBase.mirror = true;
		this.setRotation(this.ElectrodeBase, 0.0F, 0.0F, 0.0F);
		this.ElectrodeNode = new ModelRenderer(this, 0, 49);
		this.ElectrodeNode.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
		this.ElectrodeNode.setRotationPoint(-2.5F, 15.0F, -2.5F);
		this.ElectrodeNode.setTextureSize(128, 128);
		this.ElectrodeNode.mirror = true;
		this.setRotation(this.ElectrodeNode, 0.0F, 0.0F, 0.0F);
		this.WallBottom = new ModelRenderer(this, 0, 0);
		this.WallBottom.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16);
		this.WallBottom.setRotationPoint(-8.0F, 23.0F, -8.0F);
		this.WallBottom.setTextureSize(128, 128);
		this.WallBottom.mirror = true;
		this.setRotation(this.WallBottom, 0.0F, 0.0F, 0.0F);
		this.WallFront = new ModelRenderer(this, 65, 0);
		this.WallFront.addBox(0.0F, 0.0F, 0.0F, 16, 15, 1);
		this.WallFront.setRotationPoint(-8.0F, 8.0F, -8.0F);
		this.WallFront.setTextureSize(128, 128);
		this.WallFront.mirror = true;
		this.setRotation(this.WallFront, 0.0F, 0.0F, 0.0F);
		this.WallBack = new ModelRenderer(this, 65, 17);
		this.WallBack.addBox(0.0F, 0.0F, 0.0F, 16, 15, 1);
		this.WallBack.setRotationPoint(-8.0F, 8.0F, 7.0F);
		this.WallBack.setTextureSize(128, 128);
		this.WallBack.mirror = true;
		this.setRotation(this.WallBack, 0.0F, 0.0F, 0.0F);
		this.WallLeft = new ModelRenderer(this, 30, 50);
		this.WallLeft.addBox(0.0F, 0.0F, 0.0F, 1, 15, 14);
		this.WallLeft.setRotationPoint(-8.0F, 8.0F, -7.0F);
		this.WallLeft.setTextureSize(128, 128);
		this.WallLeft.mirror = true;
		this.setRotation(this.WallLeft, 0.0F, 0.0F, 0.0F);
		this.WallRight = new ModelRenderer(this, 30, 19);
		this.WallRight.addBox(0.0F, 0.0F, 0.0F, 1, 15, 14);
		this.WallRight.setRotationPoint(7.0F, 8.0F, -7.0F);
		this.WallRight.setTextureSize(128, 128);
		this.WallRight.mirror = true;
		this.setRotation(this.WallRight, 0.0F, 0.0F, 0.0F);
		this.WallTop = new ModelRenderer(this, 61, 36);
		this.WallTop.addBox(0.0F, 0.0F, 0.0F, 14, 1, 14);
		this.WallTop.setRotationPoint(-7.0F, 8.0F, -7.0F);
		this.WallTop.setTextureSize(128, 128);
		this.WallTop.mirror = true;
		this.setRotation(this.WallTop, 0.0F, 0.0F, 0.0F);
	}

	public void render(float f5) {
		this.ElectrodePillar.render(f5);
		this.ElectrodeBase.render(f5);
		this.ElectrodeNode.render(f5);
		this.WallBottom.render(f5);
		this.WallFront.render(f5);
		this.WallBack.render(f5);
		this.WallLeft.render(f5);
		this.WallRight.render(f5);
		this.WallTop.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
