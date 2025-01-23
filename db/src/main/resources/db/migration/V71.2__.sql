INSERT INTO remocra.MODULE (
    module_id,
    module_type,
    module_titre,
    module_image,
    module_contenu_html,
    module_colonne,
    module_ligne,
    module_nb_document,
    module_protected
) VALUES (
    gen_random_uuid(),
    'ADMIN',
    'Administration',
    null,
    null,
    2,
    1,
    null,
    true
), (
    gen_random_uuid(),
    'DECI',
    'Gestion des PEI',
    null,
    null,
    1,
    1,
    null,
    true
), (
     gen_random_uuid(),
     'RAPPORT_PERSONNALISE',
     'Rapports personnalisés',
     null,
     null,
     1,
     2,
     null,
     true
 ), (
      gen_random_uuid(),
      'OPERATIONS_DIVERSES',
      'Opérations diverses',
      null,
      null,
      2,
      2,
      null,
      true
  )
;