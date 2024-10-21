import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import CreateButton from "../../../components/Form/CreateButton.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconClose,
  IconEtude,
  IconImport,
} from "../../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../droits.tsx";
import EtudeStatutEnum from "../../../Entities/EtudeEntity.tsx";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "../../../Entities/UtilisateurEntity.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "./FilterEtude.tsx";

const ListEtude = () => {
  const typeEtudeState = useGet(url`/api/couverture-hydraulique/type-etudes`);
  const { user }: { user: UtilisateurEntity } = useAppContext();

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.ETUDE_R)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.SEE,
      href: (etudeId) => URLS.OUVRIR_ETUDE(etudeId),
    });
  }

  if (hasDroit(user, TYPE_DROIT.ETUDE_U)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (etudeId) => URLS.UPDATE_ETUDE(etudeId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.CONFIRM,
      disable: (v) => {
        return (
          EtudeStatutEnum[v.original.etudeStatut] === EtudeStatutEnum.TERMINEE
        );
      },
      textDisable: "Impossible de clore une étude qui n'est pas en cours",
      textEnable: "Clore l'étude",
      path: url`/api/couverture-hydraulique/etude/clore/`,
      icon: <IconClose />,
      classEnable: "danger",
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (etudeId) => URLS.IMPORTER_COUVERTURE_HYDRAULIQUE(etudeId),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconImport />,
      textEnable: "Importer des fichiers shapes pour l'étude",
      classEnable: "warning",
    });
  }

  return (
    <>
      <Container>
        <PageTitle
          icon={<IconEtude />}
          title={"Liste des études"}
          right={
            hasDroit(user, TYPE_DROIT.ETUDE_C) && (
              <CreateButton
                href={URLS.CREATE_ETUDE}
                title={"Ajouter une nouvelle étude"}
              />
            )
          }
        />
        <QueryTable
          query={url`/api/couverture-hydraulique`}
          columns={[
            {
              Header: "Type d'étude",
              accessor: "typeEtudeLibelle",
              sortField: "typeEtudeLibelle",
              Filter: typeEtudeState?.data && (
                <SelectFilterFromList
                  listIdCodeLibelle={typeEtudeState?.data}
                  name={"typeEtudeId"}
                />
              ),
            },
            {
              Header: "Numéro",
              accessor: "etudeNumero",
              sortField: "etudeNumero",
              Filter: <FilterInput type="text" name="etudeNumero" />,
            },
            {
              Header: "Nom",
              accessor: "etudeLibelle",
              sortField: "etudeLibelle",
              Filter: <FilterInput type="text" name="etudeLibelle" />,
            },
            {
              Header: "Description",
              accessor: "etudeDescription",
              sortField: "etudeDescription",
              Filter: <FilterInput type="text" name="etudeDescription" />,
            },
            {
              Header: "Statut",
              accessor: "etudeStatut",
              sortField: "etudeStatut",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && EtudeStatutEnum[value.value]}
                  </div>
                );
              },
              Filter: (
                <SelectEnumOption
                  options={EtudeStatutEnum}
                  name={"etudeStatut"}
                />
              ),
            },
            {
              Header: "Communes",
              accessor: "listeCommune",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value
                      ?.map(
                        (e) =>
                          e.communeLibelle + " (" + e.communeCodeInsee + ")",
                      )
                      ?.join(", ")}
                  </div>
                );
              },
            },
            {
              Header: "Date de dernière mise à jour",
              accessor: "etudeDateMaj",
              sortField: "etudeDateMaj",
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
              accessor: "etudeId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableEtudeId"}
          filterValuesToVariable={filterValuesToVariable}
          filterContext={useFilterContext({
            typeEtudeId: undefined,
            etudeNumero: undefined,
            etudeLibelle: undefined,
            etudeDescription: undefined,
            etudeStatut: undefined,
            etudeDateMaj: undefined,
          })}
        />
      </Container>
    </>
  );
};

export default ListEtude;
