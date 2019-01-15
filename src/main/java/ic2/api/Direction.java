package ic2.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public enum Direction {
	XN(0),
	XP(1),
	YN(2),
	YP(3),
	ZN(4),
	ZP(5);

	private int dir;
	private static final Direction[] directions = values();

	private Direction(int dir) {
		this.dir = dir;
	}

	public TileEntity applyToTileEntity(TileEntity tileEntity) {
		int[] coords = new int[]{tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord};
		int var10001 = this.dir / 2;
		coords[var10001] += this.getSign();
		return tileEntity.worldObj != null && tileEntity.worldObj.blockExists(coords[0], coords[1], coords[2]) ? tileEntity.worldObj.getBlockTileEntity(coords[0], coords[1], coords[2]) : null;
	}

	public Direction getInverse() {
		int inverseDir = this.dir - this.getSign();
		Direction[] arr$ = directions;
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			Direction direction = arr$[i$];
			if (direction.dir == inverseDir) {
				return direction;
			}
		}

		return this;
	}

	public int toSideValue() {
		return (this.dir + 4) % 6;
	}

	private int getSign() {
		return this.dir % 2 * 2 - 1;
	}

	public ForgeDirection toForgeDirection() {
		return ForgeDirection.getOrientation(this.toSideValue());
	}
}
