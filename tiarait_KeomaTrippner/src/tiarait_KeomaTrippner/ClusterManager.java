package tiarait_KeomaTrippner;

import java.util.ArrayList;
import java.util.Collections;

import lenz.htw.tiarait.net.NetworkClient;

public class ClusterManager {
	
	ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	Spielfeld feld;
	
	
	public ClusterManager(Spielfeld _feld) {
		this.feld = _feld;
	}
	
	
	
	public ArrayList<Cluster> createClusters(NetworkClient c) {
		
		this.clusters =new ArrayList<Cluster>();
		
		for (int i = 0; i < 4; i++) {
			int xbot = (int) c.getX(i, 1);
			int ybot = (int) c.getY(i, 1);
			
			Cluster base = new Cluster(feld.spielfeld, xbot, ybot);
			clusters.add(base);
			
		}
		
		
		for (int i = 0; i < 4; i++) {
			int xbot = (int) c.getX(i, 2);
			int ybot = (int) c.getY(i, 2);
			
			Cluster base = new Cluster(feld.spielfeld, xbot, ybot);
			clusters.add(base);
			
		}
		
		for (int i = 0; i < 4; i++) {
			Cluster base = new Cluster(feld.spielfeld);
			clusters.add(base);
		}
		
	
		
		return this.clusters;

		
	}
	
	public Cluster getBiggestCluster() {
		Collections.sort(clusters,Collections.reverseOrder());
		Cluster toReturn = clusters.get(0);
		return toReturn;
		
	}
	
	
	
	

}
