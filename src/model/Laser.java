package model;

import java.io.File;

import javax.swing.ImageIcon;

public class Laser extends CollidableObject {

	private int speed = 10;
	private Direction direction;
	public boolean isPlayerOne;

	public Laser(Direction dir, boolean isPlayerOne) {
		this.isPlayerOne = isPlayerOne;
		direction = dir;
		if(direction == Direction.LEFT || direction == Direction.UP){
			speed = -speed;
		}
		String nm;
		if(isPlayerOne)
			nm="1";
		else
			nm="2";
		this.setIcon(new ImageIcon("images"+File.separator+"p"+nm+"_bullet.gif"));
	}

	@Override
	public int getXSpeed() {
		if(direction== Direction.RIGHT || direction== Direction.LEFT)
			return speed;
		else
			return 0;
	}

	@Override
	public int getYSpeed() {
		if(direction== Direction.UP || direction== Direction.DOWN)
			return speed;
		else
			return 0;
	}

	@Override
	public boolean isMoving() {
		return true;
	}

	@Override
	public void setXSpeed(int speed) {
		// nothing
	}

	@Override
	public void setYSpeed(int speed) {
		// nothing

	}

	@Override
	public boolean isLaser() {
		return true;
	}
	
	public boolean shot(Direction dir, CollidableObject obj){
		return true;
	}
}
