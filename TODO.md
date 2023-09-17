# TODO LIST
*
* Command Framework
  * API for fetching commands
    * different flavours 
      * fetch all
      * fetch last command of each type
      * fetch last command of type
      * existing command types
* 
* Transactions have to time out via scheduler after one hour
*
* normalize db/map entities table
* use @Embeddable types for coordinates ...
* map data immutable (do not offer setters); use @Access(AccessType.FIELD) for all unmodifiable entities!
*
*
* db migration scripts
    * add map default account to db in init (same as used in postman collection)
*
* DB-based Configuration System
    * new db config values for type-colors, z-indexes, etc.
    * Open for Comments; see TODOS in AsyncCacheableRequestProcessor -> app cache management move to this class; do not use  @cacheable annotations anymore?
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
*
* Use spring health actuator instead health controller ...
*
* jwt authentication implemented; add roles and permissions (also to db); after login account-token should make claims, from the database persisted roles/claims
    * add tests for RSAKeyGenerator.java
* 
* Create INITIAL ADMIN-USER on FIRST STARTUP if not exists (random password)
    * secure authenticate/new-account endpoints
    * add account management endpoints (delete, update, change password)
* 
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
* add tests for Runners
*
*
* Implement concave hull generator (besides existing convex hull)
* add dependency license generator
* Add Docker Compose
* Transactional Controller Template?
* map entity query/filter controller
* documentation / wiki
* project page
* build pipeline / cd

------

* ConcaveHull.java implements Point on its own; change to Point2D Implementation so we dont need to map this data
    * but first we have to test if we want to keep the ConcaveHull implementation~~


------
separate libs / dependencies / common shared code
* dbcached
* observables
* hint: https://stackoverflow.com/questions/2585220/how-to-configure-a-subproject-dependency-in-maven-without-deploying-jars
* eventually create java modules?