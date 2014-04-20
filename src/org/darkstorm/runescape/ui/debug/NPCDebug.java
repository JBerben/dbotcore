package org.darkstorm.runescape.ui.debug;

import java.awt.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.event.EventHandler;
import org.darkstorm.runescape.event.game.PaintEvent;

public class NPCDebug extends Debug {

	public NPCDebug(Bot bot) {
		super(bot);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		GameContext context = bot.getGameContext();
		Calculations calculations = context.getCalculations();
		NPCs npcs = context.getNPCs();
		Graphics render = event.getGraphics();
		if(context.getPlayers().getSelf() == null)
			return;
		FontMetrics metrics = render.getFontMetrics();
		for(NPC npc : npcs.getAll()) {
			Tile tile = npc.getLocation();
			Point location = calculations
					.getWorldScreenLocation(tile.getPreciseX(),
							tile.getPreciseY(), npc.getHeight() / 2);
			if(!calculations.isOnScreen(location))
				continue;
			render.setColor(Color.RED);
			render.fillRect((int) location.getX() - 1,
					(int) location.getY() - 1, 2, 2);
			String s = npc.getName() + " (" + npc.getLevel() + ") "
					+ npc.getHealthPercentage() + "%";
			render.setColor(npc.isInCombat() ? Color.RED
					: npc.isMoving() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2,
					location.y - metrics.getHeight() / 2);
			StringBuilder message = new StringBuilder("(");
			if(npc.getId() != -1)
				message.append("ID: ").append(npc.getId()).append(" | ");
			if(npc.getAnimation() != -1)
				message.append("A: ").append(npc.getAnimation()).append(" | ");
			if(npc.getMotion() > 0)
				message.append("M: ").append(npc.getMotion()).append(" | ");
			String str = message.length() > 2 ? message.toString().substring(0,
					message.length() - 3)
					+ ")" : "";
			render.drawString(str, location.x - metrics.stringWidth(str) / 2,
					location.y - metrics.getHeight() * 3 / 2);

			Model model = npc.getModel();
			if(model == null)
				continue;
			render.setColor(Color.CYAN);
			model.draw(render);
			// Polygon hull = model.getHull();
			// if(hull == null)
			// continue;
			// render.drawPolygon(hull);
		}
	}
}
