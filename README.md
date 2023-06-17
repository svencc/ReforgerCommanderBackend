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
* DB-based Configuration System4
  * Control Map Rendering (color, points)
  * rework cluster rendering to direct polygon rendering; controlled by server?
* map size is not necessarily square! -> fix scanner!
* save map size to map data and add output to map meta data
* add db entity types to config (for cluster detection) - replace hardcoded approach
* woods detection
* mil detection
* industrial detection
* Command Framework
* add real JSON endpoints in addition to the necessary "consuming = MediaType.APPLICATION_FORM_URLENCODED_VALUE" Endpoints ...
* Transactions have to time out via scheduler after one hour
* Error Handling
    * DB based error logging with route, payload, timestamp, request-ip in error logging table!
    * controller/controller advice
    * async error handler
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