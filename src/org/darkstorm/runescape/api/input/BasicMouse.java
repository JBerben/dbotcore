package org.darkstorm.runescape.api.input;

import java.awt.*;
import java.util.concurrent.*;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.game.PaintEvent;

public class BasicMouse implements Mouse, EventListener {
	private final GameContext context;
	private final KBot2Mouse mouse;
	private final ExecutorService service;

	private Future<Boolean> mouseTask;
	private MouseTarget currentTarget;

	private boolean synchronous = true;

	public BasicMouse(GameContext context) {
		this.context = context;
		mouse = new KBot2Mouse(context);

		service = Executors.newSingleThreadExecutor();

		context.getBot().getEventManager().registerListener(this);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		MouseTarget current = currentTarget;
		if(current == null)
			return;
		Graphics g = event.getGraphics();
		g.setColor(Color.RED);
		((Graphics2D) g).setStroke(new BasicStroke(1.0f));
		current.render(g);
	}

	@Override
	public Point getLocation() {
		return mouse.getMousePos();
	}

	@Override
	public void hover(final MouseTargetable target) {
		if(mouseTask != null && !mouseTask.isDone())
			mouseTask.cancel(true);
		mouseTask = service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				try {
					while(true) {
						MouseTarget t = target.getTarget();
						if(t == null)
							return Boolean.FALSE;
						currentTarget = t;
						Point location = t.getLocation();
						if(location == null)
							return Boolean.FALSE;
						updateSpeed(location);
						mouse.setMouseSpeed(mouse.getSpeed() * 2.5);
						mouse.moveMouse(location.x, location.y);
						Thread.sleep(250);
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				} finally {
					currentTarget = null;
				}
				return Boolean.TRUE;
			}
		});
	}

	@Override
	public void move(final MouseTargetable target) {
		if(mouseTask != null && !mouseTask.isDone())
			mouseTask.cancel(true);
		mouseTask = service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				try {
					MouseTarget t = target.getTarget();
					if(t == null)
						return Boolean.FALSE;
					currentTarget = t;
					do {
						Point location = t.getLocation();
						if(location == null)
							return Boolean.FALSE;
						updateSpeed(location);
						mouse.moveMouse(location.x, location.y);
						t = target.getTarget();
					} while(!t.isOver(mouse.getMousePos()));
				} catch(Exception exception) {
					exception.printStackTrace();
					return Boolean.FALSE;
				} finally {
					currentTarget = null;
				}
				return Boolean.TRUE;
			}
		});
		if(synchronous) {
			await(5000);
			stop();
		}
	}

	@Override
	public void click(MouseTargetable target) {
		click(target, true);
	}

	@Override
	public void click(final MouseTargetable target, final boolean left) {
		if(mouseTask != null && !mouseTask.isDone())
			mouseTask.cancel(true);
		mouseTask = service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				try {
					MouseTarget t = target.getTarget();
					if(t == null)
						return Boolean.FALSE;
					currentTarget = t;
					do {
						Point location = t.getLocation();
						if(location == null)
							return Boolean.FALSE;
						updateSpeed(location);
						mouse.moveMouse(location.x, location.y);
						t = target.getTarget();
					} while(!t.isOver(mouse.getMousePos()));
					mouse.clickMouse(left);
				} catch(Exception exception) {
					exception.printStackTrace();
					return Boolean.FALSE;
				} finally {
					currentTarget = null;
				}
				return Boolean.TRUE;
			}
		});
		if(synchronous) {
			await(5000);
			stop();
		}
	}

	@Override
	public void click(boolean left) {
		mouse.clickMouse(left);
	}

	@Override
	public void moveRandomly(final int maximumDeviation) {
		if(mouseTask != null && !mouseTask.isDone())
			mouseTask.cancel(true);
		mouseTask = service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				try {
					updateSpeed(new Point(maximumDeviation, maximumDeviation));
					mouse.setMouseSpeed(mouse.getSpeed() * 1.65);
					mouse.moveMouseRandomly(maximumDeviation);
				} catch(Exception exception) {
					exception.printStackTrace();
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
		});
		if(synchronous) {
			await(5000);
			stop();
		}
	}

	private void updateSpeed(Point target) {
		Point current = mouse.getMousePos();
		int width = context.getBot().getGame().getWidth(), height = context
				.getBot().getGame().getHeight();
		double dist = current.distance(target);
		double maxDist = Math.sqrt(width * width + height * height);
		double factor = context.getCalculations().random(2.5, 3.5)
				* ((maxDist - dist) / maxDist);
		mouse.setMouseSpeed(factor);

	}

	@Override
	public boolean isActive() {
		return mouseTask != null && !mouseTask.isDone();
	}

	@Override
	public void stop() {
		if(mouseTask != null && !mouseTask.isDone())
			mouseTask.cancel(true);
	}

	@Override
	public boolean await() {
		if(mouseTask != null && !mouseTask.isCancelled()) {
			try {
				return mouseTask.get();
			} catch(Exception exception) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean await(int timeout) {
		if(mouseTask != null && !mouseTask.isCancelled()) {
			try {
				return mouseTask.get(timeout, TimeUnit.MILLISECONDS);
			} catch(Exception exception) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isSynchronous() {
		return synchronous;
	}

	@Override
	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	@Override
	public GameContext getContext() {
		return context;
	}

}
