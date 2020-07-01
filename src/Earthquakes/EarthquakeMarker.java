package Earthquakes;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

public abstract class EarthquakeMarker extends SimplePointMarker
{
	
	protected boolean isOnLand;

	
	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
		
	
	// constructor
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude );
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}
	

	//draws earthquake markers depending on their depth and adds an X if they occured in the past day
	public void draw(PGraphics pg, float x, float y) {
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
	
	// private helper method: determines color of marker from depth, and set pg's fill color 
	// Deep = red, intermediate = blue, shallow = yellow
	private void colorDetermine(PGraphics pg) {
		float depth = this.getDepth();
		if(depth<=70) {
			pg.fill(255,255,0);
		}
		else {
			if(depth<=300) {
				pg.fill(0,0,255);
			}
			else {
				pg.fill(255,0,0);
			}
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

