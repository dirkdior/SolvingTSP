# SolvingTSP
> Solving a Traveling salesman problem in Java

## Algorithm(s) Implemented
### K-Nearest Neighbour Algorithm
In this implementation of the nearest neighbour algorithm, each city is selected as a starting point then the distance between that city and the other cities not yet selected is measured, the city with the closest distance to the selected city is selected as the next city to travel. This is done until there is only one city left that hasn't been selected, then that city is added as the last city before returning to the starting city. This whole process is then carried out over again, using another city as the starting point. Each travelled path calculated according to the starting city is added into a list. At the end of the process, the list is sorted, and the route with the lowest travel distance is returned as the best path for the salesman to travel.

### Genetic Algorithm 
This is implemented as the second algorithm in this solution. Components used in this GA are crossover, mutation, selection and elites. In this case, the GA has been set to run over 200 generations with a population size of 2,000 and a mutation rate of 1.5%

`Crossover`:​ ​ Here, two cities are selected as the father and mother gene. The ordered crossover method is used so there are no duplicate cities as the child is created. The breeding is done twice so two children are created and added into the new population.

`Mutation`:​ Mutation probability in this implementation is set at 1.5%. Swap mutation is done when a gene is mutated, this means two cities are selected at random from the route and their positions are swapped.

`Selection`:​ Once the breeding has been done and selection is performed. In this implementation, the children created from breading the previous generation are selected and sorted. The ones with the highest fitness (inverse of distance) will be at the top of the list, and those with lower fitness stay bottom. Half of the population in the list is then removed, which ideally means the least fit routes have been done away with.

`Elites`: ​ Upon each generation, the very best route is selected and added into a list called “elites”. The list is always sorted, so the best elite stays top. When breeding, the best elite is added into the population in multiple positions till the population completes the total population size of 2,000.

###tspfiles folder
This folder contains the cities, and their coordinates in a line by line basis.