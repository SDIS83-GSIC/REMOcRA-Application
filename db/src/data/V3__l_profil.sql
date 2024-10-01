INSERT INTO remocra.l_profil_utilisateur_organisme_droit
(profil_utilisateur_id, profil_organisme_id, profil_droit_id)
VALUES(

          (SELECT profil_utilisateur_id FROM profil_utilisateur WHERE profil_utilisateur_code = 'REMOCRA'),

          (SELECT profil_organisme_id FROM remocra.profil_organisme
           WHERE profil_organisme_code='REMOCRA'),

          (SELECT profil_droit_id
           FROM remocra.profil_droit
           WHERE profil_droit_code='REMOCRA'))
ON CONFLICT (profil_organisme_id, profil_utilisateur_id) DO NOTHING;
