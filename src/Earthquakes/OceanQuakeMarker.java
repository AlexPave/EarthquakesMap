package Earthquakes;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		isOnLand = false;
	}

	// Drawing a centered square for Ocean earthquakes

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.pushStyle();
		float radius = this.getRadius();
		pg.rect(x,y,radius, radius);
		pg.popStyle();
		
	}
	


	

}

