CREATE TABLE remocra.l_thematique_module (
    thematique_id           UUID     NOT NULL REFERENCES remocra.thematique(thematique_id),
    module_id        UUID     NOT NULL REFERENCES remocra.module(module_id),

    PRIMARY KEY (thematique_id, module_id)
);

ALTER TABLE remocra.module ADD COLUMN module_nb_document INTEGER NULL;