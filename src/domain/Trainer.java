package domain;

public class Trainer {

	private int id;

	private Pokemon currentPokemon;

	private BagPack bagPack;

	private Team team;

	public Trainer(Team team, BagPack bagPack) {
		this.team = team;
		this.bagPack = bagPack;
		this.currentPokemon = team.getPokemons().get(0);
	}

    public Trainer() {
    }

    public void changePokemon(int id) {
		if(currentPokemon.getId() != id){
			currentPokemon.setActive(false);
			currentPokemon = team.getPokemon(id);
			currentPokemon.setActive(true);
		}
	}

	public String[] activePokemon(){
		return this.currentPokemon.getInfo();
	}

	public Item getItem(int id) {
		return null;
	}

}
