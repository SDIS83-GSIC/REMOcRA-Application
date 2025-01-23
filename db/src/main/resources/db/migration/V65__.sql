ALTER TABLE incoming.new_pei
ADD COLUMN new_pei_domaine_id UUID REFERENCES remocra.domaine(domaine_id) NOT NULL;