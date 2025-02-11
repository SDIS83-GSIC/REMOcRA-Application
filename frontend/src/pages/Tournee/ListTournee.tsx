import { useState } from "react";
import { Container } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import CreateButton from "../../components/Button/CreateButton.tsx";
import SelectEnumOption from "../../components/Form/SelectEnumOption.tsx";
import {
  IconCentPourcent,
  IconDesaffecter,
  IconSortList,
  IconTournee,
  IconZeroPourcent,
} from "../../components/Icon/Icon.tsx";
import QueryTableWithListingPei from "../../components/ListePeiTable/QueryTableWithListingPei.tsx";
import {
  ActionColumn,
  ListePeiColumn,
} from "../../components/Table/columns.tsx";
import {
  columnType,
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../components/Table/TableActionColumn.tsx";
import { hasDroit, isAuthorized } from "../../droits.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import DELTA_DATE from "../../enums/DeltaDateEnum.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { formatDate } from "../../utils/formatDateUtils.tsx";
import VRAI_FAUX from "../../enums/VraiFauxEnum.tsx";
import { filterValuesToVariable } from "./FilterTournee.tsx";

const ListTournee = ({ peiId }: { peiId: string }) => {
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
      Header: "Pourcentage d'avancement",
      accessor: "tourneePourcentageAvancement",
      sortField: "tourneePourcentageAvancement",
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
      Filter: <SelectEnumOption options={VRAI_FAUX} name={"tourneeActif"} />,
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

  const listeButton: ButtonType[] = [];

  const textDisable =
    "La tournée est réservée ou contient des PEI qui sont en dehors de votre zone de compétence";
  if (hasDroit(user, TYPE_DROIT.TOURNEE_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.UPDATE,
      disable: (v) => {
        return isDisabled(v);
      },
      route: (idTournee) => URLS.UPDATE_TOURNEE(idTournee),
      textDisable: textDisable,
    });

    listeButton.push({
      disable: (v) => {
        return isDisabled(v);
      },
      textDisable: textDisable,
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      pathname: url`/api/tournee/`,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (idTournee) => URLS.TOURNEE_PEI(idTournee),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconSortList />,
      textEnable: "Gérer les PEI et leur ordre dans une tournée",
      textDisable: textDisable,
      classEnable: "warning",
      disable: (v) => {
        return isDisabled(v);
      },
    });
  }
  function isDisabled(v: any): boolean {
    return (
      v.original.tourneeUtilisateurReservationLibelle != null ||
      !v.original.isModifiable
    );
  }

  // Bouton d'accès à la saisie en masse des visites
  const hasVisiteTourneeRight =
    isAuthorized(user, [TYPE_DROIT.TOURNEE_A, TYPE_DROIT.TOURNEE_R]) &&
    isAuthorized(user, [
      TYPE_DROIT.VISITE_RECEP_C,
      TYPE_DROIT.VISITE_RECO_INIT_C,
      TYPE_DROIT.VISITE_NON_PROGRAMME_C,
      TYPE_DROIT.VISITE_CONTROLE_TECHNIQUE_C,
      TYPE_DROIT.VISITE_RECO_C,
    ]);
  if (hasVisiteTourneeRight) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (idTournee) => URLS.TOURNEE_VISITE(idTournee),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconTournee />,
      textEnable: "Saisir toutes les visites de la tournée",
      textDisable: textDisable,
      classEnable: "warning",
      disable: (v) => {
        return isDisabled(v);
      },
    });
  }

  // Bouton désaffectation de la tournée
  if (hasDroit(user, TYPE_DROIT.TOURNEE_RESERVATION_D)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.CONFIRM,
      textEnable: "Retirer la réservation",
      pathname: url`/api/tournee/desaffecter/`,
      icon: <IconDesaffecter />,
      classEnable: "danger",
      hide: (v: any) => {
        return v.tourneeUtilisateurReservationLibelle == null;
      },
    });
  }

  // Bouton forcer l'avancement d'une tournée
  if (hasDroit(user, TYPE_DROIT.TOURNEE_FORCER_POURCENTAGE_E)) {
    // Forcer à 0%
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.CONFIRM,
      conditionnalTextDisable: (row) => {
        return row.original.tourneePourcentageAvancement === 0
          ? "L'avancement de la tournée est déjà à 0%"
          : "Impossible de modifier une tournée réservée";
      },
      textEnable: "Forcer l'avancement de la tournée à 0",
      pathname: url`/api/tournee/avancement-force-0/`,
      icon: <IconZeroPourcent />,
      disable: (v) => {
        return v.original.tourneePourcentageAvancement === 0 || isDisabled(v);
      },
      classEnable: "warning",
    });

    // Forcer à 100%
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.CONFIRM,
      conditionnalTextDisable: (row) => {
        return row.original.tourneePourcentageAvancement === 100
          ? "L'avancement de la tournée est déjà à 100"
          : "Impossible de modifier une tournée réservée";
      },
      textEnable: "Forcer l'avancement de la tournée à 100",
      pathname: url`/api/tournee/avancement-force-100/`,
      icon: <IconCentPourcent />,
      disable: (v) => {
        return v.original.tourneePourcentageAvancement === 100 || isDisabled(v);
      },
      classEnable: "warning",
    });
  }

  column.push(
    ActionColumn({
      Header: "Actions",
      accessor: "tourneeId",
      buttons: listeButton,
    }),
  );

  return (
    <>
      <Container>
        <PageTitle
          icon={<IconTournee />}
          title={"Liste des tournées"}
          displayReturnButton={peiId == null}
          right={
            <CreateButton
              href={URLS.CREATE_TOURNEE}
              title={"Ajouter une tournée"}
            />
          }
        />
      </Container>
      <Container fluid className={"px-5"}>
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
              peiId: peiId,
            })}
          />
        }
      </Container>
    </>
  );
};

export default ListTournee;
