import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
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
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import FilterValues from "./FilterSite.tsx";

export const getInitialValues = () => ({
  fileSites: null,
});

export const validationSchema = object({});
export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append("fileSites", values.fileSites);
  return formData;
};

const ListSite = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const { data } = useGet(url`/api/gestionnaire/get`);

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.GEST_SITE_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (siteId) => URLS.UPDATE_SITE(siteId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      path: url`/api/site/delete/`,
    });
  }

  const { handleShowClose, activesKeys } = useAccordionState([false]);

  return (
    <>
      <Container>
        <PageTitle icon={<IconList />} title={"Liste des sites"} />

        <AccordionCustom
          activesKeys={activesKeys}
          handleShowClose={handleShowClose}
          list={[
            {
              header: "Importer des sites ",
              content: (
                <>
                  <p>
                    Permet d&apos;importer un fichier SHAPE contenant des sites
                    ; l&apos;identification se fait sur le code, avec un
                    mécanisme d&apos;insertion / mise à jour.
                  </p>
                  <MyFormik
                    initialValues={getInitialValues()}
                    validationSchema={validationSchema}
                    isPost={false}
                    isMultipartFormData={true}
                    submitUrl={`/api/site/import/`}
                    prepareVariables={(values) => prepareVariables(values)}
                    redirectUrl={URLS.LIST_SITE}
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
          query={url`/api/site`}
          columns={[
            {
              Header: "Code",
              accessor: "siteCode",
              sortField: "siteCode",
              Filter: <FilterInput type="text" name="siteCode" />,
            },
            {
              Header: "Libellé",
              accessor: "siteLibelle",
              sortField: "siteLibelle",
              Filter: <FilterInput type="text" name="siteLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "siteActif",
              sortField: "siteActif",
              Filter: (
                <SelectEnumOption options={VRAI_FAUX} name={"siteActif"} />
              ),
            }),

            {
              Header: "Gestionnaire",
              accessor: "gestionnaireLibelle",
              sortField: "gestionnaireLibelle",
              Filter: (
                <SelectFilterFromList
                  name={"siteGestionnaireId"}
                  listIdCodeLibelle={data}
                />
              ),
            },
            ActionColumn({
              Header: "Actions",
              accessor: "siteId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableSite"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({
            siteCode: undefined,
            siteLibelle: undefined,
            siteActif: undefined,
            siteGestionnaireId: undefined,
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
        name="fileSites"
        accept=".zip"
        label={
          <>
            Fichier zip contenant les sites
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
                    <div className="ms-2">Code (unique) du site</div>
                  </Row>
                  <Row>
                    <strong>
                      libelle <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">Libellé du site</div>
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
              tooltipId={"site"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
        required={false}
        onChange={(e) => setFieldValue("fileSites", e.target.files[0])}
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

export default ListSite;
