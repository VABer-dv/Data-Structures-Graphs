package ProblemA; /**
 * @author Vladislav Berezhnoy
 * @email vaber93@mail.ru
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;

public class Graph<TDataValue> {
    public List<Edge> edges = new ArrayList<>();
    public List<Vertex> vertices = new ArrayList<>();

    static Function<Object, Comparable> map = new Function<Object, Comparable>() {
        @Override
        public Comparable apply(Object t) {
            return (Comparable) t;
        }
    };

    public static class Vertex<TDataValue> {
        TDataValue value;

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
        protected double weight;

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

    //HeapSort Block starts
    public static List<Vertex> heapSort(List<Vertex> cities) {
        buildheap(cities);
        int n = cities.size() - 1;
        for (int i = n; i > 0; i--) {
            swap(cities, 0, i);
            n--;
            maxheap(cities, 0, n);
        }
        return cities;
    }

    private static void buildheap(List<Vertex> cities) {
        int n = cities.size() - 1;
        for (int i = n / 2; i >= 0; i--) {
            maxheap(cities, i, n);
        }
    }

    private static void maxheap(List<Vertex> cities, int i, int n) {
        int left = 2 * i;
        int right = 2 * i + 1;
        int largest;
        if (left <= n && map.apply(cities.get(left).value).compareTo(cities.get(i).value) > 0) {
            largest = left;
        } else {
            largest = i;
        }

        if (right <= n && map.apply(cities.get(right).value).compareTo(cities.get(largest).value) > 0) {
            largest = right;
        }
        if (largest != i) {
            swap(cities, i, largest);
            maxheap(cities, largest, n);
        }
    }

    private static void swap(List<Vertex> cities, int i, int largest) {
        Vertex t = cities.get(i);
        cities.set(i, cities.get(largest));
        cities.set(largest, t);
    }
    //HeapSort Block ends
    //=====================

    //=====================
    //MergeSort Block Starts
    public static List<Vertex> mergeSort(List<Vertex> cities)
    {
        //create empty List<Vertex> with reserved size
        List<Vertex> tmp = Arrays.asList(new Vertex[cities.size()]);
        mergeSort(cities, tmp,  0,  cities.size() - 1);
        return cities;
    }

    private static void mergeSort(List<Vertex> cities, List<Vertex> tmp, int left, int right)
    {
        if( left < right )
        {
            //Recursively split into parts
            int center = (left + right) / 2;
            mergeSort(cities, tmp, left, center);
            mergeSort(cities, tmp, center + 1, right);
            merge(cities, tmp, left, center + 1, right);
        }
    }

    private static void merge(List<Vertex> cities, List<Vertex> tmp, int left, int right, int rightEnd )
    {
        int last = rightEnd - left + 1;
        int leftEnd = right - 1;
        int temp = left;

        while(left <= leftEnd && right <= rightEnd)
            if(map.apply(cities.get(right).value).compareTo(cities.get(left).value) >= 0)
                tmp.set(temp++, cities.get(left++));
            else
                tmp.set(temp++, cities.get(right++));

        // Copy other Vertexes of left half
        while(left <= leftEnd)
            tmp.set(temp++, cities.get(left++));

        // Copy other Vertexes of right half
        while(right <= rightEnd)
            tmp.set(temp++, cities.get(right++));

        for(int i = 0; i < last; i++, rightEnd--)
            cities.set(rightEnd, tmp.get(rightEnd));
    }
    //MergeSort Block ends

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

        PrintWriter writer = new PrintWriter("around.txt", "UTF-8");

        //get list of incident Vertexes of certain city and sort it
        List<String> citiesFind = Arrays.asList("Donetsk-DU", "Kiev-U", "Lviv-U", "Batumi-G", "Rostov-R");
        for (int i = 0; i < citiesFind.size(); i++) {
            Vertex temp = graph.findVert(citiesFind.get(i));
            if (temp != null) {
                List<Vertex> sortedCities = heapSort(graph.findVert(citiesFind.get(i)).adjacent());
                String str = "";
                for (int j = 0; j < sortedCities.size(); j++) {
                    str = str + sortedCities.get(j).value + " ";
                }
                writer.println(str);
            }
            else
                writer.println();
        }

        writer.close();
    }

}
