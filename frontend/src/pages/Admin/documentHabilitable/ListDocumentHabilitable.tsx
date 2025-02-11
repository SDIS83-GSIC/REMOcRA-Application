import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import { IconExport, IconList } from "../../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../droits.tsx";
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import FilterValues from "./FilterDocumentHabilitable.tsx";

const ListDocumentHabilitable = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const thematiqueState = useGet(url`/api/thematique/`);
  const profilDroitState = useGet(url`/api/profil-droit`);
  const listeButton: ButtonType[] = [];
  const { handleShowClose, activesKeys } = useAccordionState([false]);

  if (hasDroit(user, TYPE_DROIT.DOCUMENTS_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (documentHabilitableId) =>
        URLS.UPDATE_DOCUMENT_HABILITABLE(documentHabilitableId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      pathname: url`/api/document-habilitable/delete/`,
    });
  }

  if (hasDroit(user, TYPE_DROIT.DOCUMENTS_R)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (documentHabilitableId) =>
        url`/api/document-habilitable/telecharger/` + documentHabilitableId,
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconExport />,
      textEnable: "Télécharger le document",
      classEnable: "warning",
    });
  }
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Liste des documents habilitables"}
          right={
            hasDroit(user, TYPE_DROIT.DOCUMENTS_A) && (
              <CreateButton
                href={URLS.ADD_DOCUMENT_HABILITABLE}
                title={"Ajouter un document habilitable"}
              />
            )
          }
        />

        <AccordionCustom
          activesKeys={activesKeys}
          handleShowClose={handleShowClose}
          list={[
            {
              header: "Informations utiles",
              content: (
                <p>
                  Les documents habilitables sont
                  <ul>
                    <li>
                      rattachables à zéro ou plusieurs <b>thématiques</b>
                    </li>
                    <li>
                      accessibles à zéro ou plusieurs <b>profils droit</b>
                    </li>
                  </ul>
                  Ils remontent sur la page d&apos;accueil dans le bloc de type{" "}
                  <b>document</b> idoine (donc en prenant en compte le
                  paramétrage des thématiques de celui-ci, ainsi que les droits
                  de l&apos;utilisateur connecté).
                  <br />
                  Attention, aucune thématique ou aucun profil droit
                  sélectionnés --&gt; le document ne pourra pas remonter sur la
                  page d&apos;accueil
                </p>
              ),
            },
          ]}
        />

        <QueryTable
          query={url`/api/document-habilitable/`}
          columns={[
            {
              Header: "Libellé",
              accessor: "documentHabilitableLibelle",
              sortField: "documentHabilitableLibelle",
              Filter: (
                <FilterInput type="text" name="documentHabilitableLibelle" />
              ),
            },
            {
              Header: "Thématiques",
              accessor: "listeThematique",
              Filter: (
                <MultiSelectFilterFromList
                  name={"listThematiqueId"}
                  listIdCodeLibelle={thematiqueState.data}
                />
              ),
            },
            {
              Header: "Profils droits",
              accessor: "listeProfilDroit",
              Filter: (
                <MultiSelectFilterFromList
                  name={"listProfilDroitId"}
                  listIdCodeLibelle={profilDroitState.data}
                />
              ),
            },
            {
              Header: "Mise à jour le",
              accessor: "documentHabilitableDateMaj",
              sortField: "documentHabilitableDateMaj",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && formatDateTime(value.value)}
                  </div>
                );
              },
            },
            ActionColumn({
              Header: "Actions",
              accessor: "documentHabilitableId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableDocumentHabilitable"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({})}
        />
      </Container>
    </>
  );
};

export default ListDocumentHabilitable;
