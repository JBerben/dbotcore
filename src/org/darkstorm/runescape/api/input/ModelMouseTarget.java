package org.darkstorm.runescape.api.input;

import java.awt.*;

import org.darkstorm.runescape.api.wrapper.Model;

public class ModelMouseTarget implements MouseTarget {
	private final Model model;

	public ModelMouseTarget(Model model) {
		this.model = model;
	}

	@Override
	public MouseTarget getTarget() {
		return this;
	}

	@Override
	public Point getLocation() {
		Point point = model.getRandomPointWithin();
		if(point == null
				|| !model.getContext().getCalculations().isInGameArea(point))
			return null;
		return point;
	}

	@Override
	public boolean isOver(Point point) {
		return model.contains(point);
	}

	@Override
	public void render(Graphics g) {
		g.drawPolygon(model.getHull());
	}
}
