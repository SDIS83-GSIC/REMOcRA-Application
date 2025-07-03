CREATE TABLE type_engin (
    type_engin_id                  UUID            NOT NULL PRIMARY KEY,
    type_engin_actif               BOOLEAN         NOT NULL,
    type_engin_code                TEXT    UNIQUE  NOT NULL,
    type_engin_libelle             TEXT            NOT NULL
);


CREATE TABLE l_pena_type_engin (
    pena_id                          UUID            NOT NULL REFERENCES pena(pena_id),
    type_engin_id                    UUID            NOT NULL references type_engin(type_engin_id),
    primary key (pena_id, type_engin_id)
);

alter type historique.type_objet add value 'TYPE_ENGIN';
