import { Button } from "react-bootstrap";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "../Entities/UtilisateurEntity.tsx";
import { usePut } from "../components/Fetch/useFetch.tsx";
import FilterInput from "../components/Filter/FilterInput.tsx";
import SelectFilterFromUrl from "../components/Filter/SelectFilterFromUrl.tsx";
import SelectNomenclaturesFilter from "../components/Filter/SelectNomenclaturesFilter.tsx";
import SelectEnumOption from "../components/Form/SelectEnumOption.tsx";
import {
  IconAireAspiration,
  IconCloseIndisponibiliteTemporaire,
} from "../components/Icon/Icon.tsx";
import { columnType } from "../components/Table/QueryTable.tsx";
import EditColumn, {
  BooleanColumn,
  DeleteColumn,
} from "../components/Table/columns.tsx";
import TooltipCustom from "../components/Tooltip/Tooltip.tsx";
import { hasDroit } from "../droits.tsx";
import COLUMN_INDISPONIBILITE_TEMPORAIRE from "../enums/ColumnIndisponibiliteTemporaireEnum.tsx";
import COLUMN_PEI from "../enums/ColumnPeiEnum.tsx";
import DISPONIBILITE_PEI from "../enums/DisponibiliteEnum.tsx";
import NOMENCLATURES, {
  NOMENCLATURE_ORGANISME,
} from "../enums/NomenclaturesEnum.tsx";
import STATUT_INDISPONIBILITE_TEMPORAIRE from "../enums/StatutIndisponibiliteTemporaireEnum.tsx";
import TYPE_PEI from "../enums/TypePeiEnum.tsx";
import VRAI_FAUX from "../enums/VraiFauxEnum.tsx";
import url from "../module/fetch.tsx";
import { URLS } from "../routes.tsx";
import getStringListeAnomalie from "./anomaliesUtils.tsx";
import formatDateTime from "./formatDateUtils.tsx";

function getColumnPeiByStringArray(
  parametres: Array<COLUMN_PEI>,
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
        });
        break;
      case COLUMN_PEI.NUMERO_INTERNE:
        column.push({
          Header: "Numéro interne",
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
            return <div>{getStringListeAnomalie(value.value)}</div>;
          },
          Filter: <FilterInput type="text" name="listeAnomalie" />,
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

  column.push(
    EditColumn({
      to: (peiId) => URLS.UPDATE_PEI(peiId),
      title: true,
      accessor: "peiId",
      canEdit: true, // TODO voir avec les rôles
      title: false,
    }),
  );

  column.push({
    Cell: (row: any) => {
      return (
        <>
          {row.value.peiTypePei === TYPE_PEI.PENA && (
            <TooltipCustom
              tooltipText=" Gérer les aires d'aspiration"
              tooltipId={row.value.peiId}
            >
              <Button
                variant="link"
                href={URLS.UPDATE_PENA_ASPIRATION(row.value.peiId)}
              >
                <IconAireAspiration />
              </Button>
            </TooltipCustom>
          )}
        </>
      );
    },
    accessor: ({ peiTypePei, peiId }) => {
      return { peiTypePei, peiId };
    },
    width: 90,
  });

  return column;
}

/***********************INDISPO_TEMPORARIE******************/
export function getColumnIndisponibiliteTemporaireByStringArray(
  user: UtilisateurEntity,
  parametres: Array<COLUMN_INDISPONIBILITE_TEMPORAIRE>,
): Array<columnType> {
  const column: Array<columnType> = [];
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
          sortField: "indisponibiliteTemporaireStatut",
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
      case COLUMN_INDISPONIBILITE_TEMPORAIRE.AUTO_DISPONIBLE:
        column.push(
          BooleanColumn({
            Header: "Bascule vers disponible",
            accessor: "indisponibiliteTemporaireBasculeAutoDisponible",
            sortField: "indisponibiliteTemporaireBasculeAutoDisponible",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"indisponibiliteTemporaireBasculeAutoDisponible"}
              />
            ),
          }),
        );
        break;
      case COLUMN_INDISPONIBILITE_TEMPORAIRE.AUTO_INDISPONIBLE:
        column.push(
          BooleanColumn({
            Header: "Bascule vers indisponible",
            accessor: "indisponibiliteTemporaireBasculeAutoIndisponible",
            sortField: "indisponibiliteTemporaireBasculeAutoIndisponible",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"indisponibiliteTemporaireBasculeAutoIndisponible"}
              />
            ),
          }),
        );

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
          Filter: <FilterInput type="text" name="listeNumeroPei" />,
        });
        break;

      case COLUMN_INDISPONIBILITE_TEMPORAIRE.DATE_DEBUT:
        column.push({
          Header: "Date de début",
          accessor: "indisponibiliteTemporaireDateDebut",
          sortField: "indisponibiliteTemporaireDateDebut",
          Cell: (value) => {
            return <div>{formatDateTime(value.value)}</div>;
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
    if (hasDroit(user, TYPE_DROIT.INDISPO_TEMP_U)) {
      column.push(
        EditColumn({
          to: (indisponibiliteTemporaireId) =>
            URLS.UPDATE_INDISPONIBILITE_TEMPORAIRE(indisponibiliteTemporaireId),
          title: true,
          accessor: "indisponibiliteTemporaireId",
          canEdit: hasDroit(user, TYPE_DROIT.INDISPO_TEMP_U),
          disable: (v) => {
            return (
              STATUT_INDISPONIBILITE_TEMPORAIRE[
                v.original.indisponibiliteTemporaireStatut
              ] === STATUT_INDISPONIBILITE_TEMPORAIRE.TERMINEE
            );
          },
          textDisable:
            "Impossible de modifier une indisponibilité temporaire terminée",
          title: false,
        }),
      );
    }
  }
  {
    if (hasDroit(user, TYPE_DROIT.INDISPO_TEMP_D)) {
      column.push(
        DeleteColumn({
          path: url`/api/indisponibilite-temporaire/delete/`,
          title: false,
          canSupress: hasDroit(user, TYPE_DROIT.INDISPO_TEMP_D),
          accessor: "indisponibiliteTemporaireId",
          textDisable: "Impossible de supprimer une IT en cours",
          disable: (v) => {
            return (
              STATUT_INDISPONIBILITE_TEMPORAIRE[
                v.original.indisponibiliteTemporaireStatut
              ] === STATUT_INDISPONIBILITE_TEMPORAIRE.EN_COURS
            );
          },
        }),
      );
    }
  }
  {
    if (hasDroit(user, TYPE_DROIT.INDISPO_TEMP_U)) {
      const closeIndispo = (value) =>
        // eslint-disable-next-line react-hooks/rules-of-hooks
        usePut(
          url`/api/indisponibilite-temporaire/clore/` + value,
          {
            onResolve: () => {
              // TODO: Ajouter un toast
              window.location.reload();
            },
          },
          true,
        );
      column.push({
        Header: "",
        accessor: "indisponibiliteTemporaireId",
        Cell: (value) => {
          const disable =
            STATUT_INDISPONIBILITE_TEMPORAIRE[
              value.original.indisponibiliteTemporaireStatut
            ] !== STATUT_INDISPONIBILITE_TEMPORAIRE.EN_COURS;
          return (
            <TooltipCustom
              tooltipText={
                !disable
                  ? "Clore l'indisponibilité temporaire"
                  : "Impossible de clore une indisponibilité temporaire qui n'est pas en cours"
              }
              tooltipId={value}
            >
              <div>
                <Button
                  variant={"link"}
                  onClick={closeIndispo(value.value).run}
                  disabled={disable}
                  className={disable ? "" : "text-warning"}
                >
                  <IconCloseIndisponibiliteTemporaire />
                </Button>
              </div>
            </TooltipCustom>
          );
        },
      });
    }
  }

  return column;
}

export default getColumnPeiByStringArray;
