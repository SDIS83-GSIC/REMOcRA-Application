import { columnType } from "../components/Table/QueryTable.tsx";
import COLUMN_PEI from "../enums/ColumnPeiEnum.tsx";
import SelectEnumOption from "../components/Form/SelectEnumOption.tsx";
import DISPONIBILITE_PEI from "../enums/DisponibiliteEnum.tsx";
import FilterInput from "../components/Filter/FilterInput.tsx";
import SelectNomenclatures from "../components/Form/SelectNomenclatures.tsx";
import TYPE_PEI from "../enums/TypePeiEnum.tsx";
import SelectIdLibelleData from "../components/Form/SelectIdLibelleData.tsx";
import NOMENCLATURES, {
  NOMENCLATURE_ORGANISME,
} from "../enums/NomenclaturesEnum.tsx";
import url from "../module/fetch.tsx";

function getColumnByStringArray(
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
          accessor: "libelle",
          sortField: "libelle",
          Filter: (
            <SelectNomenclatures
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
            <SelectIdLibelleData
              name={"autoriteDeci"}
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
            <SelectNomenclatures
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
            <SelectIdLibelleData
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
            <SelectIdLibelleData
              name={"servicePublicDeci"}
              url={
                url`/api/organisme/get-list-` +
                NOMENCLATURE_ORGANISME.SERVICE_PUBLIC_DECI
              }
            />
          ),
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
  return column;
}

export default getColumnByStringArray;
