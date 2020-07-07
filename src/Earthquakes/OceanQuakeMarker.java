package Earthquakes;

import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
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
	
	//creates lines between the ocean quake and cities it affects
	public List<Marker> createLines(List<Marker> cityMarkers, List<Marker> lines) {
		List<Marker> withinThreat = this.withinThreatFromEq(cityMarkers, this);
		for(Marker m: withinThreat) {
			SimpleLinesMarker line = new SimpleLinesMarker(this.getLocation(),m.getLocation());
			line.setStrokeWeight(3);
			line.setColor(0);
			lines.add(line);
		}
		return lines;
	}
	
	//overrides the hideNotWithinThreat in order to put the lines to the map
	public void hideNotWithinThreat(List<Marker> quakeMarkers, List<Marker> cityMarkers, List<Marker> lines, UnfoldingMap map) {
		super.hideNotWithinThreat(quakeMarkers, cityMarkers, lines, map);
		lines = this.createLines(cityMarkers, lines);
		map.addMarkers(lines);
	}
	

	

}

