USE recom_db;

TRUNCATE TABLE map_structure_entity;
TRUNCATE TABLE map_topography;
TRUNCATE TABLE message;

DELETE FROM game_map WHERE 1=1;