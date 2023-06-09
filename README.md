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
* DB-based Configuration System
** Default-Global
** Default-Map-specific-Conf
** User-specific-Conf (Global + Map)
** (with inheritance in above order)
* Command Framework
* add real JSON endpoints in addition to the necessary "consuming = MediaType.APPLICATION_FORM_URLENCODED_VALUE" Endpoints ...
* save map size to map data and add output to map meta data
* Error Handling
    * DB based error logging with route, payload, timestamp, request-ip in error logging table!
    * controller/controller advice
    * async error handler
* add dependency license generator
* Add Docker Compose
* Transactions have to time out via scheduler after one hour
* Transactional Controller Template?
* map entity query/filter controller
* settings READ/WRITE REST API
* authentication
* documentation / wiki
* project page
* docker compose
* build pipeline / cd