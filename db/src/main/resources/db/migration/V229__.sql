CREATE TYPE type_debroussaillement AS ENUM ('DFCI', 'ARCHIVE', 'AUTRE');
CREATE TYPE type_equipement AS ENUM ('POINT_EAU', 'DIRECTION', 'ENTREE_PISTE');
CREATE TYPE type_panneau AS ENUM ('SIGNAL_COMPLEMENT', 'SIGNAL_PRINCIPAL');
CREATE TYPE type_position AS ENUM ('GAUCHE','DROITE');
CREATE TYPE type_bzero AS ENUM ('PERMANENT','TEMPORAIRE', 'AUCUN');

CREATE TABLE dfci_deb (
    dfci_deb_id UUID PRIMARY KEY,
    dfci_deb_libelle TEXT NOT NULL,
    dfci_deb_annee_programme INTEGER,
    dfci_deb_annee_travaux INTEGER,
    dfci_deb_mois_travaux INTEGER,
    dfci_deb_annee_edition INTEGER,
    dfci_deb_largeur DOUBLE PRECISION NOT NULL,
    dfci_deb_surface DOUBLE PRECISION NOT NULL ,
    dfci_deb_geometrie GEOMETRY NOT NULL,
    dfci_deb_type type_debroussaillement NOT NULL,
    dfci_deb_programme type_programme,
    dfci_deb_travaux type_travaux,
    dfci_deb_remarque TEXT,
    dfci_deb_dfci_massif_id UUID NOT NULL REFERENCES dfci_massif(dfci_massif_id),
    dfci_deb_dfci_ouvrage_id UUID REFERENCES dfci_ouvrage(dfci_ouvrage_id),
    dfci_deb_dfci_prestataire_id UUID REFERENCES dfci_prestataire(dfci_prestataire_id),
    dfci_deb_code TEXT UNIQUE NOT NULL,
    dfci_deb_version INTEGER NOT NULL
);

CREATE TABLE dfci_panneau (
    dfci_panneau_id UUID PRIMARY KEY,
    dfci_panneau_type type_panneau NOT NULL,
    dfci_panneau_etat BOOL NOT NULL,
    dfci_panneau_bzero type_bzero NOT NULL,
    dfci_panneau_date_gps TIMESTAMPTZ NOT NULL,
    dfci_panneau_position type_position NOT NULL,
    dfci_panneau_equipement type_equipement NOT NULL,
    dfci_panneau_dfci_piste_id UUID NOT NULL REFERENCES dfci_piste(dfci_piste_id),
    dfci_panneau_num_piste BOOL NOT NULL,
    dfci_panneau_libelle_piste BOOL NOT NULL,
    dfci_panneau_remarque TEXT,
    dfci_panneau_geometrie GEOMETRY NOT NULL CHECK (GeometryType(dfci_panneau_geometrie) = 'POINT' ),
    dfci_panneau_code TEXT UNIQUE NOT NULL,
    dfci_panneau_version INTEGER NOT NULL
);

ALTER TYPE historique.type_objet ADD VALUE 'DFCI_DEB';
ALTER TYPE historique.type_objet ADD VALUE 'DFCI_PANNEAU';
