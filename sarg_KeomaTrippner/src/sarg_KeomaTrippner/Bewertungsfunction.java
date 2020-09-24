package sarg_KeomaTrippner;

import java.awt.Point;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Bewertungsfunction implements Comparable<Bewertungsfunction> {
	
	public float[] weights;
	int[] attributes;
	public Point nextTurn;
	
	public int suchTiefe = 7 ;
	
	public int siege = 0;


	
	public Bewertungsfunction() {
		attributes = new int[11];
		weights = new float[11];
		 
		weights[0] =  0.7f;
		weights[1] = -0.5f;
		weights[2] = -0.5f;
		
		weights[3] =  0.5f;
		weights[4] = -0.2f;
		weights[5] = -0.2f;
		
		weights[6] = 0.1f;
		weights[7] = -0.04f;
		weights[8] = -0.04f;
		
		weights[9] = 1.0f;
	}
	
	
	public Bewertungsfunction(float c1, float c2, float c3, float c4, float c5, float c6, float c7, float c8, float c9, float c10, float c11) {
		attributes = new int[11];
		weights = new float[11];
		 
		weights[0] = c1;
		weights[1] = c2;
		weights[2] = c3;
		
		weights[3] = c4;
		weights[4] = c5;
		weights[5] = c6;
		
		weights[6] = c7;
		weights[7] = c8;
		weights[8] = c9;
		
		weights[9] = c10;
		weights[10] = c11;
		
	}
	
	public Bewertungsfunction (float[] w) {
		attributes = new int[11];
		this.weights = new float[11];
		for (int c = 0; c < w.length; c++) {
			this.weights[c]= w[c];
		}

	}
	
	private  Bewertungsfunction mutation (float scale) {
		Bewertungsfunction mutated = new Bewertungsfunction();
		float summe = 0;
		for (int w = 0; w < weights.length; w++)  {
			float randomNum = (ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat())*scale;
			summe +=  randomNum + this.weights[w];
			mutated.weights[w] = randomNum + this.weights[w];
		}
		mutated.norm(summe);
		return mutated;
	}
	
	public float bewerteSpielbrett(Spielbrett zuBewerten, int playerID ) {
		
		attributes[0] =  zuBewerten.playerPoints[playerID];
		attributes[1] =  zuBewerten.playerPoints[(playerID+1)%3];
		attributes[2] =  zuBewerten.playerPoints[(playerID+2)%3];
		attributes[3] =  zuBewerten.stepCounters[playerID];
		attributes[4] =  zuBewerten.stepCounters[(playerID+1)%3];
		attributes[5] =  zuBewerten.stepCounters[(playerID+2)%3];
		attributes[6] =  zuBewerten.spielerSteine[playerID].size();
	    attributes[7] =  zuBewerten.spielerSteine[(playerID+1)%3].size();
		attributes[8] =  zuBewerten.spielerSteine[(playerID+2)%3].size();
		
		if(zuBewerten.playerPoints[playerID] >= 5) attributes[9] = 1000;
		else  attributes[9] = 0;
		
		if(zuBewerten.spielerSteine[playerID].size() < 1) attributes[10] = -500;
		else attributes[10] = 1;
		
		float wert = 0;
		
		for(int i = 0; i<attributes.length; i++) {
			wert += weights[i]*attributes[i];
		}
		
		zuBewerten.evaluation = wert;
		 return wert;
	}
	
	public void  setNextTurn(Point p) {
		this.nextTurn = new Point(p.x,p.y);
	}
	
    public float minimax (Spielbrett brett, int tiefe, float alpha, float beta, int playerID, int maxPlay ) {
    	
    	// player ID stimmt noch nicht
    	int activerSpieler = playerID%3;
    	int maxPlayer = maxPlay%3; 
    	if(tiefe == 0)return bewerteSpielbrett(brett, activerSpieler);
    	
    	if(maxPlayer == 0) {
    		float evalMax = -Float.MAX_VALUE;
    		for (Object o : brett.spielerSteine[activerSpieler]) {
    			Point p = (Point)o;
    			Spielbrett next =  new Spielbrett(brett.spielfeld,brett.playerPoints,brett.spielerSteine);
    			next =  Spielbrett.makeMove(next, p.x, p.y, activerSpieler, false);
				float evaluation = minimax(next,tiefe-1,alpha, beta, activerSpieler+1, maxPlayer+1);
				if(evaluation > evalMax) {
					evalMax = evaluation;
					if(tiefe==suchTiefe) setNextTurn(p);
				}
				alpha = Math.max(alpha, evaluation);
				if(beta <= alpha) break;
				//evalMax = Math.max(evalMax, evaluation);
			}
    		return evalMax;
    	}
    	
    	else {
    		float evalMin = Float.MAX_VALUE;
    		for (Object o : brett.spielerSteine[activerSpieler]) {
    			Point p = (Point)o;
    			Spielbrett next =  new Spielbrett(brett.spielfeld,brett.playerPoints,brett.spielerSteine);
    			next =  Spielbrett.makeMove(next, p.x, p.y, activerSpieler, false);
				float evaluation = minimax(next,tiefe-1, alpha, beta, activerSpieler+1, maxPlayer+1);
				if(evaluation < evalMin) {
					evalMin = evaluation;
					if(tiefe==suchTiefe) setNextTurn(p);
				}
				beta = Math.min(beta, evaluation);
				if(beta <= alpha) break;
				//evalMin = Math.max(evalMin, evaluation);
			}
    		return evalMin;
    	}


    }
	
    public static ArrayList<Bewertungsfunction> getNextGeneration(Bewertungsfunction[] parents ) {
    	
    	ArrayList<Bewertungsfunction> childs = new ArrayList<Bewertungsfunction>();
		
    	for (int p = 0; p < parents.length; p++) {
    		for (int n = 0; n < parents.length; n++) {
    			if(p == n+ Math.abs(p-n)) continue;
    			
    			float c1 = (parents[p].weights[0] + parents[n].weights[0])/2;
    			float c2 = (parents[p].weights[1] + parents[n].weights[1])/2;
    			float c3 = (parents[p].weights[2] + parents[n].weights[2])/2;
    			float c4 = (parents[p].weights[3] + parents[n].weights[3])/2;
    			float c5 = (parents[p].weights[4] + parents[n].weights[4])/2;
    			float c6 = (parents[p].weights[5] + parents[n].weights[5])/2;
    			float c7 = (parents[p].weights[6] + parents[n].weights[6])/2;
    			float c8 = (parents[p].weights[7] + parents[n].weights[7])/2;
    			float c9 = (parents[p].weights[8] + parents[n].weights[8])/2;
    			float c10 = (parents[p].weights[9] + parents[n].weights[9])/2;
    			float c11 = (parents[p].weights[10] + parents[n].weights[10])/2;


    			childs.add( new Bewertungsfunction(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11));
    		}
		}
    	
    	Bewertungsfunction[] mutations = new Bewertungsfunction[childs.size()];
    	for (int b = 0; b < mutations.length; b++) {
    		mutations[b] = childs.get(b).mutation(0.15f);
		}
    	
    	Bewertungsfunction strongMutation = parents[0].mutation(1f);
    	
    	ArrayList<Bewertungsfunction> toReturnArrayList = new ArrayList<Bewertungsfunction>();
    	for (Bewertungsfunction m : mutations) {
			toReturnArrayList.add(m);
		}
    	
		toReturnArrayList.add(parents[0]);
		toReturnArrayList.add(parents[1]);
		toReturnArrayList.add(Bewertungsfunction.reinforceBewertung(parents[0]));
		
    	toReturnArrayList.add(strongMutation);
    	
    	return toReturnArrayList;
    }

    public static Bewertungsfunction reinforceBewertung(Bewertungsfunction f) {
    	Bewertungsfunction reinforced = new Bewertungsfunction(f.weights.clone());
    	float summe = 0;
    	
    	for (int w = 0; w < reinforced.weights.length; w++)  {
			reinforced.weights[w]= (f.weights[w]*f.weights[w])*(Math.abs(f.weights[w])/f.weights[w]);
			summe += reinforced.weights[w];
		}
    	reinforced.norm(summe);
    	return reinforced;
    }
    
    public void norm(float summe) {
    	for (int w = 0; w < weights.length; w++) {
			this.weights[w] = this.weights[w]/summe;
		}
    }
    
    //Git die Summe aller Gewichte zurück
    public float getCoefficientSum() {
    	float sum = 0;
    	for (float f : weights) {
			sum += f;
		}
    	return sum;
    }

	@Override
	public int compareTo(Bewertungsfunction o) {
		
		return Integer.compare(this.siege, o.siege);
	}
    
    
    

}
