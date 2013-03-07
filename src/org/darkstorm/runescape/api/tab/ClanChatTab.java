package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface ClanChatTab extends Tab {
	public interface ChatMember {
		public String getName();

		public Rank getRank();

		public int getWorld();
	}

	public enum Rank {
		NONE,
		RECRUIT,
		CORPORAL,
		SERGEANT,
		LIEUTENANT,
		CAPTAIN,
		GENERAL,
		OWNER
	}

	public boolean isInChat();

	public String getCurrentChat();

	public void joinChat(String name);

	public ChatMember getChatMember(String name);

	public ChatMember[] getChatMembers();

	public void leaveChat();

	public void addUserToChat(String name);

	public InterfaceComponent getMemberComponent(ChatMember member);
}
