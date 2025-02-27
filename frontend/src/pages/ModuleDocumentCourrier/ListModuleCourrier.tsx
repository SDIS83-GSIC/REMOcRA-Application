import { Badge, Col, Container, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import React from "react";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import Header from "../../components/Header/Header.tsx";
import {
  IconAccuse,
  IconExport,
  IconList,
} from "../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../components/Table/TableActionColumn.tsx";
import url from "../../module/fetch.tsx";
import SquelettePage from "../SquelettePage.tsx";
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";
import SelectEnumOption from "../../components/Form/SelectEnumOption.tsx";
import VRAI_FAUX from "../../enums/VraiFauxEnum.tsx";
import MultiSelectFilterFromList from "../../components/Filter/MultiSelectFilterFromList.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import FilterValues from "./FilterModuleCourrier.tsx";

const ListModuleCourrier = () => {
  const { moduleId } = useParams();
  const listeIdMailUtilisateur = useGet(url`/api/utilisateur/get-id-mail`);
  return (
    <SquelettePage navbar={<Header />}>
      <Container>
        <PageTitle icon={<IconList />} title={"Liste des courriers"} />
        <QueryTable
          query={url`/api/modules/courriers/all?${{
            moduleId: moduleId,
          }}`}
          columns={[
            {
              Header: "Objet",
              accessor: "courrierObjet",
              sortField: "courrierObjet",
              Filter: <FilterInput type="text" name="courrierObjet" />,
            },
            {
              Header: "Référence",
              accessor: "courrierReference",
              sortField: "courrierReference",
              Filter: <FilterInput type="text" name="courrierReference" />,
            },
            {
              Header: "Destinataires",
              accessor: "emailDestinataire",
              sortField: "emailDestinataire",
              Filter: (
                <MultiSelectFilterFromList
                  name={"emailDestinataire"}
                  listIdCodeLibelle={listeIdMailUtilisateur?.data?.map((e) => ({
                    id: e.utilisateurId,
                    code: e.utilisateurEmail,
                    libelle: e.utilisateurEmail,
                  }))}
                />
              ),
              width: 300,
              Cell: (value) => {
                return value.value.map((v, key) => {
                  return (
                    <Row key={key}>
                      <Col className={"mt-2"}>{v.email}</Col>
                    </Row>
                  );
                });
              },
            },
            {
              Header: "Accuse",
              accessor: "emailDestinataire",
              Filter: <SelectEnumOption options={VRAI_FAUX} name={"accuse"} />,
              Cell: (value) => {
                return value.value.map((v, key) => {
                  return (
                    <Row className={"align-items-center"} key={key}>
                      <Col className="text-center mt-2" xs={12}>
                        <TooltipCustom
                          tooltipText={
                            v.accuse ? "Courrier ouvert" : "Courrier non-ouvert"
                          }
                          tooltipId={value}
                        >
                          <Badge bg={v.accuse ? "success" : "danger"} pill>
                            <IconAccuse />
                          </Badge>
                        </TooltipCustom>
                      </Col>
                    </Row>
                  );
                });
              },
            },

            {
              Header: "Expiditeur",
              accessor: "courrierExpediteur",
              sortField: "courrierExpediteur",
              Filter: <FilterInput type="text" name="courrierExpediteur" />,
            },
            ActionColumn({
              Header: "Actions",
              accessor: "courrierDocumentId",
              buttons: [
                {
                  row: (row) => {
                    return row;
                  },
                  route: (id) => url`/api/documents/telecharger/` + id,
                  type: TYPE_BUTTON.BUTTON,
                  icon: <IconExport />,
                  textEnable: "Télécharger le courrier",
                  classEnable: "warning",
                },
              ],
              width: 90,
            }),
          ]}
          idName={"tableModuleCourrier"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({})}
        />
      </Container>
    </SquelettePage>
  );
};

export default ListModuleCourrier;
