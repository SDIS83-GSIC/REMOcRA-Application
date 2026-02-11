-- Ajout d'un droit pour permettre à un PEI de déplacer depuis l'application mobile
ALTER TYPE remocra."DROIT" ADD VALUE 'MOBILE_DEPLACER_PEI_U';


CREATE TABLE incoming.pei_deplacement (
                                          pei_deplacement_pei_id UUID NOT NULL REFERENCES remocra.pei (pei_id),
                                          pei_deplacement_tournee_id UUID NOT NULL REFERENCES incoming.tournee (tournee_id),
                                          pei_deplacement_geometrie Geometry NOT NULL,

                                          PRIMARY KEY (pei_deplacement_pei_id, pei_deplacement_tournee_id)
);
