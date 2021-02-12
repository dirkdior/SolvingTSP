import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class SolvingTSP {
    public static void main(String[] args) {

        String filePath               = "tspfiles/test4-20.txt"; //set file path
        BufferedReader bufferedReader = null;
        String line                   = "";

        ArrayList<City> cities        = new ArrayList<City>(); //cities from files are populated into this list
        ArrayList<String> cityKeys    = new ArrayList<>(); //add up cities coordinates as keys into this array to allow check for duplicates

        try {

            bufferedReader = new BufferedReader(new FileReader(filePath));
            while ((line = bufferedReader.readLine()) != null) { //loop through each line in the file

                // use whitespace, comma, space, pipe and semicolon as separators
                String[] cols      = line.trim().split("\\s+|[, |;]");
                String name        = cols[0]; //set first colon as city name
                double xCoordinate = Double.parseDouble(cols[1]); //set second colon as the city's x coordinate, parse as a double
                double yCoordinate = Double.parseDouble(cols[2]); //set third colon as the city's y coordinate, parse as a double
                String currentKey  = xCoordinate + ":" + yCoordinate; //make a key from each city coordinate passed from file line

                System.out.println(""+ name + "|" + xCoordinate + "|" + yCoordinate); //print out cities

                //check if current city (key of the coordinates) already exists in cityKeys, allows filtering of duplicates
                if(!cityKeys.contains(currentKey)) {
                    //Add coordinates as a new City since it doesn't already exist
                    cities.add(new City(
                            name,
                            xCoordinate,
                            yCoordinate
                    ));
                    cityKeys.add(currentKey); //add file entry as a key into the cityKeys list
                } else {
                    System.out.println("Duplicate Found: "+ name + "|" + xCoordinate + "|" + yCoordinate); //print out duplicate city
                }
            }
        } catch (IOException e) { //handle any exception that could occur
            e.printStackTrace(); //print out exception
        } finally {
            //attempt to close the file reader and print any exception
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("\nNUMBER OF CITIES: " + cities.size()); //print number of cities

        //Use K-Nearest Neighbour Algorithm
        System.out.println("\nUSING K-NEAREST NEIGHBOUR ALGORITHM");

        long nnStartTime = System.nanoTime(); //start time for k-nn algorithm
        NearestNeighbour nearestNeighbour = new NearestNeighbour(cities); //call NearestNeighbour class and parse cities into constructor

        System.out.println("BEST ROUTE: " + nearestNeighbour.getBestRoute().getRouteAsString()); //print the best route
        System.out.println("DISTANCE: " + nearestNeighbour.getBestRoute().getDistance()); //print the distance

        long nnEndTime   = System.nanoTime(); //stop time for k-nn algorithm

        System.out.println("DURATION (in Seconds): " + (double)(nnEndTime - nnStartTime) / 1_000_000_000.0); //print duration in seconds

        //Use Genetic Algorithm
        System.out.println("\nUSING GENETIC ALGORITHM");
        long gaStartTime = System.nanoTime(); //start time for genetic algorithm

        GA geneticAlgo = new GA(cities); //call GA class and parse cities into constructor

        System.out.println("BEST ROUTE: " + geneticAlgo.getBestRoute().getRouteAsString()); //print the best route
        System.out.println("DISTANCE: " + geneticAlgo.getBestRoute().getDistance()); //print the distance

        long gaEndTime   = System.nanoTime(); //stop time for genetic algorithm

        System.out.println("DURATION (in Seconds): " + (double)(gaEndTime - gaStartTime) / 1_000_000_000.0); //print duration in seconds
    }
}

//This class represents a city
class City {
    private final String name; //every city has a name
    private final double xCoordinate; //a x coordinate
    private final double yCoordinate; //and a y coordinate

    //constructor sets city name, x and y coordinates to the global variables
    public City(String name, double xCoordinate, double yCoordinate) {
        this.name        = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public String getName() {
        return this.name;
    } //get the city's name

    public double getxCoordinate() {
        return this.xCoordinate;
    } //get the city's x coordinate

    public double getyCoordinate() {
        return this.yCoordinate;
    } //get the city's y coordinate
}

//This class represents a Route
class Route implements Comparable<Route> {
    private final double fitness; //every Route has a fitness value
    private final double distance; //every Route has a distance
    private final ArrayList<City> cities; //every Route consists of a list of cities

    //set global variables in Route constructor
    public Route (ArrayList<City> cities) {
        this.cities   = cities;
        this.distance = distance();
        this.fitness  = 1 / distance; //fitness will be the inverse of the distance, a higher fitness represents a shorter path
    }

    //get the routes as names of all the cities concatenated by a comma
    public String getRouteAsString() {
        StringBuilder sb = new StringBuilder();
        for (City city : cities) {
            sb.append(city.getName()).append(", ");
        }
        return sb.toString() + cities.get(0).getName(); //add name of first city to the end as tsp returns to starting point and return as string
    }

    public ArrayList<City> getCities() {
        return cities;
    } //get list of cities in Route

    public double getFitness() {
        return fitness;
    } //get fitness value of Route

    public double getDistance() { return distance; } //get distance of Route

    //calculate distance
    private double distance() {
        double dist     = 0; //start distance as 0
        int numOfCities = cities.size(); //set size of cities list as a local variable

        //loop through all cities and calculate distance between two then add to total distance
        for(int i = 1; i <= numOfCities; i++) {
            if(i == numOfCities) {
                //on last city, add distance between starting city and last city
                dist += distanceBetweenTwo(
                        cities.get(0).getxCoordinate(),
                        cities.get(0).getyCoordinate(),
                        cities.get(i - 1).getxCoordinate(),
                        cities.get(i - 1).getyCoordinate()
                );
            } else {
                //calculate distance between current city (in loop) and next city
                dist += distanceBetweenTwo(
                        cities.get(i - 1).getxCoordinate(),
                        cities.get(i - 1).getyCoordinate(),
                        cities.get(i).getxCoordinate(),
                        cities.get(i).getyCoordinate()
                );
            }
        }
        return dist;
    }

    //return distance between two points 1 and 2
    private static double distanceBetweenTwo(double x1, double y1, double x2, double y2) {
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    //allows each route to be compared by fitness levels
    @Override
    public int compareTo(Route r) {
        return Double.compare(fitness, r.getFitness());
    }
}