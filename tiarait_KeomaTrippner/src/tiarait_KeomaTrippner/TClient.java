package tiarait_KeomaTrippner;

import java.awt.Point;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import lenz.htw.tiarait.ColorChange;
import lenz.htw.tiarait.net.NetworkClient;

public class TClient {

	
	public String name;
	public boolean started = false;
	
	public TClient(String name) {
		this.name = name;
		System.out.println(name);
		 

		NetworkClient networkClient = new NetworkClient("87.123.152.188", name);
		int player = networkClient.getMyPlayerNumber(); // 0-3 (ACHTUNG! andere Nummerierung als beim ColorChange)
		//int botNr = 0;
		

		Spielfeld feld = new Spielfeld(networkClient);
		Zeitmesser zeit = new Zeitmesser();
		ClusterManager  clusterM = new ClusterManager(feld);
		
		Bot eraser = new Bot(BotKind.ERASER,feld,networkClient);
		Bot normal = new Bot(BotKind.NORMAL,feld,networkClient);
		Bot big = new Bot(BotKind.BIG,feld,networkClient);
		
		
		zeit.reset();
		clusterM.createClusters(networkClient);

		Collections.sort(clusterM.clusters,Collections.reverseOrder());
		
		//eraser.findWay(eraser.getNewDestination(clusterM.clusters));
		//normal.findWay(normal.getNewDestination(clusterM.clusters));
		//big.findWay(big.getNewDestination(clusterM.clusters));


		
		while (networkClient.isAlive()) {
			

			ColorChange cc;
			while ((cc = networkClient.getNextColorChange()) != null) {
				if(!started) started = true;
				feld.updateSpielfeld(cc);
			}
			
			
			if(zeit.getElapsedTime()>11000 && started) {
				zeit.reset();
				clusterM.createClusters(networkClient);
		
				Collections.sort(clusterM.clusters,Collections.reverseOrder());
				
				eraser.findWay(eraser.getNewDestination(clusterM.clusters));
				normal.findWay(normal.getNewDestination(clusterM.clusters));
				big.findWay(big.getNewDestination(clusterM.clusters));	
				
			}
			
		
				eraser.go();
				normal.go();
				big.go();
			
			
		
		}
		System.out.println(name + "is dead");

		
		
	}
	
	
	
	
	 public static void main(String[] args)  {
			
		 
			//int randomTwo = ThreadLocalRandom.current().nextInt(0, 100 );
			
			TClient c = new TClient("Inkling");
			
	
	
	 }
}


