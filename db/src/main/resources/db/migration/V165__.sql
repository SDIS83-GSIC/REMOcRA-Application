-- Administration de la couche "risque epress" : import et purge
ALTER TYPE remocra."DROIT" ADD VALUE 'RISQUE_KML_A';

-- Renommage de la valeur 'RISQUES_KML_R' en 'RISQUE_KML_R'
ALTER TYPE remocra."DROIT" RENAME VALUE 'RISQUES_KML_R' TO 'RISQUE_KML_R';

ALTER TYPE historique.TYPE_OBJET ADD VALUE 'RISQUE_EXPRESS';
