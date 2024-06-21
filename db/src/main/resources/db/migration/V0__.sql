DROP TABLE IF EXISTS remocra.utilisateur;

CREATE TABLE remocra.utilisateur
(
  utilisateur_id uuid PRIMARY KEY,
  utilisateur_actif BOOLEAN NOT NULL,
  utilisateur_email text UNIQUE NOT NULL,
  utilisateur_nom text NOT NULL,
  utilisateur_prenom text NOT NULL,
  utilisateur_username text UNIQUE NOT NULL
  -- TODO compléter avec tout ce qu'on a besoin (organisme, profil...)
);