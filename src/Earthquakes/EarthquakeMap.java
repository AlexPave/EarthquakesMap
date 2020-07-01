package Earthquakes;

//java util libraries
import java.util.ArrayList;
import java.util.List;

//parse feed class
import Parsing.ParseFeed;

//unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class EarthquakeMap extends PApplet {
	
	private static final long serialVersionUID = 1L;
	
	//the map
	private UnfoldingMap map;
	//URL for the earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	public void setup() {
		//setting the size of the Applet
		size(950, 600, OPENGL);
		
		map = new UnfoldingMap (this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomToLevel(2);
		
		MapUtils.createDefaultEventDispatcher(this, map);
		
		List<Marker> markers = new ArrayList<Marker>();
		
		//taking the characteristics of the earthquakes from the site
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    //creating the markers
	    for(PointFeature feature: earthquakes) {
	    	markers.add(createMarker(feature));
	    }
	    
	    //adding the markers to the map
	    map.addMarkers(markers);
	}
	
	public void draw() {
		//setting the background black
		background(10);
		//adding the map
		map.draw();
		//adding the legend
		addKey();
	}
	
	//helper method for creating the markers using PointFeatures 
	private SimplePointMarker createMarker(PointFeature feature)
	{
		SimplePointMarker mark = new SimplePointMarker(feature.getLocation(), feature.getProperties());
		
		//getting the magnitude
		Object magObj = feature.getProperty("magnitude");
	   	float mag = Float.parseFloat(magObj.toString());
	   	
	   	//creating different markers depending on the magnitude
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
	
	//helper method to create the legend 
	private void addKey() {
		
		fill(255, 250, 240);
		rect(25, 50, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", 50, 75);
		
		fill(color(255, 255, 255));
		ellipse(50, 95, 15, 15);
		fill(color(255, 255, 255));
		rect(43, 110, 15, 15);
		fill(163,136,60);
		triangle(50,130,45,140,55,140);
		fill(color(255, 255, 0));
		ellipse(50, 175, 15, 15);
		fill(color(0, 0, 255));
		ellipse(50, 195, 15, 15);
		fill(color(255, 0, 0));
		ellipse(50, 215, 15, 15);
		fill(color(255, 255, 255));
		ellipse(50, 235, 15, 15);
		line(60,225,40,245);
		line(40,225,60,245);
		
		fill(0, 0, 0);
		text("Land quake", 75, 95);
		text("Ocean quake", 75, 115);
		text("Cities", 75, 135);
		text("Shallow", 75, 175);
		text("Intermidiate", 75, 195);
		text("Deep", 75, 215);
		text("Past Day", 75, 235);
		
		textAlign(LEFT, CENTER);
		text("Size ~ Magnitude", 50, 155);
	}
	
}
