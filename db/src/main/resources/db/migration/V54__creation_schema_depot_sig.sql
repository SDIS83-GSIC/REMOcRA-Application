-- Création du schéma entrepotsig
CREATE SCHEMA entrepotsig;

-- Création de la vue permettant la synchronisation de la table commune
---- Cette vue pose les champs pouvant être mis à jour ou servant à la mise à jour coté remocra
---- Elle devra être override sur la base de données client pour correspondre au modèle de données reçu depuis le SIG
---- Les alias sur les noms des champs sont importants puisque jOOQ s'appui sur ces derniers pour faire sa tambouille
---- Peu importe la manière, la vue doit retourner ces noms de colonne avec le bon type
CREATE OR REPLACE VIEW entrepotsig.v_commune_sig AS
SELECT
	commune.commune_id AS v_commune_sig_id,
	commune.commune_libelle AS v_commune_sig_libelle,
	commune.commune_code_insee AS v_commune_sig_code_insee,
	commune.commune_code_postal AS v_commune_sig_code_postal,
	commune.commune_geometrie AS v_commune_sig_geometrie,
	commune.commune_pprif AS v_commune_sig_pprif
FROM remocra.commune;

-- Création de la vue permettant la synchronisation de la table voie
CREATE OR REPLACE VIEW entrepotsig.v_voie_sig AS
SELECT
	voie.voie_id AS v_voie_sig_id,
	voie.voie_libelle AS v_voie_sig_libelle,
	voie.voie_geometrie AS v_voie_sig_geometrie,
	voie.voie_commune_id AS v_voie_sig_commune_id
FROM remocra.voie;
