package sarg_KeomaTrippner;

import java.awt.Point;
import java.io.ObjectInputStream.GetField;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import javax.swing.DebugGraphics;


public class Spielbrett {
	
	
	public int [ ] [ ] spielfeld; 
	
	public float evaluation;
	public boolean gameEnded= false;


	 public ArrayList[] spielerSteine;
	public int [] playerPoints;
	 int[] stepCounters;
	
	
	 public Spielbrett(){
		 playerPoints =  new int[3];
		 stepCounters = new int[3];
		 spielfeld = new int[9][9];
		 spielerSteine = new ArrayList[3];
		 spielerSteine[0]= new ArrayList<Point>();
		 spielerSteine[1]=  new ArrayList<Point>();
		 spielerSteine[2]= new ArrayList<Point>();
	
		 
		for (int x = 0; x < spielfeld.length; x++) {
			for (int y = 0; y < spielfeld[x].length; y++) {
				spielfeld[x][y] = -1;
				if(y == 0)spielfeld[x][y] = 0;
				else if(x< 5 && y-x == 4) spielfeld[x][y] = 1;
				else if(x == 8)  spielfeld[x][y] = 2;
				
			}
		}
	

	 }
	 
	 public Spielbrett(int [ ] [ ] oldPositions, int[] oldPlayerPoints, ArrayList[] oldSpielsteine) {
		 playerPoints =  oldPlayerPoints.clone();
		 stepCounters = new int[3];
		 //spielfeld = oldPositions.clone();
		 
		 spielfeld = new int[oldPositions.length][];
		    for (int r = 0; r < oldPositions.length; r++) {
		        spielfeld[r] = oldPositions[r].clone();
		    }
		 
		 
		 spielerSteine = oldSpielsteine.clone();
	
	
	
		
	}
	 
	 public void setPosition(int x, int y, int zahl) {
		 spielfeld[x][y]= zahl; 
	 }
	 
	 public int getPosition(Point p) {
		return spielfeld[p.x][p.y];
	 }
	 
	 public static boolean validatePosition(int x, int y) {
		 if(x < 0 || y < 0)return false;
		 if(x > 8 || y > 8)return false;
		 if(y-x > 4) return false;
		 if(y-x < -4) return false;

		 
		 return true;
	 }
	 
	 public static Spielbrett makeMove(Spielbrett aktuell, int xPos, int yPos, int playerId, boolean isRealMove) {

		 //if(isRealMove)System.out.println("Input: " + xPos + "/"+ yPos);		
		 Spielbrett temp = aktuell;
		 temp.setPosition(xPos, yPos, -1);
		 temp.spielerSteine[playerId] = temp.UpdateArrayListAndRemove(temp.spielerSteine[playerId], new Point(xPos,yPos));
		 temp.stepCounters[playerId] =0;
		// if(temp.spielerSteine[playerId].size() <=0 && isRealMove)System.out.println("NULL STEINE" );
		 
		 boolean rightMoveWasJump = false;
		 boolean leftMoveWasJump = false;
		 boolean rightMadePoint = false;
		 boolean leftMadePoint = false;


		 Point modMove1 = temp.getModifications(xPos, yPos, playerId, true);
		 Point modMove2 = temp.getModifications(xPos, yPos, playerId, false);
		 

		 if(!validatePosition(modMove1.x, modMove1.y)) {
			 temp.playerPoints[playerId]++;
			 
			 rightMadePoint = true;
			 if(temp.playerPoints[playerId] >= 5)temp.gameEnded = true;
			// if(isRealMove)System.out.println("Spieler " + playerId + " hat einen Punkt gemacht! Punkte: " + temp.playerPoints[playerId] );

			 } 
		 else {
				 if(temp.getPosition(modMove1)>=0) {
					 //if(isRealMove)System.out.println("Spieler " + playerId + " springt über " + modMove1.x +"/"+modMove1.y + " der Wert ist: " + temp.getPosition(modMove1));
						temp.stepCounters[playerId]++;
						temp.spielerSteine[temp.getPosition(modMove1)] = temp.UpdateArrayListAndRemove(temp.spielerSteine[temp.getPosition(modMove1)], modMove1);
					temp = jump(temp, modMove1.x, modMove1.y,true, playerId,isRealMove);

					 rightMoveWasJump = true;
				 }
			 }
		 
		 if(!validatePosition(modMove2.x, modMove2.y)) {
			 temp.playerPoints[playerId]++;

			 if(temp.playerPoints[playerId] >= 5)temp.gameEnded = true;
			 leftMadePoint = true;
			 //if(isRealMove)System.out.println("Spieler " + playerId + " hat einen Punkt gemacht! Punkte: " + temp.playerPoints[playerId] );

			 }
		 else {
			 if(temp.getPosition(modMove2)>=0) {
				 //if(isRealMove)System.out.println("Spieler " + playerId + " springt über " + modMove2.x +"/"+modMove2.y + " der Wert ist: " + temp.getPosition(modMove2));
				temp.stepCounters[playerId]++;
				temp.spielerSteine[temp.getPosition(modMove2)] = temp.UpdateArrayListAndRemove(temp.spielerSteine[temp.getPosition(modMove2)], modMove2);
				temp =  jump(temp, modMove2.x, modMove2.y,false, playerId,isRealMove);
				leftMoveWasJump = true;
			 }
		 }

		if(!rightMoveWasJump && !rightMadePoint) {
			temp.setPosition(modMove1.x, modMove1.y, playerId); 
			temp.spielerSteine[playerId].add(new Point(modMove1.x,modMove1.y));
			//if(isRealMove)System.out.println("Feld gesetzt: " +modMove1.x + " " + modMove1.y + " auf " + playerId);
		}
		
		if(!leftMoveWasJump && !leftMadePoint) {
			temp.setPosition(modMove2.x, modMove2.y, playerId); 
			temp.spielerSteine[playerId].add(new Point(modMove2.x,modMove2.y));
			//if(isRealMove)System.out.println("Feld gesetzt: " +modMove2.x + " " + modMove2.y + " auf " + playerId);
		}
	
		return temp;
	 }

	 
	 private Point getModifications(int xPos, int yPos, int playerId, boolean direction) {
		 Point modifications = new Point(-1,-1);
		 
		 switch (playerId) {
		   case 1:
			if(direction) {
				modifications.x = xPos+1;
				modifications.y = yPos;
			}
			else {
				modifications.x = xPos;
				modifications.y = yPos-1;
			}
			break;
			
		   case 2:
			 if(direction) {
				 modifications.x = xPos-1;
				 modifications.y = yPos-1;
			 }
			 else {
				 modifications.x = xPos-1;
				 modifications.y = yPos;	
			 }
			 break;
			 
		   default:
			   if(direction) {
				   modifications.x = xPos;
				   modifications.y= yPos+1;
			   }
			   else {
				   modifications.x = xPos+1;
				   modifications.y = yPos+1;
			   }
			  break;
		}
		 return modifications;
	 }
	 
	 
	 public static Spielbrett jump(Spielbrett aktuell, int xPos, int yPos, boolean direction,  int playerId, boolean isRealMove) {
		// if(isRealMove)System.out.println("Jump Over: " + xPos + "/"+ yPos);
		 Spielbrett temp = aktuell;
		 temp.setPosition(xPos, yPos, -1);
		 temp.spielerSteine[playerId] = temp.UpdateArrayListAndRemove(temp.spielerSteine[playerId], new Point(xPos,yPos));

		 //Aus der SteineListe vom Besitzer wird nach der Methode entfernt.
 
		 boolean anotherJump = false;
		 boolean madePoint = false;

		 
		 Point jumpPos = temp.getModifications(xPos, yPos, playerId, direction);
		 
		 if(!validatePosition(jumpPos.x, jumpPos.y)) {
			 temp.playerPoints[playerId]++;
			 if(temp.playerPoints[playerId] >= 5)temp.gameEnded = true;
			 madePoint = true;
			// if(isRealMove)System.out.println("Spieler " + playerId + " hat einen Punkt gemacht! Punkte: " + temp.playerPoints[playerId] );

			 } 
		 else {
				 if(temp.getPosition(jumpPos)>=0) {
					// if(isRealMove)System.out.println("Spieler " + playerId + " springt über " + jumpPos.x +"/"+jumpPos.y + " der Wert ist: " + temp.getPosition(jumpPos));
						temp.stepCounters[playerId]++;
						temp.spielerSteine[temp.getPosition(jumpPos)] = temp.UpdateArrayListAndRemove(temp.spielerSteine[temp.getPosition(jumpPos)], jumpPos);
					temp = jump(temp, jumpPos.x, jumpPos.y,direction, playerId, isRealMove);

					anotherJump = true;
				 }

			 }


		if(!anotherJump && !madePoint) {
			temp.setPosition(jumpPos.x, jumpPos.y, playerId); 
			temp.spielerSteine[playerId].add(new Point(jumpPos.x,jumpPos.y));
		//	if(isRealMove)System.out.println("Feld gesetzt: " +jumpPos.x + " " + jumpPos.y + " auf " + playerId);
		}
	
		return temp;
	 }
	 
	 public ArrayList<Point> UpdateArrayListAndRemove( ArrayList<Point> list, Point toRemove ) {
		 ArrayList<Point> newList = new ArrayList();
		 for (Point point : list) {
			if(point.x == toRemove.x && point.y == toRemove.y)continue;
			if(point != null) newList.add(point);
		}
		return newList;
	 }

	
	

}
