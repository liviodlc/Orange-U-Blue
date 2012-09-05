package model;

import java.awt.Rectangle;
import java.util.Random;

import javax.swing.JLabel;

public abstract class CollidableObject extends JLabel {

	private long identification;
	protected int xLoc;
	protected int yLoc;
	protected int xOffset;
	protected int yOffset;
	protected int prevXLoc;
	protected int prevYLoc;
	protected boolean setPrevX;
	protected boolean setPrevY;
	protected boolean lockX = false;
	protected boolean lockY = false;
	protected int height = 20;
	protected int width = 20;

	public CollidableObject() {
		Random rand = new Random();
		// Generates a random number that will act as this istance's unique ID.
		identification = ((System.currentTimeMillis() * (rand.nextInt(42) + 1)) / (rand
				.nextInt(50) + 1)) + 12;
	}

	/**
	 * Returns the unique identification of this Player instance. This is
	 * necessary due to unfortunate side effects of Java's Object I/O.
	 * 
	 * @return The unique ID of this instance of Player.
	 */
	public long getID() {
		return identification;
	}

	/**
	 * Clears the identification of this Player. A player with a cleared
	 * identification notifies the clients that the player is gone and needs to
	 * be removed.
	 */
	public void clearID() {
		this.identification = 0;
	}

	/**
	 * Returns the current x location of the mover.
	 * 
	 * @return The current x location of the mover.
	 */
	public int getXLoc() {
		return xLoc;
	}
	
	public int getDrawX(){
		return xLoc - xOffset;
	}

	/**
	 * Returns the current y location of the mover.
	 * 
	 * @return The current y location of the mover.
	 */
	public int getYLoc() {
		return yLoc;
	}
	
	public int getDrawY(){
		return yLoc - yOffset;
	}

	public int getPrevXLoc() {
		return prevXLoc;
	}

	public int getPrevYLoc() {
		return prevYLoc;
	}

	/**
	 * Sets the x location of this mover.
	 * 
	 * @param newXLoc
	 *            The new x location of this Player.
	 */
	public void setXLoc(int newXLoc) {
		xLoc = newXLoc;
	}

	/**
	 * Sets the y location of this mover.
	 * 
	 * @param newXLoc
	 *            The new y location of this Player..
	 */
	public void setYLoc(int newYLoc) {
		yLoc = newYLoc;
	}

	/**
	 * Sets the x and y location of this Player.
	 * 
	 * @param newXLoc
	 *            The new x location of this Player.
	 * @param newYLoc
	 *            The new y location of this Player.
	 */
	public void setLoc(int newXLoc, int newYLoc) {
		xLoc = newXLoc;
		yLoc = newYLoc;
	}

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
	}

	public boolean hasMovedUp() {
		if (prevYLoc > yLoc)
			return true;
		else
			return false;
	}

	public boolean hasMovedDown() {
		if (prevYLoc < yLoc)
			return true;
		else
			return false;
	}

	public boolean hasMovedRight() {
		if (prevXLoc < xLoc)
			return true;
		else
			return false;
	}

	public boolean hasMovedLeft() {
		if (prevXLoc > xLoc)
			return true;
		else
			return false;
	}

	public Rectangle getRect() {
		return new Rectangle(xLoc, yLoc, width, height);
	}

	public int getHitWidth() {
		return width;
	}

	public int getHitHeight() {
		return height;
	}

	public boolean collidesWith(Rectangle other) {
		return getRect().intersects(other);
	}

	public abstract void setYSpeed(int speed);

	public abstract void setXSpeed(int speed);

	public abstract int getYSpeed();

	public abstract int getXSpeed();

	public abstract boolean isMoving();
	
	public abstract boolean shot(Direction dir, CollidableObject laser);

	public void lockX() {
		lockX = true;
	}

	public void lockY() {
		lockY = true;
	}

	public void setPrevXLoc(int mag) {
		prevXLoc = mag;
		setPrevX = true;
	}

	public void setPrevYLoc(int mag) {
		prevYLoc = mag;
		setPrevY = true;
	}
	
	public abstract boolean isLaser();
	
	public void incrementLocation() {
		xLoc += getXSpeed();
		yLoc += getYSpeed();
	}
}
