INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES (gen_random_uuid(), 'PEI_ORGANISME_NOTIFICATION_VISITE_RECEPTION', '', 'STRING'::remocra."TYPE_PARAMETRE"),
       (gen_random_uuid(), 'PEI_ORGANISME_NOTIFICATION_ROI', '', 'STRING'::remocra."TYPE_PARAMETRE"),
       (gen_random_uuid(), 'AUTORISER_MAIL_ROI', false, 'BOOLEAN'::remocra."TYPE_PARAMETRE"),
       (gen_random_uuid(), 'AUTORISER_MAIL_VISITE_RECEPTION', false, 'BOOLEAN'::remocra."TYPE_PARAMETRE");

ALTER TYPE remocra."type_courrier" ADD VALUE 'COURRIER_ROI';
ALTER TYPE remocra."type_courrier" ADD VALUE 'COURRIER_VISITE_RECEPTION';


insert into role_contact(
    role_contact_id,
    role_contact_actif,
    role_contact_code,
    role_contact_libelle,
    role_contact_protected
) values (
             gen_random_uuid(),
             true,
             'DESTINATAIRE_COURRIER_ROI_VISITE_RECEPTION',
             'Destinataire des courriers générés automatiquement à la création d''une visite de réception ou d''une Reconnaissance Opérationnelle Initiale',
             true
         );
