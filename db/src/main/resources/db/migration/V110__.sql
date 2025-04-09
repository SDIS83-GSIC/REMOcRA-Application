-- supprime la mention 'PEI_NEXT_RECOP' du paramètre PEI_COLONNES
-- et ajoute la mention 'PEI_NEXT_ROP' à la place

UPDATE remocra.parametre
SET parametre_valeur = REPLACE(parametre_valeur, 'PEI_NEXT_RECOP', 'PEI_NEXT_ROP')
WHERE parametre_code = 'PEI_COLONNES';
