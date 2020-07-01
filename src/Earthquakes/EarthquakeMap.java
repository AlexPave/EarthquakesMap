package Earthquakes;

//java util libraries
import java.util.ArrayList;
import java.util.List;

//parse feed class
import Parsing.ParseFeed;

//unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
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
	
	//files with city and country names and info
	private String cityFile = "C:/Users/Anda/git/EarthquakesMap/data/city-data.json";
	private String countryFile = "C:/Users/Anda/git/EarthquakesMap/data/countries.geo.json";

	//marker lists for cities, countries and earthquakes
	private List<Marker> quakeMarkers;
	private List<Marker> cityMarkers;
	private List<Marker> countryMarkers;
	
	public void setup() {
		//setting the size of the Applet
		size(950, 600, OPENGL);
		
		map = new UnfoldingMap (this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomToLevel(2);
		
		MapUtils.createDefaultEventDispatcher(this, map);
		
		//loading country features and markers
	    List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//loading city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
		
		//reading earthquakes from RSS feed
		List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    //check if LandQuake or OceanQuake
	    for(PointFeature feature : earthquakes) {
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }
	    //adding the markers to the map
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	}
	
	public void draw() {
		//setting the background black
		background(10);
		//adding the map
		map.draw();
		//adding the legend
		addKey();
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
	
	//checks whether the quake occurred on land
	private boolean isLand(PointFeature earthquake) {
		
		for (Marker m : countryMarkers) {
			if(isInCountry(earthquake, m)) {
				return true;
			}
		}
		
		return false;
	}

	//helper method to see if a given quake is in a given country
	
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		if(country.getClass() == MultiMarker.class) {
				
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					return true;
				}
			}
		}
			
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}
	
}
