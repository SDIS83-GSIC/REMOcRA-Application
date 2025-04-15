import { useState } from "react";
import Container from "react-bootstrap/Container";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import CreateButton from "../../components/Button/CreateButton.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconIndisponibiliteTemporaire } from "../../components/Icon/Icon.tsx";
import QueryTableWithListingPei from "../../components/ListePeiTable/QueryTableWithListingPei.tsx";
import useLocalisation from "../../components/Localisation/useLocalisation.tsx";
import { useFilterContext } from "../../components/Table/QueryTable.tsx";
import { hasDroit } from "../../droits.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import COLUMN_INDISPONIBILITE_TEMPORAIRE from "../../enums/ColumnIndisponibiliteTemporaireEnum.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { GetColumnIndisponibiliteTemporaireByStringArray } from "../../utils/columnUtils.tsx";
import STATUT_INDISPONIBILITE_TEMPORAIRE from "../../enums/StatutIndisponibiliteTemporaireEnum.tsx";
import { getEnumKey } from "../../utils/fonctionsUtils.tsx";
import filterValuesToVariable from "./FilterIndisponibiliteTemporaire.tsx";

const ListIndisponibiliteTemporaire = ({
  peiId,
  colonnes,
}: {
  peiId?: string;
  colonnes?: COLUMN_INDISPONIBILITE_TEMPORAIRE[];
}) => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const { fetchGeometry } = useLocalisation();

  //TODO a aller chercher en base
  const column: COLUMN_INDISPONIBILITE_TEMPORAIRE[] = colonnes ?? [
    COLUMN_INDISPONIBILITE_TEMPORAIRE.MOTIF,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.DATE_FIN,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.STATUT,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.MAIL_APRES,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.LIST_PEI,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.DESCRIPTION,
  ];

  /***Constante permetant de gérer les état des composants "QueryTable" et "ListePei" *****/
  const [showTable, setShowTable] = useState(true); // Contrôle de l'affichage du tableau
  const [idIndisponibiliteTemporaire, setIdIndisponibiliteTemporaire] =
    useState(null); // Variable à mettre à jour

  // Fonction qui sera appelée quand le bouton dans la cellule est cliqué
  const handleButtonClick = (value) => {
    setIdIndisponibiliteTemporaire(value); // Met à jour la valeur
    setShowTable(false); // Cache le tableau actuel
  };

  return (
    <>
      <Container>
        <PageTitle
          displayReturnButton={peiId != null}
          title={"Indisponibilités temporaires"}
          icon={<IconIndisponibiliteTemporaire />}
          right={
            hasDroit(user, TYPE_DROIT.INDISPO_TEMP_C) && (
              <CreateButton
                title={"Ajouter une indisponibilité temporaire"}
                href={URLS.CREATE_INDISPONIBILITE_TEMPORAIRE}
              />
            )
          }
        />
      </Container>
      {
        //pas besoin de container il est dans le composant QueryTableWithListingPei
        <QueryTableWithListingPei
          column={GetColumnIndisponibiliteTemporaireByStringArray({
            user: user,
            parametres: column,
            handleButtonClick: handleButtonClick,
            fetchGeometry: fetchGeometry,
          })}
          query={url`/api/indisponibilite-temporaire`}
          idName={"IndisponibiliteTemporaireTable"}
          filterPage={FILTER_PAGE.INDISPONIBILITE_TEMPORAIRE}
          showTable={showTable}
          filterId={idIndisponibiliteTemporaire}
          //on envoie les constantes au composant enfant pour qu'il mette à jour le composant parent
          setFilterId={setIdIndisponibiliteTemporaire}
          setShowTable={setShowTable}
          filterValuesToVariable={filterValuesToVariable}
          useFilterContext={useFilterContext({
            indisponibiliteTemporaireDateDebut: undefined,
            indisponibiliteTemporaireDateFin: undefined,
            indisponibiliteTemporaireMotif: undefined,
            indisponibiliteTemporaireStatut: getEnumKey(
              STATUT_INDISPONIBILITE_TEMPORAIRE,
              STATUT_INDISPONIBILITE_TEMPORAIRE.EN_COURS_PLANIFIEE,
            ),
            indisponibiliteTemporaireObservation: undefined,
            indisponibiliteTemporaireMailAvantIndisponibilite: undefined,
            indisponibiliteTemporaireMailApresIndisponibilite: undefined,
            listePeiId: peiId ? [peiId] : undefined,
          })}
        />
      }
    </>
  );
};
export default ListIndisponibiliteTemporaire;
