import java.io.*;
import java.util.*;

class preprocessing {

    private static HashMap<String, Integer> lineways;
    
    private static boolean isForbidden(String x) {
	return x.equals("proposed") || x.equals("pedestrian") || x.equals("elevator") || x.equals("service") || x.equals("cycleway") || x.equals("track") || x.equals("construction") || x.equals("bridleway") || x.equals("services") || x.equals("path") || x.equals("footway") || x.equals("unclassified") || x.equals("steps");
    }

    private static String findLim(String x) {
	if (x.equals("primary_link")) {
	    return "60";
	} else if (x.equals("living_street")) {
	    return "30";
	} else if (x.equals("residential")) {
	    return "50";
	} else if (x.equals("trunk")) {
	    return "90";
	} else if (x.equals("primary")) {
	    return "360";
	} else if (x.equals("motorway_link")) {
	    return "60";
	} else if (x.equals("motorway")) {
	    return "480";
	} else if (x.equals("tertiary_link")) {
	    return "60";
	} else if (x.equals("secondary_link")) {
	    return "60";
	} else if (x.equals("trunk_link")) {
	    return "60";
	} else if (x.equals("tertiary")) {
	    return "90";
	} else if (x.equals("secondary")) {
	    return "100";
	}
	return "50";
    }

    private static void makeLines() throws IOException {
	lineways = new HashMap<>();
	File file = new File("data/lines.csv");
	FileWriter fw = new FileWriter("lines.pl");
	BufferedWriter bw = new BufferedWriter(fw);
	BufferedReader br = new BufferedReader(new FileReader(file));
	br.readLine();
	String st;
	String[] parts;
	int way;
	String lim;
	String toll;
	while ((st = br.readLine()) != null) {
	    parts = st.split(",");
	    if (parts.length == 1) {
		continue;
	    }
	    if (parts[1].length() == 0) {
		continue;
	    }
	    if (isForbidden(parts[1])) {
		continue;
	    }
	    if (parts.length < 4) {
		way = 0;
	    }
	    else if (parts[3].equals("yes")) {
		way = 1;
	    }
	    else if (parts[3].equals("-1")) {
		way = -1;
	    }
	    else {
		way = 0;
	    }
	    lineways.put(parts[0], way);
	    if (parts.length < 7) {
		lim = findLim(parts[1]);
	    }
	    else if (parts[6].length() == 0) {
		lim = findLim(parts[1]);
	    }
	    else {
		lim = parts[6];
	    }
	    if (parts.length < 18) {
		toll = "no";
	    }
	    else if (parts[17].equals("yes")) {
		toll = "yes";
	    }
	    else {
		toll = "no";
	    }
	    bw.write("line("+parts[0]+","+lim+","+toll+").\n");
	}
	br.close();
	bw.close();
	fw.close();
    }

    private static void makeNodes() throws IOException {
	File file = new File("data/nodes.csv");
	FileWriter fw = new FileWriter("nodes.pl");
	BufferedWriter bw = new BufferedWriter(fw);
	FileWriter fw2 = new FileWriter("graph.pl");
	BufferedWriter bw2 = new BufferedWriter(fw2);
	BufferedReader br = new BufferedReader(new FileReader(file));
	br.readLine();
	String st;
	String[] parts;
	String curid = "-1";
	String curnode = "";
	while ((st = br.readLine()) != null) {
	    parts = st.split(",");
	    if (!lineways.containsKey(parts[2])){
		continue;
	    }
	    bw.write("node(" + parts[3] + "," + parts[2] + "," + parts[0] + "," + parts[1] + ").\n");
	    if (parts[2].equals(curid)) {
		if (lineways.get(parts[2]) >= 0) {
		    bw2.write("child(" + curnode + "," + parts[3] + ").\n");
		}
		if (lineways.get(parts[2]) <= 0) {
		    bw2.write("child(" + parts[3] + "," + curnode + ").\n");
		}
	    }
	    curid = parts[2];
	    curnode = parts[3];
	}
	br.close();
	bw.close();
	fw.close();
	bw2.close();
	fw2.close();
    }

    private static String coef(String x) {
	if (x.equals("high")) {
	    return "0.3";
	}
	else if (x.equals("medium")) {
	    return "0.6";
	}
	else if (x.equals("low")) {
	    return "0.9";
	}
	return "1";
    }

    private static void makeTraffic() throws IOException {
	File file = new File("data/traffic.csv");
	FileWriter fw = new FileWriter("traffic.pl");
	BufferedWriter bw = new BufferedWriter(fw);
	BufferedReader br = new BufferedReader(new FileReader(file));
	br.readLine();
	String st;
	String[] parts;
	String[] parts2;
	String[] parts3;
	while ((st = br.readLine()) != null) {
	    parts = st.split(",");
	    if (!lineways.containsKey(parts[0])) {
		continue;
	    }
	    if (parts.length < 3) {
		continue;
	    }
	    if (parts[2].length() == 0) {
		continue;
	    }
	    parts2 = parts[2].split("\\|");
	    for (int i = 0; i < parts2.length; i++) {
		bw.write("traffic("+parts[0]+","+Integer.parseInt(parts2[i].substring(0,2)) + "," + Integer.parseInt(parts2[i].substring(6,8))+","+coef(parts2[i].substring(12)) + ").\n");
	    }
	}
	br.close();
	bw.close();
	fw.close();
    }

    private static void makeTaxis() throws IOException {
	File file = new File("data/taxis.csv");
	FileWriter fw = new FileWriter("taxis.pl");
	BufferedWriter bw = new BufferedWriter(fw);
	BufferedReader br = new BufferedReader(new FileReader(file));
	br.readLine();
	String st;
	String[] parts;
	String[] parts2;
	while ((st = br.readLine()) != null) {
	    parts = st.split(",");
	    if (parts[3].equals("no")) {
		continue;
	    }
	    parts2 = parts[5].split("\\|");
	    for (int i = 0; i < parts2.length; i++) {
		bw.write("taxi("+parts[0]+","+parts[1]+","+parts[2]+","+parts[4].substring(2)+","+parts2[i]+","+parts[6]+","+parts[7]+").\n");
	    }
	}
	br.close();
	bw.close();
	fw.close();
    }

    private static void makeClient() throws IOException {
	File file = new File("data/client.csv");
	FileWriter fw = new FileWriter("client.pl");
	BufferedWriter bw = new BufferedWriter(fw);
	BufferedReader br = new BufferedReader(new FileReader(file));
	br.readLine();
	String c = br.readLine();
	bw.write("client("+c.substring(0, c.length() - 2).replace(":00", "")+").\n");
	bw.close();
	fw.close();
	br.close();
    }
    
    public static void main(String[] args) throws IOException {
	makeLines();
	makeNodes();
	makeTraffic();
	makeClient();
	makeTaxis();
    }
}
