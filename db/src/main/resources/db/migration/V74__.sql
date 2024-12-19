CREATE TABLE remocra.l_couche_module
(
    couche_id   UUID                NOT NULL REFERENCES remocra.couche (couche_id),
    module_type remocra.TYPE_MODULE NOT NULL,
    PRIMARY KEY (couche_id, module_type)
);
