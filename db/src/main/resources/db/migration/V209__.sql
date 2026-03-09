UPDATE remocra.rapport_personnalise SET
    rapport_personnalise_source_sql='select
                                                p.pei_numero_complet as "Numéro",
                                                            v.voie_libelle as "Voie",
                                                            v2.voie_libelle as "Carrefour",
                                                            ld.lieu_dit_libelle as "Lieu dit",
                                                            commune.commune_libelle as "Commune",
                                                            St_X(p.pei_geometrie)::integer as "X",
                                                            St_Y(p.pei_geometrie)::integer as "Y",
                                                            n.nature_libelle as "Nature",
                                                            d.domaine_libelle as "Domaine",
                                                            na.nature_deci_libelle as "Nature DECI",
                                                            case
                                                    p.pei_disponibilite_terrestre
                                                            when ''DISPONIBLE'' then ''Disponible''
                                                    when ''INDISPONIBLE'' then ''Indisponible''
                                                    else ''Non conforme''
                                                end as "Disponibilité",
                                                            to_char(v_psd.last_ctp ,
                                                ''dd/mm/yyyy'') as "Contrôle",
                                                            to_char(v_psd.last_reco_init ,
                                                ''dd/mm/yyyy'') as "Reconnaissance"
                                            from
                                                remocra.pei p
                                            join remocra.commune commune on
                                                commune.commune_id = p.pei_commune_id
                                            left join remocra.voie v on
                                                v.voie_id = p.pei_voie_id
                                            join remocra.nature_deci na on
                                                na.nature_deci_id = p.pei_nature_deci_id
                                            left join remocra.voie v2 on
                                                v2.voie_id = p.pei_croisement_id
                                            left join remocra.lieu_dit ld on
                                                ld.lieu_dit_id = p.pei_lieu_dit_id
                                            join remocra.nature n on
                                                n.nature_id = p.pei_nature_id
                                            join remocra.domaine d on
                                                d.domaine_id = p.pei_domaine_id
                                            left join remocra.v_pei_visite_date v_psd on
                                                v_psd.pei_id = p.pei_id
                                            where
                                                (''COMMUNE'' = ''tous''
                                                    or p.pei_commune_id::text = ''COMMUNE'')
                                                and (''#NATURE_DECI#'' = ''tous''
                                                    or p.pei_nature_deci_id::text = ''#NATURE_DECI#'')
                                                and (''DISPONIBILITE_CODE'' = ''tous''
                                                    or p.pei_disponibilite_terrestre::text = ''DISPONIBILITE_CODE'');'
WHERE rapport_personnalise_code = 'PEI_COMMUNE';
