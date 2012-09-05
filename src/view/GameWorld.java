package view;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import model.CollidableObject;
import model.Direction;
import model.Laser;
import model.Player;

public class GameWorld extends JLayeredPane implements KeyListener {

	private static final int FRAME_RATE = 30; // number of game updates per
	// second
	private static final long UPDATE_PERIOD = 1000000000L / FRAME_RATE; // nanoseconds
	private static final int BORDER_BUFFER = 10;

	public Player player1;
	public Player player2;
	private Stack<CollidableObject> allObjs;

	private boolean p1KeyUp;
	private boolean p1KeyDown;
	private boolean p1KeyLeft;
	private boolean p1KeyRight;
	private boolean p1KeyFire;

	private boolean p2KeyUp;
	private boolean p2KeyDown;
	private boolean p2KeyLeft;
	private boolean p2KeyRight;
	private boolean p2KeyFire;

	public boolean isPaused;
	private boolean toRestart;

	public GameWorld() {
		setLayout(null);
		setFocusable(true);
		initBG();
		initScoreBoard();
		initPlayers();
		initObjStack();

		addKeyListener(this);
	}

	private void initBG() {
		final int imgWidth = 128;
		final int imgHeight = 128;
		for (int x = 0; x <= GameFrame.WINDOW_WIDTH; x += imgWidth) {
			for (int y = 0; y <= GameFrame.WINDOW_HEIGHT; y += imgHeight) {
				JLabel img = new JLabel();
				img.setIcon(new ImageIcon("images/metalTexture.gif"));
				img.setLocation(x, y);
				img.setBounds(x, y, imgWidth, imgHeight);
				this.add(img);
			}
		}
	}

	private void initScoreBoard() {
		ScoreBoard sb = ScoreBoard.makeMe(this);
	}

	private void initObjStack() {
		allObjs = new Stack<CollidableObject>();
		allObjs.add(player1);
		allObjs.add(player2);
	}
	
	public long startTime;
	/**
	 * starts the game loop
	 */
	public void startGame() {
		GameFrame.showHowToPlay();
		long beginTime, timeTaken, timeLeft;
		while (true) {
			if(startTime==0)
				startTime = System.nanoTime();
			beginTime = System.nanoTime();

			this.grabFocus();
			
			if(toRestart){
				toRestart=false;
				onRestart();
			}

			if (!isPaused) {
				// moves players based on keyboard:
				doKeyboardUpdates();

				// updates all objects for this frame:
				doObjectUpdates();

				// test for collisions and act appropriately:
				collisionTest();

				// sets everything to be drawn:
				drawReady();

				// Refresh the display
				repaint();

				// self explanatory
				prepareForNextLoop();

			}

			// Provides necessary delay to meet the target frame rate
			timeTaken = System.nanoTime() - beginTime;
			timeLeft = (UPDATE_PERIOD - timeTaken) / 1000000; // in milliseconds
			if (timeLeft < 10)
				timeLeft = 10; // set a minimum
			try {
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) {
			}
		}
	}

	private void doKeyboardUpdates() {
		if (p1KeyLeft && !p1KeyRight) {
			player1.moveLeft();
		} else if (p1KeyRight && !p1KeyLeft) {
			player1.moveRight();
		} else {
			player1.brakeX();
		}
		if (p1KeyUp && !p1KeyDown) {
			player1.moveUp();
		} else if (p1KeyDown && !p1KeyUp) {
			player1.moveDown();
		} else {
			player1.brakeY();
		}
		if (p1KeyFire) {
			Laser newLaser = player1.fire();
			if (newLaser != null) {
				allObjs.add(newLaser);
				this.add(newLaser, 0);
			}
		}

		if (p2KeyLeft && !p2KeyRight) {
			player2.moveLeft();
		} else if (p2KeyRight && !p2KeyLeft) {
			player2.moveRight();
		} else {
			player2.brakeX();
		}
		if (p2KeyUp && !p2KeyDown) {
			player2.moveUp();
		} else if (p2KeyDown && !p2KeyUp) {
			player2.moveDown();
		} else {
			player2.brakeY();
		}
		if (p2KeyFire) {
			Laser newLaser = player2.fire();
			if (newLaser != null) {
				allObjs.add(newLaser);
				this.add(newLaser, 0);
			}
		}

		wrapAround(player1);
		wrapAround(player2);
	}

	private void doObjectUpdates() {
		for (CollidableObject obj : allObjs) {
			obj.incrementLocation();
			wrapAround(obj);
		}
	}

	private void wrapAround(CollidableObject obj) {
		// if player passes left wall
		if (obj.getXLoc() < -BORDER_BUFFER) {
			obj.setXLoc(this.getWidth() + BORDER_BUFFER - obj.getWidth());
			obj.setPrevXLoc(this.getWidth() + BORDER_BUFFER + 100);
			obj.lockX();
		}

		// if player passes right wall
		else if ((obj.getXLoc() + obj.getWidth()) > (this.getWidth() + BORDER_BUFFER)) {
			obj.setXLoc(-BORDER_BUFFER);
			obj.setPrevXLoc(-BORDER_BUFFER - 30);
			obj.lockX();
		}

		// if player passes north wall
		if (obj.getYLoc() < -BORDER_BUFFER) {
			obj.setYLoc(this.getHeight() + BORDER_BUFFER - obj.getHeight());
			obj.setPrevYLoc(this.getHeight() + BORDER_BUFFER + 30);
		}

		// if player passes south wall
		else if ((obj.getYLoc() + obj.getHeight()) > (this.getHeight() + BORDER_BUFFER)) {
			obj.setYLoc(-BORDER_BUFFER);
			obj.setPrevYLoc(-BORDER_BUFFER - 30);
		}
	}

	private void collisionTest() {
		Stack<CollidableObject> toRemove = new Stack<CollidableObject>();
		int size = allObjs.size();
		for (int i = 0; i < size - 1; i++) {
			CollidableObject obj1 = allObjs.get(i);
			for (int j = i + 1; j < size; j++) {
				CollidableObject obj2 = allObjs.get(j);
				Rectangle intersection = obj1.getRect().intersection(
						obj2.getRect());
				if (!intersection.isEmpty()) {
					// there is a collision!

					if (obj1.getPrevYLoc() + obj1.getHitHeight() <= obj2
							.getPrevYLoc()) {
						// vertical collision!
						// obj1 is above obj2!
						if (obj1 instanceof Laser || obj2 instanceof Laser) {
							if (obj1.shot(Direction.DOWN,obj2)) {
								toRemove.add(obj1);
							}
							if (obj2.shot(Direction.UP,obj1)) {
								toRemove.add(obj2);
							}
						} else {
							obj1.setYLoc(obj1.getYLoc() - intersection.height
									/ 2);
							obj2.setYLoc(obj2.getYLoc() + intersection.height
									/ 2 + intersection.height % 2);
						}

					} else if (obj2.getPrevYLoc() + obj2.getHitHeight() <= obj1
							.getPrevYLoc()) {
						// vertical collision!
						// obj2 is above obj1!
						if (obj1 instanceof Laser || obj2 instanceof Laser) {
							if (obj2.shot(Direction.DOWN,obj1)) {
								toRemove.add(obj2);
							}
							if (obj1.shot(Direction.UP,obj2)) {
								toRemove.add(obj1);
							}
						} else {
							obj2.setYLoc(obj2.getYLoc() - intersection.height
									/ 2);
							obj1.setYLoc(obj1.getYLoc() + intersection.height
									/ 2 + intersection.height % 2);
						}
					} else if (obj1.getPrevXLoc() + obj1.getHitWidth() <= obj2
							.getPrevXLoc()) {
						// horizontal collision!
						// obj1 is to the left of obj2!
						if (obj1 instanceof Laser || obj2 instanceof Laser) {
							if (obj1.shot(Direction.RIGHT, obj2)) {
								toRemove.add(obj1);
							}
							if (obj2.shot(Direction.LEFT, obj1)) {
								toRemove.add(obj2);
							}
						} else {
							obj1.setXLoc(obj1.getXLoc() - intersection.width
									/ 2);
							obj2.setXLoc(obj2.getXLoc() + intersection.width
									/ 2 + intersection.width % 2);
						}

					} else if (obj2.getPrevXLoc() + obj2.getHitWidth() <= obj1
							.getPrevXLoc()) {
						// vertical collision!
						// obj2 is to the left of obj1!
						if (obj1 instanceof Laser || obj2 instanceof Laser) {
							if (obj1.shot(Direction.LEFT,obj2)) {
								toRemove.add(obj1);
							}
							if (obj2.shot(Direction.RIGHT,obj1)) {
								toRemove.add(obj2);
							}
						} else {
							obj2.setXLoc(obj2.getXLoc() - intersection.width
									/ 2);
							obj1.setXLoc(obj1.getXLoc() + intersection.width
									/ 2 + intersection.width % 2);
						}
					}
				}
			}
		}
		for (CollidableObject obj : toRemove) {
			allObjs.remove(obj);
			remove(obj);
		}
	}

	private void drawReady() {
		for (CollidableObject obj : allObjs) {
			obj.setLocation(obj.getDrawX(), obj.getDrawY());
		}
	}

	private void prepareForNextLoop() {
		for (CollidableObject obj : allObjs) {
			obj.recordPrevLoc();
		}
	}

	public CollidableObject getPlayer() {
		return player1;
	}

	private void initPlayers() {
		player1 = new Player(true);
		player1.setLoc(GameFrame.WINDOW_WIDTH / 2 + 100,
				GameFrame.WINDOW_HEIGHT / 2);
		player1.setLocation(player1.getDrawX(), player1.getDrawY());
		player1.setBounds(player1.getDrawX(), player1.getDrawY(), 32, 32);
		this.add(player1, 0);

		player2 = new Player(false);
		player2.setLoc(GameFrame.WINDOW_WIDTH / 2 - player2.getWidth() - 100,
				GameFrame.WINDOW_HEIGHT / 2);
		player2.setLocation(player2.getDrawX(), player2.getDrawY());
		player2.setBounds(player2.getDrawX(), player2.getDrawY(), 32, 32);
		this.add(player2, 0);
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		switch (evt.getKeyCode()) {
		case KeyEvent.VK_UP:
			p1KeyUp = true;
			break;
		case KeyEvent.VK_W:
			p2KeyUp = true;
			break;
		case KeyEvent.VK_DOWN:
			p1KeyDown = true;
			break;
		case KeyEvent.VK_S:
			p2KeyDown = true;
			break;
		case KeyEvent.VK_LEFT:
			p1KeyLeft = true;
			break;
		case KeyEvent.VK_A:
			p2KeyLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			p1KeyRight = true;
			break;
		case KeyEvent.VK_D:
			p2KeyRight = true;
			break;
		case KeyEvent.VK_BACK_SLASH:
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_NUMPAD0:
		case KeyEvent.VK_NUMPAD1:
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_NUMPAD3:
		case KeyEvent.VK_END:
		case KeyEvent.VK_PAGE_DOWN:
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_HOME:
		case KeyEvent.VK_INSERT:
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_SLASH:
		case KeyEvent.VK_PERIOD:
		case KeyEvent.VK_COMMA:
		case KeyEvent.VK_M:
		case KeyEvent.VK_QUOTE:
		case KeyEvent.VK_SEMICOLON:
		case KeyEvent.VK_L:
		case KeyEvent.VK_K:
		case KeyEvent.VK_J:
		case KeyEvent.VK_BRACERIGHT:
		case KeyEvent.VK_BRACELEFT:
		case KeyEvent.VK_P:
		case KeyEvent.VK_O:
		case KeyEvent.VK_I:
			p1KeyFire = true;
			break;
		case KeyEvent.VK_CAPS_LOCK:
		case KeyEvent.VK_Q:
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_E:
		case KeyEvent.VK_R:
		case KeyEvent.VK_F:
		case KeyEvent.VK_G:
		case KeyEvent.VK_H:
		case KeyEvent.VK_Y:
		case KeyEvent.VK_B:
		case KeyEvent.VK_T:
		case KeyEvent.VK_V:
		case KeyEvent.VK_C:
		case KeyEvent.VK_X:
		case KeyEvent.VK_Z:
		case KeyEvent.VK_TAB:
			p2KeyFire = true;
			break;
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_SHIFT:
			isPaused = !isPaused;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		switch (evt.getKeyCode()) {
		case KeyEvent.VK_UP:
			p1KeyUp = false;
			break;
		case KeyEvent.VK_W:
			p2KeyUp = false;
			break;
		case KeyEvent.VK_DOWN:
			p1KeyDown = false;
			break;
		case KeyEvent.VK_S:
			p2KeyDown = false;
			break;
		case KeyEvent.VK_LEFT:
			p1KeyLeft = false;
			break;
		case KeyEvent.VK_A:
			p2KeyLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			p1KeyRight = false;
			break;
		case KeyEvent.VK_D:
			p2KeyRight = false;
			break;
		case KeyEvent.VK_BACK_SLASH:
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_NUMPAD0:
		case KeyEvent.VK_NUMPAD1:
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_NUMPAD3:
		case KeyEvent.VK_END:
		case KeyEvent.VK_PAGE_DOWN:
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_HOME:
		case KeyEvent.VK_INSERT:
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_SLASH:
		case KeyEvent.VK_PERIOD:
		case KeyEvent.VK_COMMA:
		case KeyEvent.VK_M:
		case KeyEvent.VK_QUOTE:
		case KeyEvent.VK_SEMICOLON:
		case KeyEvent.VK_L:
		case KeyEvent.VK_K:
		case KeyEvent.VK_J:
		case KeyEvent.VK_BRACERIGHT:
		case KeyEvent.VK_BRACELEFT:
		case KeyEvent.VK_P:
		case KeyEvent.VK_O:
		case KeyEvent.VK_I:
			p1KeyFire = false;
			player1.firePressFalse();
			break;
		case KeyEvent.VK_CAPS_LOCK:
		case KeyEvent.VK_Q:
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_E:
		case KeyEvent.VK_R:
		case KeyEvent.VK_F:
		case KeyEvent.VK_G:
		case KeyEvent.VK_H:
		case KeyEvent.VK_Y:
		case KeyEvent.VK_B:
		case KeyEvent.VK_T:
		case KeyEvent.VK_V:
		case KeyEvent.VK_C:
		case KeyEvent.VK_X:
		case KeyEvent.VK_Z:
		case KeyEvent.VK_TAB:
			p2KeyFire = false;
			player2.firePressFalse();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void restart() {
		toRestart = true;
	}
	
	private void onRestart(){
		allObjs.clear();
		removeAll();

		initBG();
		initScoreBoard();
		initPlayers();
		allObjs.add(player1);
		allObjs.add(player2);

		isPaused=false;
		p1KeyUp = false;
		p2KeyUp = false;
		p1KeyDown = false;
		p2KeyDown = false;
		p1KeyLeft = false;
		p2KeyLeft = false;
		p1KeyRight = false;
		p2KeyRight = false;
		p1KeyFire = false;
		p2KeyFire = false;
		
		repaint();
	}
}
