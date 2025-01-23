CREATE TABLE  IF NOT EXISTS remocra.profil_utilisateur (
    profil_utilisateur_id                   UUID            PRIMARY KEY,
    profil_utilisateur_actif                BOOLEAN         NOT NULL,
    profil_utilisateur_code                 TEXT            UNIQUE NOT NULL,
    profil_utilisateur_libelle              TEXT            NOT NULL,
    profil_utilisateur_type_organisme_id    UUID            NOT NULL REFERENCES remocra.type_organisme(type_organisme_id)
);

INSERT INTO remocra.profil_utilisateur
(profil_utilisateur_id, profil_utilisateur_actif, profil_utilisateur_code, profil_utilisateur_libelle, profil_utilisateur_type_organisme_id)
SELECT
    gen_random_uuid() as profil_utilisateur_id,
    true as actif,
    'REMOCRA' as profil_utilisateur_code,
    'Profil utilisateur réservé à l''équipe projet' as profil_utilisateur_libelle,
    type_organisme.type_organisme_id as profil_utilisateur_type_organisme_id
FROM remocra.type_organisme
where type_organisme.type_organisme_code = 'REMOCRA'
on CONFLICT (profil_utilisateur_code) DO NOTHING;

ALTER TABLE remocra.utilisateur ADD COLUMN  IF NOT EXISTS utilisateur_telephone TEXT;
ALTER TABLE remocra.utilisateur ADD COLUMN  IF NOT EXISTS utilisateur_can_be_notified BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE remocra.utilisateur ADD COLUMN  IF NOT EXISTS utilisateur_profil_utilisateur_id UUID REFERENCES profil_utilisateur(profil_utilisateur_id);
ALTER TABLE remocra.utilisateur ADD COLUMN  IF NOT EXISTS utilisateur_organisme_id UUID REFERENCES organisme(organisme_id);
