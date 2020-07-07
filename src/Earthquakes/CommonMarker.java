package Earthquakes;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

public abstract class CommonMarker extends SimplePointMarker {

	// Records whether this marker has been clicked
	protected boolean clicked = false;
	
	public CommonMarker(Location location) {
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
	}
	
	// Getter method for clicked field
	public boolean getClicked() {
		return clicked;
	}
	
	// Setter method for clicked field
	public void setClicked(boolean state) {
		clicked = state;
	}
	
	public void draw(PGraphics pg, float x, float y) {
		if (!hidden) {
			drawMarker(pg, x, y);
			if (selected) {
				showTitle(pg, x, y);  
			}
		}
		
	}

	public List<Marker> notWithinThreatFromEq(List<Marker> markers, EarthquakeMarker eqm){
		List<Marker> within = new ArrayList<Marker>();
		for(Marker marker : markers) {
			if(this.getDistanceTo(marker.getLocation())>eqm.threatCircle()) {
				within.add(marker);
			}
		}
		return within;
	}
	
	public List<Marker> withinThreatFromEq(List<Marker> markers, EarthquakeMarker eqm){
		List<Marker> within = new ArrayList<Marker>();
		for(Marker marker : markers) {
			if(this.getDistanceTo(marker.getLocation())<=eqm.threatCircle()) {
				within.add(marker);
			}
		}
		return within;
	}
	
	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void showTitle(PGraphics pg, float x, float y);
	public abstract void hideNotWithinThreat(List<Marker> markersA, List<Marker> markersB,List<Marker> markersC, UnfoldingMap map);
}

