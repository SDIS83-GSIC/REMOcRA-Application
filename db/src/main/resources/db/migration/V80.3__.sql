INSERT INTO remocra.rcci_type_degre_certitude(rcci_type_degre_certitude_id, rcci_type_degre_certitude_actif,
                                              rcci_type_degre_certitude_code, rcci_type_degre_certitude_libelle)
VALUES (gen_random_uuid(), true, 'CERTAINE', 'Certaine'),
       (gen_random_uuid(), true, 'SUPPOSE', 'Supposée'),
       (gen_random_uuid(), true, 'DTERMINE', 'Déterminée'),
       (gen_random_uuid(), true, 'PROBABLE', 'Probable'),
       (gen_random_uuid(), true, 'INCONNUE', 'Inconnue');

INSERT INTO remocra.rcci_type_origine_alerte(rcci_type_origine_alerte_id, rcci_type_origine_alerte_actif,
                                             rcci_type_origine_alerte_code, rcci_type_origine_alerte_libelle)
VALUES (gen_random_uuid(), true, 'VIGIE', 'Vigie'),
       (gen_random_uuid(), true, 'POLICEGENDARMERIE', 'Police-Gendarmerie'),
       (gen_random_uuid(), true, 'POPULATION', 'Population'),
       (gen_random_uuid(), true, 'PATROUILLE', 'Patrouille'),
       (gen_random_uuid(), true, 'MOYENARIEN', 'Moyen aérien'),
       (gen_random_uuid(), true, 'AUTRE', 'Autre');

INSERT INTO remocra.rcci_type_promethee_famille(rcci_type_promethee_famille_id, rcci_type_promethee_famille_actif,
                                                rcci_type_promethee_famille_code, rcci_type_promethee_famille_libelle)
VALUES (gen_random_uuid(), true, 'NATURELLE', 'Naturelle'),
       (gen_random_uuid(), true, 'ACCIDENTELLE', 'Accidentelle liée aux installations'),
       (gen_random_uuid(), true, 'MALVEILLANCE', 'Malveillance origine humaine intentionnelle'),
       (gen_random_uuid(), true, 'INVOLONTAIREPRO', 'Involontaire liée aux travaux professionnels'),
       (gen_random_uuid(), true, 'INVOLONTAIREPART', 'Involontaire liée aux particuliers');

INSERT INTO remocra.rcci_type_promethee_partition(rcci_type_promethee_partition_id, rcci_type_promethee_partition_actif,
                                                  rcci_type_promethee_partition_code,
                                                  rcci_type_promethee_partition_libelle,
                                                  rcci_type_promethee_partition_rcci_type_promethee_famille_id)
VALUES (gen_random_uuid(), true, 'NATFOUDRE', 'Foudre', (SELECT rcci_type_promethee_famille_id
                                                         FROM remocra.rcci_type_promethee_famille
                                                         WHERE rcci_type_promethee_famille_code = 'NATURELLE')),
       (gen_random_uuid(), true, 'ACCLIGNEELEC', 'Ligne électrique', (SELECT rcci_type_promethee_famille_id
                                                                      FROM remocra.rcci_type_promethee_famille
                                                                      WHERE rcci_type_promethee_famille_code = 'ACCIDENTELLE')),
       (gen_random_uuid(), true, 'ACCCHEMINFER', 'Chemin de fer', (SELECT rcci_type_promethee_famille_id
                                                                   FROM remocra.rcci_type_promethee_famille
                                                                   WHERE rcci_type_promethee_famille_code = 'ACCIDENTELLE')),
       (gen_random_uuid(), true, 'ACCVEHICULE', 'Véhicule', (SELECT rcci_type_promethee_famille_id
                                                             FROM remocra.rcci_type_promethee_famille
                                                             WHERE rcci_type_promethee_famille_code = 'ACCIDENTELLE')),
       (gen_random_uuid(), true, 'ACCDEPOTORD', 'Dépôt ordure', (SELECT rcci_type_promethee_famille_id
                                                                 FROM remocra.rcci_type_promethee_famille
                                                                 WHERE rcci_type_promethee_famille_code = 'ACCIDENTELLE')),
       (gen_random_uuid(), true, 'MALCONFLIT', 'Conflit', (SELECT rcci_type_promethee_famille_id
                                                           FROM remocra.rcci_type_promethee_famille
                                                           WHERE rcci_type_promethee_famille_code = 'MALVEILLANCE')),
       (gen_random_uuid(), true, 'MALINTERET', 'Intérêt', (SELECT rcci_type_promethee_famille_id
                                                           FROM remocra.rcci_type_promethee_famille
                                                           WHERE rcci_type_promethee_famille_code = 'MALVEILLANCE')),
       (gen_random_uuid(), true, 'MALPYROMAN', 'Pyromanie', (SELECT rcci_type_promethee_famille_id
                                                             FROM remocra.rcci_type_promethee_famille
                                                             WHERE rcci_type_promethee_famille_code = 'MALVEILLANCE')),
       (gen_random_uuid(), true, 'TRAVPROFORST', 'Travaux forestiers', (SELECT rcci_type_promethee_famille_id
                                                                        FROM remocra.rcci_type_promethee_famille
                                                                        WHERE rcci_type_promethee_famille_code = 'INVOLONTAIREPRO')),
       (gen_random_uuid(), true, 'TRAVPROAGRIC', 'Travaux agricoles', (SELECT rcci_type_promethee_famille_id
                                                                       FROM remocra.rcci_type_promethee_famille
                                                                       WHERE rcci_type_promethee_famille_code = 'INVOLONTAIREPRO')),
       (gen_random_uuid(), true, 'TRAVPROINDUS', 'Travaux industriels, publics, artisanaux...',
        (SELECT rcci_type_promethee_famille_id
         FROM remocra.rcci_type_promethee_famille
         WHERE rcci_type_promethee_famille_code = 'INVOLONTAIREPRO')),
       (gen_random_uuid(), true, 'TRAVPROREPR', 'Reprise', (SELECT rcci_type_promethee_famille_id
                                                            FROM remocra.rcci_type_promethee_famille
                                                            WHERE rcci_type_promethee_famille_code = 'INVOLONTAIREPRO')),
       (gen_random_uuid(), true, 'TRAVPARTTRAV', 'Travaux', (SELECT rcci_type_promethee_famille_id
                                                             FROM remocra.rcci_type_promethee_famille
                                                             WHERE rcci_type_promethee_famille_code = 'INVOLONTAIREPART')),
       (gen_random_uuid(), true, 'TRAVPARTLOIS', 'Loisirs', (SELECT rcci_type_promethee_famille_id
                                                             FROM remocra.rcci_type_promethee_famille
                                                             WHERE rcci_type_promethee_famille_code = 'INVOLONTAIREPART')),
       (gen_random_uuid(), true, 'TRAVPARTJETOB', 'Jet d''objets incandescents', (SELECT rcci_type_promethee_famille_id
                                                                                  FROM remocra.rcci_type_promethee_famille
                                                                                  WHERE rcci_type_promethee_famille_code = 'INVOLONTAIREPART'));

INSERT INTO remocra.rcci_type_promethee_categorie(rcci_type_promethee_categorie_id, rcci_type_promethee_categorie_actif,
                                                  rcci_type_promethee_categorie_code,
                                                  rcci_type_promethee_categorie_libelle,
                                                  rcci_type_promethee_categorie_rcci_type_promethee_partition_id)
VALUES (gen_random_uuid(), true, 'NATFOUDRE', 'Foudre', (SELECT rcci_type_promethee_partition_id
                                                         FROM remocra.rcci_type_promethee_partition
                                                         WHERE rcci_type_promethee_partition_code = 'NATFOUDRE')),
       (gen_random_uuid(), true, 'ACCLIGNEELECRUPT', 'Rupture', (SELECT rcci_type_promethee_partition_id
                                                                 FROM remocra.rcci_type_promethee_partition
                                                                 WHERE rcci_type_promethee_partition_code = 'ACCLIGNEELEC')),
       (gen_random_uuid(), true, 'ACCLIGNEELECAMOR', 'Amorçage', (SELECT rcci_type_promethee_partition_id
                                                                  FROM remocra.rcci_type_promethee_partition
                                                                  WHERE rcci_type_promethee_partition_code = 'ACCLIGNEELEC')),
       (gen_random_uuid(), true, 'ACCCHEMINFER', 'Chemin de fer', (SELECT rcci_type_promethee_partition_id
                                                                   FROM remocra.rcci_type_promethee_partition
                                                                   WHERE rcci_type_promethee_partition_code = 'ACCCHEMINFER')),
       (gen_random_uuid(), true, 'ACCVEHICULEECH', 'Echappement, freins...', (SELECT rcci_type_promethee_partition_id
                                                                              FROM remocra.rcci_type_promethee_partition
                                                                              WHERE rcci_type_promethee_partition_code = 'ACCVEHICULE')),
       (gen_random_uuid(), true, 'ACCVEHICULEINC', 'Incendie', (SELECT rcci_type_promethee_partition_id
                                                                FROM remocra.rcci_type_promethee_partition
                                                                WHERE rcci_type_promethee_partition_code = 'ACCVEHICULE')),
       (gen_random_uuid(), true, 'ACCDEPOTORDOFF', 'Officiel', (SELECT rcci_type_promethee_partition_id
                                                                FROM remocra.rcci_type_promethee_partition
                                                                WHERE rcci_type_promethee_partition_code = 'ACCDEPOTORD')),
       (gen_random_uuid(), true, 'ACCDEPOTORDCLAN', 'Clandestin', (SELECT rcci_type_promethee_partition_id
                                                                   FROM remocra.rcci_type_promethee_partition
                                                                   WHERE rcci_type_promethee_partition_code = 'ACCDEPOTORD')),
       (gen_random_uuid(), true, 'MALCONFLITSOL', 'Occupation du sol', (SELECT rcci_type_promethee_partition_id
                                                                        FROM remocra.rcci_type_promethee_partition
                                                                        WHERE rcci_type_promethee_partition_code = 'MALCONFLIT')),
       (gen_random_uuid(), true, 'MALCONFLITCHAS', 'Chasse', (SELECT rcci_type_promethee_partition_id
                                                              FROM remocra.rcci_type_promethee_partition
                                                              WHERE rcci_type_promethee_partition_code = 'MALCONFLIT')),
       (gen_random_uuid(), true, 'MALINTERETSOL', 'Occupation du sol', (SELECT rcci_type_promethee_partition_id
                                                                        FROM remocra.rcci_type_promethee_partition
                                                                        WHERE rcci_type_promethee_partition_code = 'MALINTERET')),
       (gen_random_uuid(), true, 'MALINTERETCYN', 'Cynégétique', (SELECT rcci_type_promethee_partition_id
                                                                  FROM remocra.rcci_type_promethee_partition
                                                                  WHERE rcci_type_promethee_partition_code = 'MALINTERET')),
       (gen_random_uuid(), true, 'MALINTERETPAS', 'Pastoralisme', (SELECT rcci_type_promethee_partition_id
                                                                   FROM remocra.rcci_type_promethee_partition
                                                                   WHERE rcci_type_promethee_partition_code = 'MALINTERET')),
       (gen_random_uuid(), true, 'MALPYROMAN', 'Pyromanie', (SELECT rcci_type_promethee_partition_id
                                                             FROM remocra.rcci_type_promethee_partition
                                                             WHERE rcci_type_promethee_partition_code = 'MALPYROMAN')),
       (gen_random_uuid(), true, 'TRAVPROFORSTMAC', 'Machine-outil', (SELECT rcci_type_promethee_partition_id
                                                                      FROM remocra.rcci_type_promethee_partition
                                                                      WHERE rcci_type_promethee_partition_code = 'TRAVPROFORST')),
       (gen_random_uuid(), true, 'TRAVPROFORSTVEGP', 'Feu végétaux sur pied', (SELECT rcci_type_promethee_partition_id
                                                                               FROM remocra.rcci_type_promethee_partition
                                                                               WHERE rcci_type_promethee_partition_code = 'TRAVPROFORST')),
       (gen_random_uuid(), true, 'TRAVPROFORSTVEGC', 'Feu végétaux coupés', (SELECT rcci_type_promethee_partition_id
                                                                             FROM remocra.rcci_type_promethee_partition
                                                                             WHERE rcci_type_promethee_partition_code = 'TRAVPROFORST')),
       (gen_random_uuid(), true, 'TRAVPROAGRICMAC', 'Machine-outil', (SELECT rcci_type_promethee_partition_id
                                                                      FROM remocra.rcci_type_promethee_partition
                                                                      WHERE rcci_type_promethee_partition_code = 'TRAVPROAGRIC')),
       (gen_random_uuid(), true, 'TRAVPROAGRICVEGP', 'Feu végétaux sur pied', (SELECT rcci_type_promethee_partition_id
                                                                               FROM remocra.rcci_type_promethee_partition
                                                                               WHERE rcci_type_promethee_partition_code = 'TRAVPROAGRIC')),
       (gen_random_uuid(), true, 'TRAVPROAGRICVEGC', 'Feu végétaux coupés', (SELECT rcci_type_promethee_partition_id
                                                                             FROM remocra.rcci_type_promethee_partition
                                                                             WHERE rcci_type_promethee_partition_code = 'TRAVPROAGRIC')),
       (gen_random_uuid(), true, 'TRAVPROAGRICPAS', 'Feu pastoral', (SELECT rcci_type_promethee_partition_id
                                                                     FROM remocra.rcci_type_promethee_partition
                                                                     WHERE rcci_type_promethee_partition_code = 'TRAVPROAGRIC')),
       (gen_random_uuid(), true, 'TRAVPROINDUSMAC', 'Machine-outil', (SELECT rcci_type_promethee_partition_id
                                                                      FROM remocra.rcci_type_promethee_partition
                                                                      WHERE rcci_type_promethee_partition_code = 'TRAVPROINDUS')),
       (gen_random_uuid(), true, 'TRAVPROINDUSVEGP', 'Feu végétaux sur pied', (SELECT rcci_type_promethee_partition_id
                                                                               FROM remocra.rcci_type_promethee_partition
                                                                               WHERE rcci_type_promethee_partition_code = 'TRAVPROINDUS')),
       (gen_random_uuid(), true, 'TRAVPROINDUSVEGC', 'Feu végétaux coupés', (SELECT rcci_type_promethee_partition_id
                                                                             FROM remocra.rcci_type_promethee_partition
                                                                             WHERE rcci_type_promethee_partition_code = 'TRAVPROINDUS')),
       (gen_random_uuid(), true, 'TRAVPROREPR', 'Reprise', (SELECT rcci_type_promethee_partition_id
                                                            FROM remocra.rcci_type_promethee_partition
                                                            WHERE rcci_type_promethee_partition_code = 'TRAVPROREPR')),
       (gen_random_uuid(), true, 'TRAVPARTTRAVMAC', 'Machine-outil', (SELECT rcci_type_promethee_partition_id
                                                                      FROM remocra.rcci_type_promethee_partition
                                                                      WHERE rcci_type_promethee_partition_code = 'TRAVPARTTRAV')),
       (gen_random_uuid(), true, 'TRAVPARTTRAVVEGP', 'Feu végétaux sur pied', (SELECT rcci_type_promethee_partition_id
                                                                               FROM remocra.rcci_type_promethee_partition
                                                                               WHERE rcci_type_promethee_partition_code = 'TRAVPARTTRAV')),
       (gen_random_uuid(), true, 'TRAVPARTTRAVCEGC', 'Feu végétaux coupés', (SELECT rcci_type_promethee_partition_id
                                                                             FROM remocra.rcci_type_promethee_partition
                                                                             WHERE rcci_type_promethee_partition_code = 'TRAVPARTTRAV')),
       (gen_random_uuid(), true, 'TRAVPARTLOISJEU', 'Jeu d''enfants, pétard...',
        (SELECT rcci_type_promethee_partition_id
         FROM remocra.rcci_type_promethee_partition
         WHERE rcci_type_promethee_partition_code = 'TRAVPARTLOIS')),
       (gen_random_uuid(), true, 'TRAVPARTLOISFART', 'Feu d''artifice', (SELECT rcci_type_promethee_partition_id
                                                                         FROM remocra.rcci_type_promethee_partition
                                                                         WHERE rcci_type_promethee_partition_code = 'TRAVPARTLOIS')),
       (gen_random_uuid(), true, 'TRAVPARTLOISBARB', 'Barbecue, réchaud, feu loisir',
        (SELECT rcci_type_promethee_partition_id
         FROM remocra.rcci_type_promethee_partition
         WHERE rcci_type_promethee_partition_code = 'TRAVPARTLOIS')),
       (gen_random_uuid(), true, 'TRAVPARTJETOBMEGP', 'Mégot de promeneur', (SELECT rcci_type_promethee_partition_id
                                                                             FROM remocra.rcci_type_promethee_partition
                                                                             WHERE rcci_type_promethee_partition_code = 'TRAVPARTJETOB')),
       (gen_random_uuid(), true, 'TRAVPARTJETOBMEGV', 'Mégot par véhicule', (SELECT rcci_type_promethee_partition_id
                                                                             FROM remocra.rcci_type_promethee_partition
                                                                             WHERE rcci_type_promethee_partition_code = 'TRAVPARTJETOB')),
       (gen_random_uuid(), true, 'TRAVPARTJETOBFUS', 'Fusée de détresse', (SELECT rcci_type_promethee_partition_id
                                                                           FROM remocra.rcci_type_promethee_partition
                                                                           WHERE rcci_type_promethee_partition_code = 'TRAVPARTJETOB')),
       (gen_random_uuid(), true, 'TRAVPARTJETOBCEND', 'Déversement cendres chaudes',
        (SELECT rcci_type_promethee_partition_id
         FROM remocra.rcci_type_promethee_partition
         WHERE rcci_type_promethee_partition_code = 'TRAVPARTJETOB'));
