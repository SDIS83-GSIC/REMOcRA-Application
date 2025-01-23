DROP TABLE IF EXISTS remocra.pei;
DROP TABLE IF EXISTS remocra.pena;
DROP TABLE IF EXISTS remocra.pibi;


CREATE TABLE remocra.pei
(
    pei_id                      uuid PRIMARY KEY,
    pei_annee_fabrication       INTEGER,
    pei_type_pei                "TYPE_PEI"      NOT NULL,
    pei_numero_voie             INTEGER,
    pei_voie_id                 UUID REFERENCES remocra.voie (voie_id),
    pei_suffixe_voie            TEXT,
    pei_croisement_id           UUID REFERENCES remocra.voie (voie_id),
    pei_complement_adresse      TEXT,
    pei_disponibilite_terrestre "DISPONIBILITE" NOT NULL,
    pei_geometrie               geometry        NOT NULL,
    pei_lieu_dit_id             UUID REFERENCES remocra.lieu_dit (lieu_dit_id),
    pei_numero_complet          TEXT            NOT NULL UNIQUE,
    pei_numero_interne          INTEGER         NOT NULL,
    pei_observation             TEXT,
    pei_commune_id              UUID            NOT NULL REFERENCES remocra.commune (commune_id),
    pei_domaine_id              UUID            NOT NULL REFERENCES remocra.domaine (domaine_id),
    pei_nature_id               UUID            NOT NULL REFERENCES remocra.nature (nature_id),
    pei_nature_deci_id          UUID            NOT NULL REFERENCES remocra.nature_deci (nature_deci_id),
    pei_zone_speciale_id        UUID REFERENCES remocra.zone_integration (zone_integration_id),
    pei_niveau_id               UUID REFERENCES remocra.niveau (niveau_id),
    pei_gestionnaire_id         UUID REFERENCES remocra.gestionnaire (gestionnaire_id),
    pei_site_id                 UUID REFERENCES remocra.site (site_id),
    pei_autorite_deci_id        UUID REFERENCES remocra.organisme (organisme_id),
    pei_service_public_deci_id  UUID REFERENCES remocra.organisme (organisme_id),
    pei_maintenance_deci_id     UUID REFERENCES remocra.organisme (organisme_id),
    pei_en_face                 BOOLEAN

);
--Permet de bloquer les géométries des PEI a des points uniques, impossible de donner un multipolygone ou une ligne
ALTER TABLE remocra.pei
    ADD CONSTRAINT geometrie_point_pei CHECK (geometrytype(pei_geometrie) = 'POINT'::text);

COMMENT
    ON COLUMN remocra.pei.pei_croisement_id
    IS 'Permet de donner le nom de la deuxième voie si un PEI se trouve à un carrefour entre 2 voies';
COMMENT
    ON COLUMN remocra.pei.pei_en_face
    IS 'Sert à spécifier si le PEI se trouve sur le trottoir d''en face de l''adresse indiquée';
COMMENT
    ON COLUMN remocra.pei.pei_complement_adresse
    IS 'Exemple : "derrière le panneau publicitaire lumineux"';
COMMENT
    ON COLUMN remocra.pei.pei_suffixe_voie
    IS 'BIS, TER etc.';


CREATE TABLE remocra.pena
(
    pena_id                  UUID PRIMARY KEY REFERENCES remocra.pei (pei_id),
    pena_disponibilite_hbe   "DISPONIBILITE"   NOT NULL,
    pena_capacite            INTEGER,
    pena_coordonne_dfci      TEXT,
    pena_materiau_id         UUID REFERENCES remocra.materiau (materiau_id),
    pena_capacite_illimitee  BOOLEAN,
    pena_capacite_incertaine BOOLEAN,
    pena_quantite_appoint    FLOAT
);

COMMENT
    ON COLUMN remocra.pena.pena_capacite
    IS 'En m³ (mètre cube)';

COMMENT
    ON COLUMN remocra.pena.pena_capacite_incertaine
    IS 'Vaut VRAI lorsqu''on n''est pas certain de la capacité du PENA';

COMMENT
    ON COLUMN remocra.pena.pena_capacite_illimitee
    IS 'Vaut VRAI lorsqu''on considère que ce PENA possède une capacité illimitée';



CREATE TABLE remocra.pibi
(
    pibi_id                       UUID PRIMARY KEY REFERENCES remocra.pei (pei_id),
    pibi_diametre_id              UUID REFERENCES remocra.diametre (diametre_id),
    pibi_service_eau_id           UUID REFERENCES remocra.organisme (organisme_id),
    pibi_numero_scp               TEXT,
    pibi_renversable              BOOLEAN,
    pibi_dispositif_inviolabilite BOOLEAN,
    pibi_modele_pibi_id           UUID REFERENCES remocra.modele_pibi (modele_pibi_id),
    pibi_marque_pibi_id           UUID REFERENCES remocra.marque_pibi (marque_pibi_id),
    pibi_pena_id                  UUID REFERENCES remocra.pena (pena_id),
    pibi_jumele_id                UUID REFERENCES remocra.pibi (pibi_id),
    pibi_reservoir_id             UUID REFERENCES remocra.reservoir (reservoir_id),
    pibi_debit_renforce           BOOLEAN,
    pibi_type_canalisation_id     UUID REFERENCES remocra.type_canalisation (type_canalisation_id),
    pibi_type_reseau_id           UUID REFERENCES remocra.type_reseau (type_reseau_id),
    pibi_diametre_canalisation    INTEGER,
    pibi_surpresse                BOOLEAN,
    pibi_additive                 BOOLEAN
    --pibi_debit_nominal INTEGER Voir ce qu'on en fait

);


COMMENT
    ON COLUMN remocra.pibi.pibi_service_eau_id
    IS 'Des organismes qui ont le type "SERVICE_EAU" uniquement';
COMMENT
    ON COLUMN remocra.pibi.pibi_pena_id
    IS 'PENA auquel le PIBI est rattaché (cas d''une citerne ou d''un point d''aspiration par exemple)';


