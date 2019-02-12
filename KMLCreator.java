import java.util.*;
import java.io.*;
import javafx.util.*;
class KMLCreator {

    private static void writeToDepth(int depth, PrintWriter writer, String st) {
	String ret = "";
	for (int i = 0; i < depth; i++) {
	    ret += "    ";
	}
	writer.write(ret + st);
    }
    
    private static void writePath(ArrayList<Pair<String,String>> path, PrintWriter writer, int depth, String color, String id) {
	writeToDepth(depth++, writer, "<Placemark>\n");
	writeToDepth(depth, writer, "<name>Taxi " + id + " route</name>\n");
	writeToDepth(depth, writer, "<styleUrl>#" + color + "</styleUrl>\n");
	writeToDepth(depth++, writer, "<LineString>\n");
	writeToDepth(depth, writer, "<extrude>1</extrude>\n");
	writeToDepth(depth, writer, "<tessellate>1</tessellate>\n");
	writeToDepth(depth, writer, "<altitudeMode>relativeToGround</altitudeMode>\n");
	writeToDepth(depth++, writer, "<coordinates>\n");
	for(int i = 0; i < path.size(); i++) {
	    if (color.equals("red")) {
		writeToDepth(depth, writer, path.get(i).getKey() + "," + path.get(i).getValue() + ",5\n");
	    }
	    else {
		writeToDepth(depth, writer, path.get(i).getKey() + "," + path.get(i).getValue() + ",10\n");
	    }
	}
	writeToDepth(--depth, writer, "</coordinates>\n");
	writeToDepth(--depth, writer, "</LineString>\n");
	writeToDepth(--depth, writer, "</Placemark>\n");
    }
    
    public static void createKML(ArrayList<ArrayList<ArrayList<Pair<String,String > > > > paths, Pair<String, String> client, ArrayList<Pair<String, String> > taxis, ArrayList<String> taxiids, String besttaxiid, String filename, ArrayList<ArrayList<Pair<String,String>> > dest) throws IOException {
	PrintWriter writer = new PrintWriter(filename, "UTF-8");
	int depth, besttaxi=0;
	writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
	depth = 1;
	writeToDepth(depth++, writer, "<Document>\n");
	writeToDepth(depth, writer, "<name>Paths</name>\n");
	writeToDepth(depth++, writer, "<Style id=\"green\">\n");
	writeToDepth(depth++, writer, "<LineStyle>\n");
        writeToDepth(depth, writer, "<color>ff009900</color>\n");
        writeToDepth(depth, writer, "<width>5</width>\n");
	writeToDepth(--depth, writer, "</LineStyle>\n");
	writeToDepth(--depth, writer, "</Style>\n");
	writeToDepth(depth++, writer, "<Style id=\"yellow\">\n");
	writeToDepth(depth++, writer, "<LineStyle>\n");
        writeToDepth(depth, writer, "<color>ffbb0000</color>\n");
        writeToDepth(depth, writer, "<width>5</width>\n");
	writeToDepth(--depth, writer, "</LineStyle>\n");
	writeToDepth(--depth, writer, "</Style>\n");
	writeToDepth(depth++, writer, "<Style id=\"red\">\n");
	writeToDepth(depth++, writer, "<LineStyle>\n");
        writeToDepth(depth, writer, "<color>ff0000ff</color>\n");
        writeToDepth(depth, writer, "<width>5</width>\n");
	writeToDepth(--depth, writer, "</LineStyle>\n");
	writeToDepth(--depth, writer, "</Style>\n");
	writeToDepth(depth++, writer, "<Placemark>\n");
	writeToDepth(depth, writer, "<name>Client</name>\n");
	writeToDepth(depth++, writer, "<Point>\n");
	writeToDepth(depth, writer, "<coordinates>" + client.getKey() + "," + client.getValue() + ",0</coordinates>\n");
	writeToDepth(--depth, writer, "</Point>\n");
	writeToDepth(--depth, writer, "</Placemark>\n");
	String color;
	for (int i = 0; i < paths.size(); i++) {
	    if (taxiids.get(i).equals(besttaxiid)) {
		besttaxi = i;
		continue;
	    }
	    writeToDepth(depth++, writer, "<Placemark>\n");
	    writeToDepth(depth, writer, "<name>Taxi " + taxiids.get(i) + "</name>\n");
	    writeToDepth(depth++, writer, "<Point>\n");
	    writeToDepth(depth, writer, "<coordinates>" + taxis.get(i).getKey() + "," + taxis.get(i).getValue() + ",0</coordinates>\n");
	    writeToDepth(--depth, writer, "</Point>\n");
	    writeToDepth(--depth, writer, "</Placemark>\n");
	    for (int j = 0; j < paths.get(i).size(); j++) {
		color = "red";
		writePath(paths.get(i).get(j), writer, depth, color, taxiids.get(i));
	    }
	}
	color = "green";
	writeToDepth(depth++, writer, "<Placemark>\n");
	writeToDepth(depth, writer, "<name>Taxi " + taxiids.get(besttaxi) + "</name>\n");
	writeToDepth(depth++, writer, "<Point>\n");
	writeToDepth(depth, writer, "<coordinates>" + taxis.get(besttaxi).getKey() + "," + taxis.get(besttaxi).getValue() + ",0</coordinates>\n");
	writeToDepth(--depth, writer, "</Point>\n");
	writeToDepth(--depth, writer, "</Placemark>\n");
	for (int j = 0; j < paths.get(besttaxi).size(); j++) {
	    writePath(paths.get(besttaxi).get(j), writer, depth, color, taxiids.get(besttaxi));
	}
	color = "yellow";
	writeToDepth(depth++, writer, "<Placemark>\n");
	writeToDepth(depth, writer, "<name>Destination</name>\n");
	writeToDepth(depth++, writer, "<Point>\n");
	writeToDepth(depth, writer, "<coordinates>" + dest.get(0).get(dest.get(0).size()-1).getKey() + "," + dest.get(0).get(dest.get(0).size()-1).getValue() + ",0</coordinates>\n");
	writeToDepth(--depth, writer, "</Point>\n");
	writeToDepth(--depth, writer, "</Placemark>\n");
	for (int j = 0; j < dest.size(); j++) {
	    writePath(dest.get(j), writer, depth, color, "");
	}
	writeToDepth(--depth, writer, "</Document>\n");
	writer.write("</kml>\n");
	writer.close();
    }
}
