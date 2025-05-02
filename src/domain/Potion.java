package domain;

import java.util.ArrayList;

public class Potion extends Item {
	private int healthPoints;
	public Potion(int number, int id, int health){
		super(number,id);
		this.healthPoints = health;
	}
}
