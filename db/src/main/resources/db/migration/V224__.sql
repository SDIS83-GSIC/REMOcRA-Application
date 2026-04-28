create unique index idx_v_mesure_pei_id on v_pei_last_mesures(pei_id);
create unique index idx_v_date_pei_id on v_pei_visite_date(pei_id);

-- -----------------------------
-- Fonction pour refresh la vue matérialisée v_pei_visite_date
--------------------------------
CREATE OR REPLACE FUNCTION remocra.refresh_v_pei_visite_date()
RETURNS TRIGGER AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY remocra.v_pei_visite_date;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Trigger sur INSERT de visite
CREATE TRIGGER trg_visite_insert_refresh_pei_visite_date
AFTER INSERT ON visite
FOR EACH ROW
EXECUTE FUNCTION remocra.refresh_v_pei_visite_date();

-- Trigger sur DELETE de visite
CREATE TRIGGER trg_visite_delete_refresh_pei_visite_date
AFTER DELETE ON visite
FOR EACH ROW
EXECUTE FUNCTION remocra.refresh_v_pei_visite_date();

-- Trigger sur UPDATE de visite
CREATE TRIGGER trg_visite_update_refresh_pei_visite_date
    AFTER UPDATE ON visite
    FOR EACH ROW
    EXECUTE FUNCTION remocra.refresh_v_pei_visite_date();

-- Trigger sur UPDATE de pei.pei_nature_deci_id
CREATE TRIGGER trg_pei_update_nature_deci_refresh_pei_visite_date
AFTER UPDATE OF pei_nature_deci_id ON pei
FOR EACH ROW
WHEN (OLD.pei_nature_deci_id IS DISTINCT FROM NEW.pei_nature_deci_id)
EXECUTE FUNCTION remocra.refresh_v_pei_visite_date();

-- Trigger sur DELETE de pei
CREATE TRIGGER trg_pei_delete_refresh_pei_visite_date
AFTER DELETE ON pei
FOR EACH ROW
EXECUTE FUNCTION remocra.refresh_v_pei_visite_date();

-- -----------------------------
-- Fonction pour refresh la vue matérialisée s
--------------------------------
CREATE OR REPLACE FUNCTION remocra.refresh_v_pei_last_mesures()
RETURNS TRIGGER AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY remocra.v_pei_last_mesures;
RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Trigger sur INSERT de mesure
CREATE TRIGGER trg_mesure_insert_refresh_pei_last_mesure
AFTER INSERT ON visite_ctrl_debit_pression
FOR EACH ROW
EXECUTE FUNCTION remocra.refresh_v_pei_last_mesures();

-- Trigger sur UPDATE de mesure (via l'api possible)
CREATE TRIGGER trg_mesure_update_refresh_pei_last_mesure
AFTER UPDATE ON visite_ctrl_debit_pression
FOR EACH ROW
EXECUTE FUNCTION remocra.refresh_v_pei_last_mesures();

-- Trigger sur DELETE de mesure (à la suppression d'une visite)
CREATE TRIGGER trg_mesure_delete_refresh_pei_last_mesure
AFTER DELETE ON visite_ctrl_debit_pression
FOR EACH ROW
EXECUTE FUNCTION remocra.refresh_v_pei_last_mesures();


