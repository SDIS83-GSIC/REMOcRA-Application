DROP FUNCTION couverturehydraulique.couverturehydraulique_zonage(uuid, _int4, int4, int4, text);

CREATE OR REPLACE FUNCTION couverturehydraulique.couverturehydraulique_zonage(idetude uuid, isodistances integer[], profondeurcouverture integer, srid integer, codesdis text)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
  tabDistances int[] := ARRAY[50, 100, 250, 300];
  dist integer;
  grosDebits int[];
  couverture_pei record;
  couverture_voisin record;
  couverture_risque_courant_faible geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), srid);
  couverture_risque_courant_ordinaire geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), srid);
  couverture_risque_important geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), srid);
  couverture_risque_particulier geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), srid);
  couverture_distance geometry := ST_SetSRID(ST_GeomFromText('POLYGON EMPTY'), srid);
BEGIN

  SELECT ARRAY(SELECT UNNEST-profondeurCouverture FROM unnest(isodistances) ORDER BY unnest) INTO tabDistances;

  FOREACH dist in ARRAY tabDistances LOOP
    DELETE FROM couverturehydraulique.couverture_tracee WHERE couverture_tracee_label = dist || 'm' AND couverture_tracee_etude_id IS NOT DISTINCT FROM idEtude;
	FOR couverture_pei IN (
	    SELECT * FROM couverturehydraulique.couverture_tracee_pei
	    WHERE couverture_tracee_pei_distance = dist
	    AND couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude)
    LOOP
		couverture_distance = couverturehydraulique.safe_union(couverture_distance, couverture_pei.couverture_tracee_pei_geometrie);
	END LOOP;
	INSERT INTO couverturehydraulique.couverture_tracee(couverture_tracee_label, couverture_tracee_etude_id, couverture_tracee_geometrie)
	VALUES(dist || 'm', idEtude, couverture_distance);
  END LOOP;

  -- Tracé du risque courant faible
  -- Conditions: 1 PEI de 60m3/h sur 150m (buffer compris)
  FOR couverture_pei IN (SELECT chp.couverture_tracee_pei_geometrie, chp.couverture_tracee_pei_id
    FROM couverturehydraulique.couverture_tracee_pei chp
    JOIN remocra.pei p ON p.pei_id = chp.couverture_tracee_pei_id
    LEFT JOIN remocra.pibi ON pibi.pibi_id = p.pei_id
    WHERE chp.couverture_tracee_pei_distance = 100
        AND (pibi.pibi_id IS NULL OR NOT couverturehydraulique.is_gros_debit(pei_id, codeSdis))
        AND chp.couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude) LOOP
    couverture_risque_courant_faible = couverturehydraulique.safe_union(couverture_risque_courant_faible, couverture_pei.couverture_tracee_pei_geometrie);
  END LOOP;

  DELETE FROM couverturehydraulique.couverture_tracee WHERE couverture_tracee_label = 'risque_courant_faible' AND couverture_tracee_etude_id IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverturehydraulique.couverture_tracee(couverture_tracee_label, couverture_tracee_etude_id, couverture_tracee_geometrie)
  VALUES('risque_courant_faible', idEtude, couverture_risque_courant_faible);

  -- Tracé du risque courant ordinaire
  -- Conditions: 2 PEI de 60 m3/h, intersection sur distances 150m et 350m (buffer compris)
  FOR couverture_pei in (SELECT chp.couverture_tracee_pei_geometrie, chp.couverture_tracee_pei_id
    FROM couverturehydraulique.couverture_tracee_pei chp
    JOIN couverturehydraulique.pei_projet p ON p.pei_projet_id = chp.couverture_tracee_pei_id
    WHERE chp.couverture_tracee_pei_distance = 100
        AND NOT couverturehydraulique.is_gros_debit(p.pei_projet_id, codeSdis) AND couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude) loop
      FOR couverture_voisin in (SELECT chp2.couverture_tracee_pei_geometrie, chp2.couverture_tracee_pei_id
        FROM couverturehydraulique.couverture_tracee_pei chp2
        JOIN remocra.remocra.pei_projet p ON p.pei_projet_id = chp.couverture_tracee_pei_id
        WHERE chp2.couverture_tracee_pei_distance = 300 AND NOT couverturehydraulique.is_gros_debit(p.pei_projet_id, codeSdis)
            AND chp2.pei != couverture_pei.couverture_tracee_pei_id AND couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude) loop
            couverture_risque_courant_ordinaire = couverturehydraulique.safe_union(
                couverture_risque_courant_ordinaire,
                ST_INTERSECTION(couverture_pei.couverture_tracee_pei_geometrie, couverture_voisin.couverture_tracee_pei_geometrie)
            );
      end loop;
  end loop;

  DELETE FROM couverturehydraulique.couverture_tracee WHERE
    couverture_tracee_label = 'risque_courant_ordinaire' AND couverture_tracee_etude_id IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverturehydraulique.couverture_tracee(couverture_tracee_label, couverture_tracee_etude_id, couverture_tracee_geometrie)
  VALUES('risque_courant_ordinaire', idEtude, couverture_risque_courant_ordinaire);

  -- Tracé du risque courant important
  -- Conditions: A ce stade, identique au risque courant ordinaire
  raise info 'risque courant important';
  DELETE FROM couverturehydraulique.couverture_tracee WHERE couverture_tracee_label = 'risque_courant_important'
        AND couverture_tracee_etude_id IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverturehydraulique.couverture_tracee(couverture_tracee_label, couverture_tracee_etude_id, couverture_tracee_geometrie)
    VALUES ('risque_courant_important', idEtude, couverture_risque_courant_ordinaire);

  -- Tracé du risque particulier
  -- Conditions: Intersection distances 100m et 300m (buffer compris), au moins un des deux PEI gros débit (débit >= 150 m3/h)
    -- Etape 1 : couverture 50m d'un gros débit avec une couverture 250m
  FOR couverture_pei IN (SELECT chp.couverture_tracee_pei_geometrie, chp.couverture_tracee_pei_id
    FROM couverturehydraulique.couverture_tracee_pei chp
    LEFT JOIN remocra.pei p on p.pei_id = chp.couverture_tracee_pei_id
    LEFT JOIN couverturehydraulique.pei_projet on pei_projet.pei_projet_id = chp.couverture_tracee_pei_id
    WHERE chp.couverture_tracee_pei_distance = 50
       AND ((couverturehydraulique.is_gros_debit(p.pei_id, codeSdis) OR couverturehydraulique.is_gros_debit(pei_projet.pei_projet_id, codeSdis)) AND chp.couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude)) LOOP
    FOR couverture_voisin IN (SELECT *
        FROM couverturehydraulique.couverture_tracee_pei
        WHERE couverture_tracee_pei_distance = 250 AND couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude
            AND pei != couverture_pei.couverture_tracee_pei_id
            AND ST_DISTANCE(couverture_tracee_pei_geometrie, couverture_pei.couverture_tracee_pei_geometrie) <= 1000
        ) LOOP
      couverture_risque_particulier = couverturehydraulique.safe_union(
          couverture_risque_particulier,
          ST_INTERSECTION(couverture_pei.couverture_tracee_pei_geometrie, couverture_voisin.couverture_tracee_pei_geometrie
      ));
    END LOOP;
  END LOOP;

    -- Etape 2 : couverture 250m d'un gros débit avec une couverture 50m
  FOR couverture_pei IN (SELECT chp.couverture_tracee_pei_geometrie, chp.couverture_tracee_pei_id
     FROM couverturehydraulique.couverture_tracee_pei chp
     LEFT JOIN remocra.pei p on p.pei_id = chp.couverture_tracee_pei_id
     LEFT JOIN couverturehydraulique.pei_projet on pei_projet.pei_projet_id = chp.couverture_tracee_pei_id
     WHERE chp.couverture_tracee_pei_distance = 250
        AND ((couverturehydraulique.is_gros_debit(p.pei_id, codeSdis) OR couverturehydraulique.is_gros_debit(pei_projet.pei_projet_id, codeSdis)) AND chp.couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude) )LOOP
    FOR couverture_voisin IN (SELECT *
      FROM couverturehydraulique.couverture_tracee_pei
      WHERE couverture_tracee_pei_distance = 50 AND couverture_tracee_pei_etude_id IS NOT DISTINCT FROM idEtude
          AND pei != couverture_pei.couverture_tracee_pei_id
          AND ST_DISTANCE(couverture_tracee_pei_geometrie, couverture_pei.couverture_tracee_pei_geometrie) <= 1000
      ) LOOP
      couverture_risque_particulier = couverturehydraulique.safe_union(
        couverture_risque_particulier,
        ST_INTERSECTION(couverture_pei.couverture_tracee_pei_geometrie, couverture_voisin.couverture_tracee_pei_geometrie
      ));
    END LOOP;
  END LOOP;

  DELETE FROM couverturehydraulique.couverture_tracee WHERE couverture_tracee_label = 'risque_particulier'
    AND couverture_tracee_etude_id IS NOT DISTINCT FROM idEtude;
  INSERT INTO couverturehydraulique.couverture_tracee(couverture_tracee_label, couverture_tracee_etude_id, couverture_tracee_geometrie)
    VALUES ('risque_particulier', idEtude, couverture_risque_particulier);
  RETURN 1;
END
$function$
;


DROP FUNCTION couverturehydraulique.creation_topologie(uuid);

CREATE OR REPLACE FUNCTION couverturehydraulique.creation_topologie(idetude uuid)
 RETURNS void
 LANGUAGE plpgsql
AS $function$
DECLARE
  voie record;
  debut geometry;
  fin geometry;
  sommetId uuid;
  t record;
BEGIN
  FOR voie IN (SELECT * FROM couverturehydraulique.reseau WHERE reseau_etude_id IS NOT DISTINCT FROM idEtude) LOOP
    debut = ST_StartPoint(voie.reseau_geometrie);
    fin = ST_EndPoint(voie.reseau_geometrie);

    -- On sélectionne le sommet de la topologie correspondant au croisement au début de la voie pour alimenter le champ 'source'
    SELECT * INTO t
        FROM couverturehydraulique.sommet
        WHERE ST_WITHIN(sommet_geometrie, ST_EXPAND(debut, 0.2))
        AND sommet_etude_id IS NOT DISTINCT FROM idEtude LIMIT 1;

    IF t IS NULL THEN -- Si le sommet n'existe pas, on le créé
      INSERT INTO couverturehydraulique.sommet(sommet_id, sommet_geometrie, sommet_etude_id)
        VALUES (gen_random_uuid(), debut, idEtude) RETURNING sommet_id INTO sommetId;
      UPDATE couverturehydraulique.reseau SET reseau_sommet_source = sommetId WHERE reseau_id = voie.reseau_id;
    ELSE
      UPDATE couverturehydraulique.reseau SET reseau_sommet_source = t.sommet_id WHERE reseau_id = voie.reseau_id;
    END IF;

    -- On sélectionne le sommet de la topologie correspondant au croisement à la fin de la voie pour alimenter le champ 'destination'
    SELECT * INTO t
        FROM couverturehydraulique.sommet
        WHERE ST_WITHIN(sommet_geometrie, ST_EXPAND(fin, 0.2))
        AND sommet_etude_id IS NOT DISTINCT FROM idEtude LIMIT 1;

    IF t IS NULL THEN -- Si le sommet n'existe pas, on le créé
      INSERT INTO couverturehydraulique.sommet(sommet_id, sommet_geometrie, sommet_etude_id)
        VALUES (gen_random_uuid(), fin, idEtude) RETURNING sommet_id INTO sommetId;
      UPDATE couverturehydraulique.reseau SET reseau_sommet_destination = sommetId WHERE reseau_id = voie.reseau_id;
    ELSE
      UPDATE couverturehydraulique.reseau SET reseau_sommet_destination = t.sommet_id WHERE reseau_id = voie.reseau_id;
    END IF;
  END LOOP;
END
$function$
;

DROP FUNCTION couverturehydraulique.inserer_jonction_pei(uuid, int4, uuid, bool);

CREATE OR REPLACE FUNCTION couverturehydraulique.inserer_jonction_pei(peiid uuid, distance_max_au_reseau integer, idetude uuid, usereseauimportewithcourant boolean)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
  DECLARE
    jonction record;
    voie1 record;
    voie2 record;
    voie2Id uuid;
    voieJonctionId uuid;
    sommetJonction uuid;
    sommetPei uuid;

  BEGIN
    --Récupère le troncon le + proche du PEI et calcule de point de jonction sur le troncon
    SELECT INTO jonction
      ST_ClosestPoint(t.reseau_geometrie, p.pei_geometrie) AS jonction_geometrie,
	  ST_LineLocatePoint(t.reseau_geometrie, p.pei_geometrie) AS jonction_fraction,
      p.pei_id,
      p.pei_geometrie,
      t.reseau_id AS troncon_id,
      t.reseau_geometrie AS troncon_geometrie
    FROM
      (
      SELECT pei.pei_id, pei_geometrie FROM remocra.pei WHERE pei.pei_id = peiid
        UNION
      SELECT pei_projet.pei_projet_id as pei_id, pei_projet_geometrie as pei_geometrie  FROM couverturehydraulique.pei_projet
        WHERE pei_projet.pei_projet_id = peiid
    ) AS p
      CROSS JOIN LATERAL
      (SELECT DISTINCT ON (p.pei_id)
        t.reseau_id,
        t.reseau_geometrie,
        ST_Distance(t.reseau_geometrie, p.pei_geometrie) AS distance
      FROM
        couverturehydraulique.reseau t
      WHERE
        ST_Dwithin(p.pei_geometrie,t.reseau_geometrie, distance_max_au_reseau)
	    AND CASE
		    WHEN useReseauImporteWithCourant = true THEN  (t.reseau_etude_id IS NOT DISTINCT FROM  idEtude OR t.reseau_etude_id IS NULL)
		    ELSE t.reseau_etude_id IS NOT DISTINCT FROM  idEtude
		 END
	    AND t.reseau_pei_troncon IS NULL
      ORDER BY
        p.pei_id,
        distance ASC
      ) AS t;
    -- Si le point de jonction n'est pas null et qu'il ne corresponds pas à une extremité existante du tronçon,
    -- procède à la découpe du troncon initial en 2.
    IF jonction.jonction_geometrie IS NOT NULL AND jonction.jonction_fraction >= 0.00001 AND jonction.jonction_fraction < 0.99999 THEN
	  --Mise à jour de géométrie du troncon à découper
      UPDATE couverturehydraulique.reseau t SET
        reseau_geometrie = ST_LineSubstring(t.reseau_geometrie, 0, jonction.jonction_fraction)
      WHERE
        reseau_id = jonction.troncon_id;

      -- Insertion du complément
	  SELECT * INTO voie1 FROM couverturehydraulique.reseau WHERE reseau_id = jonction.troncon_id;
      INSERT INTO couverturehydraulique.reseau (
        reseau_id, reseau_geometrie, reseau_etude_id, reseau_traversable, reseau_sens_unique, reseau_niveau
      ) VALUES (
        gen_random_uuid(),
        ST_LineSubstring(jonction.troncon_geometrie,jonction.jonction_fraction,1),
		idEtude,
		voie1.reseau_traversable,
		voie1.reseau_sens_unique,
		voie1.reseau_niveau
      ) RETURNING reseau_id INTO voie2Id;

      -- Insertion de la jonction entre le PEI et le réseau
      INSERT INTO couverturehydraulique.reseau (
        reseau_id,
        reseau_geometrie,
        reseau_pei_troncon,
		reseau_etude_id
      ) VALUES (
        gen_random_uuid(),
        St_MakeLine(jonction.pei_geometrie, jonction.jonction_geometrie),
        peiId,
		idEtude
      ) RETURNING reseau_id INTO voieJonctionId;

      -- Création du sommet entre les 3 voies et du sommet sur le PEI, modification des sommets source/destination en adéquation
      SELECT * into voie1 from couverturehydraulique.reseau where reseau_id = jonction.troncon_id;
      SELECT * into voie2 from couverturehydraulique.reseau where reseau_id = voie2Id;

     SELECT sommet_id INTO sommetJonction FROM couverturehydraulique.sommet WHERE sommet_geometrie = jonction.jonction_geometrie;
     SELECT sommet_id INTO sommetPei FROM couverturehydraulique.sommet WHERE sommet_geometrie = jonction.pei_geometrie;

    IF sommetJonction IS NULL THEN
      INSERT INTO couverturehydraulique.sommet(sommet_id, sommet_geometrie)
        VALUES(gen_random_uuid(), jonction.jonction_geometrie) RETURNING sommet_id INTO sommetJonction;
     END IF;
    IF sommetPei IS NULL THEN
      INSERT INTO couverturehydraulique.sommet(sommet_id, sommet_geometrie)
        VALUES(gen_random_uuid(), jonction.pei_geometrie) RETURNING sommet_id INTO sommetPei;
     END IF;

      UPDATE couverturehydraulique.reseau SET reseau_sommet_destination = voie1.reseau_sommet_destination WHERE reseau_id = voie2.reseau_id;
      UPDATE couverturehydraulique.reseau SET reseau_sommet_destination = sommetJonction WHERE reseau_id = voie1.reseau_id OR reseau_id = voieJonctionId;
      UPDATE couverturehydraulique.reseau SET reseau_sommet_source = sommetJonction WHERE reseau_id = voie2.reseau_id;
      UPDATE couverturehydraulique.reseau SET reseau_sommet_source = sommetPei WHERE reseau_id = voieJonctionId;

	ELSIF jonction.jonction_geometrie IS NOT NULL AND (jonction.jonction_fraction < 0.00001 OR jonction.jonction_fraction = 1) THEN
	  SELECT sommet_id INTO sommetJonction
	    FROM couverturehydraulique.sommet order by st_distance(sommet_geometrie, jonction.jonction_geometrie) limit 1;
      SELECT sommet_id INTO sommetPei
        FROM couverturehydraulique.sommet
        WHERE sommet_geometrie = jonction.pei_geometrie;

	  IF sommetPei IS NULL THEN
	      INSERT INTO couverturehydraulique.sommet(sommet_id, sommet_geometrie) VALUES( gen_random_uuid(), jonction.pei_geometrie) RETURNING sommet_id INTO sommetPei;
	     END IF;
	  INSERT INTO couverturehydraulique.reseau (
	    reseau_id,
        reseau_geometrie,
        reseau_pei_troncon,
		reseau_etude_id,
		reseau_sommet_source,
		reseau_sommet_destination
       ) VALUES (
        gen_random_uuid(),
        St_MakeLine(jonction.pei_geometrie, jonction.jonction_geometrie),
        peiId,
		idEtude,
		sommetPei,
		sommetJonction
       );
    END IF;

    RETURN 1;
  END;
$function$
;

DROP FUNCTION couverturehydraulique.parcours_couverturehydraulique(uuid, uuid, uuid, _int4, int4, bool);

CREATE OR REPLACE FUNCTION couverturehydraulique.parcours_couverturehydraulique(depart uuid, idetude uuid, idreseauimporte uuid, isodistances integer[], profondeurcouverture integer, usereseauimportewithcourant boolean)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
  tabDistances int[];
  dist integer;
  noeudsAVisiter uuid[];
  noeudsVisites uuid[];
  noeudCourant uuid;
  debutChemin boolean;
  voisinRecord record;
  voisinRecordFromVoieLaterale record;
  premiereVoieNonTraversableRencontree record;
  courantRecord record;
  t record;
  distanceParcourue double precision;
  voieCourante uuid;
  bufferSizeRestreint integer;
  voieGauche record;
  voieDroite record;
  buffer geometry;
  blade geometry;
  bladeSommets geometry;
  splitResult geometry;
  bufferSommets geometry;
  bufferEndPoint geometry;
  bufferSide character varying;
  bufferEndCap character varying;
  recordCouverture record;
BEGIN
  TRUNCATE ONLY couverturehydraulique.temp_distance;
  bufferSizeRestreint = 5; -- Buffer pour les voies restreintes (pont, tunnels, etc) via champ niveau != 0

  -- On trie les distances par ordre croissant et on retranche la taille du buffer
  SELECT ARRAY(SELECT unnest-profondeurcouverture FROM unnest(isodistances) ORDER BY unnest) INTO tabDistances;

  FOREACH dist in ARRAY tabDistances LOOP
    noeudsAVisiter = array[]::uuid[];
    noeudsVisites = array[]::uuid[];
    debutChemin = true;
    distanceParcourue = 0;
    -- Premier parcourt depuis ce PEI: on récupère le premier noeud à visite
    IF tabDistances[1] = dist THEN
      DELETE FROM couverturehydraulique.temp_distance where temp_distance_pei_start = depart;
      SELECT reseau_sommet_source into noeudCourant from couverturehydraulique.reseau where reseau_pei_troncon = depart;
      noeudsAVisiter = array_append(noeudsAVisiter, noeudCourant);
    -- Parcours suivants: on reprend une partie des données (le parcours N contient le parcours N-1)
    ELSE
      noeudsAVisiter = (SELECT ARRAY(
        SELECT DISTINCT temp_distance_sommet FROM couverturehydraulique.temp_distance WHERE temp_distance_voie_courante IN (
          SELECT DISTINCT temp_distance_voie_precedente
          FROM couverturehydraulique.temp_distance
          WHERE temp_distance_distance = tabDistances[(array_position(tabDistances, dist)-1)]
        )
      )::uuid[]);
       DELETE FROM couverturehydraulique.temp_distance
          where temp_distance_pei_start = depart
          AND temp_distance_distance = tabDistances[(array_position(tabDistances, dist)-1)];
    END IF;
    -- Pour tous les noeuds à visiter
    WHILE cardinality(noeudsAVisiter) > 0 LOOP
      SELECT noeudsAVisiter[1] into noeudCourant;
      noeudsVisites = array_append(noeudsVisites, noeudCourant);
      SELECT temp_distance_distance as distance, temp_distance_sommet as sommet, temp_distance_pei_start as start, temp_distance_voie_courante as voie, temp_distance_traversable as traversable, temp_distance_side as side  INTO courantRecord FROM couverturehydraulique.temp_distance
          WHERE temp_distance_pei_start = depart AND temp_distance_sommet = noeudCourant
          ORDER BY temp_distance_distance LIMIT 1;
      -- Si c'est la voie de départ, il n'y a aucune occurence dans la table temp_distances. On récupère néanmoins son ID afin de pouvoir déterminer les voies partant à sa gauche et à sa droite
      IF courantRecord IS NULL THEN
        SELECT reseau_id INTO voieCourante FROM couverturehydraulique.reseau WHERE reseau_pei_troncon = depart;
      END IF;

      PERFORM couverturehydraulique.voieslaterales(COALESCE(courantRecord.voie, voieCourante), noeudCourant, idReseauImporte, useReseauImporteWithCourant);
      SELECT voie_laterale_accessible as accessible, voie_laterale_angle as angle, voie_laterale_voie_voisine as voie, voie_laterale_droite as droite, voie_laterale_gauche as gauche, voie_laterale_traversable as traversable FROM couverturehydraulique.voie_laterale WHERE voie_laterale_gauche INTO voieGauche;
      SELECT voie_laterale_accessible as accessible, voie_laterale_angle as angle, voie_laterale_voie_voisine as voie, voie_laterale_droite as droite, voie_laterale_gauche as gauche, voie_laterale_traversable as traversable FROM couverturehydraulique.voie_laterale WHERE voie_laterale_droite INTO voieDroite;

      -- Pour tous les noeuds voisins
       FOR voisinRecord IN (SELECT * FROM (
        (SELECT reseau_id as id, reseau_sommet_destination as destination, reseau_sommet_source as source,
				ST_LENGTH(reseau_geometrie) as distance, reseau_geometrie as geometrie, reseau_pei_troncon as pei_troncon,
				reseau_traversable as traversable, reseau_niveau as niveau
        	FROM couverturehydraulique.reseau
        	WHERE reseau_sommet_source = noeudCourant
                AND (reseau_id IN ((SELECT voie_laterale_voie_voisine FROM couverturehydraulique.voie_laterale))
                OR (voieGauche.voie IS NULL AND voieDroite.voie IS NULL))
        	    AND CASE
                    WHEN useReseauImporteWithCourant THEN  (reseau_etude_id IS NOT DISTINCT FROM idReseauImporte OR reseau_etude_id IS NULL)
                    ELSE reseau_etude_id IS NOT DISTINCT FROM idReseauImporte
                END)
         UNION
        (SELECT reseau_id as id ,
				reseau_sommet_source as destination, reseau_sommet_source as source, ST_LENGTH(reseau_geometrie) as distance,
                ST_REVERSE(reseau_geometrie) as geometrie, reseau_pei_troncon as pei_troncon,
				reseau_traversable as traversable, reseau_niveau as niveau
        	FROM couverturehydraulique.reseau
        	WHERE reseau_sommet_destination = noeudCourant
                AND (reseau_id IN ((SELECT voie_laterale_voie_voisine FROM couverturehydraulique.voie_laterale))
                OR (voieGauche.voie IS NULL AND voieDroite.voie IS NULL))
                AND  CASE
                    WHEN  useReseauImporteWithCourant THEN (reseau_etude_id IS NOT DISTINCT FROM idReseauImporte OR reseau_etude_id IS NULL)
                    ELSE reseau_etude_id IS NOT DISTINCT FROM idReseauImporte
                END)
      ) as R ) LOOP

        -- On prend les voies étant un troncon pei seulement lors du premier parcours. Lors des suivants, ces troncons sont ignorés
        CONTINUE WHEN voisinRecord is not null and ((voisinRecord.pei_troncon is not null AND debutChemin = FALSE) OR (voisinRecord.id = courantRecord.voie));

		SELECT  voie_laterale_accessible as accessible, voie_laterale_angle as angle, voie_laterale_voie_voisine as voie, voie_laterale_droite as droite, voie_laterale_gauche as gauche
       INTO voisinRecordFromVoieLaterale FROM couverturehydraulique.voie_laterale
		    WHERE voie_laterale_voie_voisine = voisinRecord.id;

        -- Si c'est une voie non accessible (carrefour où l'on doit passer auparavant par des voies gauche et droite non traversables), on stoppe
		CONTINUE WHEN (voisinRecordFromVoieLaterale.accessible IS NOT NULL AND voisinRecordFromVoieLaterale.accessible = FALSE);

		IF(voieGauche IS NOT NULL AND voieDroite IS NOT NULL AND voieGauche.voie != voieDroite.voie) THEN
		  -- Si c'est une voie à gauche, que la voie courante est non traversable et que l'on trace le buffer sur notre droite et que ce
		  -- n'est pas la première voie non traversable rencontrée sur notre droite
		  SELECT voie_laterale_accessible as accessible, voie_laterale_angle as angle, voie_laterale_voie_voisine as voie, voie_laterale_droite as droite, voie_laterale_gauche as gauche  INTO premiereVoieNonTraversableRencontree
		  FROM couverturehydraulique.voie_laterale
		  WHERE voie_laterale_traversable = false
		  ORDER BY voie_laterale_angle DESC LIMIT 1;
		  CONTINUE WHEN (voisinRecordFromVoieLaterale.gauche = TRUE
						 and courantRecord.traversable = FALSE
						 AND courantRecord.side = 'RIGHT'
						 AND premiereVoieNonTraversableRencontree.voie != voisinRecordFromVoieLaterale.voie);

		  -- Si c'est une voie à droite, que la voie courante est non traversable, que l'on trace le buffer sur notre gauche et que ce
		  -- n'est pas la première voie non traversable rencontrée sur notre gauche
		  SELECT voie_laterale_accessible as accessible, voie_laterale_angle as angle, voie_laterale_voie_voisine as voie, voie_laterale_droite as droite, voie_laterale_gauche as gauche  INTO premiereVoieNonTraversableRencontree
		  FROM couverturehydraulique.voie_laterale
		  WHERE voie_laterale_traversable = false
		  ORDER BY voie_laterale_angle DESC LIMIT 1;
		  CONTINUE WHEN (voisinRecordFromVoieLaterale.droite = TRUE
						  and courantRecord.traversable = FALSE
						 AND courantRecord.side = 'LEFT'
						 AND premiereVoieNonTraversableRencontree.voie != voisinRecordFromVoieLaterale.voie);

		END IF;

        -- Si la voie est trop longue, on n'en parcourt qu'une partie
        IF (voisinRecord.distance + COALESCE(courantRecord.distance, 0)) > dist AND COALESCE(courantRecord.distance, 0) < dist THEN
          distanceParcourue = COALESCE(courantRecord.distance, 0) + voisinRecord.distance;
          Select ST_LineSubstring(voisinRecord.geometrie, 0,
            (CASE
              WHEN distanceParcourue <= dist THEN 1
              ELSE (1 -((distanceParcourue - dist)/voisinRecord.distance))
            END)
          )::geometry(LineString,2154) INTO voisinRecord.geometrie;
          SELECT ST_PointN(
            voisinRecord.geometrie,
            generate_series(2, ST_NPoints(voisinRecord.geometrie))
          ) INTO bufferEndPoint;
          distanceParcourue = dist;
        ELSE
          distanceParcourue = COALESCE(courantRecord.distance, 0) + voisinRecord.distance;
          SELECT sommet_geometrie INTO bufferEndPoint FROM couverturehydraulique.sommet
            WHERE sommet_id = (
                CASE
                    WHEN voisinRecord.source != noeudCourant THEN voisinRecord.source
                    ELSE voisinRecord.destination
                END)
            AND debutChemin = false;
        END IF;

        -- Le chemin pour aller au noeud n'existe pas en mémoire ou il en existe déjà un plus long => on remplace par le chemin courant
        SELECT * INTO t FROM couverturehydraulique.temp_distance
            WHERE temp_distance_pei_start = depart AND temp_distance_sommet = voisinRecord.destination and temp_distance_voie_courante = voisinRecord.id;

        IF t IS NULL AND distanceParcourue <= dist THEN
              /** =============================================== Tracé du buffer de la voie ==================================================== **/
              -- On détermine de quel côté tracer le buffer
            IF courantRecord.traversable = false and voisinRecord.traversable = false then
                bufferSide = courantRecord.side::text;
                bufferEndCap = 'round';
              elsif voisinRecord.traversable THEN
                bufferSide = 'both';
                bufferEndCap = 'round';
              ELSIF voieGauche.voie = voieDroite.voie and voieGauche.accessible = true THEN
                bufferSide = courantRecord.side::text;
                bufferEndCap = 'round';
              ELSIF voisinRecord.id = voieGauche.voie AND voieGauche.accessible = true THEN
                bufferSide = CASE
                  WHEN courantRecord.side = 'BOTH' THEN 'left'
                  ELSE courantRecord.side::text
                END;
                bufferEndCap = 'flat';
              ELSIF voisinRecord.id = voieDroite.voie AND voieDroite.accessible = true THEN
                bufferSide = CASE
                  WHEN courantRecord.side = 'BOTH' THEN 'right'
                  ELSE courantRecord.side::text
                END;
                bufferEndCap = 'flat';
else
                bufferSide = 'both';
                bufferEndCap = 'round';

              END IF;

          buffer = ST_BUFFER(voisinRecord.geometrie, (
            CASE WHEN voisinRecord.niveau != 0 THEN bufferSizeRestreint
            ELSE profondeurcouverture END
          ), CONCAT('side=', lower(bufferSide), ' endcap=', bufferEndCap));

          -- Si le buffer traverse une voie non traversable, on retire la partie en trop
          SELECT ST_UNION(geometrie) INTO blade
            FROM (SELECT reseau_geometrie as geometrie FROM couverturehydraulique.reseau
                    WHERE reseau_pei_troncon IS NULL AND
                    NOT reseau_traversable
                    AND reseau_niveau = 0 AND reseau_id != voisinRecord.id
                    AND ST_INTERSECTS(buffer, reseau_geometrie)
                    AND reseau_etude_id IS NOT DISTINCT FROM idReseauImporte) AS R;

          IF blade IS NOT NULL THEN
            splitResult = ST_SPLIT(buffer, blade);
            IF ST_NUMGEOMETRIES(splitResult) > 1 THEN
              SELECT geom INTO buffer FROM (
                SELECT (st_dump(splitResult)).geom
              ) AS R ORDER BY st_distance(ST_LineInterpolatePoint(voisinRecord.geometrie, 0.001), geom) LIMIT 1;
            END IF;
          END IF;
          /** =============================================================================================================================== **/

          /** ================================== Ajout des buffer de destination des voies ===================================== **/
          IF lower(bufferSide) != lower('both') THEN
            bufferSommets = ST_BUFFER(bufferEndPoint,profondeurcouverture);
            SELECT ST_UNION(geometrie) INTO bladeSommets
                FROM (SELECT reseau_geometrie as geometrie FROM couverturehydraulique.reseau
                        WHERE ST_INTERSECTS(reseau_geometrie, bufferSommets) AND reseau_pei_troncon IS NULL
                        AND NOT reseau_traversable AND reseau_niveau = 0) AS R;

            IF bladeSommets IS NOT NULL THEN
              SELECT ST_UNION(buffer, geom) INTO buffer FROM (
                SELECT (st_dump(st_split(bufferSommets, bladeSommets))).geom
              ) AS R ORDER BY ST_AREA(ST_INTERSECTION(geom, buffer))/ST_AREA(geom) DESC LIMIT 1;
            END IF;
          END IF;
          /** =============================================================================================================================== **/
          DELETE FROM couverturehydraulique.temp_distance
            WHERE temp_distance_pei_start = depart AND temp_distance_voie_courante = voisinRecord.id AND  temp_distance_sommet = voisinRecord.destination;

          -- Si le buffer est un MultiPolygon, on le cast en Polygon
          IF ST_GeometryType(buffer) = 'ST_MultiPolygon' THEN
            SELECT (ST_Dump(buffer)).geom INTO buffer;
          END IF;
          INSERT INTO couverturehydraulique.temp_distance (
              temp_distance_id,
              temp_distance_pei_start,
              temp_distance_sommet,
              temp_distance_voie_courante,
              temp_distance_distance,
              temp_distance_geometrie,
              temp_distance_voie_precedente,
              temp_distance_traversable,
              temp_distance_side)
            SELECT gen_random_uuid(), depart, voisinRecord.destination, voisinRecord.id, distanceParcourue, buffer,
                COALESCE(courantRecord.voie, voieCourante), voisinRecord.traversable, upper(bufferSide)::couverturehydraulique.type_side;
        END IF;
		-- Si coût inférieur à la limite de recherche et le noeud n'a jamais été visité
        IF NOT noeudsVisites @> ARRAY[voisinRecord.destination] AND distanceParcourue < dist THEN
          noeudsAVisiter = array_append(noeudsAVisiter, voisinRecord.destination);
        END IF;
        debutChemin = FALSE;
      END LOOP; -- Fin parcourt des voies voisines

      noeudsAVisiter = array_remove(noeudsAVisiter, noeudCourant);

    END LOOP; -- Fin tant que des sommets restent à visiter

    -- Récupération de la géométrie de la couverture hydraulique
    --DELETE FROM couverturehydraulique.couverture_hydraulique_pei WHERE distance = dist AND pei = depart;
    SELECT dist, depart, idEtude, ST_Union(temp_distance_geometrie) as geometrie INTO recordCouverture
      FROM couverturehydraulique.temp_distance WHERE temp_distance_pei_start = depart;

	-- Si MultiPolygon, on cherche à récupérer un Polygon
	IF ST_GeometryType(recordCouverture.geometrie) = 'ST_MultiPolygon' THEN
	  recordCouverture.geometrie = ST_BUFFER(ST_BUFFER(recordCouverture.geometrie, 0.001), -0,001);
    END IF;

	-- Si la condition n'est pas validée à ce stade, l'erreur provient très probablement du jeu de données
	IF ST_GeometryType(recordCouverture.geometrie) = 'ST_Polygon' THEN
      INSERT INTO couverturehydraulique.couverture_tracee_pei
        (couverture_tracee_pei_distance,
			couverture_tracee_pei_id,
			couverture_tracee_pei_etude_id,
			couverture_tracee_pei_geometrie)
      VALUES(recordCouverture.dist, recordCouverture.depart, recordCouverture.idEtude, recordCouverture.geometrie);
	END IF;
  END LOOP; -- Fin for pour chaque distance

 DELETE FROM couverturehydraulique.voie_laterale;
 DELETE FROM couverturehydraulique.temp_distance;

  RETURN 1;
END
$function$
;

DROP FUNCTION couverturehydraulique.retirer_jonction_pei(uuid);

CREATE OR REPLACE FUNCTION couverturehydraulique.retirer_jonction_pei(peiid uuid)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
  tronconPei record;
  voie1 record;
  voie2 record;
  geom geometry;
BEGIN
  SELECT * INTO tronconPei FROM couverturehydraulique.reseau
    WHERE reseau_pei_troncon = peiId;
  SELECT * INTO voie1 FROM couverturehydraulique.reseau
    WHERE reseau_sommet_destination = tronconPei.reseau_sommet_destination AND reseau_pei_troncon IS NULL;
  SELECT * INTO voie2 FROM couverturehydraulique.reseau
    WHERE reseau_sommet_source = tronconPei.reseau_sommet_destination AND reseau_pei_troncon IS NULL;
  DELETE FROM couverturehydraulique.sommet
    WHERE sommet_id = tronconPei.reseau_sommet_source;
  SELECT ST_LINEMERGE(ST_UNION(voie1.reseau_geometrie, voie2.reseau_geometrie)) INTO geom;

  IF ST_GeometryType(geom) = 'ST_LineString' THEN
    UPDATE couverturehydraulique.reseau
      SET reseau_geometrie = geom,
          reseau_sommet_destination = voie2.reseau_sommet_destination
      WHERE reseau_id = voie1.reseau_id;
	DELETE FROM couverturehydraulique.reseau
	    WHERE reseau_id = voie2.reseau_id;
	DELETE FROM couverturehydraulique.sommet
	    WHERE sommet_id = tronconPei.reseau_sommet_destination;
  END IF;

  DELETE FROM couverturehydraulique.reseau
    WHERE reseau_pei_troncon = peiId;
  RETURN 1;
END
$function$
;
