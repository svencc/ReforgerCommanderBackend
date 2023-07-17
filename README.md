# RECOM

## ARMA Reforger+ AI Commander Backend Project

![RECOM.png](md-media%2FRECOM.png)


## Examples % Progress
To show some progress, here are some examples of the current state of the project.

What works and what you can see in the example images:
* Map Scanning and persisting to DB
* basic Cluster Generation (Convex Hull) using ML k-means-nearest-neighbour DBSCAN algorithm
* basic Cluster Rendering
* Configuration System (DB based) 
    * Cluster Entity-Type Configuration

![cluster-example-1.png](md-media%2Fcluster-example-1.png)
![cluster-example-2.png](md-media%2Fcluster-example-2.png)
![cluster-example-3.png](md-media%2Fcluster-example-3.png)

To create more intelligence from map data the following features are planned:
* Forest detection / forest heatmap
* Terrain heightmap
* Mil detection
* Industrial detection
* Qualification and prioritization of clusters / targets

After having more intelligence from the map data, the work on the AI Commander will start.
Planned is to harness the power of OpenAI to create a commander that make decisions and gives orders to his units.

## Setup or run mariaDb docker container

Just execute the docker/mariadb-docker-setup-or-start-win.ps1 script from the docker directory!
Database is ready configured for application-server connection.

## Run application server

Run com.recom.Application with "local" profile.
application-local.properties is preconfigured to work with the provided docker-mariaDb instance.

## Project Pages:

- https://github.com/svencc/ReforgerCommanderClient
- https://github.com/svencc/ReforgerCommanderBackend
- Discord: https://discord.gg/SsJjAS9dqq

