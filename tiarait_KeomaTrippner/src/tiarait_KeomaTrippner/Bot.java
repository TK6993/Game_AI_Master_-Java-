package tiarait_KeomaTrippner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import lenz.htw.tiarait.net.NetworkClient;

public class Bot {
	
	public BotKind bot;
	
	public Spielfeld feld;
	
	private NetworkClient client;
	
	private int player;
	
	
	
	
	public boolean isAtDestination = true;
	
	private ArrayList<Knoten> openList; 
	private HashSet<Knoten> closeList; 
	private Stack<Knoten> way; 
	private Knoten nextKnoten;
	private int colorToFind;

	
	private Point lastPos;
	private Zeitmesser positonTimer;
	
	
	public Bot(BotKind kind, Spielfeld _feld, NetworkClient _client) {
		this.client = _client;
		this.bot = kind;		
		this.feld = _feld;
		player = this.client.getMyPlayerNumber()+1;
		if(bot == BotKind.NORMAL )System.out.println("Spieler " +player);

		this.openList = new ArrayList<Knoten>(); 
		this.closeList = new HashSet<Knoten>(); 
		this.lastPos = botOnTile(client.getX(player-1, bot.ordinal()), client.getY(player-1, bot.ordinal()));
		positonTimer = new Zeitmesser();
	}
	
	
	public void findWay( Point  z) {
		this.openList = new ArrayList<Knoten>(); 
		this.closeList = new HashSet<Knoten>();
		this.way = new Stack<Knoten>(); 
		this.nextKnoten = null;
		client.setMoveDirection(bot.ordinal(), 0, 0);
		
		Knoten ziel = new Knoten(z.x,z.y,feld);
		Point p = botOnTile(client.getX(player-1, bot.ordinal()), client.getY(player-1, bot.ordinal()));
		Knoten anfang = new Knoten(p.x, p.y, feld, ziel);
		openList.add(anfang);
		if( player==2 )System.out.println(bot.toString() + " "+"Starte suche " + ziel.x +"/"+ziel.y  + " von " + ziel.inhalt );


		while(!openList.isEmpty()  && listHasNode(openList, ziel)==null) {

			Knoten current = Knoten.getCheapestElemetFromList(openList);
			
			closeList.add(current);
			
			addNeighborsToOpenList(current, ziel);
			//if(bot == BotKind.NORMAL&& player==2 && positonTimer.getElapsedTime()>4000 )System.out.println("Suche weg" );

		}

		positonTimer.reset();
		Knoten destination = listHasNode(openList, ziel);
		if( player==2 )System.out.println(bot.toString() + " "+"Ich will von " + anfang.x +"/"+ anfang.y  + " zus " + destination.x+ "/" + destination.y);
		//if(bot == BotKind.NORMAL && player==2)System.out.println("Position " + client.getX(player-1, bot.ordinal()) +"/"+ client.getY(player-1, bot.ordinal()));
		//if(bot == BotKind.NORMAL && player==2)System.out.println("Kosten: " + destination.realCosts);


		colorToFind = destination.inhalt;
		createWay(destination);
	   if( player==2)System.out.println(bot.toString() + " "+"Weg Länge: " + way.size());
		//if(bot == BotKind.NORMAL && player==2 )feld.printAreaWithWay(way);

		nextKnoten = way.pop();
		isAtDestination =false;
		
	}
	
	private void createWay(Knoten d) {
		way.push(d);
		if(d.parent!=null) {
			createWay(d.parent);
			}
		}
		
	



	private void addNeighborsToOpenList(Knoten p, Knoten ziel) {
		
		for (int x = p.x-1; x <	p.x+2; x++) {
			for (int y = p.y-1; y < p.y+2; y++) {
				
				if(Math.abs((x-p.x)+(y-p.y))!=1)continue;

				if(x>=32 || x<0)continue;
				if(y>=32 || y<0)continue;
				if(x==p.x && y==p.y)continue;
				
				if (1000==feld.spielfeld[x][y])continue;
				
				if(listHasNode(openList, new Knoten(x,y,feld))==null)openList.add(new Knoten(x,y,p,this.feld,ziel));
				
				
			}
		}
		
		
	}
	
	public void go() {
		float xdir= 0;
		float ydir= 0;
		float xPos=client.getX(player-1, bot.ordinal());
		float yPos=client.getY(player-1, bot.ordinal());
		Point p = botOnTile(xPos, yPos);
		
		if(!isAtDestination) {
			
			if(player==2 && positonTimer.getElapsedTime()>2000 )System.out.println(bot.toString() + " "+"nächster miniZiel: "+ nextKnoten.x + "/" + nextKnoten.y + " in " + nextKnoten.inhalt);

			if( p.x == nextKnoten.x && p.y == nextKnoten.y) { 
				if(!way.isEmpty()) {
					positonTimer.reset();

					this.nextKnoten = way.pop();
					if( player==2 )System.out.println(bot.toString() + " "+"Bin da: " +xPos +"/"+yPos+" "+ nextKnoten.x+"/"+ nextKnoten.y);
				}
				else {
					isAtDestination=true;
					if( player==2 )System.out.println(bot.toString() + " "+ "Bin angekommen");
				}

			}
			//if(bot == BotKind.NORMAL && player==2 )System.out.println("Richtung " + (nextKnoten.x - xPos) +"/"+(nextKnoten.y - yPos) + "  Position: "+ p.x +"/"+ p.y + "  Kurzziel: "+ nextKnoten.x + "/"+ nextKnoten.y);
			//if(bot == BotKind.NORMAL && player==2 )System.out.println(xPos+"/"+yPos +" -> "+ nextKnoten.x+"/"+ nextKnoten.y);

			xdir = nextKnoten.x - p.x;
			ydir = nextKnoten.y - p.y;
		}
		else if(isAtDestination) {

			Point nextPos = wander(p, this.colorToFind);

			xdir = nextPos.x - p.x;
			ydir = nextPos.y - p.y;
			
		}
		
		
		//isBotStillMoving(p);
		client.setMoveDirection(bot.ordinal(), xdir, ydir);
		
		
	}
	
	
	private Point botOnTile( float xPos, float yPos) {

		Point p = new Point();
		p.x = (int)xPos;
		p.y = (int)yPos;
		 return p;
		
	}
	
	
	public Point wander(Point p, int colorTofind) {
		positonTimer.reset();

		Point pointToGo = new Point(1,1);
		int goodresult= 0;
		
		for (int x = p.x-1; x <	p.x+2; x++) {
			for (int y = p.y-1; y < p.y+2; y++) {
				
				
				if(Math.abs((x-p.x)+(y-p.y))!=1 )continue;
				if(x>=32 || x<0)continue;
				if(y>=32 || y<0)continue;
				if(x==p.x && y==p.y)continue;
				
				if (1000==feld.spielfeld[x][y])continue;
				

				else if (colorTofind==feld.spielfeld[x][y] && goodresult<3) { 
					pointToGo = new Point(x,y);
					goodresult = 3;
				}
				
				else if(player!=feld.spielfeld[x][y] && goodresult<2 ) {
					pointToGo = new Point(x,y);
					goodresult = 2;
				}
				else if(player==feld.spielfeld[x][y] && goodresult<1 )  pointToGo = new Point(x,y);
				
				
			}
		}
			Point toReturn = new Point(pointToGo.x,pointToGo.y);

			return toReturn;

	}
	
	
	private Knoten listHasNode(ArrayList<Knoten> list, Knoten k) {
		for (Knoten knoten : list) {
			if(knoten.x == k.x && knoten.y == k.y)return knoten;
		}
		return null;
	}
	
	
	public Point getNewDestination(ArrayList<Cluster> clusters) {
		Point toReturn =null;
		int clusterColor = this.player;
		int botPriority;
		int wallCounter = 0;
		int index = 0;
		
		if(bot == BotKind.ERASER) botPriority = 1;
		else if(bot == BotKind.NORMAL)botPriority = 2;
		else botPriority = 0;
		
		
		
			while(clusterColor == player) {
				Cluster c = clusters.get(index+botPriority);
				clusterColor = c.clusterContent;
				
				if(clusterColor == 0 && bot == BotKind.ERASER)clusterColor = player;
				if(clusterColor == player)index++;
				
				
				
				if(c.walls > wallCounter && bot == BotKind.BIG) {
					clusterColor = player;
					wallCounter++;
				}
				
			}
			toReturn = clusters.get(index+botPriority).clusterPunkte.get(0);
		
		if(toReturn ==null)toReturn =clusters.get(0).clusterPunkte.get(0);
		return toReturn;
	}
	
	
	private void isBotStillMoving(Point pos) {
		if(pos.x == lastPos.x && pos.y == lastPos.y && positonTimer.getElapsedTime()>5000) {
			System.out.println("Spieler "+ player+ " Bot " + bot.toString() + " steht!" );
			positonTimer.reset();
		}
		else if(pos.x != lastPos.x && pos.y != lastPos.y) {
			lastPos = pos;	
			positonTimer.reset();

		}

	}
	

}

enum BotKind {
	  ERASER,
	  NORMAL,
	  BIG
	}


