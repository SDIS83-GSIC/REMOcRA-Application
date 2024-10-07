import { Button, Container } from "react-bootstrap";
import EtudeStatutEnum from "../../../Entities/EtudeEntity.tsx";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "../../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconClose,
  IconEdit,
  IconEtude,
  IconImport,
  IconSee,
} from "../../../components/Icon/Icon.tsx";
import ConfirmModal from "../../../components/Modal/ConfirmModal.tsx";
import useModal from "../../../components/Modal/ModalUtils.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import { hasDroit } from "../../../droits.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "./FilterEtude.tsx";

const ListEtude = () => {
  const typeEtudeState = useGet(url`/api/couverture-hydraulique/type-etudes`);
  const { user }: { user: UtilisateurEntity } = useAppContext();

  return (
    <>
      <Container>
        <PageTitle
          icon={<IconEtude />}
          title={"Liste des études"}
          right={
            hasDroit(user, TYPE_DROIT.ETUDE_C) && (
              <Button href={URLS.CREATE_ETUDE}>
                Ajouter une nouvelle étude
              </Button>
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
            {
              accessor: "etudeId",
              Cell: (row: any) => {
                return (
                  <>
                    <TooltipCustom
                      tooltipText="Modifier l'étude"
                      tooltipId={row.value}
                    >
                      <Button
                        variant="link"
                        href={URLS.UPDATE_ETUDE(row.value)}
                      >
                        <IconEdit />
                      </Button>
                    </TooltipCustom>
                  </>
                );
              },
              width: 90,
            },
            CellCloreEtude(),
            {
              accessor: "etudeId",
              Cell: (row: any) => {
                return (
                  // TODO le déplacer au bon endroit quand on autre la page
                  <>
                    <TooltipCustom
                      tooltipText="Importer des fichiers shapes pour l'étude"
                      tooltipId={row.value}
                    >
                      <Button
                        variant="link"
                        href={URLS.IMPORTER_COUVERTURE_HYDRAULIQUE(row.value)}
                      >
                        <IconImport />
                      </Button>
                    </TooltipCustom>
                  </>
                );
              },
              width: 90,
            },
            {
              accessor: "etudeId",
              Cell: (row: any) => {
                return (
                  // TODO le déplacer au bon endroit quand on autre la page
                  <>
                    <TooltipCustom
                      tooltipText="Ouvrir l'étude"
                      tooltipId={row.value}
                    >
                      <Button
                        variant="link"
                        href={URLS.OUVRIR_ETUDE(row.value)}
                      >
                        <IconSee />
                      </Button>
                    </TooltipCustom>
                  </>
                );
              },
              width: 90,
            },
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

const CellCloreEtude = () => ({
  accessor: "etudeId",
  Cell: (row) => {
    const { visible, show, close } = useModal();
    const query = `/api/couverture-hydraulique/etude/clore`;
    return (
      <>
        <>
          <TooltipCustom tooltipText={"Clore l'étude"} tooltipId={row.value}>
            <Button variant={"link"} className={"text-danger"} onClick={show}>
              <IconClose />
            </Button>
          </TooltipCustom>
          <ConfirmModal
            id={row.value}
            visible={visible}
            closeModal={close}
            query={query}
            onConfirm={() => window.location.reload()}
          />
        </>
      </>
    );
  },
});

export default ListEtude;
