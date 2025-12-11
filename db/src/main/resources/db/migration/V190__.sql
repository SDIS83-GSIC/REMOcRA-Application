INSERT INTO remocra.rapport_personnalise (
rapport_personnalise_id,
    rapport_personnalise_code,
    rapport_personnalise_libelle,
    rapport_personnalise_description,
    rapport_personnalise_actif,
    rapport_personnalise_protected,
    rapport_personnalise_champ_geometrie,
    rapport_personnalise_source_sql,
    rapport_personnalise_module
) VALUES (
    gen_random_uuid(),
    'PEI_HISTORIQUE',
    'Historique d''un PEI',
    'Permet de générer l''historique d''un PEI sous forme différentielle',
    true,
    true,
    null,
    E'with historique as (
              SELECT
                  row_number() OVER (ORDER BY tracabilite_date DESC) AS num_ligne,
      			(t.tracabilite_auteur_data::json->>''nom'')::text || '' '' || (t.tracabilite_auteur_data::json->>''prenom'')::text AS auteur,
                  (t.tracabilite_objet_data::json->>''peiId'')::text AS peiId,
                  (t.tracabilite_objet_data::json->>''peiNumeroComplet'')::text AS peiNumeroComplet,
                  (t.tracabilite_objet_data::json->>''peiNumeroInterne'')::numeric AS peiNumeroInterne,
                  (t.tracabilite_objet_data::json->>''peiDisponibiliteTerrestre'')::text AS peiDisponibiliteTerrestre,
                  (t.tracabilite_objet_data::json->>''peiTypePei'')::text AS peiTypePei,
                  (t.tracabilite_objet_data::json->>''peiGeometrie'')::text AS geometrie,
                  st_x((t.tracabilite_objet_data::json->>''peiGeometrie'')::geometry)::text AS peiX,
                  st_y((t.tracabilite_objet_data::json->>''peiGeometrie'')::geometry)::text AS peiY,
                  (t.tracabilite_objet_data::json->>''peiAutoriteDeciId'')::text AS peiAutoriteDeciId,
                  (t.tracabilite_objet_data::json->>''peiServicePublicDeciId'')::text AS peiServicePublicDeciId,
                  (t.tracabilite_objet_data::json->>''peiMaintenanceDeciId'')::text AS peiMaintenanceDeciId,

                  (t.tracabilite_objet_data::json->>''peiCommuneId'')::text AS peiCommuneId,
                  (t.tracabilite_objet_data::json->>''peiVoieId'')::text AS peiVoieId,
                  (t.tracabilite_objet_data::json->>''peiNumeroVoie'')::text AS peiNumeroVoie,
                  (t.tracabilite_objet_data::json->>''peiSuffixeVoie'')::text AS peiSuffixeVoie,
                  (t.tracabilite_objet_data::json->>''peiVoieTexte'')::text AS peiVoieTexte,
                  (t.tracabilite_objet_data::json->>''peiLieuDitId'')::text AS peiLieuDitId,
                  (t.tracabilite_objet_data::json->>''peiCroisementId'')::text AS peiCroisementId,
                  (t.tracabilite_objet_data::json->>''peiComplementAdresse'')::text AS peiComplementAdresse,
                  (t.tracabilite_objet_data::json->>''peiEnFace'')::boolean AS peiEnFace,

                  (t.tracabilite_objet_data::json->>''peiDomaineId'')::text AS peiDomaineId,
                  (t.tracabilite_objet_data::json->>''peiNatureId'')::text AS peiNatureId,
                  (t.tracabilite_objet_data::json->>''peiSiteId'')::text AS peiSiteId,
                  (t.tracabilite_objet_data::json->>''peiGestionnaireId'')::text AS peiGestionnaireId,
                  (t.tracabilite_objet_data::json->>''peiNatureDeciId'')::text AS peiNatureDeciId,
                  (t.tracabilite_objet_data::json->>''peiZoneSpecialeId'')::text AS peiZoneSpecialeId,
                  (t.tracabilite_objet_data::json->>''peiAnneeFabrication'')::numeric AS peiAnneeFabrication,
                  (t.tracabilite_objet_data::json->>''peiNiveauId'')::text AS peiNiveauId,
                  (t.tracabilite_objet_data::json->>''peiObservation'')::text AS peiObservation,

                  (t.tracabilite_objet_data::json->>''peiDateChangementDispo'')::timestamp with time zone AS peiDateChangementDispo,

                  (t.tracabilite_objet_data::json->>''pibiDiametreId'')::text AS pibiDiametreId,
                  (t.tracabilite_objet_data::json->>''pibiServiceEauId'')::text AS pibiServiceEauId,
                  (t.tracabilite_objet_data::json->>''pibiIdentifiantGestionnaire'')::text AS pibiIdentifiantGestionnaire,
                  (t.tracabilite_objet_data::json->>''pibiRenversable'')::boolean AS pibiRenversable,
                  (t.tracabilite_objet_data::json->>''pibiDispositifInviolabilite'')::boolean AS pibiDispositifInviolabilite,
                  (t.tracabilite_objet_data::json->>''pibiModeleId'')::text AS pibiModeleId,
                  (t.tracabilite_objet_data::json->>''pibiMarqueId'')::text AS pibiMarqueId,
                  (t.tracabilite_objet_data::json->>''pibiReservoirId'')::text AS pibiReservoirId,
                  (t.tracabilite_objet_data::json->>''pibiDebitRenforce'')::boolean AS pibiDebitRenforce,
                  (t.tracabilite_objet_data::json->>''pibiTypeCanalisationId'')::text AS pibiTypeCanalisationId,
                  (t.tracabilite_objet_data::json->>''pibiTypeReseauId'')::text AS pibiTypeReseauId,
                  (t.tracabilite_objet_data::json->>''pibiDiametreCanalisation'')::numeric AS pibiDiametreCanalisation,
                  (t.tracabilite_objet_data::json->>''pibiSurpresse'')::boolean AS pibiSurpresse,
                  (t.tracabilite_objet_data::json->>''pibiAdditive'')::boolean AS pibiAdditive,
                  (t.tracabilite_objet_data::json->>''pibiJumeleId'')::text AS pibiJumeleId,

                  (t.tracabilite_objet_data::json->>''penaDisponibiliteHbe'')::text AS penaDisponibiliteHbe,
                  (t.tracabilite_objet_data::json->>''penaCapacite'')::numeric AS penaCapacite,
                  (t.tracabilite_objet_data::json->>''penaCapaciteIllimitee'')::boolean AS penaCapaciteIllimitee,
                  (t.tracabilite_objet_data::json->>''penaCapaciteIncertaine'')::boolean AS penaCapaciteIncertaine,
                  (t.tracabilite_objet_data::json->>''penaQuantiteAppoint'')::numeric AS penaQuantiteAppoint,
                  (t.tracabilite_objet_data::json->>''penaMateriauId'')::text AS penaMateriauId,
                  (t.tracabilite_objet_data::json->>''penaEquipeHbe'')::boolean AS penaEquipeHbe,
                  (t.tracabilite_objet_data::json->>''typeEnginIds'')::text AS typeEnginIds,
                          t.*
                      FROM
                          historique.tracabilite t
                          INNER JOIN remocra.pei ON t.tracabilite_type_objet = ''PEI'' AND t.tracabilite_objet_data::json->>''peiNumeroComplet'' = remocra.pei.pei_numero_complet
                          LEFT JOIN remocra.utilisateur ON utilisateur_id = t.tracabilite_auteur_id
                      WHERE pei_numero_complet = ''PEI_NUMERO_COMPLET''
          )
          select CAST(to_char(tracabilite_date,''dd/MM/YYYY à HH24hMImSSs'') AS character varying) AS "Date",
              CAST(auteur AS character varying) AS "Auteur",
              CAST(CASE
                  WHEN tracabilite_type_operation = ''INSERT'' THEN ''Création''
                  WHEN tracabilite_type_operation = ''UPDATE'' THEN ''Mise à jour''
                  ELSE ''Suppression''
                  END AS character varying
              ) AS "Type",
              CASE WHEN tracabilite_type_operation = ''INSERT'' OR tracabilite_type_operation = ''UPDATE'' THEN CAST(array_to_string(array_agg(changement),'', '') AS character varying)
              ELSE ''''
              END AS "Modifications réalisées"
          from (
              select
                  a.tracabilite_type_operation, a.tracabilite_date,
                  a.auteur,
                  unnest(
                      ARRAY[
                          COALESCE(CASE WHEN (a.peiNumeroComplet IS DISTINCT FROM b.peiNumeroComplet) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiNumeroComplet IS NOT NULL AND TRIM(a.peiNumeroComplet) <> '''') THEN ''Numéro complet-> '' || a.peiNumeroComplet END,''''),
                          COALESCE(CASE WHEN (NOT(st_equals(a.geometrie,b.geometrie))) OR (a.tracabilite_type_operation =  ''INSERT'' AND a.geometrie IS NOT NULL)  THEN ''Position -> X=''||a.peix || '' Y=''||a.peiy END,''''),
                          COALESCE(CASE WHEN (a.peiNumeroInterne != b.peiNumeroInterne) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiNumeroInterne IS NOT NULL ) THEN ''Numéro Interne -> '' || a.peiNumeroInterne END,''''),
                          COALESCE(CASE WHEN (a.peiDisponibiliteTerrestre IS DISTINCT FROM b.peiDisponibiliteTerrestre) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiDisponibiliteTerrestre IS NOT NULL AND TRIM(a.peiDisponibiliteTerrestre) <> '''') THEN ''Dispo terrestre -> '' || a.peiDisponibiliteTerrestre END,''''),
                          COALESCE(CASE WHEN (a.peiTypePei IS DISTINCT FROM b.peiTypePei) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiTypePei IS NOT NULL AND TRIM(a.peiTypePei) <> '''') THEN ''Type -> '' || a.peiTypePei END,''''),

                          COALESCE(CASE WHEN (a.peiAutoriteDeciId IS DISTINCT FROM b.peiAutoriteDeciId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiAutoriteDeciId IS NOT NULL AND TRIM(a.peiAutoriteDeciId) <> '''') THEN ''Autorité de police DECI -> '' || a.peiAutoriteDeciId END,''''),
                          COALESCE(CASE WHEN (a.peiServicePublicDeciId IS DISTINCT FROM b.peiServicePublicDeciId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiServicePublicDeciId IS NOT NULL AND TRIM(a.peiServicePublicDeciId) <> '''') THEN ''Service public DECI -> '' || a.peiServicePublicDeciId END,''''),
                          COALESCE(CASE WHEN (a.peiMaintenanceDeciId IS DISTINCT FROM b.peiMaintenanceDeciId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiMaintenanceDeciId IS NOT NULL AND TRIM(a.peiMaintenanceDeciId) <> '''') THEN ''Maintenance et CTP -> '' || a.peiMaintenanceDeciId END,''''),

                          COALESCE(CASE WHEN (a.peiCommuneId IS DISTINCT FROM b.peiCommuneId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiCommuneId IS NOT NULL AND TRIM(a.peiCommuneId) <> '''') THEN ''Commune -> '' || a.peiCommuneId END,''''),
                          COALESCE(CASE WHEN (a.peiVoieId IS DISTINCT FROM b.peiVoieId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiVoieId IS NOT NULL AND TRIM(a.peiVoieId) <> '''') THEN ''Voie -> '' || a.peiVoieId END,''''),
                          COALESCE(CASE WHEN (a.peiNumeroVoie IS DISTINCT FROM b.peiNumeroVoie) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiNumeroVoie IS NOT NULL AND a.peiNumeroVoie <> '''') THEN ''Numéro de voie -> '' || a.peiNumeroVoie END,''''),
                          COALESCE(CASE WHEN (a.peiSuffixeVoie IS DISTINCT FROM b.peiSuffixeVoie) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiSuffixeVoie IS NOT NULL AND TRIM(a.peiSuffixeVoie) <> '''') THEN ''Suffixe -> '' || a.peiSuffixeVoie END,''''),
                          COALESCE(CASE WHEN (a.peiVoieTexte IS DISTINCT FROM b.peiVoieTexte) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiVoieTexte IS NOT NULL AND TRIM(a.peiVoieTexte) <> '''') THEN ''Voie (saisie libre) -> '' || a.peiVoieTexte END,''''),
                          COALESCE(CASE WHEN (a.peiLieuDitId IS DISTINCT FROM b.peiLieuDitId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiLieuDitId IS NOT NULL AND TRIM(a.peiLieuDitId) <> '''') THEN ''Lieu dit -> '' || a.peiLieuDitId END,''''),
                          COALESCE(CASE WHEN (a.peiCroisementId IS DISTINCT FROM b.peiCroisementId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiCroisementId IS NOT NULL AND TRIM(a.peiCroisementId) <> '''') THEN ''Croisement -> '' || a.peiCroisementId END,''''),
                          COALESCE(CASE WHEN (a.peiComplementAdresse IS DISTINCT FROM b.peiComplementAdresse) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiComplementAdresse IS NOT NULL AND TRIM(a.peiComplementAdresse) <> '''') THEN ''Complément d''''adresse -> '' || a.peiComplementAdresse END,''''),
                          COALESCE(CASE WHEN (a.peiEnFace IS DISTINCT FROM b.peiEnFace) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiEnFace IS NOT NULL ) THEN ''Situé en face -> '' || a.peiEnFace END,''''),

                          COALESCE(CASE WHEN (a.peiDomaineId IS DISTINCT FROM b.peiDomaineId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiDomaineId IS NOT NULL AND TRIM(a.peiDomaineId) <> '''') THEN ''Domaine -> '' || a.peiDomaineId END,''''),
                          COALESCE(CASE WHEN (a.peiNatureId IS DISTINCT FROM b.peiNatureId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiNatureId IS NOT NULL AND TRIM(a.peiNatureId) <> '''') THEN ''Nature -> '' || a.peiNatureId END,''''),
                          COALESCE(CASE WHEN (a.peiSiteId IS DISTINCT FROM b.peiSiteId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiSiteId IS NOT NULL AND TRIM(a.peiSiteId) <> '''') THEN ''Site -> '' || a.peiSiteId END,''''),
                          COALESCE(CASE WHEN (a.peiGestionnaireId IS DISTINCT FROM b.peiGestionnaireId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiGestionnaireId IS NOT NULL AND TRIM(a.peiGestionnaireId) <> '''') THEN ''Gestionnaire -> '' || a.peiGestionnaireId END,''''),
                          COALESCE(CASE WHEN (a.peiNatureDeciId IS DISTINCT FROM b.peiNatureDeciId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiNatureDeciId IS NOT NULL AND TRIM(a.peiNatureDeciId) <> '''') THEN ''Nature DECI -> '' || a.peiNatureDeciId END,''''),
                          COALESCE(CASE WHEN (a.peiZoneSpecialeId IS DISTINCT FROM b.peiZoneSpecialeId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiZoneSpecialeId IS NOT NULL AND TRIM(a.peiZoneSpecialeId) <> '''') THEN ''Zone spéciale -> '' || a.peiZoneSpecialeId END,''''),
                          COALESCE(CASE WHEN (a.peiAnneeFabrication != b.peiAnneeFabrication) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiAnneeFabrication IS NOT NULL ) THEN ''Année de fabrication -> '' || a.peiAnneeFabrication END,''''),
                          COALESCE(CASE WHEN (a.peiNiveauId IS DISTINCT FROM b.peiNiveauId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiNiveauId IS NOT NULL AND TRIM(a.peiNiveauId) <> '''') THEN ''Niveau -> '' || a.peiNiveauId END,''''),
                          COALESCE(CASE WHEN (a.peiObservation IS DISTINCT FROM b.peiObservation) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiObservation IS NOT NULL AND TRIM(a.peiObservation) <> '''') THEN ''Observations -> '' || a.peiObservation END,''''),

                          COALESCE(CASE WHEN (a.peiDateChangementDispo IS DISTINCT FROM b.peiDateChangementDispo) OR (a.tracabilite_type_operation = ''INSERT'' AND a.peiDateChangementDispo IS NOT NULL ) THEN ''Date de dernier changement de dispo -> '' || to_char(a.peiDateChangementDispo, ''YYYY-MM-DDTHH24:MI:SSZ'') END,''''),

                          COALESCE(CASE WHEN (a.pibiDiametreId IS DISTINCT FROM b.pibiDiametreId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiDiametreId IS NOT NULL AND TRIM(a.pibiDiametreId) <> '''') THEN ''Diamètre -> '' || a.pibiDiametreId END,''''),
                          COALESCE(CASE WHEN (a.pibiServiceEauId IS DISTINCT FROM b.pibiServiceEauId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiServiceEauId IS NOT NULL AND TRIM(a.pibiServiceEauId) <> '''') THEN ''Service des eaux -> '' || a.pibiServiceEauId END,''''),
                          COALESCE(CASE WHEN (a.pibiIdentifiantGestionnaire IS DISTINCT FROM b.pibiIdentifiantGestionnaire) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiIdentifiantGestionnaire IS NOT NULL AND TRIM(a.pibiIdentifiantGestionnaire) <> '''') THEN ''Identifiant gestionnaire  -> '' || a.pibiIdentifiantGestionnaire END,''''),
                          COALESCE(CASE WHEN (a.pibiRenversable IS DISTINCT FROM b.pibiRenversable) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiRenversable IS NOT NULL ) THEN ''Renversable -> '' || a.pibiRenversable END,''''),
                          COALESCE(CASE WHEN (a.pibiDispositifInviolabilite IS DISTINCT FROM b.pibiDispositifInviolabilite) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiDispositifInviolabilite IS NOT NULL ) THEN ''Dispositif d''''inviolabilite -> '' || a.pibiDispositifInviolabilite END,''''),
                          COALESCE(CASE WHEN (a.pibiModeleId IS DISTINCT FROM b.pibiModeleId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiModeleId IS NOT NULL AND TRIM(a.pibiModeleId) <> '''') THEN ''Modèle -> '' || a.pibiModeleId END,''''),
                          COALESCE(CASE WHEN (a.pibiMarqueId IS DISTINCT FROM b.pibiMarqueId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiMarqueId IS NOT NULL AND TRIM(a.pibiMarqueId) <> '''') THEN ''Marque -> '' || a.pibiMarqueId END,''''),
                          COALESCE(CASE WHEN (a.pibiReservoirId IS DISTINCT FROM b.pibiReservoirId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiReservoirId IS NOT NULL AND TRIM(a.pibiReservoirId) <> '''') THEN ''Réservoir -> '' || a.pibiReservoirId END,''''),
                          COALESCE(CASE WHEN (a.pibiDebitRenforce IS DISTINCT FROM b.pibiDebitRenforce) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiDebitRenforce IS NOT NULL ) THEN ''Débit renforcé -> '' || a.pibiDebitRenforce END,''''),
                          COALESCE(CASE WHEN (a.pibiTypeCanalisationId IS DISTINCT FROM b.pibiTypeCanalisationId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiTypeCanalisationId IS NOT NULL AND TRIM(a.pibiTypeCanalisationId) <> '''') THEN ''Type de canalisation -> '' || a.pibiTypeCanalisationId END,''''),
                          COALESCE(CASE WHEN (a.pibiTypeReseauId IS DISTINCT FROM b.pibiTypeReseauId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiTypeReseauId IS NOT NULL AND TRIM(a.pibiTypeReseauId) <> '''') THEN ''Type de réseau -> '' || a.pibiTypeReseauId END,''''),
                          COALESCE(CASE WHEN (a.pibiDiametreCanalisation != b.pibiDiametreCanalisation) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiDiametreCanalisation IS NOT NULL ) THEN ''Diamètre de canalisation -> '' || a.pibiDiametreCanalisation END,''''),
                          COALESCE(CASE WHEN (a.pibiSurpresse IS DISTINCT FROM b.pibiSurpresse) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiSurpresse IS NOT NULL ) THEN ''Réseau surpressé -> '' || a.pibiSurpresse END,''''),
                          COALESCE(CASE WHEN (a.pibiAdditive IS DISTINCT FROM b.pibiAdditive) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiAdditive IS NOT NULL ) THEN ''Réseau additivé -> '' || a.pibiAdditive END,''''),
                          COALESCE(CASE WHEN (a.pibiJumeleId IS DISTINCT FROM b.pibiJumeleId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.pibiJumeleId IS NOT NULL AND TRIM(a.pibiJumeleId) <> '''') THEN ''PIBI jumelé -> '' || a.pibiJumeleId END,''''),

                          COALESCE(CASE WHEN (a.penaDisponibiliteHbe IS DISTINCT FROM b.penaDisponibiliteHbe) OR (a.tracabilite_type_operation = ''INSERT'' AND a.penaDisponibiliteHbe IS NOT NULL AND TRIM(a.penaDisponibiliteHbe) <> '''') THEN ''Dispo HBE -> '' || a.penaDisponibiliteHbe END,''''),
                          COALESCE(CASE WHEN (a.penaCapacite != b.penaCapacite) OR (a.tracabilite_type_operation = ''INSERT'' AND a.penaCapacite IS NOT NULL ) THEN ''penaCapacite -> '' || a.penaCapacite END,''''),
                          COALESCE(CASE WHEN (a.penaCapaciteIllimitee IS DISTINCT FROM b.penaCapaciteIllimitee) OR (a.tracabilite_type_operation = ''INSERT'' AND a.penaCapaciteIllimitee IS NOT NULL ) THEN ''Capacité illimitée -> '' || a.penaCapaciteIllimitee END,''''),
                          COALESCE(CASE WHEN (a.penaCapaciteIncertaine IS DISTINCT FROM b.penaCapaciteIncertaine) OR (a.tracabilite_type_operation = ''INSERT'' AND a.penaCapaciteIncertaine IS NOT NULL ) THEN ''Capacité incertaine -> '' || a.penaCapaciteIncertaine END,''''),
                          COALESCE(CASE WHEN (a.penaQuantiteAppoint != b.penaQuantiteAppoint) OR (a.tracabilite_type_operation = ''INSERT'' AND a.penaQuantiteAppoint IS NOT NULL ) THEN ''Quantité d''''appoint -> '' || a.penaQuantiteAppoint END,''''),
                          COALESCE(CASE WHEN (a.penaMateriauId IS DISTINCT FROM b.penaMateriauId) OR (a.tracabilite_type_operation = ''INSERT'' AND a.penaMateriauId IS NOT NULL AND TRIM(a.penaMateriauId) <> '''') THEN ''Matériau -> '' || a.penaMateriauId END,''''),
                          COALESCE(CASE WHEN (a.penaEquipeHbe IS DISTINCT FROM b.penaEquipeHbe) OR (a.tracabilite_type_operation = ''INSERT'' AND a.penaEquipeHbe IS NOT NULL) THEN ''Equipé HBE  -> '' || a.penaEquipeHbe END,''''),
                          COALESCE(CASE WHEN (a.typeEnginIds IS DISTINCT FROM b.typeEnginIds) OR (a.tracabilite_type_operation = ''INSERT'' AND a.typeEnginIds IS NOT NULL AND a.typeEnginIds <> '''') THEN ''Types d''''engins -> '' || a.typeEnginIds END,'''')



                      ]
                  )  AS changement
              from historique AS a
                  LEFT JOIN historique AS b ON (b.num_ligne =  a.num_ligne + 1)
                WHERE b.num_ligne IS NOT NULL
              )
          wHERE
              (changement IS NOT NULL AND changement <> '''' OR tracabilite_type_operation = ''DELETE'')
          GROUP BY
              tracabilite_type_operation,
              tracabilite_date,
              auteur
          ORDER BY
              tracabilite_date DESC
          ;',


    'DECI'
);


INSERT INTO remocra.rapport_personnalise_parametre (
    rapport_personnalise_parametre_id,
    rapport_personnalise_parametre_rapport_personnalise_id,
    rapport_personnalise_parametre_code,
    rapport_personnalise_parametre_libelle,
    rapport_personnalise_parametre_source_sql,
    rapport_personnalise_parametre_description,
    rapport_personnalise_parametre_source_sql_id,
    rapport_personnalise_parametre_source_sql_libelle,
    rapport_personnalise_parametre_valeur_defaut,
    rapport_personnalise_parametre_is_required,
    rapport_personnalise_parametre_type,
    rapport_personnalise_parametre_ordre
)
SELECT
    gen_random_uuid(),
    rapport_personnalise.rapport_personnalise_id,
    'PEI_NUMERO_COMPLET',
    'Numéro complet du PEI',
    E'select
        distinct pei.pei_numero_complet as id, pei.pei_numero_complet as libelle
      from remocra.pei
      join remocra.zone_integration on
      ST_CONTAINS(
      	zone_integration.zone_integration_geometrie,
      	pei.pei_geometrie
      )
      and zone_integration.zone_integration_id = #ZONE_COMPETENCE_ID#
      order by pei.pei_numero_complet;',
    'Numéro complet du PEI dont on veut l''historique',
    'pei.pei_numero_complet',
    'pei.pei_numero_complet',
    NULL,
    true,
    cast('SELECT_INPUT' as remocra.type_parametre_rapport_courrier),
    1
FROM remocra.rapport_personnalise WHERE rapport_personnalise_code='PEI_HISTORIQUE';
