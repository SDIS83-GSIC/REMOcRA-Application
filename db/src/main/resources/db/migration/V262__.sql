-- L'anomalie n'est, "normalement", pas assignable au cours d'une visite.
-- Il n'est donc pas possible de trouver des résultats dans les tables remocra.l_visite_anomalie.
-- Pour en être sûr et ne pas empêcher la bonne exécution du patch, on supprime quand même toute trace éventuelle :
DELETE FROM remocra.l_visite_anomalie WHERE anomalie_id = (SELECT anomalie_id FROM remocra.anomalie WHERE anomalie_code = 'INDISPONIBILITE_TEMP');
-- Idem dans le schéma incoming
DELETE FROM incoming.l_visite_anomalie WHERE anomalie_id = (SELECT anomalie_id FROM remocra.anomalie WHERE anomalie_code = 'INDISPONIBILITE_TEMP');

-- Même raisonnement pour la table remocra.poids_anomalie ; la visite est une anomalie système.
-- Il n'est donc pas possible de paramétrer la pondération (nature de PEI et type de visite) comme pour une anomalie standard.
-- Il ne devrait donc y avoir aucun résultat dans la table remocra.poids_anomalie, mais on élimine quand même pour permettre la bonne exécution du patch :
DELETE FROM remocra.poids_anomalie WHERE poids_anomalie_anomalie_id = (SELECT anomalie_id FROM remocra.anomalie WHERE anomalie_code = 'INDISPONIBILITE_TEMP');


-- On supprime toutes les assignations de l'anomalie.
-- Cela n'aura aucun impact sur la disponibilité terrestre ou HBE des PEI, car le calcul dispo V3 ignore totalement l'anomalie.
-- Il n'est donc pas nécessaire de lancer un calcul de disponibilité à la suite du patch, puisque la disparition de cette anomalie n'a véritablement aucun impacte sur les status des PEI.
DELETE FROM remocra.l_pei_anomalie WHERE anomalie_id = (SELECT anomalie_id FROM remocra.anomalie WHERE anomalie_code = 'INDISPONIBILITE_TEMP');

-- Pour finir, on supprime l'anomalie :
DELETE FROM remocra.anomalie WHERE anomalie_code = 'INDISPONIBILITE_TEMP';
