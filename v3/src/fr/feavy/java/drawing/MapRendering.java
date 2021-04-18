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
import fr.feavy.java.systemEvent.events.RefreshScreenEvent;
import fr.feavy.java.systemEvent.events.SetKeyInputEvent;
import fr.feavy.java.systemEvent.events.StartAnimationEvent;
import fr.feavy.java.systemEvent.events.WarpActivatedEvent;
import fr.feavy.java.systemEvent.listeners.EventListener;

public class MapRendering extends Rendering implements EventListener {

	private int SCROLLING_SPEED = 5;
	private int FRAME_SKIP = 0;

	private int scrollingDirection = -1;
	private Map currentMap = null;
	private Map currentBorderMap = null;
	private int borderMapSide = -1;

	private boolean warpActivated = false;

	private boolean wasOnWarp = false;
	private boolean onWarp = false;

	private int borderXScale = 0, borderYScale = 0;

	private ScrollingRunnable scrollingRunnable;

	public MapRendering(Map map) {
		Main.addEventListener(this);
		setNewMap(map, new Point(2, 2));
		scrollingRunnable = new ScrollingRunnable();
		setCameraLocation(new Point(2, 2));
	}

	@Override
	public void draw(Graphics2D g) {

		if (wasOnWarp == false && onWarp == true) {
			wasOnWarp = false;
			onWarp = false;
			doAStep();
		}

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

		// DRAW EACH VISIBLES TILES

		for (int y = -5; y < 6; y++) {
			for (int x = -5; x < 7; x++) {
				if (cx + x >= 0 && cy + y >= 0 && cx + x < w && cy + y < h)
					g.drawImage(Tiles.getTile(currentMap.getTileID(cx + x, cy + y)), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
				else if (isCurrentBorderMap()) {
					if (!Direction.isVertical(borderMapSide)) {
						if (cx + x + borderXScale >= 0 && cy + y >= 0 && cx + x + borderXScale < bw && cy + y < bh)
							g.drawImage(Tiles.getTile(currentBorderMap.getTileID(cx + x + borderXScale, cy + y )), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset,
									64, 64, null);
						else if ((cx + x < 0 && borderMapSide == Direction.LEFT) || (cx + x >= w && borderMapSide == Direction.RIGHT)) // à changer
							g.drawImage(Tiles.getTile(currentBorderMap.getBorderTileID()), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
						else
							g.drawImage(Tiles.getTile(currentMap.getBorderTileID()), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset, 64, 64, null);
					} else {
						if (cx + x >= 0 && cy + y + borderYScale >= 0 && cx + x < bw && cy + y + borderYScale < bh)
							g.drawImage(Tiles.getTile(currentBorderMap.getTileID(cx + x , cy + y + borderYScale)), (x + 4) * 64 + xOffset, (y + 4) * 64 + yOffset,
									64, 64, null);
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

	private void doAStep() {
		if (scrollingRunnable.isTerminated()) {
			setScrollingDirection(Direction.DOWN);
			startScrolling();
		} else {
			changeScrollingDirection(Direction.DOWN);
		}
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
		for(int i = 0; i < 4; i++){
			if (currentMap.isMap(i)) 
				if (isBorderMapVisible(i, newLocation)){
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
		if (scrollingRunnable == null)
			scrollingRunnable = new ScrollingRunnable();
		else
			scrollingRunnable.reset();
		scrollingRunnable.setDirection(direction);
	}

	public void changeScrollingDirection(int direction) {
		scrollingRunnable.scrolling();

		scrollingRunnable.setNewDirection(direction);
	}

	public void startScrolling() {
		new Thread(scrollingRunnable).start();
	}

	public void stopScrolling() {
		scrollingRunnable.stop();
	}

	@Override
	public void onEvent(Event e) {

		if (e.getID().equals("keyPressedEvent")) {
			KeyPressedEvent ke = (KeyPressedEvent) e;

			if (ke.isKeyArrow()) {

				int direction = ke.getKeyCode() - 37;

				if (scrollingRunnable.isTerminated()) {

					setScrollingDirection(direction);
					startScrolling();

				} else {
					changeScrollingDirection(direction);
				}

				scrollingDirection = direction;

			}
		} else if (e.getID().equals("keyReleasedEvent")) {
			KeyReleasedEvent ke = (KeyReleasedEvent) e;

			if (ke.isKeyArrow()) {

				int direction = ke.getKeyCode() - 37;

				if (direction == scrollingDirection) {

					scrollingDirection = -1;
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
			Point newLocation = new Point(0,0);
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

	class ScrollingRunnable implements Runnable {

		private boolean scrolling = true;
		private Point dest = new Point(0, 0);
		private Point newDest = new Point(0, 0);
		private boolean isVertical = false;
		private boolean isNewVertical = false;
		private boolean directionChanged = false;
		private boolean isTerminated = false;
		private int localDirection = -1;
		private int newLocalDirection = -1;

		public ScrollingRunnable() {
			reset();
		}

		@Override
		public void run() {
			isTerminated = false;
			callEvent(new CameraScrollingChangedEvent(dest, newDest));
			while (scrolling) {

				if (currentMap.getMovementID(cameraLocation.x + dest.x, cameraLocation.y + dest.y) == 0) {

					for (int i = 1; i <= 64; i++) {

						if (isVertical)
							yOffset = i * -dest.y;
						else
							xOffset = i * -dest.x;

						callEvent(new RefreshScreenEvent());

						sleep(SCROLLING_SPEED * (FRAME_SKIP + 1));
					}

					cameraLocation.x += dest.x;
					cameraLocation.y += dest.y;
					xOffset = 0;
					yOffset = 0;

					if (cameraLocation.x < 0 || cameraLocation.x == currentMap.getWidth() || cameraLocation.y < 0 || cameraLocation.y == currentMap.getHeight())
						callEvent(new CameraReachMapBorderEvent(localDirection));
					callEvent(new CameraMovedEvent(new Point(cameraLocation.x - dest.x, cameraLocation.y - dest.y), cameraLocation, localDirection, false));

				} else {
					sleep(SCROLLING_SPEED * (FRAME_SKIP + 1));
					callEvent(new CameraMovedEvent(cameraLocation, new Point(cameraLocation.x + dest.x, cameraLocation.y + dest.y), localDirection, true));
				}

				if (directionChanged) {
					callEvent(new CameraScrollingChangedEvent(dest, newDest));
					dest.x = newDest.x;
					dest.y = newDest.y;
					isVertical = isNewVertical;
					directionChanged = false;
					localDirection = newLocalDirection;
					newLocalDirection = -1;
				}
			}
			isTerminated = true;
		}

		public boolean isTerminated() {
			return isTerminated;
		}

		public void reset() {
			scrolling = true;
			dest.x = 0;
			dest.y = 0;
			newDest.x = 0;
			newDest.y = 0;
			directionChanged = false;
			isVertical = false;
			isNewVertical = false;
			isTerminated = true;
		}

		public void setNewDirection(int direction) {
			newLocalDirection = direction;
			newDest.x = 0;
			newDest.y = 0;
			isNewVertical = Direction.isVertical(direction);
			if (isNewVertical)
				newDest.y = Direction.getDestination(direction);
			else
				newDest.x = Direction.getDestination(direction);
			directionChanged = true;
		}

		public void setDirection(int direction) {
			localDirection = direction;
			isVertical = Direction.isVertical(direction);
			if (isVertical)
				dest.y = Direction.getDestination(direction);
			else
				dest.x = Direction.getDestination(direction);
		}

		public void scrolling() {
			scrolling = true;
		}

		public void stop() {
			scrolling = false;
		}

		private void sleep(int time) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void callEvent(Event e) {
		Main.callEvent(e);
	}

}
