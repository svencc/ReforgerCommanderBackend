# RECOM

## ARMA Reforger+ AI Commander Backend Project

![Logo](md-media/logo.png)

## Examples % Progress
To show some progress, here are some examples of the current state of the project.

What works and what you can see in the example images:
* Map Scanning and persisting to DB
* basic Cluster Generation (Convex Hull)
* basic Cluster Rendering
* Configuration System (DB based) 
    * Cluster Entity-Type Configuration

![cluster-example-1.png](md-media%2Fcluster-example-1.png)![example1](md-media/![cluster-example-1.png](md-media%2Fcluster-example-1.png))
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

## TODO

* remove resource EP should operate on configuration list values
*
* DB-based Configuration System
    * Control Map Rendering (color, points) -> new config values
    * caching of config values (especially json-list values)
* rework cluster rendering to direct client polygon rendering; controlled by server?
*
* forest heatmap rendering
*
* replace POST clustering call with GET call (affects client)
*
* map size is not necessarily square! -> fix scanner!
* save map size to map data and add output to map meta data
*
* woods detection
* mil detection
* industrial detection
*
* add communication loop between client and server to check if the current map is actually indexed or not; only execute
  map-scan-process if map is not indexed!
    * first time: only once at startup time
    * advanced: reindex also if index is suddenly deleted from server (work with http codes if you query for clusters)
    *
* move from POST-get http methods to real GET Methods. we only need to pass MapName (and eventual token name) so we can
  get rid of the POST-Request-DTOs
*
* Unit Tests!
* Command Framework
* add real JSON endpoints in addition to the necessary "consuming = MediaType.APPLICATION_FORM_URLENCODED_VALUE"
  Endpoints ...
* Transactions have to time out via scheduler after one hour
*
* Use spring health actuator instead health controller ...
*
* Error Handling
    * DB based error logging with route, payload, timestamp, request-ip in error logging table!
    * controller/controller advice
    * async error handler
    * Should error responses give more information? also important for the error logging in db....
    * ServerErrorDto & BadRequestDto are equal in structure ...
*
* delete map via POST
  * -> remove corresponding configs
  * -> cache reset
*
* Implement concave hull generator (besides existing convex hull)
* add dependency license generator
* Add Docker Compose
* Transactional Controller Template?
* map entity query/filter controller
* settings READ/WRITE REST API
* authentication / user-token acces and tenant-based-db-table-fields?
* documentation / wiki
* project page
* build pipeline / cd

------

* ConcaveHull.java implements Point on its own; change to Point2D Implementation so we dont need to map this data
    * but first we have to test if we want to keep the ConcaveHull implementation
