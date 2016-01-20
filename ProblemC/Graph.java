package ProblemC; /**
 * @author Vladislav Berezhnoy
 * @email vaber93@mail.ru
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;

public class Graph<TDataValue, TWeight> {
    public static List<Edge> edges = new ArrayList<>();
    public static List<Vertex> vertices = new ArrayList<>();
    public static List<Edge> ignore = new ArrayList<>();

    static Function<Object, Comparable> map = new Function<Object, Comparable>() {
        @Override
        public Comparable apply(Object t) {
            return (Comparable) t;
        }
    };

    public static class Vertex<TDataValue> {
        TDataValue value;
        boolean visited = false;
        Vertex previous = null;
        protected List<Edge> incidents = new ArrayList<>();

        public Vertex(TDataValue value) {
            this.value = value;
        }

        public TDataValue getValue() {
            return value;
        }

        //add edge to the incidents list to make adjacent() calls faster
        public Edge addAdjacent(Edge edge) {
            incidents.add(edge);
            return edge;
        }

        //gain list of adjscent Vertexes (cities)
        public List<Vertex> adjacent() {
            List<Vertex> adjacent = new ArrayList<>();
            for (int i = 0; i < incidents.size(); i++) {
                if (map.apply(incidents.get(i).getA().value).compareTo(value) == 0)
                    adjacent.add(incidents.get(i).getB());
                else
                    adjacent.add(incidents.get(i).getA());
            }
            return adjacent;
        }
    }

    static class Edge {
        protected Vertex a, b;

        //crate edge of adjacent Vertex
        public Edge(Vertex a, Vertex b) {
            this.a = a;
            this.b = b;
        }

        //get 1st Vertex of Edge
        public Vertex getA() {
            return a;
        }

        //get 2nd Vertex of Edge
        public Vertex getB() {
            return b;
        }
    }

    private Vertex findVert(TDataValue city) {
        for (int i = 0; i < vertices.size(); i++) {
            if (map.apply(city).compareTo(vertices.get(i).getValue()) == 0)
                return vertices.get(i);
        }
        return null;
    }

    public static List<Vertex> bfs(Vertex from, Vertex to)
    {
        int visited = 0;
        Queue queue = new LinkedList();
        queue.add(from);
        from.visited = true;
        boolean passBuilt = false;
        while(!queue.isEmpty() && !passBuilt) {
            Vertex vertex = (Vertex)queue.remove();
            Vertex child=null;
            //can go further?
            while((child=getUnvisited(vertex))!=null && !passBuilt) {
                child.visited=true;
                child.previous = vertex;
                queue.add(child);
                if (child == to){
                    passBuilt = true;
                }
            }
        }

        // Clear visited vertexes for future bfs
        clearVisited();

        //create pass
        List<Vertex> pass =new ArrayList<>();
        while (from != to){
            pass.add(to);
            to = to.previous;
        }
        pass.add(to);

        return  pass;
    }

    private static void clearVisited()
    {
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visited = false;
        }
    }

    private static Vertex getUnvisited(Vertex vertex)
    {
        List<Vertex> adjacent = vertex.adjacent();
        for (int i = 0; i < adjacent.size(); i++) {
            if (adjacent.get(i).visited == false) {
                return adjacent.get(i);
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        Graph graph = new Graph();
        //Prepare to read file
        StringBuffer sb = null;
        Scanner in = new Scanner(new File("cities.txt"));
        sb = new StringBuffer();
        //1st line
        sb.append(in.nextLine()).append("\n");
        String[] cities = sb.toString().split("\\s+");

        //add vertices
        for (int i = 0; i < cities.length; i++) {
            Vertex temp = new Vertex(cities[i]);
            graph.vertices.add(i, temp);
        }
        sb = sb.delete(0, sb.length());

        //2nd line
        sb.append(in.nextLine()).append("\n");
        cities = sb.toString().split("\\s+");

        //create edge and add it to the incident list of certain Vertex
        for (int i = 0; i < cities.length / 2; i++) {
            Vertex from = graph.findVert(cities[i * 2]);
            Vertex to = graph.findVert(cities[i * 2 + 1]);
            Edge temp = new Edge(from, to);
            graph.edges.add(temp);
            from.addAdjacent(temp);
            to.addAdjacent(temp);
        }

        PrintWriter writer = new PrintWriter("travel.txt", "UTF-8");
        //1st answer
        List<Vertex> pass = bfs(graph.findVert("Rostov-R"), graph.findVert("Melitopol-U"));
        String str = String.valueOf((pass.size() - 1)) + " ";
        for (int i = 0; i < pass.size(); i++) {
            str = str + pass.get(i).value + " ";
        }
        writer.println(str.substring(0, str.length() - 1));

        //2nd answer
        pass = bfs(graph.findVert("Lugansk-DU"), graph.findVert("Sukhumi-DG"));
        str = String.valueOf((pass.size() - 1)) + " ";
        for (int i = 0; i < pass.size(); i++) {
            str = str + pass.get(i).value + " ";
        }
        writer.println(str.substring(0, str.length()-1));

        writer.close();
    }

}
