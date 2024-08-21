DROP TABLE IF EXISTS remocra.module;
DROP TYPE IF EXISTS remocra.TYPE_MODULE;

CREATE TYPE remocra.TYPE_MODULE as ENUM(
   'DECI',
   'CARTOGRAPHIE',
   'OLDEBS',
   'PERMIS',
   'RCI',
   'DFCI',
   'ADRESSES',
   'RISQUES',
   'ADMIN',
   'COURRIER',
   'DOCUMENT',
   'PERSONNALISE'
);

CREATE TABLE remocra.module (
    module_id              UUID                   PRIMARY KEY,
    module_type            remocra.TYPE_MODULE    NOT NULL,
    module_titre           text,
    module_image           text,
    module_contenu_html    text,
    module_colonne         integer                NOT NULL,
    module_ligne           integer                NOT NULL
);

COMMENT
    ON COLUMN remocra.module.module_image
    IS 'Chemin de l''image relatif à /var/lib/remocra/images/accueil';