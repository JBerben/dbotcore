package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface FriendsListTab extends Tab {
	public interface Friend {
		public String getName();

		public int getWorld();

		public boolean isOnline();
	}

	public boolean isFriend(String name);

	public Friend getFriend(String name);

	public void scrollTo(Friend friend);

	public Friend[] getFriends();

	public Friend[] getOnlineFriends();

	public void sendMessage(Friend friend, String name);

	public void addFriend(String name);

	public void removeFriend(Friend friend);

	public InterfaceComponent getComponent(Friend friend);
}
