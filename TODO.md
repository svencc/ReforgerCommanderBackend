# TODO LIST
* 
* DB-based Configuration System
    * new RENDER format (no cluster format):
        * goal: do not render clusters; render generic polygons and text with color and z-index!
        * meta: amount of z-indexes 
        * clusterList with type, name, color, z-index 
        * map structure / associative array with z-index as key and clusterList as value
        * cluster size / amount of points, weight the quality of an area; this should be reflected in color intensity! 
    * new db config values for type-colors, z-indexes, etc.
    * rework cluster rendering to direct client polygon rendering; controlled by server?
*
*
*
*
*
*
*
* try forest clustering again; will not work with convex hulls; we need to implement concave hulls!
* forest heatmap rendering
*
* replace POST clustering call with GET call (affects client)
*
* map size is not necessarily square! -> fix scanner!
* save map size to map data and add output to map meta data
* rework scanner; now works with sphere -> we need to implement a cube scanner!
    * get map height for setting the cube height 
*
* woods detection
* military detection
* industrial detection
* terrain heightmap
* forest heatmap
*
* improve DBcached:
    * add cache reset endpoint
    * wrap around method via annotation
    * use BeanPostProcessor to wrap around methods
    * add column for key->keyHash:indexed, cacheName->hash:indexed, dataType
    * move to separate project / dependency
*
* add communication loop between client and server to check if the current map is actually indexed or not; only execute map-scan-process if map is not indexed!
    * first time: only once at startup time
    * advanced: reindex also if index is suddenly deleted from server (work with http codes if you query for clusters)
* move from POST-get http methods to real GET Methods. we only need to pass MapName (and eventual token name) so we can
  get rid of the POST-Request-DTOs
*
* Command Framework
* add real JSON endpoints in addition to the necessary "consuming = MediaType.APPLICATION_FORM_URLENCODED_VALUE" Endpoints ...
* Transactions have to time out via scheduler after one hour
*
* normalize db/map entities table
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
* authentication / user-token acces and tenant-based-db-table-fields?
* documentation / wiki
* project page
* build pipeline / cd

------

* ConcaveHull.java implements Point on its own; change to Point2D Implementation so we dont need to map this data
    * but first we have to test if we want to keep the ConcaveHull implementation

