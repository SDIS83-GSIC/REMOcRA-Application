CREATE TYPE type_impraticabilite AS ENUM ('GABARIT','TONNAGE','ETAT_BDR','MULTIPLE');
CREATE TYPE type_travaux AS ENUM ('CREER','CONFORMITE','ENTRETENIR');
CREATE TYPE type_voie AS ENUM ('DFCI','MULTI','INTERFACE','PPRIF');
CREATE TYPE type_impasse AS ENUM ('AMENAGEE','NON_AMENAGEE','SANS');
CREATE TYPE type_foncier AS ENUM ('DFCI','PUBLIC','PRIVE','MIXTE','INCONNU');
CREATE TYPE type_croisement AS ENUM ('GENERALISEE','PONCTUELLE','SANS');
CREATE TYPE type_programme AS ENUM ('IF','MCO','AMP','AUTRE');

CREATE TABLE dfci_massif (
    dfci_massif_id UUID PRIMARY KEY,
    dfci_massif_libelle TEXT NOT NULL,
    dfci_massif_code TEXT UNIQUE NOT NULL
);

CREATE TABLE dfci_prestataire (
    dfci_prestataire_id UUID PRIMARY KEY,
    dfci_prestataire_libelle TEXT NOT NULL,
    dfci_prestataire_code TEXT UNIQUE NOT NULL
);

CREATE TABLE dfci_ouvrage (
    dfci_ouvrage_id UUID PRIMARY KEY,
    dfci_ouvrage_libelle TEXT NOT NULL,
    dfci_ouvrage_code TEXT UNIQUE NOT NULL
);

CREATE TABLE dfci_categorie_piste (
    dfci_categorie_piste_id UUID PRIMARY KEY,
    dfci_categorie_piste_libelle TEXT NOT NULL,
    dfci_categorie_piste_code TEXT UNIQUE NOT NULL
);

CREATE TABLE dfci_piste (
    dfci_piste_id UUID PRIMARY KEY,
    dfci_piste_adresse TEXT,
    dfci_piste_annee_programme INTEGER,
    dfci_piste_annee_travaux INTEGER,
    dfci_piste_circulation BOOL NOT NULL,
    dfci_piste_date_gps TIMESTAMPTZ NOT NULL,
    dfci_piste_libelle TEXT NOT NULL,
    dfci_piste_numero TEXT NOT NULL,
    dfci_piste_ouverture BOOL NOT NULL,
    dfci_piste_est_dfci BOOL NOT NULL,
    dfci_piste_retournement BOOL NOT NULL,
    dfci_piste_num_troncon INTEGER  NOT NULL,
    dfci_piste_num_objectif INTEGER,
    dfci_piste_libelle_objectif TEXT,
    dfci_piste_geometrie GEOMETRY NOT NULL CHECK (GeometryType(dfci_piste_geometrie) = 'LINESTRING' ),
    dfci_piste_impraticabilite type_impraticabilite,
    dfci_piste_travaux type_travaux,
    dfci_piste_voie type_voie  NOT NULL,
    dfci_piste_impasse type_impasse NOT NULL,
    dfci_piste_foncier type_foncier,
    dfci_piste_croisement type_croisement NOT NULL,
    dfci_piste_programme type_programme,
    dfci_piste_praticabilite BOOL NOT NULL,
    dfci_piste_remarque TEXT,
    dfci_piste_dfci_categorie_piste_id UUID NOT NULL REFERENCES dfci_categorie_piste(dfci_categorie_piste_id),
    dfci_piste_dfci_massif_id UUID  NOT NULL REFERENCES dfci_massif(dfci_massif_id),
    dfci_piste_dfci_prestataire_id UUID REFERENCES dfci_prestataire(dfci_prestataire_id),
    dfci_piste_dfci_ouvrage_id UUID REFERENCES dfci_ouvrage(dfci_ouvrage_id),
    dfci_piste_code TEXT UNIQUE NOT NULL,
    dfci_piste_version INTEGER NOT NULL --version des données de la piste pour les futurs conflits de mise à jour.
);

ALTER TYPE historique.type_objet ADD VALUE 'DFCI_PISTE';
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES (gen_random_uuid(), 'DFCI_TOLERANCE_DFCI_PISTE_METRES', null, 'INTEGER'::remocra."TYPE_PARAMETRE");
