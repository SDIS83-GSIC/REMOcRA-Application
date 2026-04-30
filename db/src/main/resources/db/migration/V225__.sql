CREATE OR REPLACE VIEW entrepotsig.v_commune_sig AS
SELECT
    commune.commune_id AS v_commune_sig_id,
    commune.commune_libelle AS v_commune_sig_libelle,
    commune.commune_code_insee AS v_commune_sig_code_insee,
    commune.commune_code_postal AS v_commune_sig_code_postal,
    commune.commune_geometrie AS v_commune_sig_geometrie,
    commune.commune_pprif AS v_commune_sig_pprif,
    commune.commune_code AS v_commune_sig_code
FROM remocra.commune;
