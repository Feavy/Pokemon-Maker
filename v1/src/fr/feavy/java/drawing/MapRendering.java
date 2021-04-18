package fr.feavy.java.drawing;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.feavy.java.Main;
import fr.feavy.java.constants.Direction;
import fr.feavy.java.constants.Tiles;
import fr.feavy.java.drawing.animations.FadeScreen;
import fr.feavy.java.map.Map;
import fr.feavy.java.map.events.Warp;
import fr.feavy.java.systemEvent.events.CameraMovedEvent;
import fr.feavy.java.systemEvent.events.CameraReachMapBorderEvent;
import fr.feavy.java.systemEvent.events.CameraScrollingChangedEvent;
import fr.feavy.java.systemEvent.events.Event;
import fr.feavy.java.systemEvent.events.KeyPressedEvent;
import fr.feavy.java.systemEvent.events.KeyReleasedEvent;
import fr.feavy.java.systemEvent.events.SetKeyInputEvent;
import fr.feavy.java.systemEvent.events.StartAnimationEvent;
import fr.feavy.java.systemEvent.events.WarpActivatedEvent;
import fr.feavy.java.systemEvent.listeners.EventListener;

public class MapRendering extends Rendering implements EventListener {

	private int SCROLLING_SPEED = 5;
	private int FRAME_SKIP = 0;

	private int currentScrollingDirection = -1;
	private int newScrollingDirection = -1;
	boolean isNewScrollingDirection = false;
	boolean isScrolling = false;
	boolean stopScrolling = false;
	private Map currentMap = null;
	private Map currentBorderMap = null;
	private int borderMapSide = -1;

	private boolean warpActivated = false;

	private boolean wasOnWarp = false;
	private boolean onWarp = false;

	private int borderXScale = 0, borderYScale = 0;

	private double xDoubleOffset = 0, yDoubleOffset = 0;

	int frameSkip = 0;

	public MapRendering(Map map) {
		Main.addEventListener(this);
		setNewMap(map, new Point(2, 2));
		setCameraLocation(new Point(2, 2));
	}

	@Override
	public void draw(Graphics2D g) {

		move();

		int cx = cameraLocation.x; // CameraX
		int cy = cameraLocation.y; // CameraY

		int w = currentMap.getWidth();
		int h = currentMap.getHeight();

		int bw = 0;
		int bh = 0;
		if (isCurrentBorderMap()) {
			bw = currentBorderMap.getWidth();
			bh = currentBorderMap.getHeight();
		}

		xOffset = (int)xDoubleOffset;
		yOffset = (int)yDoubleOffset;
		
		// DRAW EACH VISIBLES TILES

		for (int y = -5; y < 6; y++) {
			for (int x = -5; x < 7; x++) {
				if (cx + x >= 0 && cy + y >= 0 && cx + x < w && cy + y < h)
					g.drawImage(Tiles.getTile(currentMap.getTileID(cx + x, cy + y)), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
				else if (isCurrentBorderMap()) {
					if (!Direction.isVertical(borderMapSide)) {
						if (cx + x + borderXScale >= 0 && cy + y >= 0 && cx + x + borderXScale < bw && cy + y < bh)
							g.drawImage(Tiles.getTile(currentBorderMap.getTileID(cx + x + borderXScale, cy + y)), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64,
									null);
						else if ((cx + x < 0 && borderMapSide == Direction.LEFT) || (cx + x >= w && borderMapSide == Direction.RIGHT)) // �
																																		// changer
							g.drawImage(Tiles.getTile(currentBorderMap.getBorderTileID()), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
						else
							g.drawImage(Tiles.getTile(currentMap.getBorderTileID()), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
					} else {
						if (cx + x >= 0 && cy + y + borderYScale >= 0 && cx + x < bw && cy + y + borderYScale < bh)
							g.drawImage(Tiles.getTile(currentBorderMap.getTileID(cx + x, cy + y + borderYScale)), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64,
									null);
						else if ((cy + y < 0 && borderMapSide == Direction.UP) || (cy + y >= h && borderMapSide == Direction.DOWN))
							g.drawImage(Tiles.getTile(currentBorderMap.getBorderTileID()), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
						else
							g.drawImage(Tiles.getTile(currentMap.getBorderTileID()), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
					}
				} else
					g.drawImage(Tiles.getTile(currentMap.getBorderTileID()), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
			}
		}

		g.drawString(cameraLocation.x + " ; " + cameraLocation.y, 5, 15);

	}

	private void move() {

		if (!isScrolling)
			return;

		Point dest = new Point(Direction.getXDestination(currentScrollingDirection), Direction.getYDestination(currentScrollingDirection));

		if (xDoubleOffset + yDoubleOffset != 0 || currentMap.getMovementID(cameraLocation.x + dest.x, cameraLocation.y + dest.y) == 0) {
			
			if (Direction.isVertical(currentScrollingDirection))
				yDoubleOffset += -dest.y*2;
			else
				xDoubleOffset += -dest.x*2;
			
			if (Math.abs(xDoubleOffset) / 64 == 1 || Math.abs(yDoubleOffset) / 64 == 1) {
				cameraLocation.x += dest.x;
				cameraLocation.y += dest.y;
				xDoubleOffset = 0;
				yDoubleOffset = 0;
				if (stopScrolling)
					isScrolling = false;
			}

			if (cameraLocation.x < 0 || cameraLocation.x == currentMap.getWidth() || cameraLocation.y < 0 || cameraLocation.y == currentMap.getHeight())
				callEvent(new CameraReachMapBorderEvent(currentScrollingDirection));
			callEvent(new CameraMovedEvent(new Point(cameraLocation.x - dest.x, cameraLocation.y - dest.y), cameraLocation, currentScrollingDirection, false));

		} else {
			callEvent(new CameraMovedEvent(cameraLocation, new Point(cameraLocation.x + dest.x, cameraLocation.y + dest.y), currentScrollingDirection, true));
		}
	}

	private void doAStep() {

		setScrollingDirection(Direction.DOWN);
		startScrolling();

		player.startMovingAnimation();
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				stopScrolling();
				callEvent(new SetKeyInputEvent(true));
				player.stopMovingAnimation();

			}
		}, 100l);
	}

	private void setNewMap(Map newMap, Point newLocation) {
		this.currentMap = newMap;
		for (int i = 0; i < 4; i++) {
			if (currentMap.isMap(i))
				if (isBorderMapVisible(i, newLocation)) {
					updateBorderMap(i, newLocation);
					return;
				}
		}
		resetBorderMap();
	}

	private void setCameraLocation(Point location) {
		cameraLocation = location;
	}

	public void setScrollingDirection(int direction) {
		newScrollingDirection = direction;
	}

	public void startScrolling() {
		isNewScrollingDirection = true;
		stopScrolling = false;
		isScrolling = true;
		currentScrollingDirection = newScrollingDirection;
		newScrollingDirection = -1;
	}

	public void stopScrolling() {
		stopScrolling = true;
	}

	@Override
	public void onEvent(Event e) {

		if (e.getID().equals("keyPressedEvent")) {
			KeyPressedEvent ke = (KeyPressedEvent) e;

			if (ke.isKeyArrow()) {

				int direction = ke.getKeyCode() - 37;

				setScrollingDirection(direction);
				startScrolling();

			}
		} else if (e.getID().equals("keyReleasedEvent")) {
			KeyReleasedEvent ke = (KeyReleasedEvent) e;

			if (ke.isKeyArrow()) {

				int direction = ke.getKeyCode() - 37;

				if (direction == currentScrollingDirection) {
					stopScrolling();
				}

			}
		} else if (e.getID().equals("cameraMovedEvent")) {
			CameraMovedEvent cme = (CameraMovedEvent) e;
			Point l = cme.getNewLocation();
			int direction = cme.getDirection();

			if (currentMap.isMap(direction)) {
				if (isBorderMapVisible(direction, l))
					updateBorderMap(direction, l);
				else if (!isBorderMapVisible(borderMapSide, cameraLocation) && currentBorderMap != null)
					resetBorderMap();
			} else if (!isBorderMapVisible(borderMapSide, cameraLocation) && currentBorderMap != null)
				resetBorderMap();

			if (currentMap.isEvent(cme.getNewLocation())) {
				List<fr.feavy.java.map.events.Event> mapEvent = currentMap.getEvents(cme.getNewLocation());
				for (fr.feavy.java.map.events.Event ev : mapEvent) {
					if (ev instanceof Warp)
						callEvent(new WarpActivatedEvent((Warp) ev));
					callEvent(new StartAnimationEvent(new FadeScreen()));
					break;
				}

			}
		} else if (e.getID().equals("warpActivatedEvent")) {
			wasOnWarp = false;
			onWarp = false;
			WarpActivatedEvent wae = (WarpActivatedEvent) e;
			Warp w = wae.getWarp();
			try {
				if (currentMap.isEvent(cameraLocation)) {
					Warp w2 = currentMap.getWarp(cameraLocation);
					wasOnWarp = (w2 != null);
				}
				Map newMap = Main.getProject().getMap(w.getMapID());
				Point newLocation = new Point(w.getX(), w.getY());
				setNewMap(newMap, newLocation);
				setCameraLocation(newLocation);
				if (currentMap.isEvent(cameraLocation)) {
					Warp w2 = currentMap.getWarp(cameraLocation);
					onWarp = (w2 != null);
				}
				if (wasOnWarp == false && onWarp == true) {
					callEvent(new SetKeyInputEvent(false));
					player.stopMovingAnimation();
				}
				warpActivated = true;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getID().equals("cameraReachMapBorderEvent")) {
			CameraReachMapBorderEvent crmbe = (CameraReachMapBorderEvent) e;
			if (currentBorderMap == null && crmbe.getDirection() == borderMapSide)
				return;
			Point newLocation = new Point(0, 0);
			switch (crmbe.getDirection()) {
			case Direction.UP:
				newLocation = new Point(cameraLocation.x, currentBorderMap.getHeight() - 1);
				break;
			case Direction.DOWN:
				newLocation = new Point(cameraLocation.x, 0);
				break;
			case Direction.LEFT:
				newLocation = new Point(currentBorderMap.getWidth() - 1, cameraLocation.y);
				break;
			case Direction.RIGHT:
				newLocation = new Point(0, cameraLocation.y);
				break;
			}

			setCameraLocation(newLocation);
			setNewMap(currentBorderMap, cameraLocation);

			updateBorderMap(borderMapSide, cameraLocation);
		}

	}

	private boolean isCurrentBorderMap() {
		return currentBorderMap != null;
	}

	private void resetBorderMap() {
		currentBorderMap = null;
		borderMapSide = -1;
	}

	private boolean isBorderMapVisible(int direction, Point currentLocation) {
		switch (direction) {
		case Direction.UP:
			if (!(currentLocation.y <= 5))
				return false;
			break;
		case Direction.DOWN:
			if (!(currentMap.getHeight() - currentLocation.y <= 5))
				return false;
			break;
		case Direction.LEFT:
			if (!(currentLocation.x <= 5))
				return false;

			break;
		case Direction.RIGHT:
			if (!(currentMap.getWidth() - currentLocation.x <= 6))
				return false;
			break;
		}
		return true;
	}

	private void updateBorderMap(int direction, Point currentLocation) {

		try {
			currentBorderMap = Main.getProject().getMap(currentMap.getBorderMapID(direction));
			borderMapSide = direction;
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (direction) {
		case Direction.UP:
			borderYScale = currentBorderMap.getHeight();
			break;
		case Direction.DOWN:
			borderYScale = -currentMap.getHeight();
			break;
		case Direction.LEFT:
			borderXScale = currentBorderMap.getWidth();
			break;
		case Direction.RIGHT:
			borderXScale = -currentMap.getWidth();
			break;
		}
	}

	@Override
	public void callEvent(Event e) {
		Main.callEvent(e);
	}

}
