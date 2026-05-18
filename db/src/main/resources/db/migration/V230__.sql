---- Views permettant de faire la synchro du SIG vers REMOcRA pour les données du DFCI----
---Nomenclatures---
CREATE VIEW entrepotsig.v_dfci_massif_sig AS
SELECT
    dfci_massif_id AS v_dfci_massif_sig_id,
    dfci_massif_code AS v_dfci_massif_sig_code,
    dfci_massif_libelle AS v_dfci_massif_sig_libelle
FROM remocra.dfci_massif;

CREATE VIEW entrepotsig.v_dfci_categorie_piste_sig AS
SELECT
    dfci_categorie_piste_id AS v_dfci_categorie_piste_sig_id,
    dfci_categorie_piste_code AS v_dfci_categorie_piste_sig_code,
    dfci_categorie_piste_libelle AS v_dfci_categorie_piste_sig_libelle
FROM remocra.dfci_categorie_piste;

CREATE VIEW entrepotsig.v_dfci_prestataire_sig AS
SELECT
    dfci_prestataire_id AS v_dfci_prestataire_sig_id,
    dfci_prestataire_code AS v_dfci_prestataire_sig_code,
    dfci_prestataire_libelle AS v_dfci_prestataire_sig_libelle
FROM remocra.dfci_prestataire;

CREATE VIEW entrepotsig.v_dfci_ouvrage_sig AS
SELECT
    dfci_ouvrage_id AS v_dfci_ouvrage_sig_id,
    dfci_ouvrage_code AS v_dfci_ouvrage_sig_code,
    dfci_ouvrage_libelle AS v_dfci_ouvrage_sig_libelle
FROM remocra.dfci_ouvrage;

---Tables principales---
CREATE VIEW entrepotsig.v_dfci_aire_sig AS
SELECT
    dfci_aire_id AS v_dfci_aire_sig_id,
    dfci_aire_amenagement AS v_dfci_aire_sig_amenagement,
    dfci_aire_date_gps AS v_dfci_aire_sig_date_gps,
    dfci_aire_grande_dimension AS v_dfci_aire_sig_grande_dimension,
    dfci_aire_petite_dimension AS v_dfci_aire_sig_petite_dimension,
    dfci_aire_type AS v_dfci_aire_sig_type,
    dfci_aire_dfci_piste_id AS v_dfci_aire_sig_dfci_piste_id,
    dfci_aire_remarque AS v_dfci_aire_sig_remarque,
    dfci_aire_geometrie AS v_dfci_aire_sig_geometrie,
    dfci_aire_code AS v_dfci_aire_sig_code,
    dfci_aire_version AS v_dfci_aire_sig_version
FROM remocra.dfci_aire;

CREATE VIEW entrepotsig.v_dfci_piste_sig AS
SELECT
    dfci_piste_id AS v_dfci_piste_sig_id,
    dfci_piste_adresse AS v_dfci_piste_sig_adresse,
    dfci_piste_annee_programme AS v_dfci_piste_sig_annee_programme,
    dfci_piste_annee_travaux AS v_dfci_piste_sig_annee_travaux,
    dfci_piste_circulation AS v_dfci_piste_sig_circulation,
    dfci_piste_date_gps AS v_dfci_piste_sig_date_gps,
    dfci_piste_libelle AS v_dfci_piste_sig_libelle,
    dfci_piste_numero AS v_dfci_piste_sig_numero,
    dfci_piste_ouverture AS v_dfci_piste_sig_ouverture,
    dfci_piste_est_dfci AS v_dfci_piste_sig_est_dfci,
    dfci_piste_retournement AS v_dfci_piste_sig_retournement,
    dfci_piste_num_troncon AS v_dfci_piste_sig_num_troncon,
    dfci_piste_num_objectif AS v_dfci_piste_sig_num_objectif,
    dfci_piste_libelle_objectif AS v_dfci_piste_sig_libelle_objectif,
    dfci_piste_geometrie AS v_dfci_piste_sig_geometrie,
    dfci_piste_impraticabilite AS v_dfci_piste_sig_impraticabilite,
    dfci_piste_travaux AS v_dfci_piste_sig_travaux,
    dfci_piste_voie AS v_dfci_piste_sig_voie,
    dfci_piste_impasse AS v_dfci_piste_sig_impasse,
    dfci_piste_foncier AS v_dfci_piste_sig_foncier,
    dfci_piste_croisement AS v_dfci_piste_sig_croisement,
    dfci_piste_programme AS v_dfci_piste_sig_programme,
    dfci_piste_praticabilite AS v_dfci_piste_sig_praticabilite,
    dfci_piste_remarque AS v_dfci_piste_sig_remarque,
    dfci_piste_code AS v_dfci_piste_sig_code,
    dfci_piste_dfci_categorie_piste_id AS v_dfci_piste_sig_dfci_categorie_piste_id,
    dfci_piste_dfci_massif_id AS v_dfci_piste_sig_dfci_massif_id,
    dfci_piste_dfci_prestataire_id AS v_dfci_piste_sig_dfci_prestataire_id,
    dfci_piste_dfci_ouvrage_id AS v_dfci_piste_sig_dfci_ouvrage_id,
    dfci_piste_version AS v_dfci_piste_sig_version
FROM remocra.dfci_piste;

CREATE VIEW entrepotsig.v_dfci_deb_sig AS
SELECT
    dfci_deb_id AS v_dfci_deb_sig_id,
    dfci_deb_libelle AS v_dfci_deb_sig_libelle,
    dfci_deb_annee_programme AS v_dfci_deb_sig_annee_programme,
    dfci_deb_annee_travaux AS v_dfci_deb_sig_annee_travaux,
    dfci_deb_mois_travaux AS v_dfci_deb_sig_mois_travaux,
    dfci_deb_annee_edition AS v_dfci_deb_sig_annee_edition,
    dfci_deb_largeur AS v_dfci_deb_sig_largeur,
    dfci_deb_surface AS v_dfci_deb_sig_surface,
    dfci_deb_geometrie AS v_dfci_deb_sig_geometrie,
    dfci_deb_type AS v_dfci_deb_sig_type,
    dfci_deb_programme AS v_dfci_deb_sig_programme,
    dfci_deb_travaux AS v_dfci_deb_sig_travaux,
    dfci_deb_remarque AS v_dfci_deb_sig_remarque,
    dfci_deb_code AS v_dfci_deb_sig_code,
    dfci_deb_dfci_massif_id AS v_dfci_deb_sig_dfci_massif_id,
    dfci_deb_dfci_prestataire_id AS v_dfci_deb_sig_dfci_prestataire_id,
    dfci_deb_dfci_ouvrage_id AS v_dfci_deb_sig_dfci_ouvrage_id,
    dfci_deb_version AS v_dfci_deb_sig_version
FROM remocra.dfci_deb;

CREATE VIEW entrepotsig.v_dfci_panneau_sig AS
SELECT
    dfci_panneau_id AS v_dfci_panneau_sig_id,
    dfci_panneau_type AS v_dfci_panneau_sig_type,
    dfci_panneau_etat AS v_dfci_panneau_sig_etat,
    dfci_panneau_bzero AS v_dfci_panneau_sig_bzero,
    dfci_panneau_date_gps AS v_dfci_panneau_sig_date_gps,
    dfci_panneau_position AS v_dfci_panneau_sig_position,
    dfci_panneau_equipement AS v_dfci_panneau_sig_equipement,
    dfci_panneau_dfci_piste_id AS v_dfci_panneau_sig_dfci_piste_id,
    dfci_panneau_num_piste AS v_dfci_panneau_sig_num_piste,
    dfci_panneau_libelle_piste AS v_dfci_panneau_sig_libelle_piste,
    dfci_panneau_remarque AS v_dfci_panneau_sig_remarque,
    dfci_panneau_geometrie AS v_dfci_panneau_sig_geometrie,
    dfci_panneau_code AS v_dfci_panneau_sig_code,
    dfci_panneau_version AS v_dfci_panneau_sig_version
FROM remocra.dfci_panneau;

----Table conflit si les données entre le SIG et REMOcRA sont différentes après la synchro----
CREATE TABLE remocra.dfci_conflit (
    dfci_conflit_id UUID PRIMARY KEY,
    dfci_conflit_table TEXT NOT NULL,
    dfci_conflit_element_id UUID NOT NULL,
    dfci_conflit_champ TEXT NOT NULL UNIQUE,
    dfci_conflit_valeur_remocra TEXT,
    dfci_conflit_valeur_sig TEXT,
    dfci_conflit_date TIMESTAMPTZ NOT NULL,

    UNIQUE (dfci_conflit_element_id, dfci_conflit_champ)
);

ALTER TYPE historique.type_objet ADD VALUE 'DFCI_CONFLIT';
