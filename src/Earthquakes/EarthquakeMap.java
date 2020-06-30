package Earthquakes;

import de.fhpotsdam.unfolding.UnfoldingMap;
import processing.core.PApplet;

public class EarthquakeMap extends PApplet {
	
	private static final long serialVersionUID = 1L;
	private UnfoldingMap map;
	
	public void setup() {
		size(950, 600, OPENGL);
		
	}
	
	public void draw() {
		background(10);
	}

}
