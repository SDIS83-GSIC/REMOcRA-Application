-- On met à jour le poids global des anomalies systèmes en se basant sur le premier poids non null qui est trouvé dans la table poids_anomalie pour chaque anomalie système.
-- Ou on garde celui qui est paramétré si c'est déjà le cas.
update anomalie set anomalie_poids_anomalie_systeme_val_indispo_terrestre = (
    select poids_anomalie_val_indispo_terrestre from poids_anomalie
        where poids_anomalie_anomalie_id = anomalie_id
        and poids_anomalie_val_indispo_terrestre is not null
        limit 1
    )
where anomalie_id in (
    select anomalie_id from anomalie
    join anomalie_categorie on anomalie_categorie_id = anomalie_anomalie_categorie_id
    where anomalie_categorie_code = 'SYSTEME'
)
and anomalie_poids_anomalie_systeme_val_indispo_terrestre is null;


update anomalie set anomalie_poids_anomalie_systeme_val_indispo_hbe = (
    select poids_anomalie_val_indispo_hbe from poids_anomalie
    where poids_anomalie_anomalie_id = anomalie_id
    and poids_anomalie_val_indispo_hbe is not null
    limit 1
)
where anomalie_id in (
    select anomalie_id from anomalie
    join anomalie_categorie on anomalie_categorie_id = anomalie_anomalie_categorie_id
    where anomalie_categorie_code = 'SYSTEME'
)
    and anomalie_poids_anomalie_systeme_val_indispo_hbe is null;


-- Puis on supprime les poids d'anomalie pour les anomalies systèmes
DELETE FROM poids_anomalie
       where poids_anomalie_anomalie_id in (
            select anomalie_id from anomalie
            join anomalie_categorie on anomalie_categorie_id = anomalie_anomalie_categorie_id
            where anomalie_categorie_code = 'SYSTEME'
       );
