package org.darkstorm.runescape.ui.debug;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.GroundItem;
import org.darkstorm.runescape.event.EventHandler;
import org.darkstorm.runescape.event.game.PaintEvent;

public class GroundItemDebug extends Debug {

	public GroundItemDebug(Bot bot) {
		super(bot);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		GameContext context = bot.getGameContext();
		Calculations calculations = context.getCalculations();
		GroundItems groundItems = context.getGroundItems();
		Graphics render = event.getGraphics();
		if(context.getPlayers().getSelf() == null)
			return;
		FontMetrics metrics = render.getFontMetrics();
		List<Tile> foundTiles = new ArrayList<Tile>();
		for(GroundItem item : groundItems.getAll()) {
			Tile itemLoc = item.getLocation();
			Point location = item.getScreenLocation();
			if(!calculations.isOnScreen(location))
				continue;
			render.setColor(Color.GREEN);
			render.fillRect((int) location.getX() - 1,
					(int) location.getY() - 1, 2, 2);
			String s = Integer.toString(item.getId());
			int offset = 0;
			for(Tile tile : foundTiles)
				if(tile.equals(itemLoc))
					offset++;
			foundTiles.add(itemLoc);
			render.drawString(
					s,
					location.x - metrics.stringWidth(s) / 2,
					location.y - metrics.getHeight() / 2
							- (offset * (metrics.getHeight() + 2)));
		}
	}
}
