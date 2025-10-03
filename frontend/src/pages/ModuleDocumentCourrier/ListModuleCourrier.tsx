import { Badge, Col, Container, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../components/Form/SelectEnumOption.tsx";
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
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";
import { INTERVALLE_DATE_PROCHE_ENUM } from "../../enums/ProchaineDateEnum.tsx";
import VRAI_FAUX from "../../enums/VraiFauxEnum.tsx";
import url from "../../module/fetch.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import SquelettePage from "../SquelettePage.tsx";
import FilterValues from "./FilterModuleCourrier.tsx";

const ListModuleCourrier = () => {
  const { moduleId } = useParams();
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
              Header: "Date d'envoi",
              accessor: "documentDate",
              sortField: "documentDate",
              Filter: (
                <SelectEnumOption
                  options={INTERVALLE_DATE_PROCHE_ENUM}
                  name={"documentDate"}
                />
              ),
              Cell: (value) => {
                return (
                  <div>{value.value ? formatDateTime(value.value) : ""}</div>
                );
              },
            },
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
              Header: "Expéditeur",
              accessor: "courrierExpediteur",
              sortField: "courrierExpediteur",
              Filter: <FilterInput type="text" name="courrierExpediteur" />,
            },
            {
              Header: "Destinataires",
              accessor: "emailDestinataire",
              Filter: <FilterInput type="text" name="emailDestinataire" />,
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
              Header: "Accusé",
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
