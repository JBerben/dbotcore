package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.Chat.ChatMessage;
import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface Chat extends TypedUtility<ChatMessage> {
	public interface ChatMessage {
		public String getText();

		public String getPlayerName();

		public ChatType getType();

		public int getTextColor();

		public int getIndex();

		public InterfaceComponent getComponent();
	}

	public enum ChatType {
		PLAYER,
		MOD,
		ADMIN,
		QUICK_CHAT,
		CLAN_CHAT,
		OTHER
	}

	public enum ChatOption {
		ALL,
		GAME,
		PUBLIC,
		PRIVATE,
		CLAN,
		TRADE,
		ASSIST,
		REPORT
	}

	public enum ChatOptionSetting {

	}

	public ChatMessage getMessage(Filter<ChatMessage> filter);

	public ChatMessage[] getMessages(Filter<ChatMessage> filter);

	public ChatMessage[] getMessages();
}
