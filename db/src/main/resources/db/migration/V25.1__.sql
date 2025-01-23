ALTER TABLE pei ADD COLUMN pei_voie_texte TEXT NULL;

ALTER TABLE pei
    ADD CONSTRAINT pei_voie check(
        pei_voie_id IS NULL AND pei_voie_texte IS NULL
        OR
        ((pei_voie_id IS NULL) != (pei_voie_texte IS NULL))
    )


