import React from "react";
import { Container } from "react-bootstrap";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import {
  IconDelete,
  IconEdit,
  IconOldeb,
} from "../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import SelectNomenclaturesFilter from "../../components/Filter/SelectNomenclaturesFilter.tsx";
import NOMENCLATURES from "../../enums/NomenclaturesEnum.tsx";
import { ActionColumn } from "../../components/Table/columns.tsx";
import { TYPE_BUTTON } from "../../components/Table/TableActionColumn.tsx";
import SelectFilterFromUrl from "../../components/Filter/SelectFilterFromUrl.tsx";
import filterValuesToVariable from "./OldebFilter.tsx";

const OldebList = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconOldeb />}
        title={"Liste des Obligations Légales de Débroussaillement"}
      />
      <QueryTable
        query={url`/api/oldeb`}
        columns={[
          {
            Header: "Commune",
            accessor: "oldebCommune",
            sortField: "oldebCommune",
            Filter: (
              <SelectFilterFromUrl
                name={"oldebCommune"}
                url={url`/api/commune/get-libelle-commune`}
              />
            ),
          },
          {
            Header: "Section",
            accessor: "oldebSection",
            sortField: "oldebSection",
            Filter: <FilterInput type="text" name="oldebSection" />,
          },
          {
            Header: "Parcelle",
            accessor: "oldebParcelle",
            sortField: "oldebParcelle",
            Filter: <FilterInput type="text" name="oldebParcelle" />,
          },
          {
            Header: "Adresse",
            accessor: "oldebAdresse",
          },
          {
            Header: "Type zone",
            accessor: "oldebTypeZoneUrbanisme",
            sortField: "oldebTypeZoneUrbanisme",
            Filter: (
              <SelectNomenclaturesFilter
                name={"oldebTypeZoneUrbanisme"}
                nomenclature={NOMENCLATURES.OLDEB_TYPE_ZONE_URBANISME}
              />
            ),
          },
          {
            Header: "Dernière visite",
            accessor: "oldebDateDerniereVisite",
            sortField: "oldebDateDerniereVisite",
          },
          {
            Header: "Débroussaillement",
            accessor: "oldebTypeDebroussaillement",
            sortField: "oldebTypeDebroussaillement",
            Filter: (
              <SelectNomenclaturesFilter
                name={"oldebTypeDebroussaillement"}
                nomenclature={NOMENCLATURES.OLDEB_TYPE_DEBROUSSAILLEMENT}
              />
            ),
          },
          {
            Header: "Avis",
            accessor: "oldebTypeAvis",
            sortField: "oldebTypeAvis",
            Filter: (
              <SelectNomenclaturesFilter
                name={"oldebTypeAvis"}
                nomenclature={NOMENCLATURES.OLDEB_TYPE_AVIS}
              />
            ),
          },
          ActionColumn({
            Header: "Actions",
            accessor: "oldebId",
            buttons: [
              {
                row: (row) => {
                  return row;
                },
                href: (oldebId) => URLS.OLDEB_UPDATE(oldebId),
                type: TYPE_BUTTON.UPDATE,
                icon: <IconEdit />,
              },
              {
                row: (row) => {
                  return row;
                },
                path: url`/api/oldeb/`,
                type: TYPE_BUTTON.DELETE,
                icon: <IconDelete />,
              },
            ],
          }),
        ]}
        idName={"oldebId"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({})}
      />
    </Container>
  );
};

export default OldebList;
