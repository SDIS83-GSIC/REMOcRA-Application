-- l'affichage des catégories est déterminé en fonction des types de crise
CREATE TABLE remocra.crise_categorie (
    crise_categorie_id UUID PRIMARY KEY,
    crise_categorie_code TEXT NOT NULL UNIQUE,
    crise_categorie_libelle TEXT NOT NULL UNIQUE
);

-- une catégorie contient des sous types
CREATE TABLE remocra.type_crise_categorie (
    type_crise_categorie_id UUID PRIMARY KEY,
    type_crise_categorie_code TEXT NOT NULL UNIQUE,
    type_crise_categorie_libelle TEXT NOT NULL UNIQUE,
    type_crise_categorie_type_geometrie remocra."TYPE_GEOMETRY",
    type_crise_categorie_crise_categorie_id UUID REFERENCES remocra.crise_categorie(crise_categorie_id)
);

-- un type de crise peut avoir plusieurs catégories. Plusieurs catégories peuvent être dans plusieurs types de crises
CREATE TABLE remocra.l_type_crise_categorie (
    type_crise_id UUID REFERENCES remocra.type_crise(type_crise_id),
    crise_categorie_id UUID REFERENCES remocra.crise_categorie(crise_categorie_id),
    PRIMARY KEY (type_crise_id, crise_categorie_id)
);

CREATE TYPE EVENEMENT_STATUT AS ENUM('EN_COURS', 'CLOS');

-- un évènement est lié à un sous type de catégorie
CREATE TABLE remocra.evenement (
    evenement_id UUID PRIMARY KEY,
    evenement_type_crise_categorie_id UUID REFERENCES remocra.type_crise_categorie(type_crise_categorie_id),
    evenement_libelle TEXT NOT NULL,
    evenement_description TEXT,
    evenement_origine TEXT,
    evenement_date_constat TIMESTAMPTZ NOT NULL,
    evenement_importance INT CHECK (evenement_importance >= 0 AND evenement_importance <= 5),
    evenement_tags TEXT,
    evenement_is_closed BOOL,
    evenement_date_cloture TIMESTAMPTZ,
    evenement_geometrie GEOMETRY,
    evenement_crise_id UUID REFERENCES remocra.crise(crise_id),
    evenement_statut EVENEMENT_STATUT NOT NULL DEFAULT 'EN_COURS',
    utilisateur_id UUID REFERENCES utilisateur(utilisateur_id)
);

-- un événement peut avoir des documents mais je ne peux pas rajouter une clé étrangère dans "document" qui référence mes événements car tous les documents ne sont pas liés à des événements !
CREATE TABLE l_evenement_document (
    evenement_id UUID REFERENCES evenement(evenement_id),
    document_id UUID REFERENCES document(document_id),
    PRIMARY KEY (evenement_id, document_id)
);

ALTER TYPE historique.type_objet ADD VALUE 'DOCUMENT_EVENEMENT';
ALTER TYPE historique.type_objet ADD VALUE 'EVENEMENT';
ALTER TYPE remocra."DROIT" ADD VALUE 'EVENEMENT_U';
ALTER TYPE remocra."DROIT" ADD VALUE 'EVENEMENT_C';

-- ////////////////////////////////////////////////////////////////////////////////////////////////////
INSERT INTO remocra.type_crise (type_crise_id, type_crise_code, type_crise_libelle) VALUES
(gen_random_uuid(), 'TOUS', 'Tous'),
(gen_random_uuid(), 'DISPARITION', 'Disparition'),
(gen_random_uuid(), 'INONDATION', 'Inondation'),
(gen_random_uuid(), 'CANICULE', 'Canicule'),
(gen_random_uuid(), 'ATTENTAT', 'Attentat'),
(gen_random_uuid(), 'MANIFESTATION', 'Manifestation'),
(gen_random_uuid(), 'MER_AGITEE', 'Mer agitée - Vagues'),
(gen_random_uuid(), 'ORAGES', 'Orages'),
(gen_random_uuid(), 'PENURIE_ESSENCE', 'Pénurie essence'),
(gen_random_uuid(), 'POLLUTION', 'Pollution'),
(gen_random_uuid(), 'POLLUTION_MARINE', 'Pollution marine'),
(gen_random_uuid(), 'PRISE_OTAGE', 'Prise d''otage'),
(gen_random_uuid(), 'SECHERESSE', 'Sécheresse'),
(gen_random_uuid(), 'SUBMERSION', 'Submersion'),
(gen_random_uuid(), 'TORNADE', 'Tornade'),
(gen_random_uuid(), 'SEISME', 'Séisme'),
(gen_random_uuid(), 'VENTS_VIOLENTS', 'Vents violents'),
(gen_random_uuid(), 'VERGLAS', 'Verglas'),
(gen_random_uuid(), 'FROID_EXTRÊME', 'Froid extrême'),
(gen_random_uuid(), 'FUITES_GAZ', 'Fuite de gaz'),
(gen_random_uuid(), 'ACCIDENT', 'Accident'),
(gen_random_uuid(), 'ACCIDENT_AERIEN', 'Accident aérien'),
(gen_random_uuid(), 'ACCIDENT_INDUSTRIEL', 'Accident industriel'),
(gen_random_uuid(), 'BROUILLARD', 'Brouillard'),
(gen_random_uuid(), 'NEIGE', 'Neige'),
(gen_random_uuid(), 'COLIS_SUSPECT', 'Colis suspect'),
(gen_random_uuid(), 'FEUX_DE_FORÊT', 'Feux de forêt'),
(gen_random_uuid(), 'FEU_DHABITATION', 'Feux d''habitation'),
(gen_random_uuid(), 'FORTES_PLUIES', 'Fortes pluies');

INSERT INTO remocra.crise_categorie(crise_categorie_id, crise_categorie_code, crise_categorie_libelle) VALUES
  (gen_random_uuid(), 'ACTIONS', 'Actions'),
  (gen_random_uuid(), 'AUTRE', 'Autre'),
  (gen_random_uuid(), 'ENERGIE', 'Energie'),
  (gen_random_uuid(), 'ENVIRONNEMENT', 'Environnement'),
  (gen_random_uuid(), 'HYDROLOGIE', 'Hydrologie'),
  (gen_random_uuid(), 'ROC AZUR', 'Roc Azur'),
  (gen_random_uuid(), 'BIENS ET PERSONNES', 'Biens et Personnes'),
  (gen_random_uuid(), 'MOYENS', 'Moyens'),
  (gen_random_uuid(), 'RESEAU DE TRANSPORT', 'Réseau de Transport'),
  (gen_random_uuid(), 'SINISTRE', 'Sinistre'),
  (gen_random_uuid(), 'SITUATION OPERATIONNELLE PARTAGEE', 'Situation Opérationnelle Partagée')
;

INSERT INTO remocra.type_crise_categorie(type_crise_categorie_id, type_crise_categorie_code, type_crise_categorie_libelle, type_crise_categorie_type_geometrie, type_crise_categorie_crise_categorie_id) VALUES
  (gen_random_uuid(), 'MESSAGE', 'message', NULL, NULL), -- un message ne contient ni geometrie ni de catégorie
  (gen_random_uuid(), 'ACCES_PRINCIPAL', 'accès principal', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'DEFENSE_PERIMETRABLE', 'défense périmétrable', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'HELITREUILLAGE', 'hélitreuillage', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'INTERVENTION', 'intervention', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'LARGUAGE_ABE', 'larguage ABE', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'LARGUAGE_BHE', 'larguage BHE', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'LIGNE_APPUI', 'ligne d''appui', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'MISE_EN_SECURITE', 'mise en sécurité', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'PENETRANTE', 'pénétrante', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'RECHERCHE_PERSONNE', 'recherche de personne', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'RETARDANT_TERRESTRE', 'retardant terrestre', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'ROCADE', 'rocade', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'SAUVETAGE', 'sauvetage', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  (gen_random_uuid(), 'CENTRE_ACCUEIL', 'centre d''accueil', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  (gen_random_uuid(), 'TEXTE', 'texte', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  (gen_random_uuid(), 'COUPURE_LIGNE_ELECTRIQUE', 'coupure ligne électrique', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  (gen_random_uuid(), 'RUPTURE_EAU', 'rupture d''eau', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  (gen_random_uuid(), 'RUPTURE_GAZ', 'rupture de gaz', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  (gen_random_uuid(), 'SECTEUR_SANS_EAU', 'secteur sans eau', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  (gen_random_uuid(), 'SECTEUR_SANS_GAZ', 'secteur sans gaz', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  (gen_random_uuid(), 'SECTEUR_SANS_ELECTRICITE', 'secteur sans électricité', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  (gen_random_uuid(), 'GLISSEMENT_TERRAIN', 'glissement de terrain', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENVIRONNEMENT')),
  (gen_random_uuid(), 'EMBACLE', 'embâcle', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'HYDROLOGIE')),
  (gen_random_uuid(), 'TACHE_EAU', 'tâche d''eau', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'HYDROLOGIE')),
  (gen_random_uuid(), 'VAGUE_SUBMERSIVE', 'vague submersive', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'HYDROLOGIE')),
  (gen_random_uuid(), 'AUTORITE_PREFECTORALE', 'autorité préfectorale', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'BINOME', 'binôme', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'COLONNE', 'colonne', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'GROUPE', 'groupe', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'MOYEN_AERIEN', 'moyen aérien', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'MOYEN_EXTERIEUR', 'moyen exterieur', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'PCC', 'PCC', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'PCS', 'PCS', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'PATROUILLE_FRANCE', 'patrouille de france', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'POINT_TRANSIT', 'point de transit', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'SOUTIEN', 'soutien', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'VEHICULE_SEUL', 'véhicule seul', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  (gen_random_uuid(), 'Accident_ROUTE', 'Accident route', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  (gen_random_uuid(), 'ROUTE_COUPEE', 'route coupée', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  (gen_random_uuid(), 'VOIE_FERREE_COUPEE', 'voie ferrée coupée', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  (gen_random_uuid(), 'AXE_PROPAGATION', 'axe de propagation', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'CENTRE_ACTION', 'centre d''action', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'POINT_SENSIBLE', 'point sensible', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'REPERE_SECTORISATION', 'repère de sectorisation', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'SECTORISATION', 'sectorisation', 'LINESTRING', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'SOURCE_DANGER', 'source de danger', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'ZONE_PROPAGATION_POTENTIELLE', 'zone de propagation potentielle', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'ZONE_ACTION', 'zone d''action', 'POLYGON', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  (gen_random_uuid(), 'BUS', 'bus', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'CROIX_ROUGE', 'croix rouge', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'ECPAD', 'ECPAD', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'GROUPE_ELECTROGENE', 'groupe électrogène', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'HELICOPTERE_DRAGON', 'hélicoptère Dragon', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'HELICOPTERE_GENDARMERIE', 'hélicoptère gendarmerie', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'HELICOPTERE_PRESIDENTIEL', 'hélicoptère présidentiel', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'PRESIDENT', 'président', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  (gen_random_uuid(), 'SAPEURS_POMPIERS', 'sapeurs-pompiers', 'POINT', (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE'));

-- l_type_crise_categorie
INSERT INTO remocra.l_type_crise_categorie(type_crise_id, crise_categorie_id) VALUES
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SECHERESSE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'TORNADE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'TORNADE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'TORNADE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'TORNADE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'TORNADE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'TORNADE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'TORNADE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'HYDROLOGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SUBMERSION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PRISE_OTAGE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PRISE_OTAGE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PRISE_OTAGE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'BIENS ET PERSONNES')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PRISE_OTAGE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PRISE_OTAGE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PRISE_OTAGE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'HYDROLOGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MER_AGITEE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION_MARINE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION_MARINE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION_MARINE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION_MARINE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'POLLUTION_MARINE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ATTENTAT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ATTENTAT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ATTENTAT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'BIENS ET PERSONNES')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ATTENTAT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ATTENTAT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ATTENTAT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ATTENTAT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MANIFESTATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MANIFESTATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MANIFESTATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MANIFESTATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ROC AZUR')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MANIFESTATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'MANIFESTATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ORAGES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ORAGES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ORAGES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ORAGES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ORAGES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ORAGES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'CANICULE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'CANICULE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'CANICULE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PENURIE_ESSENCE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PENURIE_ESSENCE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PENURIE_ESSENCE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PENURIE_ESSENCE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'PENURIE_ESSENCE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENVIRONNEMENT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'HYDROLOGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'INONDATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'BIENS ET PERSONNES')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'SEISME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VENTS_VIOLENTS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VENTS_VIOLENTS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VENTS_VIOLENTS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VENTS_VIOLENTS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VENTS_VIOLENTS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VENTS_VIOLENTS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VENTS_VIOLENTS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VERGLAS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VERGLAS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VERGLAS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VERGLAS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VERGLAS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'VERGLAS'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FROID_EXTRÊME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FROID_EXTRÊME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FROID_EXTRÊME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FROID_EXTRÊME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FROID_EXTRÊME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FROID_EXTRÊME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FROID_EXTRÊME'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'BIENS ET PERSONNES')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FUITES_GAZ'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT_AERIEN'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT_AERIEN'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT_AERIEN'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT_AERIEN'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT_AERIEN'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'ACCIDENT_AERIEN'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'BROUILLARD'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'NEIGE'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'DISPARITION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'COLIS_SUSPECT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),

  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEUX_DE_FORÊT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEUX_DE_FORÊT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEUX_DE_FORÊT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEUX_DE_FORÊT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEUX_DE_FORÊT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEUX_DE_FORÊT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEUX_DE_FORÊT'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEU_DHABITATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEU_DHABITATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEU_DHABITATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEU_DHABITATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEU_DHABITATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEU_DHABITATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FEU_DHABITATION'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FORTES_PLUIES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ACTIONS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FORTES_PLUIES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'AUTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FORTES_PLUIES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'ENERGIE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FORTES_PLUIES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'MOYENS')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FORTES_PLUIES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'RESEAU DE TRANSPORT')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FORTES_PLUIES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SINISTRE')),
  ((SELECT type_crise_id FROM remocra.type_crise WHERE type_crise_code = 'FORTES_PLUIES'), (SELECT crise_categorie_id FROM remocra.crise_categorie WHERE crise_categorie_code = 'SITUATION OPERATIONNELLE PARTAGEE'));
