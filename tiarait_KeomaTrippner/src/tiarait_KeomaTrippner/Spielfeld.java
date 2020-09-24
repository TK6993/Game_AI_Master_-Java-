package tiarait_KeomaTrippner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;

public class Spielfeld {
	public static int PLAYER;
	
	public int [ ] [ ] spielfeld; 
	public NetworkClient client ;
	
	public int playerNumber;
	
	
	
	
	
	
	//Jeder Spieler hat Kontrolle über drei Bots (nummeriert 0, 1, 2)
	// P = 0
	// B = 1
	// R = 2
	
	
	public Spielfeld(NetworkClient _client) {
		this.client = _client;
		this.spielfeld = new int[32][32];
		
		this.playerNumber = client.getMyPlayerNumber()+1;
		
		
		
		spielfeldAnalyse();
		//printArea();
		
	}
	
	
	private void spielfeldAnalyse() {
		
		for (int x = 0; x < spielfeld.length; x++) {
			for (int y = 0; y < spielfeld[x].length; y++) {
				if(client.isWall(x, y)) spielfeld[x][y]=1000;//true wenn bei Koordinate ein Hindernis steht

			}
		}
		
		
	}
	
	public void updateSpielfeld( ColorChange cc) {
		//cc = this.client.getNextColorChange();
		// cc in eigene Struktur einarbeiten
		//z.B. brett[cc.x][cc.y] = cc.newColor;
		//cc.newColor; //0 = leer, 1-4 = spieler
		spielfeld[cc.x][cc.y] = cc.newColor;
		
		
	}
	
	public int getGuessedCosts( Knoten ziel, Knoten pos) {
		
		int yAbstand = ziel.y -pos.y;
		int xAbstand = ziel.x-pos.x;
		int yVorzeichen=0;
		int xVorzeichen=0;
		
		if(yAbstand!=0)  yVorzeichen = yAbstand/Math.abs(yAbstand);
		if(xAbstand!=0)  xVorzeichen = xAbstand/Math.abs(xAbstand);
		
		int tempX = pos.x;
		int tempY = pos.y;
		int guessedCosts = 0;
		
		 while(tempX != ziel.x && tempY != ziel.y) {
			 if(tempX != ziel.x)tempX += xVorzeichen;
			 if(tempY != ziel.y)tempY += yVorzeichen;
			  

			guessedCosts += getCostsOfField(tempX, tempY); 
		 }


		 return guessedCosts;
		
		
	}


	
	
	public int getCostsOfField(int x, int y) {
		if(spielfeld[x][y] == 1000)return 600;
		else if(spielfeld[x][y] == playerNumber)return 300;
		else if(spielfeld[x][y] == 0) return 7;
		else return 1;
		
	}
	
	
	
	
	public void printAreaWithWay(Stack<Knoten> way) {
		
		
			int[][] temp = new int[32][32];
			for (int y = 0; y < temp.length; y++) {
				for (int x = 0; x < temp.length; x++) {
					temp[x][y] = spielfeld[x][y];
				}
			}
			
			for (Knoten k : way) {
				temp[k.x][k.y]= 8;
			}
			
			for (int y = 0; y < temp.length; y++) {
				for (int x = 0; x < temp.length; x++) {
					
					if(temp[x][31-y]==1000)System.out.print("1 ");
					else if(temp[x][31-y]==8)System.out.print("X ");
					else if(temp[x][31-y]==playerNumber)System.out.print(playerNumber+" ");

					else System.out.print("0 ");
	
				}
			System.out.print(" ");
			}
			
		}
	
	
	

}
