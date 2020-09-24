package tiarait_KeomaTrippner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Cluster  implements Comparable<Cluster> {
	 
	public static int clusterSize = 1;
	ArrayList<Point> clusterPunkte;
	public int clusterContent;
	public int[][] feld;
	
	public int walls=0;
	
	private int createTrys= 0;

	

	private void grow( ArrayList<Point> pointToGrowFrom) {
		ArrayList<Point> newClusterPoints = new ArrayList<Point>();
		for (Point point : pointToGrowFrom) {
			newClusterPoints = checkForCluster(point);
			if(!newClusterPoints.isEmpty()) {
				this.clusterPunkte.addAll(newClusterPoints);
				grow(newClusterPoints);
			}
			newClusterPoints.clear();
		}
	}
	
	
	
	public Cluster (int[][] spielfeld) {
		
		this.clusterPunkte = new ArrayList<Point>();
		this.feld = spielfeld;
		
		while (this.clusterPunkte.isEmpty()) {
		
		int randomOne = ThreadLocalRandom.current().nextInt(5, feld.length-5 );
		int randomTwo = ThreadLocalRandom.current().nextInt(5, feld[randomOne].length-5 );
		if( feld[randomOne][randomTwo]== 1000) continue;


		this.clusterContent  =feld[randomOne][randomTwo];
		
		clusterPunkte = checkForCluster(new Point (randomOne,randomTwo));
		
		}
		
		ArrayList<Point> clusterPointCopy = (ArrayList<Point>) this.clusterPunkte.clone();
		grow(clusterPointCopy);
		// TODO
	}
	
	
	public Cluster (int[][] spielfeld, int x, int y) {
		
		this.clusterPunkte = new ArrayList<Point>();
		this.feld = spielfeld;
		
		if(feld[x][y]!= 0 && feld[x][y]!= 1000) {
		this.clusterContent =feld[x][y];
		clusterPunkte = checkForCluster(new Point (x,y));
		}
		
		if(this.clusterPunkte.isEmpty()) {
			while (this.clusterPunkte.isEmpty() && createTrys<5) {
			createTrys++;
				
			int randomOne = ThreadLocalRandom.current().nextInt(5, feld.length-5 );
			int randomTwo = ThreadLocalRandom.current().nextInt(5, feld[randomOne].length-5);
			
			if( feld[randomOne][randomTwo]== 1000) continue;
	
			this.clusterContent =feld[randomOne][randomTwo];
			
			this.clusterPunkte = checkForCluster(new Point (randomOne,randomTwo));
			
			}
		}
		
		ArrayList<Point> clusterPointCopy = (ArrayList<Point>) this.clusterPunkte.clone();
		grow(clusterPointCopy);

	}	
	
	
	public  ArrayList<Point> checkForCluster(Point p){
		
		ArrayList<Point> clusterPoints = new ArrayList<Point>();
		
		for (int x = p.x-1; x <	p.x+2; x++) {
			for (int y = p.y-1; y < p.y+2; y++) {
				
				if(x>=32 || x<0)continue;
				if(y>=32 || y<0)continue;
				if(x==p.x && y==p.y)continue;
				
				if(isPointRightForCluster(x, y))clusterPoints.add(new Point(x,y));
					
			}
		}
		if(clusterPoints.size()>= clusterSize)return clusterPoints;
		else return new ArrayList<Point>();
	}
	
	
	
	private boolean isPointRightForCluster(int x, int y) {
		if(this.feld[x][y]== this.clusterContent) {
			for (Point point : clusterPunkte) {
				if(point.x==x && point.y==y)return false;
			}
			return true;
		}
		else if(this.feld[x][y]== 1000) walls++;
		return false;
	}

	
	
	@Override
	public int compareTo(Cluster c) {
		return  Integer.compare(this.clusterPunkte.size(), c.clusterPunkte.size());
	}
	
	
	
	public static Cluster getClutsterWithLessWalls(ArrayList<Cluster> c) {
		int w = 1000;
		Cluster r= null;
		for (Cluster cluster : c) {
			if(cluster.walls< w) {
				r = cluster;
				w = cluster.walls;
			}
		}
		
		return r;
	}
	
	
}
