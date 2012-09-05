package model;

import java.io.File;
import java.io.Serializable;

import javax.swing.ImageIcon;

import view.ScoreBoard;

/**
 * This class outlines a player. It contains all the relevant information about
 * a certain player such as their sprite, speed, and location.
 * 
 * Future features: The current room they are in where xLoc and yLoc refer to
 * their location within that room. Perhaps the ability to change speeds based
 * on power up?
 * 
 * @author Tyler Cox
 * 
 */
public class Player extends CollidableObject implements Serializable {

	private Direction direction;
	private boolean isMoving = false;
	private int runAcc = 5;
	private int brakeAcc = 5;
	private int xSpeed = 0;
	private int ySpeed = 0;
	private static final int MAXSPEED = 6;
	private int fireTimer = 0;
	private int fireDelay = 10;
	private boolean firePress = false;
	public static String[] pictures = { "images"+File.separator+"redMover.png",
			"images"+File.separator+"purpleMover.png", "images"+File.separator+"orangeMover.png",
			"images"+File.separator+"greenMover.png", "images"+File.separator+"blueMover.png",
			"images"+File.separator+"blackMoverUp.png", "images"+File.separator+"blackMoverRight.png",
			"images"+File.separator+"blackMoverDown.png", "images"+File.separator+"BlackMoverLeft.png" };
	private ImageIcon[] images;
	private int IMG_DOWN;
	private int IMG_UP;
	private int IMG_LEFT;
	private int IMG_RIGHT;
	private int anim = 0;
	private final int animDelay = 5;
	private int animCount;
	private boolean isPlayerOne;
	public int selfShotCount;

	/**
	 * Constructs the new player with a random sprite. Future addition: The
	 * sprite is chosen by the player.
	 */
	public Player(boolean isPlayerOne) {
		this.isPlayerOne = isPlayerOne;
		if (isPlayerOne)
			initP1Frames();
		else
			initP2Frames();
		width = 20;
		height = 30;
		xOffset = 6;
		yOffset = 2;
		direction = Direction.DOWN;
		animate(true);
	}

	private void initP1Frames() {
		images = new ImageIcon[8];

		IMG_DOWN = 0;
		images[IMG_DOWN] = new ImageIcon("images"+File.separator+"p1_fr1.gif");
		images[IMG_DOWN + 1] = new ImageIcon("images"+File.separator+"p1_fr2.gif");

		IMG_UP = 2;
		images[IMG_UP] = new ImageIcon("images"+File.separator+"p1_bk1.gif");
		images[IMG_UP + 1] = new ImageIcon("images"+File.separator+"p1_bk2.gif");

		IMG_RIGHT = 4;
		images[IMG_RIGHT] = new ImageIcon("images"+File.separator+"p1_rt1.gif");
		images[IMG_RIGHT + 1] = new ImageIcon("images"+File.separator+"p1_rt2.gif");

		IMG_LEFT = 6;
		images[IMG_LEFT] = new ImageIcon("images"+File.separator+"p1_lf1.gif");
		images[IMG_LEFT + 1] = new ImageIcon("images"+File.separator+"p1_lf2.gif");
	}

	private void initP2Frames() {
		images = new ImageIcon[8];

		IMG_DOWN = 0;
		images[IMG_DOWN] = new ImageIcon("images"+File.separator+"p2_fr1.gif");
		images[IMG_DOWN + 1] = new ImageIcon("images"+File.separator+"p2_fr2.gif");

		IMG_UP = 2;
		images[IMG_UP] = new ImageIcon("images"+File.separator+"p2_bk1.gif");
		images[IMG_UP + 1] = new ImageIcon("images"+File.separator+"p2_bk2.gif");

		IMG_RIGHT = 4;
		images[IMG_RIGHT] = new ImageIcon("images"+File.separator+"p2_rt1.gif");
		images[IMG_RIGHT + 1] = new ImageIcon("images"+File.separator+"p2_rt2.gif");

		IMG_LEFT = 6;
		images[IMG_LEFT] = new ImageIcon("images"+File.separator+"p2_lf1.gif");
		images[IMG_LEFT + 1] = new ImageIcon("images"+File.separator+"p2_lf2.gif");
	}

	public void animate(boolean dirChange) {
		if (animCount == 0 || dirChange) {
			animCount = animDelay;
			if (anim == 0)
				anim = 1;
			else
				anim = 0;
			if (direction == Direction.UP) {
				this.setIcon(images[IMG_UP + anim]);
			} else if (direction == Direction.LEFT) {
				this.setIcon(images[IMG_LEFT + anim]);
			} else if (direction == Direction.RIGHT) {
				this.setIcon(images[IMG_RIGHT + anim]);
			} else if (direction == Direction.DOWN) {
				this.setIcon(images[IMG_DOWN + anim]);
			}
		} else {
			animCount--;
		}
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	public void recordPrevLoc() {
		if (!setPrevX)
			prevXLoc = getXLoc();
		if (!setPrevY)
			prevYLoc = getYLoc();
		setPrevX = setPrevY = false;
		if (getXSpeed() == 0)
			lockX = false;
		if (getYSpeed() == 0)
			lockY = false;
		isMoving = false;
	}

	/**
	 * Moves this Player right.
	 */
	public void moveRight() {
		Direction oldDir = direction;
		direction = Direction.RIGHT;
		isMoving = true;
		xSpeed += runAcc;
		if (xSpeed > MAXSPEED)
			xSpeed = MAXSPEED;
		if (oldDir != direction)
			animate(true);
		else
			animate(false);
	}

	/**
	 * Moves this Player left.
	 */
	public void moveLeft() {
		Direction oldDir = direction;
		direction = Direction.LEFT;
		isMoving = true;
		xSpeed -= runAcc;
		if (xSpeed < -MAXSPEED)
			xSpeed = -MAXSPEED;
		if (oldDir != direction)
			animate(true);
		else
			animate(false);
	}

	/**
	 * Moves this Player up.
	 */
	public void moveUp() {
		Direction oldDir = direction;
		direction = Direction.UP;
		isMoving = true;
		ySpeed -= runAcc;
		if (ySpeed < -MAXSPEED)
			ySpeed = -MAXSPEED;
		if (oldDir != direction)
			animate(true);
		else
			animate(false);
	}

	/**
	 * Moves this player down.
	 */
	public void moveDown() {
		Direction oldDir = direction;
		direction = Direction.DOWN;
		isMoving = true;
		ySpeed += runAcc;
		if (ySpeed > MAXSPEED)
			ySpeed = MAXSPEED;
		if (oldDir != direction)
			animate(true);
		else
			animate(false);
	}

	public void brakeY() {
		if (ySpeed > 0) {
			ySpeed -= brakeAcc;
			if (ySpeed < 0)
				ySpeed = 0;
		}
		if (ySpeed < 0) {
			ySpeed += brakeAcc;
			if (ySpeed > 0)
				ySpeed = 0;
		}
	}

	public void brakeX() {
		if (xSpeed > 0) {
			xSpeed -= brakeAcc;
			if (xSpeed < 0)
				xSpeed = 0;
		}
		if (xSpeed < 0) {
			xSpeed += brakeAcc;
			if (xSpeed > 0)
				xSpeed = 0;
		}
	}

	@Override
	public void incrementLocation() {
		xLoc += xSpeed;
		yLoc += ySpeed;
		if (fireTimer > 0)
			fireTimer--;
	}

	@Override
	public void setXSpeed(int speed) {
		xSpeed = speed;
	}

	@Override
	public void setYSpeed(int speed) {
		ySpeed = speed;
	}

	@Override
	public int getXSpeed() {
		return xSpeed;
	}

	@Override
	public int getYSpeed() {
		return ySpeed;
	}

	@Override
	public boolean isMoving() {
		return isMoving;
	}

	public Laser fire() {
		if (fireTimer == 0 && !firePress) {
			firePress = true;
			fireTimer = fireDelay;
			Laser fire = new Laser(direction, isPlayerOne);
			int x = 0, y = 0, prevX = 0, prevY = 0;
			if (direction == Direction.LEFT) {
				x = this.getXLoc() - fire.width;
				prevX = x + fire.width;
				y = prevY = this.getYLoc() + this.height / 2 - fire.height / 2;
			}
			if (direction == Direction.RIGHT) {
				x = this.getXLoc() + this.width;
				prevX = x - fire.width;
				y = prevY = this.getYLoc() + this.height / 2 - fire.height / 2;
			}
			if (direction == Direction.UP) {
				x = prevX = this.getXLoc() + this.width / 2 - fire.width / 2;
				y = this.getYLoc() - fire.height;
				prevY = y + fire.height;
			}
			if (direction == Direction.DOWN) {
				x = prevX = this.getXLoc() + this.width / 2 - fire.width / 2;
				y = this.getYLoc() + this.height;
				prevY = y - fire.height;
			}
			x -= fire.getXSpeed();
			y -= fire.getYSpeed();
			fire.setLoc(x, y);
			fire.setPrevXLoc(prevX);
			fire.setPrevYLoc(prevY);
			fire.setBounds(fire.getXLoc(), fire.getYLoc(), fire.width,
					fire.height);
			return fire;
		}
		return null;
	}

	public void firePressFalse() {
		firePress = false;
	}

	public boolean isLaser() {
		return false;
	}

	public boolean shot(Direction dir, CollidableObject laser) {
		if(((Laser) laser).isPlayerOne == isPlayerOne)
			selfShotCount++;
		
		if (!isPlayerOne)
			ScoreBoard.getMe().p1GotShot();
		else
			ScoreBoard.getMe().p2GotShot();
		int bounce = 30;
		if (dir == Direction.RIGHT)
			setXSpeed(-bounce);
		else if (dir == Direction.LEFT)
			setXSpeed(bounce);
		if (dir == Direction.RIGHT)
			setXSpeed(-bounce);
		else if (dir == Direction.UP)
			setYSpeed(bounce);
		else if (dir == Direction.DOWN)
			setYSpeed(-bounce);
		return false;
	}
}
