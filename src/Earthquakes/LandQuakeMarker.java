package Earthquakes;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

public class LandQuakeMarker extends EarthquakeMarker {
	
	
	public LandQuakeMarker(PointFeature quake) {
		
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = true;
	}

	// Draws a centered circle for land quakes
	
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		
		pg.pushStyle();
		float radius = this.getRadius();
		pg.ellipse(x,y,radius, radius);
		pg.popStyle();
	}
	

	// Get the country the earthquake is in
	public String getCountry() {
		return (String) getProperty("country");
	}


	



		
}
