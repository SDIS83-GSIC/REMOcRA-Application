-- On ne peut pas garantir l'état initial du service PG, l'admin sys a peut-être déjà ajouté les extensions qu'il jugeait utile ;
-- de fait, on rajoute un "if not exists", faute de quoi la migration plantera.
create extension if not exists unaccent;
