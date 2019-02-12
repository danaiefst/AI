import java.util.*;
import javafx.util.*;
import java.io.*;
import java.lang.Math;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;

public class Main {

    private static JIPEngine jip;
    private static JIPTermParser parser;
    private static JIPQuery jipQuery; 
    private static JIPTerm term;

    private static String getClosestNode(String x, String y) {
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("closestNode(" + x + "," + y + "X)."));
	return jipQuery.nextSolution().getVariablesTable().get("X").toString();
    }
    	
    private static ArrayList<String> neighbours(String x) {
	ArrayList<String> ret = new ArrayList<>();
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("child(" + x + ",Y)."));
	term = jipQuery.nextSolution();
	while (term != null) {
	    ret.add(term.getVariablesTable().get("Y").toString());
	    term = jipQuery.nextSolution();
	}
	return ret;
    }
	
    private static ArrayList<ArrayList<String>> reconstructPaths2(HashMap<String, ArrayList<String>> cameFrom, String end){
	ArrayList<ArrayList<String>> ret = new ArrayList<>();
	ArrayList<ArrayList<String>> newret = new ArrayList<>();
	ArrayList<String> temp = new ArrayList<>();
	ArrayList<String> oldpath, newpath, fathers;
	String child;
	temp.add(end);
	ret.add(temp);
	int count = 0, ulimit = 20, dlimit = -1;
	while(true){
	    newret = new ArrayList<>();
	    for(int i = 0; i < ret.size(); i++){
		oldpath = ret.get(i);
		child = oldpath.get(oldpath.size()-1);
		fathers = cameFrom.get(child);
		if(fathers.size() == 0){
		    continue;
		}
		for(int j = 0; j < fathers.size(); j++){
		    newpath = new ArrayList<>();
		    newpath.addAll(oldpath);
		    newpath.add(fathers.get(j));
		    newret.add(newpath);
		}
	    }
	    if(newret.isEmpty()) break;
	    ret = newret;
	    count++;
	}
	return ret;
    }
	
    private static double heuristic(String start, String end) {
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("node(" + start + ",_, X1, Y1)."));
	term = jipQuery.nextSolution();
	double x1 = Double.parseDouble(term.getVariablesTable().get("X1").toString());
	double y1 = Double.parseDouble(term.getVariablesTable().get("Y1").toString());
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("node(" + end + ",_, X1, Y1)."));
	term = jipQuery.nextSolution();
	double x2 = Double.parseDouble(term.getVariablesTable().get("X1").toString());
	double y2 = Double.parseDouble(term.getVariablesTable().get("Y1").toString());
	return 5 * Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) / 60 / 1000;
    }

    private static double distance(String start, String end, String time) {
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("distance(" + start + "," + end + "," + time + ",X)."));
	return  Double.parseDouble(jipQuery.nextSolution().getVariablesTable().get("X").toString());
    }

    private static double pathSize(ArrayList<String> path, String time) {
	double ret, temp;
	Double mytime = Double.parseDouble(time);
	ret = 0;
	for (int i = 0; i < path.size() - 1; i++) {
	    temp = distance(path.get(i), path.get(i+1), time);
	    ret += temp;
	}
	return ret;
    }
    
    
    private static ArrayList<ArrayList<String>> astar(String start, String end, double time) throws IOException{
	HashSet<String> closedSet = new HashSet<>();
	HashSet<String> openSet = new HashSet<>();
	openSet.add(start);
	HashMap<String, ArrayList<String>> cameFrom = new HashMap<>();
	HashMap<String, Double> gScore = new HashMap<>();
	gScore.put(start, 0.0);
	HashMap<String, Double> fScore = new HashMap<>();		
	fScore.put(start, heuristic(start, end));
	ArrayList<String> tempcame = new ArrayList<>();
	cameFrom.put(start, tempcame);
	String current;
	String temp;
	double tentativeGScore;
	Iterator i;
	ArrayList<String> neigh;
	while (!openSet.isEmpty()) {
	    //find next current node
	    i = openSet.iterator();
	    temp = (String)i.next();
	    double minscore = fScore.get(temp);
	    current = temp;
	    while (i.hasNext()) {
		temp = (String)i.next();
		if (fScore.get(temp) < minscore) {
		    current = temp;
		    minscore = fScore.get(temp);
		}
	    }
	    if (current.equals(end)) {
		return reconstructPaths2(cameFrom, current);
	    }
	    neigh = neighbours(current);
	    openSet.remove(current);
	    closedSet.add(current);	    
	    for (int j = 0; j < neigh.size(); j++) {
		if (closedSet.contains(neigh.get(j))) {
		    continue;
		}
		tentativeGScore = gScore.get(current) + distance(current, neigh.get(j), (new Double(time + gScore.get(current))).toString());
		if (!openSet.contains(neigh.get(j))) {
		    openSet.add(neigh.get(j));
		    tempcame = new ArrayList<>();
		    tempcame.add(current);
		    cameFrom.put(neigh.get(j), tempcame);
		    gScore.put(neigh.get(j), tentativeGScore);
		    fScore.put(neigh.get(j), gScore.get(neigh.get(j)) + heuristic(neigh.get(j),end));
		}
		else if (tentativeGScore > gScore.get(neigh.get(j))) {
		    continue;
		}
		else if (tentativeGScore == gScore.get(neigh.get(j))) {
		    cameFrom.get(neigh.get(j)).add(current);
		}
		else {
		    tempcame = new ArrayList<>();
		    tempcame.add(current);
		    cameFrom.replace(neigh.get(j), tempcame);
		    gScore.replace(neigh.get(j), tentativeGScore);
		    fScore.replace(neigh.get(j), gScore.get(neigh.get(j)) + heuristic(neigh.get(j), end));
		}
	    }
	}
	return null;
    }
		
    public static void main(String[] args) throws JIPSyntaxErrorException, IOException {
	jip = new JIPEngine();
	jip.consultFile("client.pl");
	System.out.println("Loaded client.pl");
	jip.consultFile("data.pl");
	System.out.println("Loaded data.pl");
	jip.consultFile("lines.pl");
	System.out.println("Loaded lines.pl");
	jip.consultFile("nodes.pl");
	System.out.println("Loaded nodes.pl");
	jip.consultFile("taxis.pl");
	System.out.println("Loaded taxis.pl");
	jip.consultFile("traffic.pl");
	System.out.println("Loaded traffic.pl");
	jip.consultFile("graph.pl");
	System.out.println("Loaded graph.pl");
	parser = jip.getTermParser();
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("client(X,Y,Xd,Yd,Time,People,Language)."));
	term = jipQuery.nextSolution();
	String x = term.getVariablesTable().get("X").toString();
	String y = term.getVariablesTable().get("Y").toString();
	String xd = term.getVariablesTable().get("Xd").toString();
	String yd = term.getVariablesTable().get("Yd").toString();
	Double time = Double.parseDouble(term.getVariablesTable().get("Time").toString());
	String time2 = term.getVariablesTable().get("Time").toString();
	String people = term.getVariablesTable().get("People").toString();
	String language = term.getVariablesTable().get("Language").toString();
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("closest_node(" + x + "," + y + ",X)."));
	String client = jipQuery.nextSolution().getVariablesTable().get("X").toString();
	System.out.println("Found client");
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("closest_node(" + xd + "," + yd + ",X)."));
	String dest = jipQuery.nextSolution().getVariablesTable().get("X").toString();
	System.out.println("Found destination");
	jipQuery = jip.openSynchronousQuery(parser.parseTerm("can_ride(client(" + x + "," + y +"," + xd + "," + yd + "," + time2 + "," + people + "," + language + "), X)."));

	ArrayList<String> taxis_ids = new ArrayList<>();
	while((term = jipQuery.nextSolution()) != null) {
	    taxis_ids.add(term.getVariablesTable().get("X").toString());
	}
	System.out.println("Found available taxis");
	ArrayList<Pair<String,Double>> taxis;
	ArrayList<Pair<String, String>> KML_taxis = new ArrayList<>();
	taxis = new ArrayList<>();
	ArrayList<ArrayList<String>> path_to_dest;
	ArrayList<ArrayList<String>> path;
	ArrayList<ArrayList<ArrayList<String>>> paths = new ArrayList<>();
	ArrayList<ArrayList<ArrayList<Pair<String,String>>>> KML_paths = new ArrayList<>();
	double dist,min_dist = 24;
	String min_dist_i = "", xt, yt;
	Pair<String, String> coor;
	for(String i: taxis_ids) {
	    jipQuery = jip.openSynchronousQuery(parser.parseTerm("taxi(X,Y,"+i+",_,_,_,_), closest_node(X,Y,Id)."));
	    term = jipQuery.nextSolution();
	    String start = term.getVariablesTable().get("Id").toString();
	    xt = term.getVariablesTable().get("X").toString();
	    yt = term.getVariablesTable().get("Y").toString();
	    coor = new Pair(xt,yt);
	    KML_taxis.add(coor);
	    path = astar(start, client, time);
	    ArrayList<ArrayList<Pair<String,String>>> tempi = new ArrayList<>();
	    for (int j = 0; j < path.size(); j++) {
	    	ArrayList<Pair<String, String>> tempj = new ArrayList<>();
	    	for (int k = 0; k < path.get(j).size(); k++) {
	    		jipQuery = jip.openSynchronousQuery(parser.parseTerm("node(" + path.get(j).get(k) + ",_,X,Y)."));
	    		term = jipQuery.nextSolution();
	    		xt = term.getVariablesTable().get("X").toString();
	    		yt = term.getVariablesTable().get("Y").toString();
	    		coor = new Pair(xt, yt);
	    		tempj.add(coor);
	    	}
	    	tempi.add(tempj);
	    }
	    KML_paths.add(tempi);
	    dist = pathSize(path.get(0), time.toString());
	    if(dist < min_dist) {
	    	min_dist = dist;
	    	min_dist_i = i;
	    }
	    System.out.println(i + " " + dist);
	    taxis.add(new Pair(start, dist));
	}
	ArrayList<ArrayList<Pair<String,String>>> KML_dest = new ArrayList<>();
	path_to_dest = astar(client, dest, time + min_dist);
	dist = pathSize(path_to_dest.get(0), time.toString());
	for (ArrayList<String> i : path_to_dest) {
		ArrayList<Pair<String,String>> tempi = new ArrayList<>();
		for(String j : i) {
			jipQuery = jip.openSynchronousQuery(parser.parseTerm("node(" + j + ",_,X,Y)."));
	    		term = jipQuery.nextSolution();
	    		xt = term.getVariablesTable().get("X").toString();
	    		yt = term.getVariablesTable().get("Y").toString();
	    		coor = new Pair(xt, yt);
	    		tempi.add(coor);
	    	}
	    	KML_dest.add(tempi);
	}
	KMLCreator.createKML(KML_paths, new Pair(x, y), KML_taxis, taxis_ids, min_dist_i, "outfile.kml", KML_dest);
    }
}
