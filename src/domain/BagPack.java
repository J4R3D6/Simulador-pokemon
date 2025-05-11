package domain;

import java.util.ArrayList;

public class BagPack {
	private ArrayList<Item> items;

	public BagPack(ArrayList<Item> items){
		this.items = new ArrayList<Item>(items);
	}

	private Item[] showItems(){
		return null;
	}

	public Item getItem(String itemName) {
		Item item = null;
		for(Item i: items){
			System.out.println(i.getName());
			if(i.getName().equals(itemName)) item = i;
		}
		return item;
	}
}
