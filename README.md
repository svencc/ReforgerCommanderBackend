# RECOM

## ARMA Reforger+ AI Commander Backend Project

<p align="center">
  <img width="396" height="241" src="https://github.com/svencc/ReforgerCommanderBackend/raw/develop/docs%2Fmd-media%2FRECOM.png">
</p>


## Examples & Progress
To show some progress, here are some examples of the current state of the project.

What works and what you can see in the example images:
* Map Scanning and persisting to DB
* basic Cluster Generation (Convex Hull) using ML k-means-nearest-neighbour DBSCAN algorithm
* basic Cluster Rendering
* Configuration System (DB based) 
    * Cluster Entity-Type Configuration

![cluster-example-1.png](docs%2Fmd-media%2Fcluster-example-1.png)
![cluster-example-2.png](docs%2Fmd-media%2Fcluster-example-2.png)
![cluster-example-3.png](docs%2Fmd-media%2Fcluster-example-3.png)

To create more intelligence from gameMap data the following features are planned:
* Visual Map Generation
  * ![mergedmap-preview.png](docs%2Fmd-media%2Fmergedmap-preview.png) 
  * ![mergedmap3-preview.png](docs%2Fmd-media%2Fmergedmap3-preview.png) 
* Forest detection / forest heatmap
  * ![forestmap-preview.png](docs%2Fmd-media%2Fforestmap-preview.png) 
* Structure detection
  * ![structuremap-preview.png](docs%2Fmd-media%2Fstructuremap-preview.png)
* Terrain heightmap
  * ![topographic-scanner-example.png](docs%2Fmd-media%2Ftopographic-scanner-example.png)
* Map shading / lightmap
  * ![mapshading-preview.png](docs%2Fmd-media%2Fmapshading2-preview.png)
* Contour lines
  * ![contourmap-preview.png](docs%2Fmd-media%2Fcontourmap-preview.png)
* Visualization of slope map (will be used for pathfinding and movement)
  * ![slopemap-preview.png](docs%2Fmd-media%2Fslopemap-preview.png)
* Basic Idea of rasterized "AI" map vision
  * ![ai_vision-preview.png](docs%2Fmd-media%2Fai_vision-preview.png)
* Mil detection
* Industrial detection
* Qualification and prioritization of clusters / targets

After having more intelligence from the gameMap data, the work on the AI Commander will start.
Planned is to harness the power of OpenAI to create a commander that make decisions and gives orders to his units.

## Run application server

Run com.recom.RecomBackendApplication with "local" profile.
application-local.properties is preconfigured to work with the provided docker-mariaDb instance.

## Project Pages:

- https://github.com/svencc/ReforgerCommanderClient
- https://github.com/svencc/ReforgerCommanderBackend
- Discord: https://discord.gg/ksbEegafhu

