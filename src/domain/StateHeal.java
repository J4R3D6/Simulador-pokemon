package domain;

public class StateHeal extends Attack {
	private int health;

	public StateHeal(int idInside, String[] infoAttack,String [] infoStatus)throws POOBkemonException{
		super(idInside,infoAttack);
	}

	public String apllyEffect(Pokemon pokemon){
		if(pokemon.currentHealth + health > pokemon.maxHealth){
			pokemon.currentHealth = pokemon.maxHealth;
			System.out.println(pokemon.getName() + "curado");
			return pokemon.getName() + " curado";
		}else{
			pokemon.currentHealth += health;
			System.out.println(pokemon.getName() + " curado " + health);
			return pokemon.getName() + "curado " + health;
		}
	}
}
