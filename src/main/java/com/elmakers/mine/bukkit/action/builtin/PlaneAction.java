package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.api.action.CastContext;
import org.bukkit.configuration.ConfigurationSection;

public class PlaneAction extends VolumeAction
{
	protected enum Axis { X, Y, Z };
	protected Axis axis;

	@Override
	public void prepare(CastContext context, ConfigurationSection parameters) {
		String axisType = parameters.getString("axis");
		if (axisType != null) {
			if (axisType.equalsIgnoreCase("x")) {
				axis = Axis.X;
			} else if (axisType.equalsIgnoreCase("z")) {
				axis = Axis.Z;
			} else {
				axis = Axis.Y;
			}
		} else {
			axis = Axis.Y;
		}
		super.prepare(context, parameters);
	}

	protected boolean calculateSize(CastContext context) {
		switch (axis)
		{
			case X:
				xSize = 0;
				break;
			case Z:
				zSize = 0;
				break;
			default:
				ySize = 0;
				break;
		}
		return super.calculateSize(context);
	}

	protected boolean containsPoint(int x, int y, int z)
	{
		return true;
	}
}
