ALTER TABLE remocra.job
ADD COLUMN job_utilisateur_id UUID REFERENCES remocra.utilisateur(utilisateur_id) ;
