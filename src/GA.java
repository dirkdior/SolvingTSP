import java.util.ArrayList;
import java.util.Collections;

public class GA {

    private final ArrayList<Route> eliteRoutes   = new ArrayList<Route>();
    private final ArrayList<Route> newPopulation = new ArrayList<Route>();
    private final int numOfCities;
    private static final int populationSize      = 2000; //Work with a population size of 2000
    private static final int generations         = 200; //Run algorithm over 200 generations
    private static final double mutationRate     = 0.015; //Mutation rate to be 1.5%

    public GA (ArrayList<City> cities) {
        this.numOfCities            = cities.size();
        ArrayList<Route> population = new ArrayList<Route>();

        //spawn first population/generation
        for(int i = 1; i <= populationSize; i++) {
            Route route = createNewRoute(cities); //Creates a new route
            population.add(route); //Add route to population
        }

        //sort firstGeneration in a descending order by fitness
        population.sort(Collections.reverseOrder());

        //Add best route from first generation to elites
        addEliteRouteFromGeneration(population);

        //crossover population over specified number of generations
        for(int i = 1; i <= generations; i++) {
            breedPopulation(population); //crossover Routes in population
            selectForNewGen(); //perform selection on population before breeding again
            attemptMutation(); //perform mutation on newPopulation ArrayList, which should be the new population generated after crossover
            Collections.copy(population, newPopulation); //copy newPopulation into population then clear newPopulation
            newPopulation.clear();
        }
    }

    //returns first route in eliteRoutes, which should be the very best route across all generations
    public Route getBestRoute() {
        return eliteRoutes.get(0);
    }

    //creates a new route out of a list of cities
    private static Route createNewRoute(ArrayList<City> cities) {
        Collections.shuffle(cities); //shuffle list of cities and return as a new Route
        ArrayList<City> newCitiesList = new ArrayList<>(cities);

        return new Route(newCitiesList);
    }

    private void breedPopulation(ArrayList<Route> population) {
        //breed generation, father with mother and adds child to the new population
        for(int i = 0; i < population.size(); i+=2) {
            Route childRoute;
            childRoute = breed(
                    population.get(i),
                    population.get(i + 1)
            );
            newPopulation.add(childRoute);
        }

        //breed mother with father and add child to new population
        for(int i = 0; i < population.size(); i+=2) {
            Route childRoute;
            childRoute = breed(
                    population.get(i + 1),
                    population.get(i)
            );
            newPopulation.add(childRoute);
        }

        //Sort new population in descending by fitness, to get best fit route of all the children created
        newPopulation.sort(Collections.reverseOrder());

        addEliteRouteFromGeneration(newPopulation); //add best route from each population to list of elites before breeding again
    }

    private Route breed(Route father, Route mother) {
        ArrayList<City> childCities = new ArrayList<>(father.getCities());
        ArrayList<City> childGene   = new ArrayList<>(); //ArrayList to build the gene for the child

        int startPosMin = 0; //Min position to generate the start position from
        int max         = numOfCities - 1; //Max position to generate positions from
        int startPos    = (int)(Math.random() * (max - 1 - startPosMin + 1) + startPosMin); //Random position to start the crossover from
        int endPos      = (int)(Math.random() * (max - 1 - startPos + 1) + startPos); //Random position to end the crossover at

        for(int i = 0; i < numOfCities; i++) {
            //So far we aren't within the crossover positions, add up the father genes
            if(!(i >= startPos && i <= endPos)) {
                childGene.add(father.getCities().get(i));
            }
        }

        for(int i = 0; i < numOfCities; i++) {
            //Once we are within the crossover positions, add up the mother genes
            if(i >= startPos && i <= endPos) {
                for(City motherCityInLoop:mother.getCities()) {
                    if(!childGene.contains(motherCityInLoop)) {
                        childCities.set(i, motherCityInLoop);
                        childGene.add(motherCityInLoop);
                        break;
                    }
                }
            }
        }
        return new Route(childCities);
    }

    private void selectForNewGen() {
        int populationSize = newPopulation.size();

        //"Kill" or remove half of the population when sorted by fitness
        newPopulation.subList(populationSize/2, populationSize).clear();

        //Add elite route into the population to be bred with other genes
        for(int i = 0; i < populationSize; i+=2) {
            newPopulation.add(i, eliteRoutes.get(0));
        }
    }

    //Attempt to mutate genes by a random rule
    private void attemptMutation() {
        ArrayList<Route> mutatedPopulation = new ArrayList<>();
        for(Route route : newPopulation) {
            if(Math.random() < mutationRate) {
                mutatedPopulation.add(mutate(route)); //if random rule is met, mutate the selected gene and add to mutated population list
            } else {
                mutatedPopulation.add(route); //if random rule isn't met, add selected gene as it is into the list for mutated population
            }
        }
        Collections.copy(newPopulation, mutatedPopulation); //copy mutated population, which includes some mutated genes, into newPopulation list
    }

    //Takes each gene, and does a swap on the genes
    private Route mutate(Route routeToMutate) {
        ArrayList<City> mutatedRoute = new ArrayList<>(routeToMutate.getCities()); //put list of cities in selected route into a list for mutation

        int swapPosMin = 0; //min position to select for swap
        int max        = numOfCities - 1; //Max position to select for a swap
        int swapTo     = (int)(Math.random() * (max - 1 - swapPosMin + 1) + swapPosMin); //generate random swap index to swap to
        int swapWith   = 0;

        //Start up a loop, to ensure that same position can't be selected as swap indexes for a swap
        for(int i = 1; i <= numOfCities; i++) {
            swapWith = 0;
            swapWith += (int)(Math.random() * (max - 1 - swapPosMin + 1) + swapPosMin); //generate random swap index to swap with
            if(swapWith != swapTo)
                break; //break loop once the swap indexes aren't the same
        }

        Collections.swap(mutatedRoute, swapTo, swapWith); //perform the swap in essence, mutating the "gene"
        return new Route(mutatedRoute); //return mutated "gene" as a new Route
    }

    private void addEliteRouteFromGeneration(ArrayList<Route> population) {
        eliteRoutes.add(population.get(0)); //add best Route from population into the list of eliteRoutes
        eliteRoutes.sort(Collections.reverseOrder()); //sort elite routes to make sure best elites always come first
    }
}