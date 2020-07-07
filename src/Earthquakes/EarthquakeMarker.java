package Earthquakes;

import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import processing.core.PGraphics;

public abstract class EarthquakeMarker extends CommonMarker
{
	
	protected boolean isOnLand;
	protected static final float kmPerMile = 1.6f;

	//Less than or equal to this threshold is a moderate earthquake 
	public static final float THRESHOLD_MODERATE = 5;
	// Less than or equal to this threshold is a light earthquake
	public static final float THRESHOLD_LIGHT = 4;

	//Less than or equal to this threshold is an intermediate depth 
	public static final float THRESHOLD_INTERMEDIATE = 70;
	// Less than or equal to this threshold is a deep depth 
	public static final float THRESHOLD_DEEP = 300;
	
	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
		
	// constructor
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude );
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}
	
	// private helper method: determines color of marker from depth, and set pg's fill color 
	// Deep = red, intermediate = green, moderate = blue, light = yellow
	private void colorDetermine(PGraphics pg) {
		float depth = this.getDepth();
		if(depth<=THRESHOLD_LIGHT) {
			pg.fill(255,255,0);
		}
		else {
			if(depth<=THRESHOLD_MODERATE) {
				pg.fill(0,0,255);
			}
			else {
				if(depth<=THRESHOLD_INTERMEDIATE) {
					pg.fill(0,255,0);
				}
				else {
					pg.fill(255,0,0);
				}
			}
		}
	}
	
	public double threatCircle() {	
		double miles = 20.0f * Math.pow(1.8, 2*getMagnitude()-5);
		double km = (miles * kmPerMile);
		return km;
	}
	
	//draws the markers on the map
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();
					
		// determine color of marker from depth
		colorDetermine(pg);
				
		drawEarthquake(pg, x, y);
					
		String age = getStringProperty("age");
		if ("Past Day".equals(age)) {
					
			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x-(radius+buffer), y-(radius+buffer), 
					x+radius+buffer, y+radius+buffer);
			pg.line(x-(radius+buffer), y+(radius+buffer), 
					x+radius+buffer, y-(radius+buffer));			
		}
		
		pg.popStyle();
		
	}
	
	//helper method for showing info on the map about earthquakes
	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		pg.pushStyle();
		
		pg.fill(0);
		String s = this.getTitle();
		pg.text(s, x+radius+2, y, 150, 80);
		
		pg.popStyle();
	}


	//hides all cities that are not within threat zone and the markers of the other earthquakes
	@Override
	public void hideNotWithinThreat(List<Marker> quakeMarkers, List<Marker> cityMarkers, List<Marker> lines, UnfoldingMap map) {
		for(Marker marker : quakeMarkers) {
			if(!this.equals(marker)) {
				marker.setHidden(true);
			}
		}
		List<Marker> withinThreat = notWithinThreatFromEq(cityMarkers, this);
		for(Marker marker : withinThreat) {
			marker.setHidden(true);
		}
		
	}
	
	//getters for earthquake properties
	
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}
	
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}
	
	public String getTitle() {
		return (String) getProperty("title");	
		
	}
	
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}
	
	public boolean isOnLand()
	{
		return isOnLand;
	}
	
	
}

