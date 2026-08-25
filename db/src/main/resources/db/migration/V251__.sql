-- Ajoute un deuxième corps de mail pour les mails avec pièce jointe
ALTER TABLE modele_courrier
RENAME COLUMN modele_courrier_corps_email TO modele_courrier_corps_email_utilisateur;

ALTER TABLE modele_courrier
ADD COLUMN modele_courrier_corps_email_piece_jointe TEXT;

UPDATE modele_courrier
SET modele_courrier_corps_email_piece_jointe =
'<p> Bonjour,<br>
Vous trouverez ci-joint un courrier qui vous est adressé.<br> </p>
<p>Cordialement.</p>';

ALTER TABLE modele_courrier
ALTER COLUMN modele_courrier_corps_email_piece_jointe SET NOT NULL;
