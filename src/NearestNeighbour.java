import java.util.*;

public final class NearestNeighbour {

    //This class holds the distance between each city and the next
    private static class CityDistances implements Comparable<CityDistances> {
        private final double distance;
        private final City fromCity;
        private final City toCity;

        public CityDistances(double distance, City fromCity, City toCity) {
            this.distance = distance;
            this.fromCity = fromCity;
            this.toCity  = toCity;
        }

        public double getDistance() {
            return distance;
        }

        public City getFromCity() {
            return fromCity;
        }

        public City getToCity() {
            return toCity;
        }

        //Allows distances to be compared to one another, makes sorting possible
        @Override
        public int compareTo(CityDistances d) {
            return Double.compare(distance, d.getDistance());
        }
    }

    private final ArrayList<City> citiesNotSearched = new ArrayList<City>(); //holds list of cities as they are searched
    private final ArrayList<City> citiesNotVisited  = new ArrayList<City>(); //holds list of cities as they are "visited" or selected
    private final ArrayList<City> tspJourney        = new ArrayList<City>(); //used to build out the tsp travelling path
    private final ArrayList<Route> bestRoutes       = new ArrayList<Route>(); //holds a list of best routes
    private Route bestRoute; //holds the best Route

    public NearestNeighbour(ArrayList<City> cities) {
        int numOfCities = cities.size(); //set the number of cities to the size of the cities list
        if(numOfCities <= 2) {
            System.out.println("Not enough cities"); //K-NN cannot handle just 2 cities
        } else {
            for(int a = 1; a <= numOfCities; a++) {
                ArrayList<City> citiesSwapped = new ArrayList<>(cities); //a new list which is an exact copy of the cities list
                if(a > 1) { //If the algorithm has already run once
                    //make the index position of this list the next city, which will them be searched for the Nearest Neighbour
                    Collections.swap(citiesSwapped, 0, a - 1);
                    //clear out lists
                    tspJourney.clear();
                    citiesNotSearched.clear();
                    citiesNotVisited.clear();
                }
                tspJourney.add(citiesSwapped.get(0)); //start travelling path with a city to be searched
                citiesNotSearched.addAll(citiesSwapped); //add to unsearched and unvisited lists
                citiesNotVisited.addAll(citiesSwapped);
                //Add first city back into the list, to be the last city to be visited as tsp goes back to the starting point
                citiesNotVisited.add(citiesNotVisited.get(0));

                //loop through cities and get the nearest city
                for(int i = 1; i < numOfCities; i++) {
                    City nearestCity = findNearestNeighbour(citiesNotSearched); //find nearest city from a list of cities
                    citiesNotSearched.remove(citiesNotSearched.get(0)); //remove first city from list, as it was used as the point of search
                    citiesNotSearched.remove(nearestCity); //also remove the city picked as nearest
                    citiesNotSearched.add(0, nearestCity); //now add nearest city back into list in index 0 for it to be used as next point for search
                }
                ArrayList<City> tspJourneyChecked = new ArrayList<City>(tspJourney);
                bestRoutes.add(new Route(tspJourneyChecked)); //Add best routes for each city into bestRoutes list
            }
            //call this function to set bestRoute variable to the shortest route of the bestRoutes list
            setBestRoute();
        }
    }

    //accepts a list of cities, returns the city nearest to the one in the first position of the array
    private City findNearestNeighbour(ArrayList<City> cities) {

        int citiesSize = cities.size(); //set the number of cities into a local variable
        if(citiesSize == 2) {
            tspJourney.add(citiesNotSearched.get(citiesSize - 1));
            return citiesNotSearched.get(0); //in the case of two cities, return the next city
        } else {

            ArrayList<CityDistances> distances = new ArrayList<CityDistances>(); //hold each city in the list and its distance with the next
            for(int i = 1; i < citiesSize; i++) { //loop through all cities in the city list
                //get distance of first city in list compared to another and set as a new distance added to the list of city distances
                double distance = getDistance(
                        cities.get(0).getxCoordinate(),
                        cities.get(0).getyCoordinate(),
                        cities.get(i).getxCoordinate(),
                        cities.get(i).getyCoordinate()
                );
                distances.add(new CityDistances(
                        distance,
                        cities.get(0),
                        cities.get(i)
                ));
            }
            Collections.sort(distances); //sort all the distances, city with the shortest distance to the selected city moves top

            CityDistances closestCity = distances.get(0); //get city with the shortest distance as the closest city

            citiesNotVisited.remove(closestCity.getToCity()); //since the closest city to the next has now been "visited", remove from list of unvisited cities
            //add closest city to selected city into the tsp journey
            tspJourney.add(closestCity
                    .getToCity()
            );
            return closestCity.getToCity(); //return the closest city to the selected
        }
    }

    //get distance between two points 1 and 2
    private double getDistance(double x1, double y1, double x2, double y2) {
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    //sorts the bestRoutes list to determine shortest route then sets shortest route to the bestRoute global variable
    private void setBestRoute() {
        bestRoutes.sort(Collections.reverseOrder()); //routes are sorted according to fitness, best fit routes move top
        this.bestRoute = bestRoutes.get(0); //first route in list, which should be the fittest and shortest is returned
    }

    //get the best route bestRoute global variable
    public Route getBestRoute() {
        return bestRoute;
    }

}
