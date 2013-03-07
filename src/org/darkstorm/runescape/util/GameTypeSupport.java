package org.darkstorm.runescape.util;

import java.lang.annotation.*;

import org.darkstorm.runescape.GameType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
		ElementType.PACKAGE, ElementType.CONSTRUCTOR, ElementType.PARAMETER,
		ElementType.LOCAL_VARIABLE })
public @interface GameTypeSupport {
	public GameType[] value();
}
