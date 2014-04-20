package org.darkstorm.runescape.ui.debug;

import java.util.Arrays;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;

public class MenuDebug extends Debug {

	public MenuDebug(Bot bot) {
		super(bot);
	}

	@Override
	public String getDisplayedText() {
		GameContext context = bot.getGameContext();
		String[] options = context.getMenu().getActions();
		return "Menu " + (context.getMenu().isOpen() ? "open" : "closed")
				+ ", items: " + Arrays.toString(options);
	}
}
