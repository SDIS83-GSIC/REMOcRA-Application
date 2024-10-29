ALTER TABLE remocra.indisponibilite_temporaire ADD COLUMN IF NOT EXISTS indisponibilite_temporaire_notification_debut timestamptz NULL;
ALTER TABLE remocra.indisponibilite_temporaire ADD COLUMN IF NOT EXISTS indisponibilite_temporaire_notification_fin timestamptz NULL;
ALTER TABLE remocra.indisponibilite_temporaire ADD COLUMN IF NOT EXISTS indisponibilite_temporaire_notification_reste_indispo timestamptz NULL;
ALTER TABLE remocra.indisponibilite_temporaire ADD COLUMN IF NOT EXISTS indisponibilite_temporaire_bascule_debut bool DEFAULT FALSE;
ALTER TABLE remocra.indisponibilite_temporaire ADD COLUMN IF NOT EXISTS indisponibilite_temporaire_bascule_fin bool DEFAULT FALSE;

COMMENT ON COLUMN remocra.indisponibilite_temporaire.indisponibilite_temporaire_notification_debut
    IS 'Date à laquelle le début de l''indisponibilité temporaire a été notifié';
COMMENT ON COLUMN remocra.indisponibilite_temporaire.indisponibilite_temporaire_notification_fin
    IS 'Date à laquelle la fin de l''indisponibilité temporaire a été notifié';
COMMENT ON COLUMN remocra.indisponibilite_temporaire.indisponibilite_temporaire_notification_reste_indispo
    IS 'Date à laquelle les pei restés indispo d''une indisponibilité temporaire ont été notifiés';
COMMENT ON COLUMN remocra.indisponibilite_temporaire.indisponibilite_temporaire_bascule_debut
    IS 'Le calcul_dispo au début de l''indisponibilité temporaire a-t-il déjà été lancé ?';
COMMENT ON COLUMN remocra.indisponibilite_temporaire.indisponibilite_temporaire_bascule_fin
    IS 'Le calcul_dispo à la fin de l''indisponibilité temporaire a-t-il déjà été lancé ?';
