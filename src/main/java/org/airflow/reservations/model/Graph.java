package org.airflow.reservations.model;
import org.airflow.reservations.DAO.CityDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

/**
 * The edges of a city are saved in a hashtable which key is the city code, in the constructor
 * of the class is explained how this hash table is created.
 * The nodes are saved in a hash table whose keys are the city codes
 */
public class Graph {
    private Map<String,Node> nodes;
    private Map<String, ArrayList<Edge>> edges;
    private CityDAO cityDAO;
    private ArrayList<City> cities;
    private static final int MAX_DISTANCE_KM = 10000;//Km

    /**
     * Node class with a weigh factor, the reason behind the use of this feature is explained
     * further
     */
    static class Node{
        int Weight_factor = 1;
        City value;
        public Node(City city){
            this.value = city;
        }
        public void setWeight_factor(int newWeightFactor){Weight_factor = newWeightFactor;}
        public int getWeight_factor(){return Weight_factor;}
        public String getCode(){return value.getCode();}
        public int getCityid(){return value.getId();}
        public double getLatitude(){return value.getLatitude();}
        public double getLongitude(){return value.getLongitude();}
        public City getValue(){return value;}
    }

    /**
     * Class edge
     * It takes only saves the city code to look for it in the node hash tables, it easily makes
     * the update of some features as the weightFactor
     */

    public static class Edge{
        String CityCode1;
        String CityCode2;
        double distance;

        public Edge(String cityCode1, String cityCode2,double distance){
            this.CityCode1 = cityCode1;
            this.CityCode2 = cityCode2;
            this.distance = distance;
        }
        // Getters y setters
        public String getCityCode1() {return CityCode1;}
        public String getCityCode2() {return CityCode2;}
        public double getDistance() {return distance;}
        public void setDistance(double newDistance){distance = newDistance;}
    }

    /**
     * Function to determinate the distance between two nodes
     *Each node has a weight factor that in case an airport of a city does not have a right
     *condition to make a scale, we can update it to a high value,so in the function to find
     *the shortest path the node will be ignored
     * @param Node1 First Node
     * @param Node2 Second Node
     * @return Double with the calculated distance
     */
    private double haversine (Node Node1 , Node Node2){
            int earthRadius = 6371;
            double dLat = Math.toRadians(Node2.getLatitude() - Node1.getLatitude());
            double dLon = Math.toRadians(Node2.getLongitude()) - Node1.getLongitude();
            double a = pow(sin(dLat/2),2) +
                    cos(Math.toRadians(Node1.getLatitude())) *
                            cos(Math.toRadians(Node2.getLatitude())) * pow(sin(dLon/2),2);
            double c = 2 * atan2(sqrt(a), sqrt(1-a));
            return earthRadius * c * Node1.Weight_factor * Node2.Weight_factor;
    }

    /**
     * Function to create an edge, it looks first if the node has not been created, is auxiliar
     * to the constructor of the class Graph
     * @param Node1 First node
     * @param Node2 second node
     * In order thar we don't have a bidirectional graph no matter if Node 1 or 2 is destiny
     * or origin
     */
    private void addEdge(Node Node1, Node Node2) {
        double distance = haversine(Node1, Node2);
        if (distance <= MAX_DISTANCE_KM) {
            List<Edge> edgeList1 = edges.get(Node1.getCode());
            List<Edge> edgeList2 = edges.get(Node2.getCode());

            for(Edge edge : edgeList1) if(edge.getCityCode2().equals(Node2.getCode())) return;
            for(Edge edge : edgeList2) if(edge.getCityCode1().equals(Node1.getCode())) return;

            Edge edge = new Edge(Node1.getCode(), Node2.getCode(), distance);
            edges.get(Node1.getCode()).add(edge);
        }
    }

    /**
     * The main contructor of the class, it makes all the cities in database a node, then, create
     * edges only if the distances between the two nodes are less than the parameter MAX_DISTANCE_KM
     * the edges are not biderectional so they will be created in this way:
     * edges of city 1 : List of all posible edges without city 1
     * edges of city 2 : List of all posible edges without city 2 and city 1
     * edges of city 3 : List of all posible edges without city 3 ,city 2 and city 1
     * etc
     * In this way if you would like to find all the neighbours of a city , for example city 5,
     * you should look in the list of edges of city 5, city 4 ,city 3 ... but not in edges of city 6,7,...
     * @param connection connection to the database
     * @throws SQLException if database access error occurs
     */
    public Graph(Connection connection) throws SQLException {
        cityDAO = new CityDAO(connection);
        cities = cityDAO.getAll();
        nodes = new HashMap<>();
        edges = new HashMap<>();
        for(City city : cities){
            Node node = new Node(city);
            nodes.put(city.getCode(),node);
        }
        ArrayList<City> auxNodes = new ArrayList<>(cities);
        for(City city1 : cities){
            for(City city2 : auxNodes){
                if(city1.getId() != city2.getId()){
                    addEdge(nodes.get(city1.getCode()),nodes.get(city2.getCode()));
                }
            }
            auxNodes.remove(city1);
        }
    }

    /**
     * Function to update the weight of the neighbors, auxiliar to the function UpdateWeight
     * @param cityCode city which neighbors to update
     */
    private void updateNeighbours (String cityCode){
        ArrayList<Edge> edgeList = edges.get(cityCode);
        for(Edge edge : edgeList){
            edge.setDistance(haversine(nodes.get(cityCode),nodes.get(edge.getCityCode2())));
        }
        int i = 0;
        while(!cities.get(i).getCode().equals(cityCode)){
            for(Edge edge : edges.get(cities.get(i).getCode())){
                if(edge.getCityCode2().equals(cityCode)){
                    edge.setDistance(haversine(nodes.get(cities.get(i).getCode()),nodes.get(edge.getCityCode2())));
                }
            }
            i++;
        }
    }

    /**
     * Function to Update the weight Factor of a city
     * @param cityCode city to update
     * @param newWeightFactor weight factor to update
     * @throws SQLException if database access error occurs
     */
    public void update_weight(String cityCode,int newWeightFactor) throws SQLException{
        if (cityDAO.cityExists(cityCode)) {

            Node node = nodes.get(cityCode);
            if (node != null) {
                node.setWeight_factor(newWeightFactor);
            }
            updateNeighbours(cityCode);
        }
    }

    /**
     * Class usefull to implement Dijkstra algorithm
     */

    public static class PathStep {
        private City city;
        private double distanceFromPrevious;

        public PathStep(City city, double distanceFromPrevious) {
            this.city = city;
            this.distanceFromPrevious = distanceFromPrevious;
        }
        public City getCity() {return city;}
        public double getDistanceFromPrevious() {return distanceFromPrevious;}
    }

    // Esto es una idea pero no esta completa, solo sirve si no hay escalas.
    /*
    public ArrayList<PathStep> shortestPath(String OriginCityCode, String DestinationCityCode) throws SQLException {
        ArrayList<PathStep> path = new ArrayList<>();
        if (cityDAO.cityExists(OriginCityCode) && cityDAO.cityExists(DestinationCityCode)) {
            Node Originnode = nodes.get(OriginCityCode);
            Node DestinationNode = nodes.get(DestinationCityCode);
            if (Originnode != null && DestinationNode != null) {
                List<Edge> OriginNeighbours = edges.get(OriginCityCode);
                for(Edge edge : OriginNeighbours) if(edge.getCityCode2().equals(DestinationCityCode)){
                    path.add(new PathStep(DestinationNode.value,edge.getDistance()));
                    return path;
                }
                List<Edge> edgeList2 = edges.get(DestinationCityCode);
                for(Edge edge : edgeList2) if(edge.getCityCode1().equals(OriginCityCode)) {
                    path.add(new PathStep(DestinationNode.value,edge.getDistance()));
                    return path;
                }

            }


        }

    }*/
}
