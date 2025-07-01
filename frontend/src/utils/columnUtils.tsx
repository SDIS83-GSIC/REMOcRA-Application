import UtilisateurEntity from "../Entities/UtilisateurEntity.tsx";
import { useGet } from "../components/Fetch/useFetch.tsx";
import FilterInput from "../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../components/Filter/MultiSelectFilterFromList.tsx";
import SelectFilterFromUrl from "../components/Filter/SelectFilterFromUrl.tsx";
import SelectNomenclaturesFilter from "../components/Filter/SelectNomenclaturesFilter.tsx";
import SelectEnumOption from "../components/Form/SelectEnumOption.tsx";
import {
  IconAireAspiration,
  IconCloseIndisponibiliteTemporaire,
  IconLocation,
  IconOeil,
  IconSee,
  IconVisite,
} from "../components/Icon/Icon.tsx";
import { GET_TYPE_GEOMETRY } from "../components/Localisation/useLocalisation.tsx";
import { columnType } from "../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../components/Table/TableActionColumn.tsx";
import {
  ActionColumn,
  BooleanColumn,
  ListePeiColumn,
} from "../components/Table/columns.tsx";
import { hasDroit, isAuthorized } from "../droits.tsx";
import COLUMN_INDISPONIBILITE_TEMPORAIRE from "../enums/ColumnIndisponibiliteTemporaireEnum.tsx";
import COLUMN_PEI from "../enums/ColumnPeiEnum.tsx";
import DISPONIBILITE_PEI from "../enums/DisponibiliteEnum.tsx";
import TYPE_DROIT from "../enums/DroitEnum.tsx";
import NOMENCLATURES, {
  NOMENCLATURE_ORGANISME,
} from "../enums/NomenclaturesEnum.tsx";
import PROCHAINE_DATE_ENUM from "../enums/ProchaineDateEnum.tsx";
import STATUT_INDISPONIBILITE_TEMPORAIRE from "../enums/StatutIndisponibiliteTemporaireEnum.tsx";
import TYPE_PEI from "../enums/TypePeiEnum.tsx";
import VRAI_FAUX from "../enums/VraiFauxEnum.tsx";
import url from "../module/fetch.tsx";
import FicheResume from "../pages/Pei/FicheResume/FicheResume.tsx";
import { URLS } from "../routes.tsx";
import TooltipCustom from "../components/Tooltip/Tooltip.tsx";
import getStringListeAnomalie from "./anomaliesUtils.tsx";
import formatDateTime, { formatDate } from "./formatDateUtils.tsx";
import { IdCodeLibelleType } from "./typeUtils.tsx";

function getColumnPeiByStringArray(
  user: UtilisateurEntity,
  parametres: Array<COLUMN_PEI>,
  listeAnomaliePossible: Array<IdCodeLibelleType>,
  fetchGeometry: (typeGeometrie: string, idPei: string) => void,
  displayFicheResumeStandalone: boolean = false,
): Array<columnType> {
  const column: Array<columnType> = [];

  parametres.forEach((_parametre: COLUMN_PEI) => {
    switch (_parametre) {
      case COLUMN_PEI.NUMERO_COMPLET:
        column.push({
          Header: "Numéro",
          accessor: "peiNumeroComplet",
          sortField: "peiNumeroComplet",
          Filter: <FilterInput type="text" name="peiNumeroComplet" />,
          width: 125,
        });
        break;
      case COLUMN_PEI.NUMERO_INTERNE:
        column.push({
          Header: "N° interne",
          accessor: "peiNumeroInterne",
          sortField: "peiNumeroInterne",
          Filter: <FilterInput type="number" name="peiNumeroInterne" />,
        });
        break;
      case COLUMN_PEI.TYPE_PEI:
        column.push({
          Header: "Type",
          accessor: "peiTypePei",
          sortField: "peiTypePei",
          Filter: <SelectEnumOption options={TYPE_PEI} name={"typePei"} />,
        });
        break;
      case COLUMN_PEI.DISPONIBILITE_TERRESTRE:
        column.push({
          Header: "Disponibilité",
          accessor: "peiDisponibiliteTerrestre",
          sortField: "peiDisponibiliteTerrestre",
          Cell: (value) => {
            const dispo =
              DISPONIBILITE_PEI[value.value] === DISPONIBILITE_PEI.NON_CONFORME
                ? { bg: "bg-warning", value: "Non conforme" }
                : DISPONIBILITE_PEI[value.value] ===
                    DISPONIBILITE_PEI.INDISPONIBLE
                  ? { bg: "bg-danger", value: "Indisponible" }
                  : { bg: "", value: "Disponible" };
            return <div className={dispo.bg}>{dispo.value}</div>;
          },
          Filter: (
            <SelectEnumOption
              options={DISPONIBILITE_PEI}
              name={"peiDisponibiliteTerrestre"}
            />
          ),
        });
        break;
      case COLUMN_PEI.DISPONIBILITE_HBE:
        column.push({
          Header: "Disponibilité HBE",
          accessor: "penaDisponibiliteHbe",
          sortField: "penaDisponibiliteHbe",
          Cell: (value) => {
            const dispo =
              DISPONIBILITE_PEI[value.value] === DISPONIBILITE_PEI.NON_CONFORME
                ? { bg: "bg-warning", value: "Non conforme" }
                : DISPONIBILITE_PEI[value.value] ===
                    DISPONIBILITE_PEI.INDISPONIBLE
                  ? { bg: "bg-danger", value: "Indispo" }
                  : { bg: "", value: "Dispo" };
            return <div className={dispo.bg}>{dispo.value}</div>;
          },
          Filter: (
            <SelectEnumOption
              options={DISPONIBILITE_PEI}
              name={"penaDisponibiliteHbe"}
            />
          ),
        });
        break;
      case COLUMN_PEI.NATURE:
        column.push({
          Header: "Nature",
          accessor: "natureLibelle",
          sortField: "natureLibelle",
          Filter: (
            <SelectNomenclaturesFilter
              name={"natureId"}
              nomenclature={NOMENCLATURES.NATURE}
            />
          ),
        });
        break;
      case COLUMN_PEI.COMMUNE:
        column.push({
          Header: "Commune",
          accessor: "communeLibelle",
          sortField: "communeLibelle",
          Filter: (
            <SelectFilterFromUrl
              name={"communeId"}
              url={url`/api/commune/get-libelle-commune`}
            />
          ),
          Cell: (value) => {
            return (
              <TooltipCustom
                tooltipText={value.value}
                tooltipId={value}
                maxWidth={167}
              >
                {value.value}
              </TooltipCustom>
            );
          },
          width: 170,
        });
        break;
      case COLUMN_PEI.NATURE_DECI:
        column.push({
          Header: "Nature DECI",
          accessor: "natureDeciLibelle",
          sortField: "natureDeciLibelle",
          Filter: (
            <SelectNomenclaturesFilter
              name={"natureDeci"}
              nomenclature={NOMENCLATURES.NATURE_DECI}
            />
          ),
        });
        break;
      case COLUMN_PEI.AUTORITE_DECI:
        column.push({
          Header: "Autorité DECI",
          accessor: "autoriteDeci",
          sortField: "autoriteDeci",
          Filter: (
            <SelectFilterFromUrl
              name={"autoriteDeci"}
              url={
                url`/api/organisme/get-list-` +
                NOMENCLATURE_ORGANISME.AUTORITE_DECI
              }
            />
          ),
          width: 150,
        });
        break;
      case COLUMN_PEI.SERVICE_PUBLIC_DECI:
        column.push({
          Header: "Service Public DECI",
          accessor: "servicePublicDeci",
          sortField: "servicePublicDeci",
          Filter: (
            <SelectFilterFromUrl
              name={"servicePublicDeci"}
              url={
                url`/api/organisme/get-list-` +
                NOMENCLATURE_ORGANISME.SERVICE_PUBLIC_DECI
              }
            />
          ),
        });
        break;
      case COLUMN_PEI.ANOMALIES:
        column.push({
          Header: "Anomalies",
          accessor: "listeAnomalie",
          Cell: (value) => {
            return (
              <div>
                {getStringListeAnomalie(value.value, listeAnomaliePossible)}
              </div>
            );
          },
          Filter: <FilterInput type="text" name="listeAnomalie" />,
        });
        break;
      case COLUMN_PEI.PEI_NEXT_ROP:
        column.push({
          Header: "Prochaine ROP",
          accessor: "peiNextRop",
          sortField: "peiNextRop",
          Cell: (value) => {
            return (
              <div className="text-center">
                {value.value ? formatDate(value.value) : ""}
              </div>
            );
          },
          Filter: (
            <SelectEnumOption
              options={PROCHAINE_DATE_ENUM}
              name={"prochaineDateRop"}
            />
          ),
        });
        break;
      case COLUMN_PEI.PEI_NEXT_CTP:
        column.push({
          Header: "Prochain CTP",
          accessor: "peiNextCtp",
          sortField: "peiNextCtp",
          Cell: (value) => {
            return (
              <div className="text-center">
                {value.value ? formatDate(value.value) : ""}
              </div>
            );
          },
          Filter: (
            <SelectEnumOption
              options={PROCHAINE_DATE_ENUM}
              name={"prochaineDateCtp"}
            />
          ),
        });
        break;
      case COLUMN_PEI.TOURNEE_LIBELLE:
        column.push({
          Header: "Tournée",
          accessor: "tourneeLibelle",
          sortField: "tourneeLibelle",
          Filter: (
            <SelectFilterFromUrl
              url={url`/api/tournee/get-libelle-tournee`}
              name="tourneeId"
            />
          ),
          width: 250,
          Cell: (value) => {
            return (
              <TooltipCustom
                tooltipText={value.value}
                tooltipId={value}
                maxWidth={247}
              >
                {value.value}
              </TooltipCustom>
            );
          },
        });
        break;

      case COLUMN_PEI.PEI_ADRESSE:
        column.push({
          Header: "Adresse",
          accessor: "adresse",
          sortField: "adresse",
          Filter: <FilterInput type="text" name="adresse" />,
          Cell: (value) => {
            return (
              <TooltipCustom
                tooltipText={value.value}
                tooltipId={value}
                maxWidth={247}
              >
                {value.value}
              </TooltipCustom>
            );
          },
          width: 250,
        });
        break;
      default:
        column.push({
          Header: "Erreur",
          accessor: "null",
          sortField: "null",
        });
    }
  });

  {
    const listeButton: ButtonType[] = [];

    const canEditPei = isAuthorized(user, [
      TYPE_DROIT.PEI_U,
      TYPE_DROIT.PEI_CARACTERISTIQUES_U,
      TYPE_DROIT.PEI_NUMERO_INTERNE_U,
      TYPE_DROIT.PEI_DEPLACEMENT_U,
      TYPE_DROIT.PEI_ADRESSE_C,
    ]);

    if (
      displayFicheResumeStandalone === true &&
      hasDroit(user, TYPE_DROIT.PEI_R)
    ) {
      listeButton.push({
        row: (row) => {
          return row;
        },
        type: TYPE_BUTTON.SIMPLE_MODAL,
        icon: <IconSee />,
        textEnable: "Voir le résumé",
        simpleModal: {
          content: (peiId: string) => <FicheResume peiId={peiId} />,
          header: (row) =>
            "Fiche Résumé du PEI " + row.original.peiNumeroComplet,
        },
      });

      // Si l'utilisateur ne peut pas update un PEI, on lui propose la fiche complète en lecture seule
      if (!canEditPei) {
        listeButton.push({
          row: (row) => {
            return row;
          },
          type: TYPE_BUTTON.SEE,
          route: (idPei) => URLS.UPDATE_PEI(idPei),
          icon: <IconOeil />,
          textEnable: "Voir la fiche complète du PEI",
        });
      }
    }
    if (canEditPei) {
      listeButton.push({
        row: (row) => {
          return row;
        },
        type: TYPE_BUTTON.UPDATE,
        route: (idPei) => URLS.UPDATE_PEI(idPei),
      });
    }
    if (hasDroit(user, TYPE_DROIT.PEI_D)) {
      listeButton.push({
        disable: (v) => {
          return v.original.tourneeLibelle != null;
        },
        textDisable: "Impossible de supprimer un PEI affecté à une tournée",
        row: (row) => {
          return row;
        },
        type: TYPE_BUTTON.DELETE,
        pathname: url`/api/pei/delete/`,
      });
    }
    if (hasDroit(user, TYPE_DROIT.VISITE_R)) {
      listeButton.push({
        row: (row) => {
          return row;
        },
        route: (idPei) => URLS.VISITE(idPei),
        type: TYPE_BUTTON.LINK,
        icon: <IconVisite />,
        textEnable: "Gérer les visites",
        classEnable: "warning",
      });
    }
    if (hasDroit(user, TYPE_DROIT.PEI_U)) {
      listeButton.push({
        row: (row) => {
          return row;
        },
        route: (idPei) => URLS.UPDATE_PENA_ASPIRATION(idPei),
        type: TYPE_BUTTON.LINK,
        icon: <IconAireAspiration />,
        textEnable: "Gérer les aires d'aspirations",
        classEnable: "warning",
        hide: (original: any) => {
          return original.peiTypePei !== TYPE_PEI.PENA;
        },
      });
    }
    listeButton.push({
      row: (row) => {
        return row;
      },
      onClick: (idPei) => fetchGeometry(GET_TYPE_GEOMETRY.PEI, idPei),
      type: TYPE_BUTTON.LINK,
      icon: <IconLocation />,
      textEnable: "Localiser",
      classEnable: "primary",
    });
    column.push(
      ActionColumn({
        Header: "Actions",
        accessor: "peiId",
        buttons: listeButton,
      }),
    );
  }

  return column;
}

/***********************INDISPO_TEMPORARIE******************/
export function GetColumnIndisponibiliteTemporaireByStringArray({
  user,
  parametres,
  handleButtonClick,
  fetchGeometry,
}: {
  user: UtilisateurEntity;
  parametres: Array<COLUMN_INDISPONIBILITE_TEMPORAIRE>;
  handleButtonClick: (value: string) => any;
  fetchGeometry: (typeGeometrie: string, idInsiponibiliteTemp: string) => void;
}): Array<columnType> {
  const listePeiState = useGet(url`/api/pei/get-id-numero`);
  const column: Array<columnType> = [];
  {
    column.push(
      ListePeiColumn({
        handleButtonClick: handleButtonClick,
        accessor: "indisponibiliteTemporaireId",
      }),
    );
  }
  parametres.forEach((_parametre: COLUMN_INDISPONIBILITE_TEMPORAIRE) => {
    switch (_parametre) {
      case COLUMN_INDISPONIBILITE_TEMPORAIRE.MOTIF:
        column.push({
          Header: "Motif",
          accessor: "indisponibiliteTemporaireMotif",
          sortField: "indisponibiliteTemporaireMotif",
          Filter: (
            <FilterInput type="text" name="indisponibiliteTemporaireMotif" />
          ),
        });
        break;
      case COLUMN_INDISPONIBILITE_TEMPORAIRE.DESCRIPTION:
        column.push({
          Header: "Observation",
          accessor: "indisponibiliteTemporaireObservation",
          sortField: "indisponibiliteTemporaireObservation",
          Filter: (
            <FilterInput
              type="text"
              name="indisponibiliteTemporaireObservation"
            />
          ),
        });
        break;
      case COLUMN_INDISPONIBILITE_TEMPORAIRE.STATUT:
        column.push({
          Header: "Statut",
          accessor: "indisponibiliteTemporaireStatut",
          Cell: (value) => {
            return (
              <div>
                {value?.value != null &&
                  STATUT_INDISPONIBILITE_TEMPORAIRE[value.value]}
              </div>
            );
          },
          Filter: (
            <SelectEnumOption
              options={STATUT_INDISPONIBILITE_TEMPORAIRE}
              name={"indisponibiliteTemporaireStatut"}
            />
          ),
        });
        break;
      case COLUMN_INDISPONIBILITE_TEMPORAIRE.MAIL_AVANT:
        column.push(
          BooleanColumn({
            Header: "Notification avant date début",
            accessor: "indisponibiliteTemporaireMailAvantIndisponibilite",
            sortField: "indisponibiliteTemporaireMailAvantIndisponibilite",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"indisponibiliteTemporaireMailAvantIndisponibilite"}
              />
            ),
          }),
        );

        break;
      case COLUMN_INDISPONIBILITE_TEMPORAIRE.MAIL_APRES:
        column.push(
          BooleanColumn({
            Header: "Notification après date fin",
            accessor: "indisponibiliteTemporaireMailApresIndisponibilite",
            sortField: "indisponibiliteTemporaireMailApresIndisponibilite",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"indisponibiliteTemporaireMailApresIndisponibilite"}
              />
            ),
          }),
        );

        break;

      case COLUMN_INDISPONIBILITE_TEMPORAIRE.LIST_PEI:
        column.push({
          Header: "PEI concerné",
          accessor: "listeNumeroPei",
          sortField: "listeNumeroPei",
          Filter: (
            <MultiSelectFilterFromList
              name={"listePeiId"}
              listIdCodeLibelle={listePeiState?.data?.map((e) => ({
                id: e.peiId,
                code: e.peiId,
                libelle: e.peiNumeroComplet,
              }))}
            />
          ),
          width: 200,
        });
        break;

      case COLUMN_INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT:
        column.push({
          Header: "Date de début",
          accessor: "indisponibiliteTemporaireDateDebut",
          sortField: "indisponibiliteTemporaireDateDebut",
          Cell: (value) => {
            return <div>{value.value ? formatDateTime(value.value) : ""}</div>;
          },
        });
        break;

      case COLUMN_INDISPONIBILITE_TEMPORAIRE.DATE_FIN:
        column.push({
          Header: "Date de fin",
          accessor: "indisponibiliteTemporaireDateFin",
          sortField: "indisponibiliteTemporaireDateFin",
          Cell: (value) => {
            return <div>{value.value ? formatDateTime(value.value) : ""}</div>;
          },
        });
        break;

      default:
    }
  });
  {
    const listeButton: ButtonType[] = [];
    if (hasDroit(user, TYPE_DROIT.INDISPO_TEMP_U)) {
      listeButton.push({
        row: (row) => {
          return row;
        },
        type: TYPE_BUTTON.UPDATE,
        disable: (v) => {
          return (
            STATUT_INDISPONIBILITE_TEMPORAIRE[
              v.original.indisponibiliteTemporaireStatut
            ] === STATUT_INDISPONIBILITE_TEMPORAIRE.TERMINEE ||
            !v.original.isModifiable
          );
        },
        textDisable:
          "L'indisponibilité temporaire est terminée ou contient des PEI en dehors de votre zone de compétence",
        route: (indisponibiliteTemporaireId) =>
          URLS.UPDATE_INDISPONIBILITE_TEMPORAIRE(indisponibiliteTemporaireId),
      });
    }
    if (hasDroit(user, TYPE_DROIT.INDISPO_TEMP_D)) {
      listeButton.push({
        row: (row) => {
          return row;
        },
        type: TYPE_BUTTON.DELETE,
        disable: (v) => {
          return (
            STATUT_INDISPONIBILITE_TEMPORAIRE[
              v.original.indisponibiliteTemporaireStatut
            ] === STATUT_INDISPONIBILITE_TEMPORAIRE.EN_COURS ||
            !v.original.isModifiable
          );
        },
        textDisable:
          "L'indisponibilité temporaire est en cours ou contient des PEI en dehors de votre zone de compétence",
        pathname: url`/api/indisponibilite-temporaire/delete/`,
      });

      if (hasDroit(user, TYPE_DROIT.INDISPO_TEMP_U)) {
        listeButton.push({
          isPost: false,
          row: (row) => {
            return row;
          },
          type: TYPE_BUTTON.CONFIRM,
          disable: (v) => {
            return (
              STATUT_INDISPONIBILITE_TEMPORAIRE[
                v.original.indisponibiliteTemporaireStatut
              ] !== STATUT_INDISPONIBILITE_TEMPORAIRE.EN_COURS
            );
          },
          confirmModal: {
            header: "Clore l'indisponibilité temporaire ?",
            content: "Voulez-vous continuer ? ",
          },
          textDisable:
            "Impossible de clore une indisponibilité temporaire qui n'est pas en cours",
          textEnable: "Clore l'indisponibilité temporaire",
          pathname: url`/api/indisponibilite-temporaire/clore/`,
          icon: <IconCloseIndisponibiliteTemporaire />,
          classEnable: "warning",
        });
      }
    }
    listeButton.push({
      row: (row) => {
        return row;
      },
      onClick: (idIndisponibiliteTemporaire) =>
        fetchGeometry(
          GET_TYPE_GEOMETRY.INDISPONIBILITE_TEMP,
          idIndisponibiliteTemporaire,
        ),
      type: TYPE_BUTTON.LINK,
      icon: <IconLocation />,
      textEnable: "Localiser",
      classEnable: "primary",
    });
    column.push(
      ActionColumn({
        Header: "Actions",
        accessor: "indisponibiliteTemporaireId",
        buttons: listeButton,
      }),
    );
  }

  return column;
}

export default getColumnPeiByStringArray;
