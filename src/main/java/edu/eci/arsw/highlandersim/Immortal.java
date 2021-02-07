package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    //private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    public final String name;

    private final Random r = new Random(System.currentTimeMillis());
    
    private AtomicInteger atmHealth;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        //this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.atmHealth = new AtomicInteger(health);
    }

    public void run() {

        while (true) {
        	synchronized(immortalsPopulation) {
        		if(immortalsPopulation.isEmpty()) {
        			try {
						immortalsPopulation.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        	Immortal im;
        	int myIndex = immortalsPopulation.indexOf(this);
        	int nextFighterIndex = r.nextInt(immortalsPopulation.size());
        	//avoid self-fight
        	if (nextFighterIndex == myIndex) {
        		nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
        	}
        	im = immortalsPopulation.get(nextFighterIndex);
        	this.fight(im);
        	try {
        		Thread.sleep(1);
        	} catch (InterruptedException e) {
        		e.printStackTrace();
        	}
        	
        		
        		
        	
        }

    }

    public void fight(Immortal i2) {
    	int points = i2.getAtmHealth().get();
    	if (points > 0) {
    		synchronized(updateCallback) {
    			synchronized (i2) {
		    		if(i2.getAtmHealth().compareAndSet(points, points - defaultDamageValue )) {
		    			i2.die();
		    			this.atmHealth.addAndGet(defaultDamageValue);
		    			updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
		    		}
    			}
    		}
    	}
    }
    
    public AtomicInteger getAtmHealth() {
    	return this.atmHealth;
    }
    
    public void die() {
    	this.stop();
    }
    
/***
    public  void changeHealth(int v) {
        health = v;
    }

    public  int getHealth() {
        return health;
    }
***/
    @Override
    public String toString() {

        return name + "[" + atmHealth.get() + "]";
    }

}
