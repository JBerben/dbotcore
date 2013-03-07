package org.darkstorm.runescape.script;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.api.wrapper.Character;
import org.darkstorm.runescape.event.*;

/**
 * Example script to assist in understanding the API
 */
@ScriptManifest(name = "CoifCrafter", authors = "DarkStorm", version = "1.12", description = "TEST!")
public class CoifCrafterScript extends AbstractScript {
	private static final TileArea CORRAL_AREA = new TileArea(2881, 3482, 9, 11);
	private static final Tile JACK_LOCATION = new Tile(2887, 3501, 0);
	private static final int JACK_OVAL_ID = 14877;
	private static final int COWHIDE_ID = 1739, SOFTLEATHER_ID = 1741,
			COIF_ID = 1169;
	private static final int[] CRAFTABLE_IDS = new int[] { 1739, 1741, 1169 };
	private static final int[] COW_IDS = new int[] { 14997, 14998, 14999 };

	private final Filter<GroundItem> cowhideFilter = filters.area(
			filters.ground(COWHIDE_ID), CORRAL_AREA);
	private final Filter<NPC> cowNonCombatFilter = filters.area(
			filters.npc(COW_IDS), CORRAL_AREA);
	private final Filter<NPC> cowFilter = filters.area(new NPCFilter<NPC>(
			filters.npc(COW_IDS)), CORRAL_AREA);

	private final NumberFormat format = new DecimalFormat("#,###,###");

	private long startTime;
	private int amountCrafted;
	private int moneyGained;
	private int startExp;
	private int startLevel;

	public CoifCrafterScript(Bot bot) {
		super(bot);
	}

	@Override
	public void onStart() {
		startTime = System.currentTimeMillis();
		startExp = skills.getExperience(Skill.CRAFTING);
		startLevel = skills.getLevel(Skill.CRAFTING);
		amountCrafted = 0;
		moneyGained = 0;
		register(new CraftTask());
		register(new CollectTask());
		register(new BankTask());
	}

	@Override
	protected void onStop() {
	}

	@EventHandler
	public void onMessageReceived(ServerMessageEvent event) {
		String message = event.getMessage();
		if(message.contains("You make a coif."))
			amountCrafted++;
		else if(message.contains(" coins have been added to your")) {
			int amount;
			try {
				amount = Integer.parseInt(message.split(" ")[0]
						.replace(",", ""));
			} catch(NumberFormatException exception) {
				return;
			}
			logger.info("Parsed " + message.split(" ")[0] + " as " + amount);
			moneyGained += amount;
		}
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		int width = 517, height = 22;
		Graphics2D g = (Graphics2D) event.getGraphics();
		g.setColor(new Color(150, 150, 150, 220));
		AffineTransform original = g.getTransform();
		AffineTransform transform = new AffineTransform(original);
		transform.translate(0, 530);
		g.setTransform(transform);
		g.fill3DRect(0, 0, width, height, true);
		g.setColor(Color.BLACK);
		g.drawString(
				"CoifCrafter "
						+ getClass().getAnnotation(ScriptManifest.class)
								.version(), 5, 15);
		StringBuilder text = new StringBuilder();
		text.append("Crafted: ").append(format.format(amountCrafted));
		text.append(" | Profit: ").append(format.format(moneyGained));
		text.append(" | Exp: ").append(
				format.format(skills.getExperience(Skill.CRAFTING) - startExp));
		int levelsGained = skills.getLevel(Skill.CRAFTING) - startLevel;
		if(levelsGained > 0)
			text.append(" (+").append(levelsGained).append(")");
		long time = (System.currentTimeMillis() - startTime) / 1000;
		long hours = time / 60 / 60, minutes = (time / 60) - (hours * 60), seconds = time
				- (minutes * 60) - (hours * 60 * 60);
		text.append(" | Time: ").append(hours).append(":")
				.append(minutes < 10 ? "0" : "").append(minutes).append(":")
				.append(seconds < 10 ? "0" : "").append(seconds);
		String string = text.toString();
		g.drawString(string,
				width - g.getFontMetrics().stringWidth(string) - 5, 15);
		g.setTransform(original);
	}

	private class CraftTask implements Task {
		@Override
		public boolean activate() {
			return inventory.isFull()
					&& inventory.contains(filters.item(CRAFTABLE_IDS));
		}

		@Override
		public void run() {
			logger.info("Crafting...");
			if(players.getSelf().isMoving())
				return;
			NPC jack = npcs.getClosest(filters.npc(JACK_OVAL_ID));
			if(jack == null) {
				walking.walkTo(JACK_LOCATION);
				return;
			} else if(players.getSelf().getLocation().distanceTo(jack) >= 6) {
				walking.walkTo(jack.getLocation());
				return;
			} else if(!jack.isOnScreen()) {
				camera.turnTo(jack);
				return;
			}
			if(inventory.contains(filters.item(COWHIDE_ID))) {
				InterfaceComponent comp = interfaces.getComponent(1371, 44);
				if(comp != null)
					comp = comp.getChild(1);
				if(comp != null) {
					if(comp.getTextureId() != 15201) {
						mouse.click(comp);
						mouse.await();
						sleep(1000, 1500);
						return;
					} else {
						InterfaceComponent tan = interfaces.getComponent(1370,
								20);
						if(tan != null) {
							mouse.click(tan);
							mouse.await();
							sleep(1000, 1500);
							return;
						}
					}
				} else {
					jack.interact("Tan-hide");
					sleep(1000, 1500);
					return;
				}
			} else if(inventory.contains(filters.item(SOFTLEATHER_ID))) {
				for(int i = 0; i < 10; i++) {
					if(players.getSelf().getAnimation() == 1249) {
						sleep(1000, 1500);
						return;
					}
					sleep(200);
				}
				InterfaceComponent comp = interfaces.getComponent(1179, 16);
				if(comp != null) {
					mouse.click(comp);
					mouse.await();
					sleep(1000, 1500);
					return;
				} else {
					comp = interfaces.getComponent(1371, 44);
					if(comp != null)
						comp = comp.getChild(37);
					if(comp != null) {
						if(comp.getTextureId() != 15201) {
							mouse.click(comp);
							mouse.await();
							sleep(1000, 1500);
							return;
						} else {
							InterfaceComponent tan = interfaces.getComponent(
									1370, 20);
							if(tan != null) {
								mouse.click(tan);
								sleep(1000, 1500);
								return;
							}
						}
					} else {
						mouse.click(inventory.getItem(
								filters.item(SOFTLEATHER_ID)).getComponent());
						sleep(1000, 1500);
						return;
					}
				}
			} else if(inventory.contains(filters.item(COIF_ID))) {
				InterfaceComponent comp = interfaces.getComponent(1265, 84);
				if(comp != null)
					comp = comp.getChild(0);
				if(comp != null && comp.getText() != null
						&& comp.getText().startsWith("Jack Oval")) {
					inventory.getItem(filters.item(COIF_ID)).getComponent()
							.interact("Sell 50");
					sleep(1000, 1500);
					return;
				} else {
					jack.interact("Trade");
					sleep(1000, 1500);
					return;
				}
			}
		}
	}

	private class BankTask implements Task {
		@Override
		public boolean activate() {
			return inventory.isFull()
					&& !inventory.contains(filters.item(CRAFTABLE_IDS));
		}

		@Override
		public void run() {
			logger.info("Banking...");
		}
	}

	private class CollectTask implements Task {
		@Override
		public boolean activate() {
			return !inventory.isFull();
		}

		@Override
		public void run() {
			logger.info("Collecting...");
			Tile current = players.getSelf().getLocation();
			if(CORRAL_AREA.contains(current.getX(), current.getY())) {
				GroundItem closestHide = groundItems.getClosest(cowhideFilter);
				if(closestHide != null) {
					logger.info("Taking hide...");
					if(closestHide.isOnScreen())
						closestHide.interact("Take Cowhide");
					else
						camera.turnTo(closestHide);
					sleep(500, 1500);
					return;
				}
				NPC closestCow = npcs.getClosest(cowNonCombatFilter);
				if(players.getSelf().isInCombat() && closestCow != null) {
					logger.info("Target cow: " + closestCow.isInCombat() + " "
							+ closestCow.getHealthPercentage());
				}
				if(players.getSelf().isInCombat()
						&& (closestCow != null && closestCow.isInCombat() ? closestCow
								.getHealthPercentage() > 0 : true)) {
					sleep(100);
					return;
				}
				closestCow = npcs.getClosest(cowFilter);
				if(closestCow != null) {
					logger.info("Attacking...");
					if(closestCow.isOnScreen())
						closestCow.interact("Attack");
					else
						camera.turnTo(closestCow);
					sleep(250, 500);
					return;
				}
				sleep(100);
			} else {
				if(players.getSelf().isMoving()) {
					sleep(250, 500);
					return;
				}
				logger.info("Walking to corral...");
				walking.walkTo(CORRAL_AREA.getRandomTileInside());
				sleep(250, 500);
			}
		}
	}

	private class NPCFilter<T extends Character> implements Filter<T> {
		private final Filter<T> filter;

		public NPCFilter(Filter<T> filter) {
			this.filter = filter;
		}

		@Override
		public boolean accept(T entity) {
			if(!filter.accept(entity))
				return false;
			return !entity.isInCombat() && entity.getHealthPercentage() > 0;
		}
	}
}
