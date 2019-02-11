import java.util.*;
import java.io.*;

import data.com.ugos.jiprolog.engine.JIPEngine;
import data.com.ugos.jiprolog.engine.JIPQuery;
import data.com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import data.com.ugos.jiprolog.engine.JIPTerm;
import data.com.ugos.jiprolog.engine.JIPTermParser;

public class Main {

	private static JIPEngine jip;
	private static JIPTermParser parser;
	private static JIPQuery jipQuery; 
        private static JIPTerm term;

	private static ArrayList<ArrayList<Node>> astar(int start, int end) {
		HashSet<Integer> closedSet = new HashSet<>();
		HashSet<Integer> openSet = new HashSet<>();
		openSet.add(start);
		HashMap<Integer, ArrayList<Node>> cameFrom = new HashMap<>();
		HashMap<Node, Double> gScore = new HashMap<>();
		gScore.put(start, 0.0);
		HashMap<Node, Double> fScore = new HashMap<>();
		fScore.put(start, start.weight(end));
		ArrayList<Node> tempcame = new ArrayList<>(), nb;
		cameFrom.put(start, tempcame);
		Node current;
		Node temp;
		double tentativeGScore;
		Iterator i;
		while (!openSet.isEmpty()) {
		    //find next current node
		    i = openSet.iterator();
		    temp = (Node)i.next();
		    double minscore = fScore.get(temp);
		    current = temp;
		    while (i.hasNext()) {
			temp = (Node)i.next();
			if (fScore.get(temp) < minscore) {
			    current = temp;
			    minscore = fScore.get(temp);
			}
		    }

		    if (current == end) {
			return reconstructPaths2(cameFrom, current);
		    }
		    for (int l = 0; l < current.edges.size(); l++) {
			if ((int)current.name.id != (int)current.edges.get(l).name.id) {
			    if (current.edges.get(l) == end) {
				return reconstructPaths2(cameFrom, current);
			    }
			}
		    }

		    openSet.remove(current);
		    closedSet.add(current);
		    for (int l = 0; l < current.edges.size(); l++) {
		    	if ((int)current.edges.get(l).name.id != (int)current.name.id) {
			    closedSet.add(current.edges.get(l));
			    openSet.remove(current.edges.get(l));
		    	}
		    }
		    
		    nb = neighbours(current);
		    for (int j = 0; j < nb.size(); j++) {
			if (closedSet.contains(nb.get(j))) {
			    continue;
			}
			tentativeGScore = gScore.get(current) + current.weight(nb.get(j));
			if (!openSet.contains(nb.get(j))) {
			    openSet.add(nb.get(j));
			    tempcame = new ArrayList<>();
			    tempcame.add(current);
			    cameFrom.put(nb.get(j), tempcame);
			    gScore.put(nb.get(j), tentativeGScore);
			    fScore.put(nb.get(j), gScore.get(nb.get(j)) + nb.get(j).weight(end));
			}
			else if (tentativeGScore > gScore.get(nb.get(j))) {
			    continue;
			}
			else if (tentativeGScore == gScore.get(nb.get(j))) {
			    cameFrom.get(nb.get(j)).add(current);
			}
			else {
			    tempcame = new ArrayList<>();
			    tempcame.add(current);
			    cameFrom.replace(nb.get(j), tempcame);
			    gScore.replace(nb.get(j), tentativeGScore);
			    fScore.replace(nb.get(j), gScore.get(nb.get(j)) + nb.get(j).weight(end));
			}
		    }
		}
		return null;
	}

	private static String getClosestNode(String x, String y) {
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("closestNode(" + x + "," + y + "X)."));
		return jipQuery.nextSolution().getVariablesTable().get("X").toString();
    	}
    	
    	private static ArrayList<String> neighbours(String x)
		
	public static void main(String[] args) throws JIPSyntaxErrorException, IOException {
		
		jip = new JIPEngine();
		jip.consultFile("data.pl");
		jip.consultFile("lines.pl");
		jip.consultFile("nodes.pl");
		jip.consultFile("client.pl");
		jip.consultFile("taxis.pl");
		jip.consultFile("traffic.pl");
		jip.consultFile("graph.pl");
		
		parser = jip.getTermParser();
		
		
		System.out.println("CASE 1");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("likes(" + x + "," + y + ")."));
		if (jipQuery.nextSolution() != null) {
			System.out.println("Yes. " + x + " likes " + y + ".");
		} else {
			System.out.println("No. " + x + " doesn't like " + y + ".");
		}
		
		System.out.println("CASE 2");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("likes(" + x + ",Y)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " likes " + term.getVariablesTable().get("Y").toString());
			term = jipQuery.nextSolution();
		}

		System.out.println("CASE 3");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("agree(X,Y)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(term.getVariablesTable().get("X").toString() + " agrees with " + term.getVariablesTable().get("Y").toString());
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 4");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("age(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " is " + term.getVariablesTable().get("Z").toString() + " years old.");
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 5");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("prefers(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " prefers " + term.getVariablesTable().get("Z").toString());
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 6");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("prefersHealthy(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " prefers " + term.getVariablesTable().get("Z").toString());
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 7");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("prefersMost(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " prefers " + term.getVariablesTable().get("Z").toString());
			term = jipQuery.nextSolution();
		}

	}
}
