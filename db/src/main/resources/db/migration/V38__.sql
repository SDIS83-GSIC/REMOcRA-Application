CREATE TABLE remocra.fonction_contact (
    fonction_contact_id                 UUID            PRIMARY KEY,
    fonction_contact_actif              BOOLEAN         NOT NULL,
    fonction_contact_protected          BOOLEAN         NOT NULL,
    fonction_contact_code               TEXT            UNIQUE NOT NULL,
    fonction_contact_libelle            TEXT            NOT NULL
);

ALTER TYPE historique.type_objet ADD VALUE IF NOT EXISTS 'FONCTION_CONTACT';

ALTER TABLE remocra.contact
DROP COLUMN contact_fonction;

ALTER TABLE remocra.contact
ADD COLUMN contact_fonction UUID REFERENCES remocra.fonction_contact(fonction_contact_id) ;

ALTER TABLE remocra.contact
RENAME COLUMN contact_fonction TO contact_fonction_contact_id ;

DROP TYPE type_fonction;

INSERT INTO remocra.fonction_contact
    (fonction_contact_id, fonction_contact_actif, fonction_contact_code, fonction_contact_libelle, fonction_contact_protected)
VALUES (gen_random_uuid(), true, 'MAIRE', 'Maire', true);

INSERT INTO remocra.fonction_contact
    (fonction_contact_id, fonction_contact_actif, fonction_contact_code, fonction_contact_libelle, fonction_contact_protected)
VALUES (gen_random_uuid(), true, 'PRESIDENT', 'Président', true);