INSERT INTO remocra.groupe_couche
VALUES (gen_random_uuid(), 'IGN', 100, 'Fonds IGN');

INSERT INTO remocra.couche
VALUES (gen_random_uuid(),
        'PLANIGNV2',
        (SELECT groupe_couche_id FROM remocra.groupe_couche WHERE groupe_couche_code = 'IGN'),
        1000, 'Plans IGN V2', 'WMTS', 'EPSG:3857', 'https://data.geopf.fr/wmts',
        'GEOGRAPHICALGRIDSYSTEMS.PLANIGNV2', 'image/png', true, true, null, null),
       (gen_random_uuid(),
        'PARCELLAIRE_EXPRESS',
        (SELECT groupe_couche_id FROM remocra.groupe_couche WHERE groupe_couche_code = 'IGN'),
        1100, 'Parcelles cadastrales', 'WMTS', 'EPSG:3857', 'https://data.geopf.fr/wmts',
        'CADASTRALPARCELS.PARCELLAIRE_EXPRESS', 'image/png', true, false, null, null),
       (gen_random_uuid(),
        'ORTHOPHOTOS',
        (SELECT groupe_couche_id FROM remocra.groupe_couche WHERE groupe_couche_code = 'IGN'),
        1200, 'Photos aériennes', 'WMTS', 'EPSG:3857', 'https://data.geopf.fr/wmts',
        'ORTHOIMAGERY.ORTHOPHOTOS', 'image/jpeg', true, false, null, null);
