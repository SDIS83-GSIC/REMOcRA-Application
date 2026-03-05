UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
  'select
                   p.pei_numero_complet as "Numéro",
                   n.nature_libelle as "Nature",
                   St_X(p.pei_geometrie)::integer as "X",
                   St_Y(p.pei_geometrie)::integer as "Y",
                   c.commune_libelle as "Commune",
                   ld.lieu_dit_libelle as "Lieu dit",
                   coalesce(v.voie_libelle, p.pei_voie_texte) as "Voie",
                   croisement.voie_libelle as "Carrefour",
                   p.pei_disponibilite_terrestre as "Disponible"
               from
                   remocra.pei p
               join remocra.commune c on
                   c.commune_id = p.pei_commune_id
               join remocra.nature n on
                   n.nature_id = p.pei_nature_id
               left join remocra.lieu_dit ld on
                   ld.lieu_dit_id = p.pei_lieu_dit_id
               left join remocra.voie v on
                   v.voie_id = p.pei_voie_id
               left join remocra.voie croisement on
                   croisement.voie_id = p.pei_croisement_id
               order by
                   CASE WHEN commune_libelle ~ ''^[0-9]+'' THEN 0 ELSE 1 END,
                   COALESCE(NULLIF(SUBSTRING(commune_libelle, ''^(\\d+)''), ''''), ''0'')::integer,
                   commune_libelle,
                   p.pei_numero_complet;'
WHERE rapport_personnalise_code = 'LISTE_PEI';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
  'select
                                                   p.pei_numero_complet as "Numéro",
                                                   p.pei_type_pei as "Type",
                                                   d.diametre_libelle as "Diametre",
                                                   vcdp.visite_ctrl_debit_pression_debit as "Débit",
                                                   domaine.domaine_libelle as "Domaine",
                                                   deci.nature_deci_libelle as "DECI",
                                                   c.commune_libelle as "Commune",
                                                   coalesce(voie.voie_libelle, p.pei_voie_texte) as "Adresse",
                                                   p.pei_observation as "Observations",
                                                   pibi.pibi_debit_renforce as "Débit renforcé",
                                                   pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
                                               from
                                                   remocra.pei p
                                               inner join remocra.visite v on
                                                   v.visite_pei_id = p.pei_id
                                               inner join remocra.visite_ctrl_debit_pression vcdp on
                                                   vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
                                                   and (''DEBIT'' = ''tous''
                                                       or vcdp.visite_ctrl_debit_pression_debit::text = ''DEBIT'')
                                               inner join remocra.domaine domaine on
                                                   domaine.domaine_id = p.pei_domaine_id
                                                   and (''DOMAINE'' = ''tous''
                                                       or domaine.domaine_id::text = ''DOMAINE'')
                                               left join remocra.pibi pibi on
                                                   pibi.pibi_id = p.pei_id
                                               left join remocra.diametre d on
                                                   d.diametre_id = pibi.pibi_diametre_id
                                               join remocra.nature_deci deci on
                                                   deci.nature_deci_id = p.pei_nature_deci_id
                                               join remocra.commune c on
                                                   c.commune_id = p.pei_commune_id
                                               left join remocra.voie voie on
                                                   voie.voie_id = p.pei_voie_id
                                               where
                                                   (''COMMUNE'' = ''tous''
                                                       or p.pei_commune_id::text = ''COMMUNE'')
                                                   and (''TYPE'' = ''tous''
                                                       or p.pei_type_pei::text = ''TYPE'')
                                                   and (
                                                       ''DIAMETRE'' = ''tous''
                                                       or (pibi.pibi_diametre_id::text = ''DIAMETRE'')
                                                   )
                                                   and (''RENFORCE'' = ''tous''
                                                       or pibi.pibi_debit_renforce::text = ''RENFORCE'')
                                                   and (''INVIOLABILITE'' = ''tous''
                                                       or pibi.pibi_dispositif_inviolabilite::text = ''INVIOLABILITE'');'
WHERE rapport_personnalise_code = 'PEI_ANOMALIES';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
  'select distinct on (p.pei_numero_complet)
            p.pei_numero_complet as "Numéro",
            p.pei_type_pei as "Type",
            d.diametre_libelle as "Diametre",
            v.debit as "Débit",
            domaine.domaine_libelle as "Domaine",
            deci.nature_deci_libelle as "DECI",
            c.commune_libelle as "Commune",
            coalesce(voie.voie_libelle, p.pei_voie_texte) as "Adresse",
            p.pei_observation as "Observations",
            pibi.pibi_debit_renforce as "Débit renforcé",
            pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
        from
            remocra.pei p
        left join remocra.v_pei_last_mesures v on
            v.pei_id = p.pei_id
        inner join remocra.domaine domaine on
            domaine.domaine_id = p.pei_domaine_id
            and (''DOMAINE'' = ''tous''
                or domaine.domaine_id::text = ''DOMAINE'')
        left join remocra.pibi pibi on
            pibi.pibi_id = p.pei_id
        left join remocra.diametre d on
            d.diametre_id = pibi.pibi_diametre_id
        join remocra.nature_deci deci on
            deci.nature_deci_id = p.pei_nature_deci_id
        join remocra.commune c on
            c.commune_id = p.pei_commune_id
        left join remocra.voie voie on
            voie.voie_id = p.pei_voie_id
        LEFT JOIN remocra.l_tournee_pei ltp
        ON ltp.pei_id = p.pei_id
        where
            ltp.tournee_id IS NULL AND
            (''COMMUNE'' = ''tous''
                or p.pei_commune_id::text = ''COMMUNE'')
            and (''TYPE'' = ''tous''
                or p.pei_type_pei::text = ''TYPE'')
            and (
                ''DIAMETRE'' = ''tous''
                or (pibi.pibi_diametre_id::text = ''DIAMETRE'')
            )
            and (''RENFORCE'' = ''tous''
                or pibi.pibi_debit_renforce::text = ''RENFORCE'')
            and (''INVIOLABILITE'' = ''tous''
                or pibi.pibi_debit_renforce::text = ''INVIOLABILITE'')
		 and (''DEBIT'' = ''tous''
		             or v.debit::text = ''DEBIT'')
        order by p.pei_numero_complet;'
WHERE rapport_personnalise_code = 'PEI_SANS_TOURNEE';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
  'select
                                                   p.pei_type_pei as "Type",
                                                   d.diametre_libelle as "Diametre",
                                                   vcdp.visite_ctrl_debit_pression_debit as "Débit",
                                                   domaine.domaine_libelle as "Domaine",
                                                   deci.nature_deci_libelle as "Nature DECI",
                                                   c.commune_libelle as "Commune",
                                                   coalesce(voie.voie_libelle, p.pei_voie_texte) as "Voie",
                                                   p.pei_observation as "Observations",
                                                   pibi.pibi_diametre_canalisation as "Diamètre canalisation",
                                                   pibi.pibi_debit_renforce as "Débit renforcé",
                                                   pibi.pibi_dispositif_inviolabilite as "Dispositif d''inviolabilité"
                                               from
                                                   remocra.pei p
                                               left join remocra.visite v on
                                                   v.visite_pei_id = p.pei_id
                                               left join remocra.visite_ctrl_debit_pression vcdp on
                                                   vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
                                                   and (''DEBIT'' = ''tous''
                                                       or vcdp.visite_ctrl_debit_pression_debit::text = ''DEBIT'')
                                               join remocra.domaine domaine on
                                                   domaine.domaine_id = p.pei_domaine_id
                                                   and (''DOMAINE'' = ''tous''
                                                       or domaine.domaine_id::text = ''DOMAINE'')
                                               join remocra.pibi pibi on
                                                   pibi.pibi_id = p.pei_id
                                               join remocra.diametre d on
                                                   d.diametre_id = pibi.pibi_diametre_id
                                               join remocra.nature_deci deci on
                                                   deci.nature_deci_id = p.pei_nature_deci_id
                                               join remocra.commune c on
                                                   c.commune_id = p.pei_commune_id
                                               left join remocra.voie voie on
                                                   voie.voie_id = p.pei_voie_id
                                               where
                                                   (''COMMUNE'' = ''tous''
                                                       or p.pei_commune_id::text = ''COMMUNE'')
                                                   and (''TYPE'' = ''tous''
                                                       or p.pei_type_pei::text = ''TYPE'')
                                                   and (
                                                       ''DIAMETRE'' = ''tous''
                                                       or (pibi.pibi_diametre_id::text = ''DIAMETRE'')
                                                   )
                                                   and (''RENFORCE'' = ''tous''
                                                       or pibi.pibi_debit_renforce::text = ''RENFORCE'')
                                                   and (''INVIOLABILITE'' = ''tous''
                                                       or pibi.pibi_dispositif_inviolabilite::text = ''INVIOLABILITE'')
                                                   and (''NATURE_DECI'' = ''tous''
                                                       or deci.nature_deci_id::text = ''NATURE_DECI'')
                                                   and (''ADRESSE'' = ''null''
                                                       or voie.voie_libelle ilike ''%'' || ''ADRESSE'' || ''%''
                                                       or p.pei_voie_texte ilike ''%'' || ''ADRESSE'' || ''%'');'
WHERE rapport_personnalise_code = 'PEI_DIAMETRE_CANALISATION';

UPDATE remocra.rapport_personnalise SET rapport_personnalise_source_sql =
  'select
                                                   p.pei_numero_complet as "Numéro",
                                                               coalesce(v.voie_libelle, p.pei_voie_texte) as "Voie",
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
