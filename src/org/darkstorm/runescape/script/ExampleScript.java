package org.darkstorm.runescape.script;

import org.darkstorm.runescape.*;

@ScriptManifest(name = "Example Script", author = "DarkStorm", version = "1.0.0", support = {
		GameType.CURRENT, GameType.OLDSCHOOL })
public class ExampleScript extends AbstractScript {

	public ExampleScript(Bot bot) {
		super(bot);
	}

	@Override
	protected void onStart() {
		register(new SomeTask());
		register(new OtherTask());

		logger.info("Started!");
	}

	@Override
	protected void onStop() {
		logger.info("Stopped.");
	}

	private class SomeTask implements Task {
		@Override
		public boolean activate() {
			return false; // activate condition for something
		}

		@Override
		public void run() {
			// do something
		}
	}

	private class OtherTask implements Task {
		@Override
		public boolean activate() {
			return false; // activate condition for something else
		}

		@Override
		public void run() {
			// do something else
		}
	}
}
