# Earthquakes and life expectancy maps

This is a project that shows both a map with earthquakes and citites markers along with some info about them and a map of the life expectancy in each country.
The map uses earthquake information from a live RSS feed  http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom and the life expectancy information from a file (which you will find in /EarthquakeMap/bin/data) taken from th world bank.

##Table of contents

The project contains 2 packages: Parsing (helps parse the information fom the files) and Earthquakes (the main package) which contains 6 classes:

```python
EarthquakeMap - creates the map and displays the markers (extends PApplet)

Methods:

setup() # creates the map, adds the markers to it (with the hidden property true), loads the data from the files and creates the lists of markers
draw()  # displays the map and the key
lifeExpCSVFileLoad(String fileName)  # returns a Map <String, Float> (helps in linking the country with its life expectancy value)
shadeCountries() # sets the color to the country markers depending on the life expectancy of each country
mouseMoved() # when the mouse lands on a city or an earthquake marker it shows info about them
mouseClicked() # when a city marker is clicked all markers disappear except that city and the earthquake that might affect it;when a earthquake marker is clicked all markers disappear except that earhquake marker and the cities it affects; if the earthquake marker is an oceanquake marker then it links it with the cities it affects
addKey(), addEqMapKey(), addLifeMapKey)() # create the key depending on which map is shown
hideMarkers(), unhideMarkers(), hideCountryMarkers(), unhideCountryMarkers(), hideLines() #set the hidden property of certain markers to either true or false


CommonMarker - helps in creating the markers (extends SimplePointMarker)

Methods:

setClicked(boolean state) # sets the clicked variable to state
getClicked() # returns the current value of the clicked variable
draw(PGraphics pg, float x, float y) # displays the markers as long as they are not set to be hidden and if the mouse is on them it displays their information
notWithinThreatFromEq(List<Marker> markers, EarthquakeMarker eqm) # returns a list of markers that are not within the threat zone of a certain earthquake
withinThreatFromEq(List<Marker> markers, EarthquakeMarker eqm) # returns a list of markers that are within the threat zone of a certain earthquake


CityMarker - helps in creating the city markers (extends CommonMarker)

Methods:

drawMarker(PGraphics pg, float x, float y) # displays a triangle for the city
showTitle(PGraphics pg, float x, float y) # displays the name and population of the city along with the country
hideNotWithinThreat(List<Marker> quakeMarkers, List<Marker> cityMarkers, List<Marker> lines, UnfoldingMap map) # sets the hidden property to false for all the cities except it and for all the earthquakes except those that affect it
getCity() # returns the name property of the city marker as a string
getCountry() # returns the country property of the city marker as a string 
getPopulation() # returns the population property of the city marker as a float


EarthquakeMarker - helps in creating the earthquake markers (extends CommonMarker)

Methods:

drawMarker(PGraphics pg, float x, float y) # calls colorDetermine and puts an X over the markers of the earthquakes that occurred in the past day
colorDetermine(PGraphics pg) # sets the color of the marker depending on the depth of the earthquake 
threatCircle() # determines the distance that has to be between an earthquake and a city for the city to be affected
showTitle(PGraphics pg, float x, float y) # displays the information about the earthquake
hideNotWithinThreat(List<Marker> quakeMarkers, List<Marker> cityMarkers, List<Marker> lines, UnfoldingMap map) # sets the hidden property to false for all the earthquakes except it and for all the cities except those it affects
getMagnitude() # returns the magnitude property of the earthquake marker as a float
getTitle() # returns the title property of the earthquake marker as a string 
getDepth() # returns the depth property of the earthquake marker as a float
getRadius() # returns the radius property of the earthquake marker as a float
isOnLand() # returns the boolean value of the isOnLand variable


LandQuakeMarker - helps in creating the landquake markers (extends EarthquakeMarker)

Methods:

drawEarthquake(PGraphics pg, float x, float y) # displays an ellipse for the earthquakes that occurred on land
getCountry() # returns the country property of the landquake marker as a string 


OceanQuakeMarker - helps in creating the oceanquake markers (extends EarthquakeMarker)

Methods:

drawEarthquake(PGraphics pg, float x, float y) # displays a rectangles for the earthquakes that didn't occur on land
createLines(List<Marker> cityMarkers, List<Marker> lines) # returns a list of line markers (lines between an oceanquake and the cities it affects)
hideNotWithinThreat(List<Marker> quakeMarkers, List<Marker> cityMarkers, List<Marker> lines, UnfoldingMap map) # adds the line markers to the map

```