package org.darkstorm.runescape.event.script;

import org.darkstorm.runescape.event.Event;
import org.darkstorm.runescape.script.Script;

public abstract class ScriptEvent extends Event {
	protected final Script script;

	public ScriptEvent(Script script) {
		this.script = script;
	}

	public Script getScript() {
		return script;
	}
}
