ALTER TABLE remocra.role RENAME TO role_contact;

ALTER TABLE remocra.role_contact ADD COLUMN role_contact_protected boolean;
ALTER TABLE remocra.role_contact RENAME COLUMN role_id TO role_contact_id;
ALTER TABLE remocra.role_contact RENAME COLUMN role_actif TO role_contact_actif;
ALTER TABLE remocra.role_contact RENAME COLUMN role_code TO role_contact_code;
ALTER TABLE remocra.role_contact RENAME COLUMN role_libelle TO role_contact_libelle;

ALTER TYPE historique.type_objet ADD VALUE IF NOT EXISTS 'ROLE_CONTACT';