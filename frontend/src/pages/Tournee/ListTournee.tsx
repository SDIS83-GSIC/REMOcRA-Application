import { useState } from "react";
import { Button, Container } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../components/Form/SelectEnumOption.tsx";
import { IconSortList, IconTournee } from "../../components/Icon/Icon.tsx";
import QueryTableWithListingPei from "../../components/ListePeiTable/QueryTableWithListingPei.tsx";
import EditColumn, {
  DeleteColumn,
  ListePeiColumn,
  LinkColumn,
} from "../../components/Table/columns.tsx";
import {
  columnType,
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";
import DELTA_DATE from "../../enums/DeltaDateEnum.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { formatDate } from "../../utils/formatDateUtils.tsx";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import { hasDroit } from "../../droits.tsx";
import { filterValuesToVariable } from "./FilterTournee.tsx";

const ListTournee = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  const column: Array<columnType> = [
    {
      Header: "Nom",
      accessor: "tourneeLibelle",
      sortField: "tourneeLibelle",
      Filter: <FilterInput type="text" name="tourneeLibelle" />,
    },
    {
      Header: "Nombre de PEI",
      accessor: "tourneeNbPei",
      sortField: "tourneeNbPei",
    },
    {
      Header: "Organisme",
      accessor: "organismeLibelle",
      sortField: "organismeLibelle",
      Filter: <FilterInput type="text" name="tourneeOrganismeLibelle" />,
    },
    {
      Header: "Etat",
      accessor: "tourneeEtat",
      sortField: "tourneeEtat",
    },
    {
      Header: "Réservation",
      accessor: "tourneeUtilisateurReservationLibelle",
      sortField: "tourneeUtilisateurReservationLibelle",
      Filter: (
        <FilterInput type="text" name="tourneeUtilisateurReservationLibelle" />
      ),
    },
    {
      Header: "Actif",
      accessor: "tourneeActif",
      Cell: (value) => {
        return (
          <Form.Check type="checkbox" disabled checked={value.value === true} />
        );
      },
      sortField: "tourneeActif",
    },
    {
      Header: "Prochaine RECOP",
      accessor: ({ tourneeNextRecopDate }) => {
        return tourneeNextRecopDate ? formatDate(tourneeNextRecopDate) : "";
      },
      sortField: "tourneeNextRecopDate",
      Filter: (
        <SelectEnumOption options={DELTA_DATE} name={"tourneeDeltaDate"} />
      ),
    },
  ];

  /***Constante permetant de gérer les états des composants "QueryTable" et "ListePei" *****/
  const [showTable, setShowTable] = useState(true); // Contrôle de l'affichage du tableau
  const [idTournee, setIdTournee] = useState(null); // Variable à mettre à jour

  // Fonction qui sera appelée quand le bouton dans la cellule est cliqué
  const handleButtonClick = (value) => {
    setIdTournee(value); // Met à jour la valeur
    setShowTable(false); // Cache le tableau actuel
  };

  {
    column.unshift(
      ListePeiColumn({
        handleButtonClick: handleButtonClick,
        accessor: "tourneeId",
      }),
    );
  }

  const hasRight = hasDroit(user, TYPE_DROIT.TOURNEE_A);
  if (hasRight) {
    column.push(
      EditColumn({
        to: (data) => URLS.UPDATE_TOURNEE(data.tourneeId),
        accessor: ({ tourneeId, tourneeUtilisateurReservationLibelle }) => {
          return { tourneeId, tourneeUtilisateurReservationLibelle };
        },
        canEdit: hasRight,
        title: false,
        textDisable: "Impossible de modifier une tournée réservée",
        disable: (v) => {
          return v.original.tourneeUtilisateurReservationLibelle != null;
        },
      }),
    );
    // Colonne d'accès à la saisie en masse des visites
    // TODO : Vérifier les droits
    column.push(
      LinkColumn({
        to: (data) => URLS.TOURNEE_VISITE(data.tourneeId),
        accessor: ({ tourneeId, tourneeUtilisateurReservationLibelle }) => {
          return { tourneeId, tourneeUtilisateurReservationLibelle };
        },
        icon: <IconTournee />,
        canInteractFunction(data) {
          return data.tourneeUtilisateurReservationLibelle == null;
        },
        tooltipText: "Saisir toutes les visites de la tournée",
        textDisable: "Impossible de saisir les visites d'une tournée réservée",
      }),
    );
    //colonne réorganisation PEI
    column.push({
      Cell: (row: any) => {
        const disable =
          row.original.tourneeUtilisateurReservationLibelle != null;
        return (
          <>
            {
              <TooltipCustom
                tooltipText={
                  !disable
                    ? "Gérer les PEI et leur ordre dans une tournée"
                    : "Impossible de modifier une tournée réservée"
                }
                tooltipId={row.value.tourneeId}
              >
                <Button
                  disabled={
                    row.original.tourneeUtilisateurReservationLibelle != null
                  }
                  variant="link"
                  href={URLS.TOURNEE_PEI(row.value.tourneeId)}
                >
                  <IconSortList />
                </Button>
              </TooltipCustom>
            }
          </>
        );
      },
      accessor: ({ tourneeId, tourneeUtilisateurReservationLibelle }) => {
        return { tourneeId, tourneeUtilisateurReservationLibelle };
      },
      width: 90,
    });

    column.push(
      DeleteColumn({
        path: url`/api/tournee/`,
        title: false,
        canSupress: hasDroit(user, TYPE_DROIT.TOURNEE_A),
        accessor: "tourneeId",
        textDisable: "Impossible de supprimer une tournée réservée",
        disable: (v) => {
          return v.original.tourneeUtilisateurReservationLibelle != null;
        },
      }),
    );
  }

  return (
    <Container>
      <PageTitle
        icon={<IconTournee />}
        title={"Liste des tournées"}
        right={
          <Button variant="primary" href={URLS.CREATE_TOURNEE}>
            Créer une tournée
          </Button>
        }
      />
      {
        //pas besoin de container il est dans le composant QueryTableWithListingPei
        <QueryTableWithListingPei
          column={column}
          query={url`/api/tournee`}
          idName={"TourneeTable"}
          filterPage={FILTER_PAGE.TOURNEE}
          showTable={showTable}
          filterId={idTournee}
          //on envoie les constantes au composant enfant pour qu'il mette à jour le composant parent
          setFilterId={setIdTournee}
          setShowTable={setShowTable}
          filterValuesToVariable={filterValuesToVariable}
          useFilterContext={useFilterContext({
            tourneeLibelle: undefined,
            tourneeOrganismeLibelle: undefined,
            tourneeUtilisateurReservationLibelle: undefined,
          })}
        />
      }
    </Container>
  );
};

export default ListTournee;
