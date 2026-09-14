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
