package sarg_KeomaTrippner;
import java.io.File;



import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.Point;


import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import lenz.htw.sarg.Move;
import lenz.htw.sarg.Server;
import lenz.htw.sarg.net.NetworkClient;

public class Client extends Thread {
	
	public int points = 0;
	private int activePlayer = -1;
	public int playerID;
	public String name;
	public int playerInTheGame = 3;
	public Spielbrett aktuellesSpielbrett;
	public Bewertungsfunction bewertung;
	
	
	public Client(int playerID, Bewertungsfunction b) {
		aktuellesSpielbrett = new Spielbrett();
		aktuellesSpielbrett.spielerSteine[0] = getStartStones(0);
		aktuellesSpielbrett.spielerSteine[1] = getStartStones(1);
		aktuellesSpielbrett.spielerSteine[2] = getStartStones(2);

		this.name ="player"; 
		bewertung = b;
		this.playerID = playerID;

	}
	
	public Client(String name) {
		aktuellesSpielbrett = new Spielbrett();
		aktuellesSpielbrett.spielerSteine[0] = getStartStones(0);
		aktuellesSpielbrett.spielerSteine[1] = getStartStones(1);
		aktuellesSpielbrett.spielerSteine[2] = getStartStones(2);
		this.name = name;
		//this.playerID = playerID;
		bewertung = new Bewertungsfunction();

	}
	
	
	private int whosTurnIsIt() {
		activePlayer++;

		activePlayer = activePlayer % playerInTheGame;
		//System.out.println("activer Spieler " + activePlayer);

		return activePlayer;
	}
	
	
	private Point makeRandomMove(Spielbrett spielbrett, int playerID) {

		int max = spielbrett.spielerSteine[playerID].size();
		
		if(spielbrett.spielerSteine[playerID].isEmpty())playerInTheGame--;
		//aktuellesSpielbrett = spielbrett.makeMove(spielbrett, p.x, p.y, whosTurnIsIt());
		int randomNum = ThreadLocalRandom.current().nextInt(0, max );

		Point p = (Point) spielbrett.spielerSteine[playerID].get(randomNum);
		System.out.println("Player Zug: " + p.x+ "/"+ p.y);
		
		
		
		return p;
	}
	
	
	private Point getBestMove(Spielbrett s, int playerID) {
		Point bestPoint = null;
		float bestScore = -100000000;
		
		for (Object p : s.spielerSteine[playerID]) {
			Point point = (Point)p;
			Spielbrett sp =  new Spielbrett(s.spielfeld,s.playerPoints,s.spielerSteine);

			sp = Spielbrett.makeMove(sp, point.x, point.y, playerID,false);
			float score = bewertung.bewerteSpielbrett(sp, playerID);
			if(score > bestScore) {
				bestScore = score;
				bestPoint = point;
			}
		}
		if(bestPoint == null) System.out.println("Fehler:Bester Zug war null");
		System.out.println("Player Zug: " + bestPoint.x+ "/"+ bestPoint.y);


		return bestPoint;
	}
	
	
	private ArrayList<Point> getStartStones(int playerId) {
		
		ArrayList<Point> temp = new ArrayList<Point>();
		switch (playerId) {
		case 1:
			temp.add(new Point(0,4));
			temp.add(new Point(1,5));
			temp.add(new Point(2,6));
			temp.add(new Point(3,7));
			temp.add(new Point(4,8)); 
			break;
		case 2:
			temp.add(new Point(8,4));
			temp.add(new Point(8,5));
			temp.add(new Point(8,6));
			temp.add(new Point(8,7));
			temp.add(new Point(8,8));
			break;
		case 0:
			temp.add(new Point(0,0));
			temp.add(new Point(1,0));
			temp.add(new Point(2,0));
			temp.add(new Point(3,0));
			temp.add(new Point(4,0));
			break;	
			
		default:
			System.out.print("Fehler beim Starttsteine hinzufügen");
			break;
		}
		return temp;
		
	}

	
	public void run() {
		NetworkClient nc;
		BufferedImage image = null;
		try {
			image =  ImageIO.read(new File("C:/Users/keoma/Documents/AIForGames/sarg_KeomaTrippner/src/CrownImage.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        nc = new NetworkClient("87.123.158.39",this.name, image);
		this.playerID = nc.getMyPlayerNumber();

        
        while (!aktuellesSpielbrett.gameEnded) {
            Move receiveMove = nc.receiveMove();
            if(receiveMove != null && (receiveMove.x  < 0 || receiveMove.y < 0)) {
            	aktuellesSpielbrett.gameEnded =true;
            	break;
            }
            
            
            else if (receiveMove == null) {
        		
        		float besterWert = bewertung.minimax(aktuellesSpielbrett, bewertung.suchTiefe, -Float.MAX_VALUE, Float.MAX_VALUE, nc.getMyPlayerNumber(), 0);
            	 Point p  = bewertung.nextTurn;
            	 receiveMove = new Move(p.x,p.y);
                nc.sendMove(receiveMove);
            } 
            else {

              aktuellesSpielbrett =  Spielbrett.makeMove(aktuellesSpielbrett, receiveMove.x, receiveMove.y, whosTurnIsIt(),true);

            }
        }

		
	}
	
	
	
	
    public static void main(String[] args) throws IOException {
    	boolean isTraining = false;
    	
    	ArrayList<Bewertungsfunction> generationen =  new ArrayList<Bewertungsfunction>();
    	generationen.add(new Bewertungsfunction(0.8f, -0.3f, -0.3f, 0.5f, -0.5f, -0.5f, 0.1f, -0.2f, -0.2f, 0.9f,1f));
    	generationen.add(new Bewertungsfunction(-0.15f, 0.5f, 0.5f, -0.1f, 0.5f, 0.5f, -0.25f, 0.25f, 0.25f, -0.5f, -0.5f));
    	generationen.add(new Bewertungsfunction(1f, 1f, 1f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 1f,1f));
    	generationen.add(new Bewertungsfunction(5.9112203f, -0.1245475f, -0.07932795f, 0.29684347f, -0.8777587f, -0.8505677f, 0.14058018f, -0.34794688f, 0.315674f, 0.3761172f, 1.5598118f));
    	generationen.add(new Bewertungsfunction(0.38231596f, 0.036521025f, 0.1547663f, 0.31126916f, -0.34162012f, -0.45807764f, 0.022619272f, -0.05851102f, 0.16019465f, 0.20027098f, 0.59025145f));
    	generationen.add(new Bewertungsfunction(0.7f, -1f, -0.8f, 0.7f, -0.2f, -0.8f, 0.3f, -0.2f, 0.2f, 1f,1f));
    	generationen.add(new Bewertungsfunction(0.7f, -1f, -0.8f, 0.7f, -0.2f, -0.8f, 0.3f, -0.2f, 0.2f, 1f,1f));
    	
    	for (Bewertungsfunction b : generationen) {
    		float sum = b.getCoefficientSum();
			b.norm(sum);
		}
    	
    	if( isTraining ) {
    		int gens = 1000;
    		int gamesPerGen = 15;
    		int genSize = 7;


    		
    		for (int i = 0; i < gens; i++) {


				for (int g = 0; g < gamesPerGen; g++) {
				 int[] players= new int[3];
				 players[0] = ThreadLocalRandom.current().nextInt(0, genSize );
				 players[1] = ThreadLocalRandom.current().nextInt(0, genSize );
				 players[2]= ThreadLocalRandom.current().nextInt(0, genSize );
				 
				System.out.println("SPIEL: " + g + " IN GENERATION: " + i); 
    		
	            Client one = new Client("Bob");
	            one.bewertung = generationen.get(players[0]);
	            one.start();
	            
	            Client two = new Client("Paul");
	            one.bewertung = generationen.get(players[1]);
	            two.start();
	            
	            Client three = new Client("Julia");
	            one.bewertung = generationen.get(players[2]);
	            three.start();
	
	            int winner = Server.runOnceAndReturnTheWinner(2);
	            if(winner>=0) generationen.get(players[winner-1]).siege++;
	            
	           
				}
				Collections.sort(generationen,Collections.reverseOrder());
				Bewertungsfunction[] winners = {generationen.get(0),generationen.get(1), generationen.get(2)};
				generationen =  Bewertungsfunction.getNextGeneration(winners);
    		}

    		for (Bewertungsfunction b : generationen) {
				
    			float[] finalWeights = b.weights;
    			System.out.println("FinalParameter: " + finalWeights[0] +"f, "+ finalWeights[1] +"f, "+ finalWeights[2] +"f, "+ finalWeights[3] +"f, "+ finalWeights[4] +"f, "+ finalWeights[5] +"f, "+ finalWeights[6] +"f, "+ finalWeights[7] +"f, "+ finalWeights[8] +"f, "+ finalWeights[9] +"f, "+ finalWeights[10] +"f");
			}

           
           
    	}
    	
  
    	else {
 		
    		
	            Client one = new Client("Team Bob");
	            one.bewertung = generationen.get(3);
	            one.start();
	 
       }
    }



}