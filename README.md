# GlobalEpidemicSimulation

Simulation of epidemic spreading and applying medicine.
![demo](https://user-images.githubusercontent.com/10689151/28241972-355e0c0e-69a0-11e7-97c8-680a2a42dd1b.gif)

The medical domain has been one of the fastest-growing and largest domains in the world for the last decade or two, and is also one of the ones that rely on the support of IT the most, so it seemed appropriate for us to choose to focus our simulation on that domain. Diseases that spread rapidly on not just a local scale, but even on the global level, are not exceedingly rare, not just in epidemic movies, but also in the real world, as evidenced by diseases such as swine flu (H1N1), and owing mostly to the increasingly globalized and mobile state of the modern world.

As such, we chose the topic to be (a rather simplified) simulation of the spread of theoretical diseases, as well as how we could counteract them using simulated medicines. We approached this topic by first gathering as realistic as possible data and statistics about all the countries in the world, which we used for fine-tuned infection and curing algorithms, the results of which were rendered on a world map in real time.


### The map
We chose to use map data from the website naturalearthdata.com, which provided countries defined by sets of points, as well as information on their borders, and after careful research, settled on using the Geotools library to read this scientific data, which allowed us to scale it and render it easily; otherwise we would have spent a lot of time reinventing the wheel. We did not use a good portion of the more advanced features the library has, and also wished it did not lack some features we needed.


### The algorithm
The disease algorithms takes into account the virulence of the disease, the population density of the country, the percent of infected people, the temperature at the current month in the country, the temperature tolerance and the medical infrastructure there.


### Web scraping
For some of the data required we could not find premade datasets available online, but the data was of large significance to our simulation and still easily available directly on some websites, so we relied on Python’s powerful web scraping modules to scrape information about temperatures, major cities in each country, as well as country borders from those websites. This whole endeavour allowed us to practice some Python, regex and overall data gathering skills.


### Datasets
A world simulation cannot work without the appropriate data for its algorithms, and seeing as our simulation was based on the real world, we definitely needed the accompanying data about real countries. To begin with, we thought that all the country data we initially specified, whether it was to be implemented or used in the first or later iterations, would be easy to find. And while that proved true for some of the most basic country attributes (e.g. population, temperatures), most datasets had missing countries (even ones we didn't expect to be missing), and some types of information entirely lacked freely available datasets of anywhere near the required scope for a simulation on a global scale. Still, we gathered all the datasets that we could find, accepting that we might have to give up on adding some of the data we wanted in the application entirely, but then we ran into a significant problem with actually aggregating that data and transforming it into a state usable by our app – naming and identification standards. There is a large variety of standards that can be used to identify countries so as to avoid relying on (often inconsistently written) names, and perhaps there are even too many. Not only did our datasets (which were often our only “viable” choice) use only singular standards, but those standards varied from set to set, and often had overlapping codes being used for different countries, and there were even datasets using only country names. To handle this problem, we were first forced to find a dataset that covered as many standards as possible for every country, and having done that, map all the standards we needed so we could match the variety we had in the datasets. With that done, we moved on to handling datasets that did not incorporate even a single standard, trying to automate the name matching, which proved doable to an extent, but required the manual handling of specific outliers. Having finally consolidated all of our datasets, we decided we had to try to auto-generate at least some of the data we had missing. Afterwards we sorted the consolidated dataset we had so it was in a more readable and standardized format, and proceeded to import it into our simulation
