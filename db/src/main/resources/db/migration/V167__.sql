-- Tables servant à la création de style pour l'affichage du 'bouton i'.
ALTER TYPE remocra."DROIT" ADD VALUE 'CARTO_METADATA_A';

CREATE TABLE remocra.couche_style (
    couche_style_id UUID PRIMARY KEY,
    couche_style_actif BOOl NOT NULL,
    couche_style_style TEXT NOT NULL,  -- Le style => texte
    couche_style_couche_id UUID REFERENCES remocra.couche(couche_id)
);

CREATE TABLE remocra.l_groupe_fonctionnalites_couche_style (
    groupe_fonctionnalites_id UUID REFERENCES remocra.groupe_fonctionnalites(groupe_fonctionnalites_id),
    couche_style_id UUID REFERENCES remocra.couche_style(couche_style_id),
    PRIMARY KEY (groupe_fonctionnalites_id, couche_style_id)
);
