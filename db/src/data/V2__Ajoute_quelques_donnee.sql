INSERT
INTO
    remocra.indisponibilite_temporaire
(indisponibilite_temporaire_id,
 indisponibilite_temporaire_date_debut,
 indisponibilite_temporaire_date_fin,
 indisponibilite_temporaire_motif,
 indisponibilite_temporaire_observation,
 indisponibilite_temporaire_mail_avant_indisponibilite,
 indisponibilite_temporaire_mail_apres_indisponibilite)
VALUES(gen_random_uuid(),
       NOW(),
       NOW()+(floor(random() * 10 + 3)::int || ' days')::INTERVAL,
       'Test 4',
       'Lorem ipsum dolor sit amet.',
       true,
       true);

UPDATE remocra.profil_droit
SET profil_droit_droits=(SELECT enum_range(NULL::remocra."DROIT"))
WHERE profil_droit_code='REMOCRA';

INSERT INTO remocra."module" (module_id, module_type, module_titre, module_image, module_contenu_html, module_colonne, module_ligne) VALUES(gen_random_uuid(), 'DECI'::remocra."type_module", 'Gestion des PEI', NULL, NULL, 1, 1);
INSERT INTO remocra."module" (module_id, module_type, module_titre, module_image, module_contenu_html, module_colonne, module_ligne) VALUES(gen_random_uuid(), 'COUVERTURE_HYDRAULIQUE'::remocra."type_module", 'Couverture hydraulique', NULL, NULL, 2, 2);

INSERT
INTO
    remocra.indisponibilite_temporaire
(indisponibilite_temporaire_id,
 indisponibilite_temporaire_date_debut,
 indisponibilite_temporaire_date_fin,
 indisponibilite_temporaire_motif,
 indisponibilite_temporaire_observation,
 indisponibilite_temporaire_mail_avant_indisponibilite,
 indisponibilite_temporaire_mail_apres_indisponibilite)
VALUES(gen_random_uuid(),
       NOW(),
       NOW()+(floor(random() * 10 + 3)::int || ' days')::INTERVAL,
       'Test 1',
       'Lorem ipsum dolor sit amet.',
       true,
       false);


INSERT
INTO
    remocra.indisponibilite_temporaire
(indisponibilite_temporaire_id,
 indisponibilite_temporaire_date_debut,
 indisponibilite_temporaire_date_fin,
 indisponibilite_temporaire_motif,
 indisponibilite_temporaire_observation,
 indisponibilite_temporaire_mail_avant_indisponibilite,
 indisponibilite_temporaire_mail_apres_indisponibilite)
VALUES(gen_random_uuid(),
       NOW()+(floor(random() * 10)::int || ' days')::INTERVAL,
       NOW()+(floor(random() * 10 + 10)::int || ' days')::INTERVAL,
       'Test 2',
       'Lorem ipsum dolor sit amet.',
       true,
       true);

INSERT INTO remocra.l_profil_utilisateur_organisme_droit
(profil_utilisateur_id, profil_organisme_id, profil_droit_id)
VALUES(

          (SELECT profil_utilisateur_id FROM profil_utilisateur WHERE profil_utilisateur_code = 'REMOCRA'),

          (SELECT profil_organisme_id FROM remocra.profil_organisme
           WHERE profil_organisme_code='REMOCRA'),

          (SELECT profil_droit_id
           FROM remocra.profil_droit
           WHERE profil_droit_code='REMOCRA'));

UPDATE remocra.utilisateur
SET utilisateur_actif=true, utilisateur_email='remocra@atolcd.com', utilisateur_nom='REMOcRA', utilisateur_prenom='REMOcRA', utilisateur_username='remocra',
    utilisateur_telephone=NULL, utilisateur_can_be_notified=true,
    utilisateur_profil_utilisateur_id = (SELECT profil_utilisateur_id FROM profil_utilisateur WHERE profil_utilisateur_code = 'REMOCRA'),
    utilisateur_organisme_id = (SELECT organisme_id FROM organisme WHERE organisme_code = 'REMOCRA')
WHERE utilisateur_username ilike 'remocra';


INSERT
INTO
    remocra.indisponibilite_temporaire
(indisponibilite_temporaire_id,
 indisponibilite_temporaire_date_debut,
 indisponibilite_temporaire_date_fin,
 indisponibilite_temporaire_motif,
 indisponibilite_temporaire_observation,
 indisponibilite_temporaire_mail_avant_indisponibilite,
 indisponibilite_temporaire_mail_apres_indisponibilite)
VALUES(gen_random_uuid(),
       NOW(),
       NOW()+(floor(random() * 10 + 3)::int || ' days')::INTERVAL,
       'Test 3',
       NULL,
       false,
       true);

INSERT INTO remocra."module"
(module_id, module_type, module_titre, module_image, module_contenu_html, module_colonne, module_ligne)
VALUES('cbd8a301-890d-49cd-b7ef-1d687ff4ae5e'::uuid, 'DECI'::remocra.type_module, 'Point d''eau', NULL, NULL, 1, 1);

INSERT INTO remocra.l_indisponibilite_temporaire_pei
(indisponibilite_temporaire_id, pei_id)
VALUES((SELECT indisponibilite_temporaire_id FROM remocra.remocra.indisponibilite_temporaire WHERE indisponibilite_temporaire_motif ='Test 1'),
       (SELECT pei_id

        FROM remocra.pei
        WHERE pei_numero_complet='2150_37'));

INSERT INTO remocra.l_indisponibilite_temporaire_pei
(indisponibilite_temporaire_id, pei_id)
VALUES((SELECT indisponibilite_temporaire_id FROM remocra.remocra.indisponibilite_temporaire WHERE indisponibilite_temporaire_motif ='Test 1'),
       (SELECT pei_id

        FROM remocra.pei
        WHERE pei_numero_complet='2150_38'));

INSERT INTO remocra.l_indisponibilite_temporaire_pei
(indisponibilite_temporaire_id, pei_id)
VALUES((SELECT indisponibilite_temporaire_id FROM remocra.remocra.indisponibilite_temporaire WHERE indisponibilite_temporaire_motif ='Test 1'),
       (SELECT pei_id

        FROM remocra.pei
        WHERE pei_numero_complet='2150_39'));

INSERT INTO remocra.l_indisponibilite_temporaire_pei
(indisponibilite_temporaire_id, pei_id)
VALUES((SELECT indisponibilite_temporaire_id FROM remocra.remocra.indisponibilite_temporaire WHERE indisponibilite_temporaire_motif ='Test 3'),
       (SELECT pei_id

        FROM remocra.pei
        WHERE pei_numero_complet='2134_40'));

INSERT INTO remocra.l_indisponibilite_temporaire_pei
(indisponibilite_temporaire_id, pei_id)
VALUES((SELECT indisponibilite_temporaire_id FROM remocra.remocra.indisponibilite_temporaire WHERE indisponibilite_temporaire_motif ='Test 2'),
       (SELECT pei_id

        FROM remocra.pei
        WHERE pei_numero_complet='2134_40'));

INSERT INTO remocra.l_indisponibilite_temporaire_pei
(indisponibilite_temporaire_id, pei_id)
VALUES((SELECT indisponibilite_temporaire_id FROM remocra.remocra.indisponibilite_temporaire WHERE indisponibilite_temporaire_motif ='Test 2'),
       (SELECT pei_id
        FROM remocra.pei
        WHERE pei_numero_complet='2150_37'));

INSERT INTO remocra.l_indisponibilite_temporaire_pei
(indisponibilite_temporaire_id, pei_id)
VALUES((SELECT indisponibilite_temporaire_id FROM remocra.remocra.indisponibilite_temporaire WHERE indisponibilite_temporaire_motif ='Test 4'),
       (SELECT pei_id
        FROM remocra.pei
        WHERE pei_numero_complet='2150_37'));



UPDATE remocra.utilisateur
SET utilisateur_profil_utilisateur_id=(SELECT profil_utilisateur_id FROM profil_utilisateur WHERE profil_utilisateur_code ilike 'REMOCRA'),
    utilisateur_organisme_id=(SELECT organisme_id FROM organisme WHERE organisme_code ilike 'REMOCRA')
WHERE utilisateur_username ILIKE 'remocra';


UPDATE remocra.parametre
SET parametre_valeur='["NUMERO_COMPLET", "TYPE_PEI", "COMMUNE", "PEI_NEXT_RECOP"]'
WHERE parametre_code='PEI_COLONNES';


UPDATE remocra.parametre
SET parametre_code='PEI_TOLERANCE_COMMUNE_METRES', parametre_valeur='0'
WHERE parametre_code='PEI_TOLERANCE_COMMUNE_METRES';

UPDATE remocra.parametre
SET parametre_code='TOLERANCE_VOIES_METRES', parametre_valeur='0'
WHERE parametre_code='TOLERANCE_VOIES_METRES';

