INSERT INTO remocra.l_couche_module
SELECT couche_id, "type_module"
FROM remocra.couche,
     unnest(enum_range(NULL::TYPE_MODULE)) AS "type_module"
ON CONFLICT DO NOTHING;
