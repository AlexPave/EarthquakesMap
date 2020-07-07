package Earthquakes;

import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

public class CityMarker extends CommonMarker {
	
	// The size of the triangle marker
	public static final int TRI_SIZE = 5;  
	
	public CityMarker(Location location) {
		super(location);
	}
	
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	}
	
	//draws the markers on the map
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();
		
		pg.fill(163,136,60);
		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		
		pg.popStyle();
		
	}
	

	//helper method for showing info on the map about earthquakes
	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		pg.pushStyle();
		
		pg.fill(0);
		String s = "";
		s = "City: "+this.getCity();
		s = s+" \nCountry: "+this.getCountry();
		s = s+" \nPopulation: "+this.getPopulation();
		pg.text(s, x+radius, y, 150, 80);
		
		pg.popStyle();
	}

	//hides all the other cities and the earthquakes that do not affect it
	@Override
	public void hideNotWithinThreat(List<Marker> quakeMarkers, List<Marker> cityMarkers, List<Marker> lines, UnfoldingMap map) {
		for(Marker marker : quakeMarkers) {
			EarthquakeMarker eqm = (EarthquakeMarker) marker;
			if(this.getDistanceTo(marker.getLocation())>eqm.threatCircle()) {
				marker.setHidden(true);
			}
		}
			
		for(Marker marker : cityMarkers) {
			if(!this.equals(marker)) {
				marker.setHidden(true);
			}
		}
		
	}
	
	//local getters for properties
	
	public String getCity()
	{
		return getStringProperty("name");
	}
	
	public String getCountry()
	{
		return getStringProperty("country");
	}
	
	public float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}
	
}
