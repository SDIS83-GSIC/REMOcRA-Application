INSERT INTO remocra.oldeb_type_acces(oldeb_type_acces_id, oldeb_type_acces_actif, oldeb_type_acces_code,
                                     oldeb_type_acces_libelle)
VALUES (gen_random_uuid(), TRUE, 'CHC', 'Chemin communal'),
       (gen_random_uuid(), TRUE, 'CHP', 'Chemin privé'),
       (gen_random_uuid(), TRUE, 'IMP', 'Impasse'),
       (gen_random_uuid(), TRUE, 'NR', 'Non renseigné'),
       (gen_random_uuid(), TRUE, 'RD', 'Route départementale');


INSERT INTO remocra.oldeb_type_action(oldeb_type_action_id, oldeb_type_action_actif, oldeb_type_action_code,
                                      oldeb_type_action_libelle)
VALUES (gen_random_uuid(), TRUE, 'CERTIFICAT', 'Certificat de conformité'),
       (gen_random_uuid(), TRUE, 'MISE_DEMEURE', 'Mise en demeure'),
       (gen_random_uuid(), TRUE, 'VERBALISATION', 'Verbalisation'),
       (gen_random_uuid(), TRUE, 'VISITE_SIX_MOIS', 'Repasser dans 6 mois'),
       (gen_random_uuid(), TRUE, 'VISITE_UN_AN', 'Repasser dans 1 an'),
       (gen_random_uuid(), TRUE, 'COURRIER_MAIRIE_1', '1er courrier Mairie'),
       (gen_random_uuid(), TRUE, 'COURRIER_MAIRIE_2', '2nd courrier Mairie');


INSERT INTO remocra.oldeb_type_categorie_anomalie (oldeb_type_categorie_anomalie_id,
                                                   oldeb_type_categorie_anomalie_actif,
                                                   oldeb_type_categorie_anomalie_code,
                                                   oldeb_type_categorie_anomalie_libelle)
VALUES (gen_random_uuid(), TRUE, 'ACCES', 'Accès au terrain'),
       (gen_random_uuid(), TRUE, 'PARCELLE', 'Terrain');


INSERT INTO remocra.oldeb_type_categorie_caracteristique (oldeb_type_categorie_caracteristique_id,
                                                          oldeb_type_categorie_caracteristique_actif,
                                                          oldeb_type_categorie_caracteristique_code,
                                                          oldeb_type_categorie_caracteristique_libelle)
VALUES (gen_random_uuid(), TRUE, 'ANIMAUX', 'Animaux'),
       (gen_random_uuid(), TRUE, 'BATIMENTS', 'Bâtiments'),
       (gen_random_uuid(), TRUE, 'DIVERS', 'Divers'),
       (gen_random_uuid(), TRUE, 'HYDROCARBURE', 'Hydrocarbures'),
       (gen_random_uuid(), TRUE, 'EAU', 'Ressources en eau et équipements'),
       (gen_random_uuid(), TRUE, 'RISQUE_HUMAIN', 'Risque humain');


INSERT INTO remocra.oldeb_type_avis (oldeb_type_avis_id, oldeb_type_avis_actif, oldeb_type_avis_code,
                                     oldeb_type_avis_libelle)
VALUES (gen_random_uuid(), TRUE, 'SATISFAISANT', 'Satisfaisant'),
       (gen_random_uuid(), TRUE, 'NON_SATISFAISANT', 'Non satisfaisant'),
       (gen_random_uuid(), TRUE, 'A_COMPLETER', 'A compléter');


INSERT INTO remocra.oldeb_type_debroussaillement (oldeb_type_debroussaillement_id, oldeb_type_debroussaillement_actif,
                                                  oldeb_type_debroussaillement_code,
                                                  oldeb_type_debroussaillement_libelle)
VALUES (gen_random_uuid(), TRUE, 'AV', 'A voir'),
       (gen_random_uuid(), TRUE, 'N', 'Non'),
       (gen_random_uuid(), TRUE, 'O', 'Oui'),
       (gen_random_uuid(), TRUE, 'PA', 'Partiel');


INSERT INTO remocra.oldeb_type_residence (oldeb_type_residence_id, oldeb_type_residence_actif,
                                          oldeb_type_residence_code, oldeb_type_residence_libelle)
VALUES (gen_random_uuid(), TRUE, 'A', 'Autre'),
       (gen_random_uuid(), TRUE, 'C', 'Cabanon'),
       (gen_random_uuid(), TRUE, 'P', 'Principale'),
       (gen_random_uuid(), TRUE, 'S', 'Secondaire');


INSERT INTO remocra.oldeb_type_suite (oldeb_type_suite_id, oldeb_type_suite_actif, oldeb_type_suite_code,
                                      oldeb_type_suite_libelle)
VALUES (gen_random_uuid(), TRUE, 'CONTRAVENTION', 'Contravention'),
       (gen_random_uuid(), TRUE, 'DECISION_TRIBUNAL', 'Décision tribunal'),
       (gen_random_uuid(), TRUE, 'MISE_DEMEURE', 'Mise en demeure'),
       (gen_random_uuid(), TRUE, 'PV_INEXECUTION', 'PV d''inexécution'),
       (gen_random_uuid(), TRUE, 'TA_MAIRIE', 'TA transmis au maire');


INSERT INTO remocra.oldeb_type_zone_urbanisme (oldeb_type_zone_urbanisme_id, oldeb_type_zone_urbanisme_actif,
                                               oldeb_type_zone_urbanisme_code, oldeb_type_zone_urbanisme_libelle)
VALUES (gen_random_uuid(), TRUE, 'A', 'Zone agricole (A)'),
       (gen_random_uuid(), TRUE, 'N', 'Zone naturelle / forestière (N)'),
       (gen_random_uuid(), TRUE, 'U', 'Zone urbaine (U)'),
       (gen_random_uuid(), TRUE, 'AU', 'Zone à urbaniser (AU)');


INSERT INTO remocra.oldeb_type_anomalie (oldeb_type_anomalie_id, oldeb_type_anomalie_actif, oldeb_type_anomalie_code,
                                         oldeb_type_anomalie_libelle,
                                         oldeb_type_anomalie_oldeb_type_categorie_anomalie_id)
VALUES (gen_random_uuid(), TRUE, 'ANO_PARCELLE_001', 'Arbres à moins de 3 m des constructions',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_002', 'Arbustes maintenus sous les arbres conservés',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_003', 'Bouquets d''arbres supérieurs à 15 m de diamètre ',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_004', 'Bouquets d''arbustes supérieurs à 3 m de diamètre',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_005', 'Haie à moins de 3 m des constructions et de plus de 15 m de long',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_006', 'Herbe non tenue rase',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_007', 'Houppiers des arbres à moins de 3 m entre eux',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_009', 'Non ratissage de la litière et des feuilles dans les 20 m',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_010', 'Non réalisation au delà des limites de la parcelle',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_008', 'Non élagage des arbres conservés à 2,5 m ou 2/3 de leur hauteur',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_011', 'Présence de bois morts, rémanents de coupe',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_ACCES_001', 'Voirie dégradée',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'ACCES')),
       (gen_random_uuid(), TRUE, 'ANO_PARCELLE_012',
        'Végétation à moins de 4 m au dessus de la plate-forme et sur l''emprise',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'PARCELLE')),
       (gen_random_uuid(), TRUE, 'ANO_ACCES_002', 'Gabarit de 4m non réalisé',
        (SELECT oldeb_type_categorie_anomalie_ID
         FROM remocra.oldeb_type_categorie_anomalie
         WHERE oldeb_type_categorie_anomalie_code = 'ACCES'));


INSERT INTO remocra.oldeb_type_caracteristique (oldeb_type_caracteristique_id, oldeb_type_caracteristique_actif,
                                                oldeb_type_caracteristique_code, oldeb_type_caracteristique_libelle,
                                                oldeb_type_caracteristique_oldeb_type_categorie_id)
VALUES (gen_random_uuid(), TRUE, 'BGA', 'Bouteille gaz extérieure',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'HYDROCARBURE')),
       (gen_random_uuid(), TRUE, 'BO', 'Bovins',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'ANIMAUX')),
       (gen_random_uuid(), TRUE, 'CA', 'Caprins',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'ANIMAUX')),
       (gen_random_uuid(), TRUE, 'CHARPENTE', 'Charpente apparente',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'BATIMENTS')),
       (gen_random_uuid(), TRUE, 'CFA', 'Cuve fuel aérienne',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'HYDROCARBURE')),
       (gen_random_uuid(), TRUE, 'CFE', 'Cuve fuel enterrée',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'HYDROCARBURE')),
       (gen_random_uuid(), TRUE, 'CGA', 'Cuve gaz aérienne',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'HYDROCARBURE')),
       (gen_random_uuid(), TRUE, 'CGE', 'Cuve gaz enterrée ',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'HYDROCARBURE')),
       (gen_random_uuid(), TRUE, 'EQ', 'Equins',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'ANIMAUX')),
       (gen_random_uuid(), TRUE, 'MOTOPOMPE', 'Motopompe',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'EAU')),
       (gen_random_uuid(), TRUE, 'OV', 'Ovins',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'ANIMAUX')),
       (gen_random_uuid(), TRUE, 'M', 'Personne malade',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'RISQUE_HUMAIN')),
       (gen_random_uuid(), TRUE, 'MR', 'Personne à mobilité réduite',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'RISQUE_HUMAIN')),
       (gen_random_uuid(), TRUE, 'AGE', 'Personne âgée',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'RISQUE_HUMAIN')),
       (gen_random_uuid(), TRUE, 'PISCINE', 'Piscine',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'EAU')),
       (gen_random_uuid(), TRUE, 'ACCES_PISCINE', 'Piscine accessible aux véhicules',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'EAU')),
       (gen_random_uuid(), TRUE, 'STOCKAGE_BOIS', 'Stockage de bois',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'DIVERS')),
       (gen_random_uuid(), TRUE, 'TOITURE', 'Toiture sale',
        (SELECT oldeb_type_categorie_caracteristique_id
         FROM remocra.oldeb_type_categorie_caracteristique
         WHERE oldeb_type_categorie_caracteristique_code = 'BATIMENTS'));
