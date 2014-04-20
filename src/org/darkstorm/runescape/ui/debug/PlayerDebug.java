package org.darkstorm.runescape.ui.debug;

import java.awt.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.Player;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.game.PaintEvent;

public class PlayerDebug extends Debug {

	public PlayerDebug(Bot bot) {
		super(bot);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		GameContext context = bot.getGameContext();
		Calculations calculations = context.getCalculations();
		Players players = context.getPlayers();
		Graphics render = event.getGraphics();
		if(players.getSelf() == null)
			return;
		FontMetrics metrics = render.getFontMetrics();
		for(Player player : players.getAll()) {
			Tile tile = player.getLocation();
			Point location = calculations.getWorldScreenLocation(
					tile.getPreciseX(), tile.getPreciseY(),
					player.getHeight() / 2);
			if(!calculations.isOnScreen(location))
				continue;
			render.setColor(Color.RED);
			render.fillRect((int) location.getX() - 1,
					(int) location.getY() - 1, 2, 2);
			String s = player.getName() + " (" + player.getLevel() + ") "
					+ player.getHealthPercentage() + "%";
			render.setColor(player.isInCombat() ? Color.RED
					: player.isMoving() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2,
					location.y - metrics.getHeight() / 2);
			StringBuilder message = new StringBuilder("(");
			if(player.getAnimation() != -1)
				message.append("A: ").append(player.getAnimation())
						.append(" | ");
			if(player.getPrayerIcon() != -1)
				message.append("P: ").append(player.getPrayerIcon())
						.append(" | ");
			if(player.getSkullIcon() != -1)
				message.append("S: ").append(player.getSkullIcon())
						.append(" | ");
			if(player.getMotion() > 0)
				message.append("M: ").append(player.getMotion()).append(" | ");
			String str = message.length() > 2 ? message.toString().substring(0,
					message.length() - 3)
					+ ")" : "";
			render.drawString(str, location.x - metrics.stringWidth(str) / 2,
					location.y - metrics.getHeight() * 3 / 2);
		}
	}
}
