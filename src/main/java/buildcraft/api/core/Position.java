package buildcraft.api.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class Position {

	public double x;
	public double y;
	public double z;
	public ForgeDirection orientation;

	public Position(double ci, double cj, double ck) {
		this.x = ci;
		this.y = cj;
		this.z = ck;
		this.orientation = ForgeDirection.UNKNOWN;
	}

	public Position(double ci, double cj, double ck, ForgeDirection corientation) {
		this.x = ci;
		this.y = cj;
		this.z = ck;
		this.orientation = corientation;
	}

	public Position(Position p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
		this.orientation = p.orientation;
	}

	public Position(NBTTagCompound nbttagcompound) {
		this.x = nbttagcompound.getDouble("i");
		this.y = nbttagcompound.getDouble("j");
		this.z = nbttagcompound.getDouble("k");
		this.orientation = ForgeDirection.UNKNOWN;
	}

	public Position(TileEntity tile) {
		this.x = (double) tile.xCoord;
		this.y = (double) tile.yCoord;
		this.z = (double) tile.zCoord;
	}

	public void moveRight(double step) {
		switch (this.orientation) {
			case SOUTH:
				this.x -= step;
				break;
			case NORTH:
				this.x += step;
				break;
			case EAST:
				this.z += step;
				break;
			case WEST:
				this.z -= step;
		}

	}

	public void moveLeft(double step) {
		this.moveRight(-step);
	}

	public void moveForwards(double step) {
		switch (this.orientation) {
			case SOUTH:
				this.z += step;
				break;
			case NORTH:
				this.z -= step;
				break;
			case EAST:
				this.x += step;
				break;
			case WEST:
				this.x -= step;
				break;
			case UP:
				this.y += step;
				break;
			case DOWN:
				this.y -= step;
		}

	}

	public void moveBackwards(double step) {
		this.moveForwards(-step);
	}

	public void moveUp(double step) {
		switch (this.orientation) {
			case SOUTH:
			case NORTH:
			case EAST:
			case WEST:
				this.y += step;
			default:
		}
	}

	public void moveDown(double step) {
		this.moveUp(-step);
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setDouble("i", this.x);
		nbttagcompound.setDouble("j", this.y);
		nbttagcompound.setDouble("k", this.z);
	}

	public String toString() {
		return "{" + this.x + ", " + this.y + ", " + this.z + "}";
	}

	public Position min(Position p) {
		return new Position(p.x > this.x ? this.x : p.x, p.y > this.y ? this.y : p.y, p.z > this.z ? this.z : p.z);
	}

	public Position max(Position p) {
		return new Position(p.x < this.x ? this.x : p.x, p.y < this.y ? this.y : p.y, p.z < this.z ? this.z : p.z);
	}
}
