package tiarait_KeomaTrippner;

import java.util.ArrayList;

public class Knoten {
	
	
	
	
	public int x;
	public int y;
	
	public int inhalt;
	
	public boolean searched = false;
	
	public int gesamtCosts = 0;
	private int geschatztCosts = 0;
	public int realCosts = 0;
	
	
	public Knoten parent;
	
	public Knoten(int x, int y, Knoten parent, Spielfeld feld, Knoten ziel) {
		this.x =x;
		this.y = y;
		
		this.parent = parent;
		this.inhalt = feld.spielfeld[x][y];
		this.realCosts = parent.realCosts + feld.getCostsOfField(x, y);
		this.geschatztCosts = feld.getGuessedCosts(ziel, this);

		this.gesamtCosts =  realCosts + geschatztCosts;
		
	}
	
	public Knoten(int x, int y, Spielfeld feld) {
		this.x =x;
		this.y = y;
		
		this.inhalt = feld.spielfeld[x][y];

		this.realCosts = feld.getCostsOfField(x, y);
		this.parent = null;
		this.geschatztCosts = 0;
		this.gesamtCosts =  0;
		
	}
	
	
	//Konstruktor nur amAnfang einer Wegesuche verwenden ,da der erste Knoten keinen parent hat.
	public Knoten(int x, int y, Spielfeld feld, Knoten ziel) {
		this.x =x;
		this.y = y;
		
		this.inhalt = feld.spielfeld[x][y];
		this.realCosts = 0;
		this.parent = null;
		this.geschatztCosts = feld.getGuessedCosts(ziel, this);
		this.gesamtCosts =  realCosts + geschatztCosts;
		
	}
	
	
	
	public static Knoten getCheapestElemetFromList(ArrayList<Knoten> list) {
	
		int lowestCosts = 999999999;
		int index =-1;
		
		for (int k =0; k< list.size(); k++) {
			if(list.get(k).gesamtCosts < lowestCosts) {
				lowestCosts = list.get(k).gesamtCosts;
				index = k;
			}
		}
		return list.remove(index);
	}

}
