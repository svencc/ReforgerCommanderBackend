USE recom_db;

DELETE FROM map_structure_entity WHERE 1=1;
DELETE FROM class_name_entity WHERE 1=1;
DELETE FROM prefab_name_entity WHERE 1=1;
DELETE FROM resource_name_entity WHERE 1=1;
DELETE FROM map_descriptor_type_entity WHERE 1=1;
TRUNCATE TABLE map_topography;

TRUNCATE TABLE message;
DELETE FROM game_map WHERE 1=1;