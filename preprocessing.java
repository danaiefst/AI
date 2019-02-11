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
	    return "110";
	} else if (x.equals("motorway_link")) {
	    return "60";
	} else if (x.equals("motorway")) {
	    return "120";
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
    
    public static void main(String[] args) throws IOException {
	makeLines();
	makeNodes();
    }
}
