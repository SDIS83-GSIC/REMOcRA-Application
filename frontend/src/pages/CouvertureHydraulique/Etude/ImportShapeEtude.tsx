import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { object } from "yup";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { FileInput, FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconImport, IconInfo } from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import { URLS } from "../../../routes.tsx";

export const getInitialValues = () => ({
  fileReseau: null,
  fileBatiment: null,
  filePeiProjet: null,
});

export const validationSchema = object({});
export const prepareVariables = (values: {
  fileReseau: Blob | null;
  fileBatiment: Blob | null;
  filePeiProjet: Blob | null;
}) => {
  const formData = new FormData();
  if (values.fileReseau) {
    formData.append("fileReseau", values.fileReseau);
  }
  if (values.fileBatiment) {
    formData.append("fileBatiment", values.fileBatiment);
  }
  if (values.filePeiProjet) {
    formData.append("filePeiProjet", values.filePeiProjet);
  }
  return formData;
};

const ImportShapeEtude = () => {
  const { etudeId } = useParams();

  return (
    <Container>
      <PageTitle
        icon={<IconImport />}
        title={"Importer des fichiers shape pour l'étude"}
      />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={true}
        submitUrl={`/api/couverture-hydraulique/import/` + etudeId}
        prepareVariables={(values) => prepareVariables(values)}
        // TODO redirect vers la carte
        redirectUrl={URLS.LIST_ETUDE}
        onSubmit={() => {}}
      >
        <FormImportShape />
      </MyFormik>
    </Container>
  );
};

const FormImportShape = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="fileReseau"
        accept=".zip"
        label={
          <>
            Fichier zip contenant le réseau routier
            <TooltipCustom
              placement="right"
              tooltipText={
                <>
                  <Row>
                    <strong>
                      the_geom <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">
                      La géométrie en LineString en projection EPSG:2154 (RGF93
                      / Lambert-93)
                    </div>
                  </Row>
                  <Row>
                    <strong>
                      traversabl <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">La voie est-elle traversable ?</div>
                  </Row>
                  <Row>
                    <strong>
                      sensUniqu <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">
                      La voie est-elle en sens unique ?
                    </div>
                  </Row>
                  <Row>
                    <strong>niveau</strong>
                    <div className="ms-2">Le niveau de la voie, un entier.</div>
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
              tooltipId={"reseau"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
        required={false}
        onChange={(e) => setFieldValue("fileReseau", e.target.files[0])}
      />
      <br />
      <FileInput
        name="fileBatiment"
        accept=".zip"
        label={
          <>
            Fichier zip contenant les bâtiments
            <TooltipCustom
              placement="right"
              tooltipText={
                <>
                  <Row>
                    <strong>
                      the_geom <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">
                      La géométrie du bâtiment en projection EPSG:2154 (RGF93 /
                      Lambert-93)
                    </div>
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
              tooltipId={"batiment"}
              tooltipHeader="Colonnes attendues"
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
        required={false}
        onChange={(e) => setFieldValue("fileBatiment", e.target.files[0])}
      />
      <br />
      <FileInput
        name="filePeiProjet"
        accept=".zip"
        label={
          <>
            Fichier zip contenant les PEI en projet
            <TooltipCustom
              placement="right"
              tooltipText={
                <>
                  <Row>
                    <strong>
                      the_geom <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">
                      La géométrie en Point en projection EPSG:2154 (RGF93 /
                      Lambert-93)
                    </div>
                  </Row>
                  <Row>
                    <strong>
                      natureDeci <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">
                      Le code de la nature DECI présent en base dans la table
                      remocra.nature_deci (PUBLIC, PRIVE, PRIVE_SOUS_CONVENTION
                      ...)
                    </div>
                  </Row>
                  <Row>
                    <strong>
                      type <span className="text-danger">*</span>
                    </strong>
                    <div className="ms-2">
                      {" "}
                      Le type de PEI en projet. Doit être égal à &quot;PA&quot;,
                      &quot;PIBI&quot; ou &quot;RESERVE&quot;.
                    </div>
                  </Row>
                  <Row>
                    <strong>diametreNo</strong>
                    <div className="ms-2">
                      Le code de diamètre nominal présent en base dans
                      remocra.diametre. A renseigner s&apos;il s&apos;agit
                      d&apos;un PIBI.
                    </div>
                  </Row>
                  <Row>
                    <strong>diametreCa</strong>
                    <div className="ms-2">
                      Le diamètre de canalisation, un entier. A renseigner
                      s&apos;il s&apos;agit d&apos;un PIBI.
                    </div>
                  </Row>
                  <Row>
                    <strong>capacite</strong>
                    <div className="ms-2">
                      La capacité en entier. A renseigner s&apos;il s&apos;agit
                      d&apos;une RESERVE.
                    </div>
                  </Row>
                  <Row>
                    <strong>debit</strong>
                    <div className="ms-2">
                      La débit en entier. A renseigner s&apos;il s&apos;agit
                      d&apos;une RESERVE ou d&apos;un PA.
                    </div>
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
              tooltipId={"peiProjet"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
        required={false}
        onChange={(e) => setFieldValue("filePeiProjet", e.target.files[0])}
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

export default ImportShapeEtude;
