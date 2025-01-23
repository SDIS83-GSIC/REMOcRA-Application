DROP TABLE IF EXISTS couverturehydraulique.couverture_tracee;
DROP TABLE IF EXISTS couverturehydraulique.couverture_tracee_pei;
DROP TABLE IF EXISTS couverturehydraulique.temp_distance;
DROP TABLE IF EXISTS couverturehydraulique.voie_laterale;
DROP TABLE IF EXISTS couverturehydraulique.reseau;
DROP TABLE IF EXISTS couverturehydraulique.sommet;
DROP TABLE IF EXISTS couverturehydraulique.batiment;
DROP TABLE IF EXISTS couverturehydraulique.pei_projet;
DROP TABLE IF EXISTS couverturehydraulique.l_etude_document;
DROP TABLE IF EXISTS couverturehydraulique.l_etude_commune;
DROP TABLE IF EXISTS couverturehydraulique.etude;
DROP TABLE IF EXISTS couverturehydraulique.type_etude;
DROP TYPE IF EXISTS couverturehydraulique.ETUDE_STATUT;
DROP TYPE IF EXISTS couverturehydraulique.TYPE_PEI_PROJET;
DROP TYPE IF EXISTS couverturehydraulique.TYPE_SIDE;

CREATE SCHEMA  IF NOT EXISTS couverturehydraulique;

CREATE TABLE couverturehydraulique.type_etude
(
    type_etude_id                  UUID            NOT NULL PRIMARY KEY,
    type_etude_actif               BOOLEAN         NOT NULL,
    type_etude_code                TEXT    UNIQUE  NOT NULL,
    type_etude_libelle             TEXT            NOT NULL
);

CREATE TYPE couverturehydraulique.ETUDE_STATUT as ENUM(
    'EN_COURS',
    'TERMINEE'
);

CREATE TABLE couverturehydraulique.etude
(
    etude_id                UUID            NOT NULL PRIMARY KEY,
    etude_type_etude_id     UUID            NOT NULL REFERENCES couverturehydraulique.type_etude(type_etude_id),
    etude_numero            TEXT            UNIQUE NOT NULL,
    etude_libelle           TEXT            NOT NULL,
    etude_description       TEXT,
    etude_organisme_id      UUID            NOT NULL REFERENCES remocra.organisme(organisme_id),
    etude_date_maj          TIMESTAMPTZ,
    etude_statut            couverturehydraulique.ETUDE_STATUT NOT NULL
);

CREATE TABLE couverturehydraulique.l_etude_commune
(
    etude_id            UUID      NOT NULL REFERENCES couverturehydraulique.etude(etude_id),
    commune_id          UUID      NOT NULL REFERENCES remocra.commune(commune_id),

    PRIMARY KEY (etude_id, commune_id)
);


CREATE TABLE couverturehydraulique.l_etude_document
(
    etude_id                    UUID     NOT NULL REFERENCES couverturehydraulique.etude(etude_id),
    document_id                 UUID     NOT NULL REFERENCES remocra.document(document_id),
    l_etude_document_libelle    TEXT,

    PRIMARY KEY (etude_id, document_id)
);

CREATE TYPE couverturehydraulique.TYPE_PEI_PROJET as ENUM(
    'PIBI',
    'RESERVE',
    'PA'
);

CREATE TABLE couverturehydraulique.pei_projet
(
    pei_projet_id                       UUID            NOT NULL PRIMARY KEY,
    pei_projet_etude_id                 UUID            NOT NULL REFERENCES couverturehydraulique.etude(etude_id),
    pei_projet_nature_deci_id           UUID            NOT NULL REFERENCES remocra.nature_deci(nature_deci_id),
    pei_projet_type_pei_projet          couverturehydraulique.TYPE_PEI_PROJET           NOT NULL,
    pei_projet_diametre_id              UUID            REFERENCES remocra.diametre(diametre_id),
    pei_projet_diametre_canalisation    INTEGER,
    pei_projet_capacite                 INTEGER,
    pei_projet_debit                    INTEGER,
    pei_projet_geometrie                Geometry NOT NULL
);

ALTER TABLE couverturehydraulique.pei_projet
    ADD CONSTRAINT geometrie_point_pei_projet CHECK (geometrytype(pei_projet_geometrie) = 'POINT'::text);


CREATE TABLE couverturehydraulique.batiment
(
     batiment_id            UUID        NOT NULL PRIMARY KEY,
     batiment_geometrie     Geometry    NOT NULL,
     batiment_etude_id      UUID        NOT NULL REFERENCES couverturehydraulique.etude(etude_id)
);

CREATE TABLE couverturehydraulique.sommet
(
     sommet_id                      UUID        NOT NULL PRIMARY KEY,
     sommet_geometrie               Geometry    NOT NULL,
     sommet_etude_id                UUID REFERENCES couverturehydraulique.etude(etude_id)
);

ALTER TABLE couverturehydraulique.sommet
    ADD CONSTRAINT geometrie_point_sommet CHECK (geometrytype(sommet_geometrie) = 'POINT'::text);


CREATE TABLE couverturehydraulique.reseau
(
     reseau_id                   UUID        NOT NULL PRIMARY KEY,
     reseau_geometrie            Geometry    NOT NULL,
     reseau_sommet_source        UUID,
     reseau_sommet_destination   UUID,
     reseau_pei_troncon          UUID, -- référence soit à remocra.pei soit à couverturehydraulique.pei_projet
     reseau_traversable          BOOLEAN,
     reseau_sens_unique          BOOLEAN,
     reseau_niveau               INTEGER,
     reseau_etude_id             UUID REFERENCES couverturehydraulique.etude(etude_id)
);

ALTER TABLE couverturehydraulique.reseau
    ADD CONSTRAINT geometrie_line_reseau CHECK (geometrytype(reseau_geometrie) = 'LINESTRING'::text);

COMMENT
    ON COLUMN couverturehydraulique.reseau.reseau_sommet_source
    IS 'Sommet source de la voie (déterminé par la création de la topologie (fonction sql))';

COMMENT
    ON COLUMN couverturehydraulique.reseau.reseau_sommet_destination
    IS 'Sommet de destination de la voie (déterminé par la création de la topologie (fonction sql))';

COMMENT
    ON COLUMN couverturehydraulique.reseau.reseau_pei_troncon
    IS 'Identifiant du pei si la voie relie un pei au réseau routier, NULL sinon';

COMMENT
    ON COLUMN couverturehydraulique.reseau.reseau_traversable
    IS 'Indique si l''on peut ou non traverser cette voie';

COMMENT
    ON COLUMN couverturehydraulique.reseau.reseau_sens_unique
    IS 'Indique si la voie est à sens unique. Le sens est celui de la digitalisation de la géométrie';

COMMENT
    ON COLUMN couverturehydraulique.reseau.reseau_niveau
    IS 'Niveau de la voie (ex: -1 pour un tunnel, 1 pour un pont, etc) si celle-ci est au-dessus ou en dessous du réseau routier';



CREATE TABLE couverturehydraulique.voie_laterale
(
     voie_laterale_id               UUID        NOT NULL PRIMARY KEY,
     voie_laterale_voie_voisine     UUID        NOT NULL REFERENCES couverturehydraulique.reseau(reseau_id),
     voie_laterale_angle            FLOAT,
     voie_laterale_gauche           BOOLEAN,
     voie_laterale_droite           BOOLEAN,
     voie_laterale_traversable      BOOLEAN,
     voie_laterale_accessible       BOOLEAN
);


COMMENT
    ON COLUMN couverturehydraulique.voie_laterale.voie_laterale_voie_voisine
    IS 'Voie voisine de la voie actuelle';

COMMENT
    ON COLUMN couverturehydraulique.voie_laterale.voie_laterale_angle
    IS 'Angle que forme la voie avec la voie actuelle';

COMMENT
    ON COLUMN couverturehydraulique.voie_laterale.voie_laterale_gauche
    IS 'Indique si la voie est celle se situant le plus à gauche';

COMMENT
    ON COLUMN couverturehydraulique.voie_laterale.voie_laterale_droite
    IS 'Indique si la voie est celle se situant le plus à droite';

COMMENT
    ON COLUMN couverturehydraulique.voie_laterale.voie_laterale_traversable
    IS 'Indique si la voie est traversable';

COMMENT
    ON COLUMN couverturehydraulique.voie_laterale.voie_laterale_accessible
    IS 'Voie accessible depuis le point de jonction (non accessible si les voies gauche et droite sont non traversables)';


CREATE TYPE couverturehydraulique.TYPE_SIDE as ENUM(
    'RIGHT',
    'LEFT',
    'BOTH'
);


CREATE TABLE couverturehydraulique.temp_distance
(
     temp_distance_id               UUID        NOT NULL PRIMARY KEY,
     temp_distance_sommet           UUID        NOT NULL REFERENCES couverturehydraulique.sommet(sommet_id),
     temp_distance_voie_courante    UUID        NOT NULL REFERENCES couverturehydraulique.reseau(reseau_id),
     temp_distance_voie_precedente  UUID        NOT NULL REFERENCES couverturehydraulique.reseau(reseau_id),
     temp_distance_distance         FLOAT,
     temp_distance_side             couverturehydraulique.TYPE_SIDE,
     temp_distance_traversable      BOOLEAN,
     temp_distance_pei_start        UUID,
     temp_distance_geometrie        Geometry
);


ALTER TABLE couverturehydraulique.temp_distance
    ADD CONSTRAINT geometrie_polygone_temp_distance CHECK (geometrytype(temp_distance_geometrie) = 'POLYGON'::text);

COMMENT
    ON TABLE couverturehydraulique.temp_distance
    IS 'Table permettant de stocker les informations nécessaire au parcours de graph; basé sur l''algorithme de Dijkstra';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_sommet
    IS 'Identifiant du sommet que l''on atteint';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_voie_courante
    IS 'Identifiant de la voie que l''on emprunte';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_voie_precedente
    IS 'Identifiant de la voie empruntée pour arriver à la voie courante. En remontant les prédéceseurs, on peut reconsituer le chemin le plus court';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_distance
    IS 'Distance parcourue depuis le départ en empruntant cette voie';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_side
    IS 'Indique sur quel côté de la voie tracer le buffer si la voie n''est pas traversable (renseigné par l''algo de calcul de la couverture hydraulique';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_traversable
    IS 'Indique si la voie est traversable';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_geometrie
    IS 'Buffer de la couverture hydraulique de la voie empruntée';

COMMENT
    ON COLUMN couverturehydraulique.temp_distance.temp_distance_pei_start
    IS 'Identifiant du PEI depuis lequel on effectue notre parcours';


CREATE TABLE couverturehydraulique.couverture_tracee_pei
(
     couverture_tracee_pei_id                 UUID           NOT NULL,
     couverture_tracee_pei_etude_id           UUID           NOT NULL REFERENCES couverturehydraulique.etude(etude_id),
     couverture_tracee_pei_distance           INTEGER        NOT NULL,
     couverture_tracee_pei_geometrie          Geometry,

     PRIMARY KEY (couverture_tracee_pei_id, couverture_tracee_pei_etude_id, couverture_tracee_pei_distance)
);

CREATE TABLE couverturehydraulique.couverture_tracee
(
     couverture_tracee_label              TEXT        NOT NULL,
     couverture_tracee_etude_id           UUID        NOT NULL REFERENCES couverturehydraulique.etude(etude_id),
     couverture_tracee_geometrie          Geometry,

     PRIMARY KEY(couverture_tracee_label, couverture_tracee_etude_id)
);

COMMENT
    ON TABLE couverturehydraulique.couverture_tracee
    IS 'Couverture hydraulique résultante de la simulation. Il s''agit de la couverture totale issue de toutes les couvertures hydrauliques de la table couverturehydraulique.couverture_tracee_pei';

COMMENT
    ON COLUMN couverturehydraulique.couverture_tracee.couverture_tracee_label
    IS 'Label de la géométrie calculée (risque courant, 50m, 250m, etc)';


ALTER TYPE historique.type_objet ADD VALUE 'ETUDE';
ALTER TYPE historique.type_objet ADD VALUE 'PEI_PROJET';
ALTER TYPE historique.type_objet ADD VALUE 'DOCUMENT_ETUDE';