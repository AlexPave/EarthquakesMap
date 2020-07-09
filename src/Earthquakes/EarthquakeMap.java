package Earthquakes;

//java util libraries
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private String cityFile = "../data/city-data.json";
	private String countryFile = "../data/countries.geo.json";
	
	//map for life expectancy (key=country, value = life expectancy value)
	private Map<String,Float>  lifeExpByCountry = lifeExpCSVFileLoad("../data/LifeExpectancyWorldBank.csv");

	//marker lists for cities, countries, earthquakes and a helper list to create lines from an ocean quake to the city it affects
	private List<Marker> quakeMarkers;
	private List<Marker> cityMarkers;
	private List<Marker> countryMarkers;
	private List<Marker> lines;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	//helpers to show which key to show
	private String lifeMap = "false";
	private String eqMap = "false";
	
	public void setup() {
		//setting the size of the Applet
		size(950, 600, OPENGL);
		
		map = new UnfoldingMap (this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomToLevel(2);
		
		MapUtils.createDefaultEventDispatcher(this, map);
		
	    lines = new ArrayList<Marker>();
		
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
	    
	    //setting the markers to be hidden
	    hideMarkers();
	    hideCountryMarkers();
	    //adding the markers to the map
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    map.addMarkers(countryMarkers);
	    map.addMarkers(lines);

	}
	
	public void draw() {
		//setting the background black
		background(10);
		//adding the map
		map.draw();
		//adding the legend 
		if(eqMap.equals("false")&&lifeMap.equals("false")) {
	    	addKey();
	    }
	    else {
	    	if(eqMap.equals("true")) {
	    		addEqMapKey();
	    	}
	    	else {
	    		addLifeMapKey();
	    	}
	    }
	}
	

	//helper method to create the map for life expectancy
	private Map<String, Float> lifeExpCSVFileLoad(String filename){
		
		Map <String, Float> lifeExpMap = new HashMap <String, Float>();
		String[] rows = loadStrings(filename);
		
		for(String row: rows) {
			String[] columns = row.split(",");
			
			//println(columns[4]+" "+columns[5]+" "+columns.length);
	
			if(columns.length == 18 && !columns[5].equals("..")) {
				float value = Float.parseFloat(columns[5]);
				lifeExpMap.put(columns[4], value);
			}
		}
		return lifeExpMap;
	}

	//shades the countries depending on the life expectancy
	private void shadeCountries() {
		
		for (Marker m: countryMarkers) {
			String countryID = m.getId();
			//println(countryID);
			if(lifeExpByCountry.containsKey(countryID)) {
				float lifeExp = lifeExpByCountry.get(countryID);
				//println(lifeExp);
				int colorLevel = (int)map(lifeExp, 40, 90, 10, 255);
				m.setColor(color(255-colorLevel, 100, colorLevel));
			}
			else {
				m.setColor(color(150, 150, 150));
			}
		}
		
	}
	

	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		
	}
	
	private void selectMarkerIfHover(List<Marker> markers)
	{
		for(Marker m: markers) {
			if(m.isInside(map, (float)mouseX, (float)mouseY)){
				if(lastSelected==null) {
					lastSelected = (CommonMarker)m;
					lastSelected.setSelected(true);
				}
			}
		}
	}
	
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			lastClicked.setClicked(false);
			lastClicked = null;
			hideLines();
			unhideMarkers();
		}
		
		if(mouseX>30&&mouseX<180&&((mouseY>90&&mouseY<105&&lifeMap.equals("false"))||(mouseY>130&&mouseY<145))) {
			eqMap = "true";
			lifeMap = "false";
			unhideMarkers();
			hideCountryMarkers();
		}
		else {
			if(mouseX>30&&mouseX<180&&((mouseY>110&&mouseY<125&&eqMap.equals("false"))||(mouseY>290&&mouseY<315))) {
				lifeMap = "true";
				eqMap = "false";
				hideMarkers();
				shadeCountries();
				unhideCountryMarkers();
			}
			else {
				selectMarkerIfClicked(quakeMarkers);
				selectMarkerIfClicked(cityMarkers);
				if(lastClicked!=null) {
					lastClicked.hideNotWithinThreat(quakeMarkers, cityMarkers,lines,map);
				}
			}
		}
		
	}
	
	private void selectMarkerIfClicked(List<Marker> markers)
	{
		for(Marker m: markers) {
			if(m.isInside(map, (float)mouseX, (float)mouseY)){
				if(lastClicked==null) {
					lastClicked = (CommonMarker)m;
					lastClicked.setClicked(true);
				}
			}
		}
	}
	
	// loop over and unhide all markers
		private void unhideMarkers() {
			for(Marker marker : quakeMarkers) {
				marker.setHidden(false);
			}
				
			for(Marker marker : cityMarkers) {
				marker.setHidden(false);
			}
		}
		
		private void hideMarkers() {
			for(Marker marker : quakeMarkers) {
				marker.setHidden(true);
			}
				
			for(Marker marker : cityMarkers) {
				marker.setHidden(true);
			}
		}
		
		private void hideCountryMarkers() {
			for(Marker marker : countryMarkers) {
				marker.setHidden(true);
			}
		}
		
		private void hideLines() {
			for(Marker m: lines) {
				m.setHidden(true);
			}
		}
		
		private void unhideCountryMarkers() {
			for(Marker marker : countryMarkers) {
				marker.setHidden(false);
			}
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

	//helper method to create the basic legend 
	private void addKey() {
		
		fill(255, 250, 240);
		rect(25, 50, 160, 300);
		
		//Eathquakes
		fill(color(100, 100, 100));
		rect(30,90,150,15);
		//LifeExpectancy
		fill(color(100, 100, 100));
		rect(30,110,150,15);
		
		fill(250, 250, 250);
		text("Earthquakes Map", 55, 95);
		text("Life Expectancy Map", 55, 115);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Key", 50, 75);
	}

	//helper method to create the earthquakes legend 
	private void addEqMapKey() {
		fill(255, 250, 240);
		rect(25, 50, 160, 300);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", 50, 75);
		
		//LandQuake
		fill(color(255, 255, 255));
		ellipse(50, 95, 15, 15);
		//OceanQuake
		fill(color(255, 255, 255));
		rect(43, 110, 15, 15);
		//City
		fill(163,136,60);
		triangle(50,130,45,140,55,140);
		//Light
		fill(color(255, 255, 0));
		ellipse(50, 175, 15, 15);
		//Moderate
		fill(color(0, 0, 255));
		ellipse(50, 195, 15, 15);
		//Intermediate
		fill(color(0, 255, 0));
		ellipse(50, 215, 15, 15);
		//Deep
		fill(color(255, 0, 0));
		ellipse(50, 235, 15, 15);
		//Past Day
		fill(color(255, 255, 255));
		ellipse(50, 255, 15, 15);
		line(60,245,40,265);
		line(40,245,60,265);
		//LifeExpectancy
		fill(color(100, 100, 100));
		rect(30,290,150,15);
		
		fill(0, 0, 0);
		text("Land quake", 75, 95);
		text("Ocean quake", 75, 115);
		text("Cities", 75, 135);
		text("Shallow", 75, 175);
		text("Moderate", 75, 195);
		text("Intermediate", 75, 215);
		text("Deep", 75, 235);
		text("Past Day", 75, 255);
		
		textAlign(LEFT, CENTER);
		text("Size ~ Magnitude", 50, 155);
		
		fill(250,250,250);
		text("Life Expectancy Map", 55, 295);
	}

	//helper method to create the life expectancy legend 
	private void addLifeMapKey() {
		fill(255, 250, 240);
		rect(25, 50, 160, 300);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Life Expectancy Key", 50, 75);
		
		//Low
		fill(color(255, 148, 77));
		rect(40,90,15,15);
		//High
		fill(color(0, 0, 255));
		rect(40,110,15,15);
		//Eathquakes
		fill(color(100, 100, 100));
		rect(30,130,150,15);
		
		fill(0, 0, 0);
		text("Low", 75, 95);
		text("High", 75, 115);
		
		fill(250, 250, 250);
		text("Earthquakes Map", 55, 135);
	}
}
