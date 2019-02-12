import java.util.*;
import java.io.*;

class KMLCreator {

    private static void writeToDepth(int depth, PrintWriter writer, String st) {
	String ret = "";
	for (int i = 0; i < depth; i++) {
	    ret += "    ";
	}
	writer.write(ret + st);
    }
    
    private static void writePath(ArrayList<Pair<Double,Double>> path, PrintWriter writer, int depth, String color, int id) {
	writeToDepth(depth++, writer, "<Placemark>\n");
	writeToDepth(depth, writer, "<name>Taxi " + Integer.toString(id) + " route</name>\n");
	writeToDepth(depth, writer, "<styleUrl>#" + color + "</styleUrl>\n");
	writeToDepth(depth++, writer, "<LineString>\n");
	writeToDepth(depth, writer, "<extrude>1</extrude>\n");
	writeToDepth(depth, writer, "<tessellate>1</tessellate>\n");
	writeToDepth(depth, writer, "<altitudeMode>relativeToGround</altitudeMode>\n");
	writeToDepth(depth++, writer, "<coordinates>\n");
	for(int i = 0; i < path.size(); i++) {
	    if (color.equals("red")) {
		writeToDepth(depth, writer, String.valueOf(path.get(i).getKey()) + "," + String.valueOf(path.get(i).getValue()) + ",5\n");
	    }
	    else {
		writeToDepth(depth, writer, String.valueOf(path.get(i).getKey()) + "," + String.valueOf(path.get(i).getValue()) + ",10\n");
	    }
	}
	writeToDepth(--depth, writer, "</coordinates>\n");
	writeToDepth(--depth, writer, "</LineString>\n");
	writeToDepth(--depth, writer, "</Placemark>\n");
    }
    
    public static void createKML(ArrayList<ArrayList<ArrayList<Pair<Double,Double > > > > paths, Pair<Double, Double> client, ArrayList<Pair<Double, Double> > taxis, ArrayList<Integer> taxiids, int besttaxi, String filename) throws IOException {
	PrintWriter writer = new PrintWriter(filename, "UTF-8");
	int depth;
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
	writeToDepth(depth++, writer, "<Style id=\"red\">\n");
	writeToDepth(depth++, writer, "<LineStyle>\n");
        writeToDepth(depth, writer, "<color>ff0000ff</color>\n");
        writeToDepth(depth, writer, "<width>5</width>\n");
	writeToDepth(--depth, writer, "</LineStyle>\n");
	writeToDepth(--depth, writer, "</Style>\n");
	writeToDepth(depth++, writer, "<Placemark>\n");
	writeToDepth(depth, writer, "<name>Client</name>\n");
	writeToDepth(depth++, writer, "<Point>\n");
	writeToDepth(depth, writer, "<coordinates>" + String.valueOf(client.getKey()) + "," + String.valueOf(client.getValue()) + ",0</coordinates>\n");
	writeToDepth(--depth, writer, "</Point>\n");
	writeToDepth(--depth, writer, "</Placemark>\n");
	String color;
	for (int i = 0; i < paths.size(); i++) {
	    if (i == besttaxi) {
		continue;
	    }
	    writeToDepth(depth++, writer, "<Placemark>\n");
	    writeToDepth(depth, writer, "<name>Taxi " + Integer.toString(taxiids.get(i)) + "</name>\n");
	    writeToDepth(depth++, writer, "<Point>\n");
	    writeToDepth(depth, writer, "<coordinates>" + String.valueOf(taxis.get(i).getKey()) + "," + String.valueOf(taxis.get(i).getValue()) + ",0</coordinates>\n");
	    writeToDepth(--depth, writer, "</Point>\n");
	    writeToDepth(--depth, writer, "</Placemark>\n");
	    for (int j = 0; j < paths.get(i).size(); j++) {
		color = "red";
		writePath(paths.get(i).get(j), writer, depth, color, taxiids.get(i));
	    }
	}
	color = "green";
	writeToDepth(depth++, writer, "<Placemark>\n");
	writeToDepth(depth, writer, "<name>Taxi " + Integer.toString(taxiids.get(besttaxi)) + "</name>\n");
	writeToDepth(depth++, writer, "<Point>\n");
	writeToDepth(depth, writer, "<coordinates>" + String.valueOf(taxis.get(besttaxi).getKey()) + "," + String.valueOf(taxis.get(besttaxi).getValue()) + ",0</coordinates>\n");
	writeToDepth(--depth, writer, "</Point>\n");
	writeToDepth(--depth, writer, "</Placemark>\n");
	for (int j = 0; j < paths.get(besttaxi).size(); j++) {
	    writePath(paths.get(besttaxi).get(j), writer, depth, color, taxiids.get(besttaxi));
	}
	writeToDepth(--depth, writer, "</Document>\n");
	writer.write("</kml>\n");
	writer.close();
    }
}
