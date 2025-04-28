ALTER TABLE anomalie ADD COLUMN anomalie_ordre int;
ALTER TABLE anomalie ALTER COLUMN anomalie_ordre SET DEFAULT 0;
