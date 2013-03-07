package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface MusicTab {
	@GameTypeSupport(GameType.OLDSCHOOL)
	public enum PlayMode {
		AUTO,
		MAN,
		LOOP
	}

	public String getCurrentTrack();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public PlayMode getPlayMode();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public void setPlayMode(PlayMode mode);

	public void scrollTo(String track);

	public void playTrack(String track);

	public boolean isUnlocked(String track);

	public String[] getUnlockedTracks();

	public String[] getTracks();

	public int getUnlockedTrackCount();

	public int getTrackCount();

	public InterfaceComponent getComponent(String track);

	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getComponent(PlayMode mode);
}
