ALTER TYPE historique."type_objet" ADD VALUE 'NATURE';
ALTER TABLE remocra.nature ADD nature_protected BOOLEAN NOT NULL default false;
