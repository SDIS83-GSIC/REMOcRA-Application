INSERT INTO remocra.role_contact
(role_contact_id, role_contact_actif, role_contact_code, role_contact_libelle, role_contact_protected)
VALUES
    (gen_random_uuid(), true, 'IT_NOTIF_AVANT_DEBUT', 'Processus de notification avant le début d''une indisponibilité temporaire', true),
    (gen_random_uuid(), true, 'IT_NOTIF_AVANT_FIN', 'Processus de notification avant la fin d''une indisponibilité temporaire', true),
    (gen_random_uuid(), true, 'IT_NOTIF_RESTE_INDISPO', 'Processus de notification des pei restés indisponibles à la suite d''une indisponibilité temporaire', true)
on conflict do nothing;
