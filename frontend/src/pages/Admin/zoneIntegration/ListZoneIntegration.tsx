import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import { FileInput, FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconInfo, IconList } from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import FilterValues from "./FilterZoneIntegration.tsx";

export const getInitialValues = () => ({
  fileGeometries: null,
});

export const validationSchema = object({});
export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append("fileGeometries", values.fileGeometries);
  return formData;
};

const ListZoneIntegration = () => {
  const { user } = useAppContext();

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.ADMIN_ZONE_COMPETENCE)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      route: (zoneIntegrationId) =>
        URLS.UPDATE_ZONE_INTEGRATION(zoneIntegrationId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row: any) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      pathname: url`/api/zone-integration/delete/`,
    });
  }

  const { handleShowClose, activesKeys } = useAccordionState([false]);

  return (
    <>
      <Container>
        <PageTitle icon={<IconList />} title={"Zones de compétence"} />

        <AccordionCustom
          activesKeys={activesKeys}
          handleShowClose={handleShowClose}
          list={[
            {
              header: "Importer des zones de compétence",
              content: (
                <>
                  <p>
                    Permet d&apos;importer un fichier SHAPE contenant des zones
                    de compétence ; l&apos;identification se fait sur le code,
                    avec un mécanisme d&apos;insertion / mise à jour.
                  </p>
                  <MyFormik
                    initialValues={getInitialValues()}
                    validationSchema={validationSchema}
                    isPost={false}
                    isMultipartFormData={true}
                    submitUrl={`/api/zone-integration/import/`}
                    prepareVariables={(values) => prepareVariables(values)}
                    redirectUrl={URLS.LIST_ZONE_INTEGRATION}
                  >
                    <FormImportShape />
                  </MyFormik>
                </>
              ),
            },
          ]}
        />
        <br />

        <QueryTable
          query={url`/api/zone-integration`}
          columns={[
            {
              Header: "Code",
              accessor: "zoneIntegrationCode",
              sortField: "zoneIntegrationCode",
              Filter: <FilterInput type="text" name="zoneIntegrationCode" />,
            },
            {
              Header: "Libellé",
              accessor: "zoneIntegrationLibelle",
              sortField: "zoneIntegrationLibelle",
              Filter: <FilterInput type="text" name="zoneIntegrationLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "zoneIntegrationActif",
              sortField: "zoneIntegrationActif",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"zoneIntegrationActif"}
                />
              ),
            }),

            ActionColumn({
              Header: "Actions",
              accessor: "zoneIntegrationId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableZoneIntegration"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({
            zoneIntegrationCode: undefined,
            zoneIntegrationLibelle: undefined,
            zoneIntegrationActif: undefined,
            zoneIntegrationGestionnaireId: undefined,
          })}
        />
      </Container>
    </>
  );
};

const FormImportShape = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="fileGeometries"
        accept=".zip"
        label={
          <>
            Fichier zip contenant les zones de compétence
            <TooltipCustom
              placement="right"
              tooltipText={
                <>
                  <Row>
                    <strong>
                      the_geom <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">La géométrie en Polygon</div>
                  </Row>
                  <Row>
                    <strong>
                      code <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">
                      Code (unique) de la zone de compétence
                    </div>
                  </Row>
                  <Row>
                    <strong>
                      libelle <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">Libellé de la zone de compétence</div>
                  </Row>
                  <br />
                  <Row>
                    <div>
                      <span className="text-danger">*</span> : Champs
                      obligatoires
                    </div>
                  </Row>
                </>
              }
              tooltipHeader="Colonnes attendues"
              tooltipId={"zoneIntegration"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
        required={false}
        onChange={(e) => setFieldValue("fileGeometries", e.target.files[0])}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Valider
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

export default ListZoneIntegration;
