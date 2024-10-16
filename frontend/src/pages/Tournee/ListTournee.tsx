import React, { useState } from "react";
import { Container } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../components/Form/SelectEnumOption.tsx";
import { IconSortList, IconTournee } from "../../components/Icon/Icon.tsx";
import QueryTableWithListingPei from "../../components/ListePeiTable/QueryTableWithListingPei.tsx";
import {
  ActionColumn,
  ListePeiColumn,
} from "../../components/Table/columns.tsx";
import {
  columnType,
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import DELTA_DATE from "../../enums/DeltaDateEnum.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { formatDate } from "../../utils/formatDateUtils.tsx";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import { hasDroit, isAuthorized } from "../../droits.tsx";
import CreateButton from "../../components/Form/CreateButton.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../components/Table/TableActionColumn.tsx";
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

  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.TOURNEE_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.UPDATE,
      disable: (v) => {
        return isDisabled(v);
      },
      href: (idTournee) => URLS.UPDATE_TOURNEE(idTournee),
      textDisable: "Impossible de modifier une tournée réservée",
    });

    listeButton.push({
      disable: (v) => {
        return isDisabled(v);
      },
      textDisable: "Impossible de supprimer une tournée réservée",
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      path: url`/api/tournee/`,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (idTournee) => URLS.TOURNEE_PEI(idTournee),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconSortList />,
      textEnable: "Gérer les PEI et leur ordre dans une tournée",
      textDisable: "Impossible de modifier une tournée réservée",
      classEnable: "warning",
      disable: (v) => {
        return isDisabled(v);
      },
    });
  }
  function isDisabled(v: any): boolean {
    return v.original.tourneeUtilisateurReservationLibelle != null;
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
      href: (idTournee) => URLS.TOURNEE_VISITE(idTournee),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconTournee />,
      textEnable: "Saisir toutes les visites de la tournée",
      textDisable: "Impossible de saisir les visites d'une tournée réservée",
      classEnable: "warning",
      disable: (v) => {
        return isDisabled(v);
      },
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
    <Container>
      <PageTitle
        icon={<IconTournee />}
        title={"Liste des tournées"}
        right={
          <CreateButton
            href={URLS.CREATE_TOURNEE}
            title={"Créer une tournée"}
          />
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
