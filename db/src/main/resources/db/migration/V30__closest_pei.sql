DROP FUNCTION couverturehydraulique.plus_proche_pei(geometry, int4, uuid);

CREATE OR REPLACE FUNCTION couverturehydraulique.plus_proche_pei(geomclic geometry, distance_max_parcours integer)
 RETURNS TABLE(pei uuid, chemin text, dist double precision)
 LANGUAGE plpgsql
AS $function$
DECLARE
  jonction record;
  distance_max_au_reseau integer := 100;
  distanceParcourue double precision; -- Distance actuelle parcourue sur le graph
  distancePlusProche double precision; -- Distance avec le pei le plus proche trouvé
  tronconPeiProche uuid; -- ID de la voie servant de jonction entre le pei le plus proche et le graph
  plusProchePredecesseur uuid;
  predecesseur record;
  plusProchePei uuid;
  chemin geometry;

  -- Variables du parcours de graph
  noeudsAVisiter uuid[];
  noeudsVisites uuid[];
  noeudCourant uuid;
  voisinRecord record;

BEGIN

  -- Jonction des PEI existants dans un certain rayon au graph
  PERFORM couverturehydraulique.inserer_jonction_pei(pei_id, distance_max_au_reseau, null, false)
  FROM (
    SELECT pei.pei_id FROM remocra.pei
        WHERE ST_DISTANCE(pei.pei_geometrie, geomClic) <= distance_max_parcours
    UNION
    SELECT pei_projet.pei_projet_id as pei_id FROM couverturehydraulique.pei_projet
        WHERE ST_DISTANCE(pei_projet_geometrie, geomClic) <= distance_max_parcours
    ) as p;

  -- Distance du clic au réseau routier
  SELECT * INTO jonction FROM (
    SELECT t.reseau_id AS troncon_id,
      ST_ClosestPoint(t.reseau_geometrie, geomClic) AS jonction_geometrie,
      ST_LineLocatePoint(t.reseau_geometrie, geomClic) AS jonction_fraction,
      t.reseau_geometrie AS voie_geometrie,
      t.reseau_sommet_source AS voie_source,
      t.reseau_sommet_destination AS voie_destination,
      ST_Distance(t.reseau_geometrie, geomClic) AS distance
    FROM couverturehydraulique.reseau t
    WHERE
        ST_Dwithin(geomClic, t.reseau_geometrie, distance_max_au_reseau)
      AND t.reseau_pei_troncon IS NULL
      ORDER BY
        distance ASC
  ) AS t;

  -- Jonction réalisable: on commence le parcours
  -- On autorise les jonctions aux fractions 0 et 1 (strict début et fin de la géométrie)
  IF jonction.jonction_geometrie IS NOT NULL THEN

   CREATE TEMP TABLE temp_chemins(
     noeud uuid,
     distance double precision,
     predecesseur uuid,
     geometrie geometry
   );
   INSERT INTO temp_chemins VALUES
   (jonction.voie_source, jonction.distance + jonction.jonction_fraction * ST_Length2D(jonction.voie_geometrie), NULL),
   (jonction.voie_destination, jonction.distance + (1-jonction.jonction_fraction) * ST_Length2D(jonction.voie_geometrie), NULL);

   noeudsAVisiter = array_append(noeudsAVisiter, jonction.voie_source);
   noeudsAVisiter = array_append(noeudsAVisiter, jonction.voie_destination);

   WHILE cardinality(noeudsAVisiter) > 0 LOOP
     SELECT noeudsAVisiter[1] into noeudCourant;
   noeudsVisites = array_append(noeudsVisites, noeudCourant);

   SELECT distance INTO distanceParcourue FROM temp_chemins WHERE noeud = noeudCourant;

   -- Pour toutes les voies voisines
   FOR voisinRecord IN (SELECT * FROM (
        (SELECT reseau_id, reseau_sommet_destination, reseau_sommet_source,
            ST_LENGTH(reseau_geometrie) as distance, reseau_geometrie, reseau_pei_troncon, reseau_traversable
            FROM couverturehydraulique.reseau
            WHERE reseau_sommet_source = noeudCourant)
         UNION
        (SELECT reseau_id, reseau_sommet_source as reseau_sommet_destination, reseau_sommet_source as reseau_sommet_source,
            ST_LENGTH(reseau_geometrie) as distance, ST_REVERSE(reseau_geometrie), reseau_pei_troncon, reseau_traversable
            FROM couverturehydraulique.reseau
            WHERE reseau_sommet_destination = noeudCourant)
      ) as R) LOOP

      -- On a trouvé un PEI, on vérifie sa distance
    IF (voisinRecord.reseau_pei_troncon IS NOT NULL) THEN
      IF (distancePlusProche IS NULL OR distancePlusProche > voisinRecord.distance + distanceParcourue) THEN
        distancePlusProche = voisinRecord.distance + distanceParcourue;
      tronconPeiProche = voisinRecord.reseau_id;
      plusProchePredecesseur = noeudCourant;
      plusProchePei = voisinRecord.reseau_pei_troncon;
      END IF;
    ELSE
      -- Si on a trouvé un chemin plus rapide pour aller à ce noeud, on le remplace
      IF (SELECT distance FROM temp_chemins WHERE noeud = voisinRecord.reseau_sommet_destination) > voisinRecord.distance + distanceParcourue THEN
        DELETE FROM temp_chemins WHERE noeud = voisinRecord.reseau_sommet_destination;
        INSERT INTO temp_chemins(noeud, distance, predecesseur, geometrie) VALUES
    (voisinRecord.reseau_sommet_destination, voisinRecord.distance + distanceParcourue, noeudCourant, voisinRecord.reseau_geometrie);
      -- Si il n'existe pas encore de chemin
      ELSIF (SELECT distance FROM temp_chemins WHERE noeud = voisinRecord.reseau_sommet_destination) IS NULL THEN
        INSERT INTO temp_chemins(noeud, distance, predecesseur, geometrie) VALUES
        (voisinRecord.reseau_sommet_destination, voisinRecord.distance + distanceParcourue, noeudCourant, voisinRecord.reseau_geometrie);
      END IF;

      -- Si le noeud n'est pas encore marqué comme visité, qu'on ne l'a pas visité et que l'on n'a pas encore atteint la distance max, on le rajoute à parcourir
      IF NOT noeudsVisites @> ARRAY[voisinRecord.reseau_sommet_destination]
            AND NOT noeudsAVisiter @> ARRAY[voisinRecord.reseau_sommet_destination]
            AND voisinRecord.distance + distanceParcourue < distance_max_parcours
      THEN
        noeudsAVisiter = array_append(noeudsAVisiter, voisinRecord.reseau_sommet_destination);
      END IF;
    END IF;

    END LOOP;
     noeudsAVisiter = array_remove(noeudsAVisiter, noeudCourant);
   END LOOP;

   -- Reconstitution du chemin
   IF tronconPeiProche IS NOT NULL THEN
       chemin = couverturehydraulique.safe_union(chemin, ST_MakeLine(geomClic, jonction.jonction_geometrie));
     WHILE plusProchePredecesseur IS NOT NULL LOOP
       SELECT * INTO predecesseur FROM temp_chemins WHERE noeud = plusProchePredecesseur;
       plusProchePredecesseur = predecesseur.predecesseur;
       chemin = couverturehydraulique.safe_union(chemin, predecesseur.geometrie);
     END LOOP;

   -- Troncon clic - réseau routier
     IF predecesseur.noeud = jonction.voie_source THEN
       chemin = couverturehydraulique.safe_union(chemin, ST_LineSubstring(jonction.voie_geometrie,0,jonction.jonction_fraction));
     ELSIF predecesseur.noeud = jonction.voie_destination THEN
       chemin = couverturehydraulique.safe_union(chemin, ST_LineSubstring(jonction.voie_geometrie,jonction.jonction_fraction, 1));
     END IF;

   -- Troncon réseau routier - PEI
     chemin = couverturehydraulique.safe_union(chemin, (select reseau_geometrie from couverturehydraulique.reseau where reseau_id = tronconPeiProche));
   END IF;

   -- Retrait jonctions PEI
   PERFORM couverturehydraulique.retirer_jonction_pei(pei_id)
   FROM remocra.pei p
   WHERE ST_DISTANCE(p.pei_geometrie, geomClic) <= distance_max_parcours;

    PERFORM couverturehydraulique.retirer_jonction_pei(pei_projet_id)
    FROM couverturehydraulique.pei_projet p
    WHERE ST_DISTANCE(p.pei_projet_geometrie, geomClic) <= distance_max_parcours;

   DROP TABLE temp_chemins;
   RETURN QUERY
   SELECT plusProchePei, ST_AsText(chemin), distancePlusProche;

  ELSE
   -- Retrait jonctions PEI
    PERFORM couverturehydraulique.retirer_jonction_pei(pei_id)
    FROM remocra.pei p
    WHERE ST_DISTANCE(p.pei_geometrie, geomClic) <= distance_max_parcours;

    PERFORM couverturehydraulique.retirer_jonction_pei(pei_projet_id)
    FROM couverturehydraulique.pei_projet p
    WHERE ST_DISTANCE(p.pei_projet_geometrie, geomClic) <= distance_max_parcours;

   RETURN QUERY
   SELECT null::uuid, NULL, 0::DOUBLE PRECISION;
  END IF;
END
$function$
;
