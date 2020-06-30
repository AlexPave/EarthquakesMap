package Earthquakes;

import java.util.ArrayList;
import java.util.List;

import Parsing.ParseFeed;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class EarthquakeMap extends PApplet {
	
	private static final long serialVersionUID = 1L;
	private UnfoldingMap map;
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	public void setup() {
		size(950, 600, OPENGL);
		map = new UnfoldingMap (this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		List<Marker> markers = new ArrayList<Marker>();
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    for(PointFeature feature: earthquakes) {
	    	markers.add(createMarker(feature));
	    }
	}
	
	public void draw() {
		background(10);
		map.draw();
		addKey();
	}
	
	private SimplePointMarker createMarker(PointFeature feature)
	{
		SimplePointMarker mark = new SimplePointMarker(feature.getLocation(), feature.getProperties());
		Object magObj = feature.getProperty("magnitude");
	   	float mag = Float.parseFloat(magObj.toString());
		if(mag<4.0) {
			mark.setRadius(6);
			mark.setColor(color(20, 179, 219));
		}
		else
		{
			if (mag<5.0) {
				mark.setRadius(10);
				mark.setColor(color(219, 219, 20));
			}
			else {
				mark.setRadius(14);
				mark.setColor(color(209, 47, 25));
			}
		}
		return mark;
	}
	
	private void addKey() {
		fill(204, 255, 204);
		rect(20, 100, 170, 400, 7);
		
		String s = "Legend";
		fill(0,0,0);
		textSize(20);
		text(s, 30, 110, 140, 400);
		textAlign(CENTER);
		
		
		fill(20, 179, 219);
		ellipse(50, 200, 15, 15);
		
		s = "Magnitude<4.0";
		fill(0,0,0);
		textSize(13);
		text(s, 55, 190, 140, 400);
		textAlign(CENTER);
		
		
		fill(219, 219, 20);
		ellipse(50, 300, 30, 30);
		
		s = "Magnitude<5.0";
		fill(0,0,0);
		text(s, 55, 290, 140, 400);
		textAlign(CENTER);
		textSize(13);
		
		fill(209, 47, 25);
		ellipse(50, 400, 45, 45);
		
		s = "Magnitude>5.0";
		fill(0,0,0);
		text(s, 55, 390, 140, 400);
		textAlign(CENTER);
		textSize(13);
	}
	
}
