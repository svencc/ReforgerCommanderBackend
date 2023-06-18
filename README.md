# Reforger Commander Backend Project
![Logo](md-media/logo.png)

## Setup or run mariaDb docker container
Just execute the docker/mariadb-docker-setup-or-start-win.ps1 script from the docker directory!
Database is ready configured for application-server connection.

## Run application server
Run com.rcb.Application with "local" profile. 
application-local.properties is preconfigured to work with the provided docker-mariaDb instance.

## Project Pages:
- https://github.com/svencc/ReforgerCommanderClient
- https://github.com/svencc/ReforgerCommanderBackend

## TODO
* read/write config via JSON/Postman -> cache reset
* add db entity types to config (for cluster detection) - replace hardcoded approach
* DB-based Configuration System
  * Control Map Rendering (color, points)
  * rework cluster rendering to direct polygon rendering; controlled by server?
* map size is not necessarily square! -> fix scanner!
* save map size to map data and add output to map meta data
* woods detection
* mil detection
* industrial detection
* Unit Tests!
* Command Framework
* add real JSON endpoints in addition to the necessary "consuming = MediaType.APPLICATION_FORM_URLENCODED_VALUE" Endpoints ...
* Transactions have to time out via scheduler after one hour
* Error Handling
    * DB based error logging with route, payload, timestamp, request-ip in error logging table!
    * controller/controller advice
    * async error handler
    * Should error responses give more information? also important for the error logging in db....
    * ServerErrorDto & BadRequestDto are equal in structure ...
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

