import { Button } from "react-bootstrap";
import Container from "react-bootstrap/Container";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconIndisponibiliteTemporaire } from "../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import COLUMN_INDISPONIBILITE_TEMPORAIRE from "../../enums/ColumnIndisponibiliteTemporaireEnum.tsx";
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

  return (
    <>
      <Container>
        <PageTitle
          title={"Indisponibilités temporaires"}
          icon={<IconIndisponibiliteTemporaire />}
          right={
            <Button href={URLS.CREATE_INDISPONIBILITE_TEMPORAIRE}>
              {" "}
              Nouvelle indisponibilité temporaire
            </Button>
          }
        />
        <QueryTable
          query={url`/api/indisponibilite-temporaire`}
          columns={getColumnIndisponibiliteTemporaireByStringArray(
            user,
            column,
          )}
          idName={"IndisponibiliteTemporaireTable"}
          filterValuesToVariable={filterValuesToVariable}
          filterContext={useFilterContext({
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
      </Container>
    </>
  );
};

export default ListIndisponibiliteTemporaire;
