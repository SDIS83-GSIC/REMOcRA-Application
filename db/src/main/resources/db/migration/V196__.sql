insert into role_contact(
    role_contact_id,
    role_contact_actif,
    role_contact_code,
    role_contact_libelle,
    role_contact_protected
) values (
    gen_random_uuid(),
    false,
    'RAPPORT_POST_ROP',
    'Destinataire du rapport post ROP',
    true
);