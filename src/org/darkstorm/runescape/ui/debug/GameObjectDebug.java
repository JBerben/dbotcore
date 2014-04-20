package org.darkstorm.runescape.ui.debug;

import java.awt.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.event.EventHandler;
import org.darkstorm.runescape.event.game.PaintEvent;

public class GameObjectDebug extends Debug {

	public GameObjectDebug(Bot bot) {
		super(bot);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		GameContext context = bot.getGameContext();
		GameObjects gameObjects = context.getGameObjects();
		Graphics g = event.getGraphics();
		if(context.getPlayers().getSelf() == null)
			return;
		FontMetrics metrics = g.getFontMetrics();
		for(GameObject object : gameObjects.getAll()) {
			Point location = object.getScreenLocation();
			g.setColor(Color.GREEN);
			String str = object.getId() + " (" + object.getType().toString()
					+ ")";
			g.drawString(str, location.x - metrics.stringWidth(str) / 2,
					location.y - metrics.getHeight() * 3 / 2);

			Model model = object.getModel();
			if(model == null)
				continue;
			g.setColor(new Color(255, 255, 0, 75));
			model.fill(g);
			g.setColor(Color.YELLOW);
			model.draw(g);
		}
	}
}
