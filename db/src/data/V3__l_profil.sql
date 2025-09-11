INSERT INTO remocra.l_profil_utilisateur_organisme_groupe_fonctionnalites
(profil_utilisateur_id, profil_organisme_id, groupe_fonctionnalites_id)
VALUES(

          (SELECT profil_utilisateur_id FROM profil_utilisateur WHERE profil_utilisateur_code = 'REMOCRA'),

          (SELECT profil_organisme_id FROM remocra.profil_organisme
           WHERE profil_organisme_code='REMOCRA'),

          (SELECT groupe_fonctionnalites_id
           FROM remocra.groupe_fonctionnalites
           WHERE groupe_fonctionnalites_code='REMOCRA'))
ON CONFLICT (profil_organisme_id, profil_utilisateur_id) DO NOTHING;
