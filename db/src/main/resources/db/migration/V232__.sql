ALTER TABLE remocra.couche
    ADD COLUMN couche_opacite decimal DEFAULT 1.0 CHECK (couche_opacite >= 0 AND couche_opacite <= 1);
