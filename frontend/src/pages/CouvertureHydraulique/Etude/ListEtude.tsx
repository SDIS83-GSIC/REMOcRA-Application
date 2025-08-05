import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconClose,
  IconEtude,
  IconImport,
  IconSee,
} from "../../../components/Icon/Icon.tsx";
import useLocalisation, {
  GET_TYPE_GEOMETRY,
} from "../../../components/Localisation/useLocalisation.tsx";
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
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "./FilterEtude.tsx";

const ListEtude = () => {
  const typeEtudeState = useGet(url`/api/couverture-hydraulique/type-etudes`);
  const { user } = useAppContext();
  const { fetchGeometry } = useLocalisation();

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.ETUDE_R)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      type: TYPE_BUTTON.LINK,
      icon: <IconSee />,
      textEnable: "Ouvrir l'étude",
      onClick: (etudeId, row) => {
        if (row.listeCommune.length > 0) {
          fetchGeometry(
            GET_TYPE_GEOMETRY.COMMUNE_ETUDE,
            etudeId,
            URLS.OUVRIR_ETUDE(etudeId),
          );
        }
      },
      route: (etudeId) => URLS.OUVRIR_ETUDE(etudeId),
    });
  }

  if (hasDroit(user, TYPE_DROIT.ETUDE_U)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      route: (etudeId) => URLS.UPDATE_ETUDE(etudeId),
      type: TYPE_BUTTON.UPDATE,
    });
  }
  if (hasDroit(user, TYPE_DROIT.ETUDE_D)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      type: TYPE_BUTTON.CONFIRM,
      disable: (v) => {
        return (
          EtudeStatutEnum[
            v.original.etudeStatut as keyof typeof EtudeStatutEnum
          ] === EtudeStatutEnum.TERMINEE
        );
      },
      textDisable: "Impossible de clore une étude qui n'est pas en cours",
      textEnable: "Clore l'étude",
      pathname: url`/api/couverture-hydraulique/etude/clore/`,
      icon: <IconClose />,
      classEnable: "danger",
    });
  }
  if (hasDroit(user, TYPE_DROIT.ETUDE_U)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      route: (etudeId) => URLS.IMPORTER_COUVERTURE_HYDRAULIQUE(etudeId),
      type: TYPE_BUTTON.LINK,
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
                title={"Ajouter une étude"}
              />
            )
          }
        />
        <QueryTable
          query={url`/api/couverture-hydraulique`}
          getList={(data: any) => data}
          getCount={(data: any) => data}
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
                    {value?.value != null &&
                      EtudeStatutEnum[
                        value.value as keyof typeof EtudeStatutEnum
                      ]}
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
                        (e: {
                          communeLibelle: string;
                          communeCodeInsee: string;
                        }) =>
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
