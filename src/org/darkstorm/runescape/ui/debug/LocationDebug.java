package org.darkstorm.runescape.ui.debug;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.Player;

public class LocationDebug extends Debug {

	public LocationDebug(Bot bot) {
		super(bot);
	}

	@Override
	public String getDisplayedText() {
		GameContext context = bot.getGameContext();
		Player self = context.getPlayers().getSelf();
		if(self != null) {
			Tile tile = self.getLocation();
			return "Location: (" + tile.getX() + ", " + tile.getY() + ", "
					+ tile.getPlane() + ")";
		}
		return "Location: Unknown";
	}
}
