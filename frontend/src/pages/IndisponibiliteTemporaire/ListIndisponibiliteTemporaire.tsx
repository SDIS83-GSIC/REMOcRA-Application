import { useState } from "react";
import { Button } from "react-bootstrap";
import Container from "react-bootstrap/Container";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconIndisponibiliteTemporaire } from "../../components/Icon/Icon.tsx";
import QueryTableWithListingPei from "../../components/ListePeiTable/QueryTableWithListingPei.tsx";
import { useFilterContext } from "../../components/Table/QueryTable.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import COLUMN_INDISPONIBILITE_TEMPORAIRE from "../../enums/ColumnIndisponibiliteTemporaireEnum.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { getColumnIndisponibiliteTemporaireByStringArray } from "../../utils/columnUtils.tsx";
import filterValuesToVariable from "./FilterIndisponibiliteTemporaire.tsx";

const ListIndisponibiliteTemporaire = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  //TODO a aller chercher en base
  const column: COLUMN_INDISPONIBILITE_TEMPORAIRE[] = [
    COLUMN_INDISPONIBILITE_TEMPORAIRE.MOTIF,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.DATE_FIN,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.STATUT,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.AUTO_DISPONIBLE,
    COLUMN_INDISPONIBILITE_TEMPORAIRE.AUTO_INDISPONIBLE,
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
          title={"Indisponibilités temporaires"}
          icon={<IconIndisponibiliteTemporaire />}
          right={
            <Button href={URLS.CREATE_INDISPONIBILITE_TEMPORAIRE}>
              Nouvelle indisponibilité temporaire
            </Button>
          }
        />
      </Container>
      {
        //pas besoin de container il est dans le composant QueryTableWithListingPei
        <QueryTableWithListingPei
          column={getColumnIndisponibiliteTemporaireByStringArray({
            user: user,
            parametres: column,
            handleButtonClick: handleButtonClick,
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
            indisponibiliteTemporaireStatut: undefined,
            indisponibiliteTemporaireObservation: undefined,
            indisponibiliteTemporaireBasculeAutoIndisponible: undefined,
            indisponibiliteTemporaireBasculeAutoDisponible: undefined,
            indisponibiliteTemporaireMailAvantIndisponibilite: undefined,
            indisponibiliteTemporaireMailApresIndisponibilite: undefined,
            listeNumeroPei: undefined,
          })}
        />
      }
    </>
  );
};
export default ListIndisponibiliteTemporaire;
