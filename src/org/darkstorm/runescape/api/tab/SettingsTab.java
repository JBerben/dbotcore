package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface SettingsTab extends Tab {
	@GameTypeSupport(GameType.OLDSCHOOL)
	public enum SettingSlider {
		AUDIO,
		MUSIC,
		AMBIENT,
		BRIGHTNESS
	}

	@GameTypeSupport(GameType.OLDSCHOOL)
	public int getRunPercentage();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public boolean isRunning();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public void setRunning(boolean running);

	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getRunButtonComponent();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public int getSliderPercentage(SettingSlider slider);

	@GameTypeSupport(GameType.OLDSCHOOL)
	public void setSliderPercentage(SettingSlider slider, int percentage);

	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getSliderComponent(SettingSlider slider);
}
