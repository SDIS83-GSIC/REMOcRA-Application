import { useFormikContext } from "formik";
import { Button, Col, Image, Row } from "react-bootstrap";
import { object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FileInput,
  FormContainer,
  Multiselect,
  SelectInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconDelete } from "../../../components/Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../../components/ModuleRemocra/ModuleRemocra.tsx";
import SOURCE_CARTO from "../../../enums/SourceCartoEnum.tsx";
import url from "../../../module/fetch.tsx";
import { requiredString } from "../../../module/validators.tsx";

type CoucheType = {
  coucheProtected: boolean;
  coucheId?: string;
  coucheCode: string;
  coucheLibelle: string;
  coucheSource: SOURCE_CARTO;
  coucheProjection: string;
  coucheUrl: string;
  coucheNom: string;
  coucheFormat: string;
  coucheCrossOrigin: string;
  couchePublic: boolean;
  coucheActive: boolean;
  coucheProxy: boolean;
  coucheTuilage: boolean;
  coucheIconeUrl?: string;
  coucheLegendeUrl?: string;
  icone?: File;
  legende?: File;
  groupeFonctionnalitesZcList?: string[];
  groupeFonctionnalitesHorsZcList?: string[];
  moduleList?: string[];
};

export const getInitialValues = (
  groupeCoucheId: string,
  data?: CoucheType,
) => ({
  coucheProtected: data?.coucheProtected ?? false,
  coucheId: data?.coucheId,
  coucheCode: data?.coucheCode,
  coucheLibelle: data?.coucheLibelle,
  coucheSource: data?.coucheSource,
  coucheProjection: data?.coucheProjection,
  coucheUrl: data?.coucheUrl,
  coucheNom: data?.coucheNom,
  coucheFormat: data?.coucheFormat,
  coucheCrossOrigin: data?.coucheCrossOrigin,
  couchePublic: data?.couchePublic ?? false,
  coucheActive: data?.coucheActive ?? true,
  coucheProxy: data?.coucheProxy ?? false,
  coucheTuilage: data?.coucheTuilage ?? false,
  groupeFonctionnalitesZcList: data?.groupeFonctionnalitesZcList || [],
  groupeFonctionnalitesHorsZcList: data?.groupeFonctionnalitesHorsZcList || [],
  moduleList: data?.moduleList || [],
  groupeCoucheId: groupeCoucheId,
  coucheIconeUrl: data?.coucheIconeUrl,
  coucheLegendeUrl: data?.coucheLegendeUrl,
});

export const prepareValues = (
  couche: CoucheType & { groupeCoucheId: string },
) => {
  const formData = new FormData();
  formData.append(
    "couche",
    JSON.stringify({
      coucheId: couche.coucheId,
      coucheCode: couche.coucheCode,
      coucheLibelle: couche.coucheLibelle,
      coucheSource: couche.coucheSource,
      coucheProjection: couche.coucheProjection,
      coucheUrl: couche.coucheUrl,
      coucheNom: couche.coucheNom,
      coucheFormat: couche.coucheFormat,
      coucheCrossOrigin: couche.coucheCrossOrigin,
      couchePublic: couche.couchePublic,
      coucheActive: couche.coucheActive,
      coucheProxy: couche.coucheProxy,
      coucheTuilage: couche.coucheTuilage,
      coucheIconeUrl: couche.coucheIconeUrl,
      coucheLegendeUrl: couche.coucheLegendeUrl,
      groupeFonctionnalitesHorsZcList: couche.groupeFonctionnalitesHorsZcList,
      groupeFonctionnalitesZcList: couche.groupeFonctionnalitesZcList,
      moduleList: couche.moduleList,
      groupeCoucheId: couche.groupeCoucheId,
    }),
  );

  formData.append("icone", couche.icone!);
  formData.append("legende", couche.legende!);

  return formData;
};

export const validationSchema = object({
  coucheCode: requiredString,
  coucheLibelle: requiredString,
  groupeCoucheId: requiredString,
});

const Couche = () => {
  const { values, setFieldValue }: any = useFormikContext<CoucheType>();
  const moduleList = Object.entries(TypeModuleRemocra).map(([key, value]) => {
    return {
      id: key,
      libelle: value,
    };
  });
  const typeSourceCarto = Object.values(SOURCE_CARTO).map((key) => {
    return {
      id: key,
      code: key,
      libelle: key,
    };
  });

  const groupeFonctionnalitesState = useGet(url`/api/groupe-fonctionnalites`);
  if (!groupeFonctionnalitesState.isResolved) {
    return null;
  }

  return (
    <FormContainer>
      <Row>
        {/* Section Général */}
        <Col xs={12} className="p-2 bg-secondary mb-3">
          <h4 className={"m-0 p-0 text-center h6"}>Général</h4>
        </Col>
        <Col xs={12} lg={6}>
          <TextInput
            name={`coucheCode`}
            disabled={values.coucheProtected}
            value={values.coucheCode}
            label={"Code"}
            placeholder={"Code"}
            required={true}
          />
        </Col>
        <Col xs={12} lg={6}>
          <TextInput
            name={`coucheLibelle`}
            value={values.coucheLibelle}
            label={"Libellé"}
            placeholder={"Libellé"}
            required={true}
          />
        </Col>

        {/* Section Flux */}
        <Col xs={12} className="p-2 bg-secondary mb-3 mt-3">
          <h4 className={"m-0 p-0 text-center h6"}>Flux</h4>
        </Col>
        <Col xs={12} lg={6}>
          <SelectInput
            name={`coucheSource`}
            disabled={values.coucheProtected}
            label="Source"
            options={typeSourceCarto}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            onChange={(e) => {
              setFieldValue(
                `coucheSource`,
                typeSourceCarto.find((type) => type.id === e.id)?.id,
              );
            }}
            defaultValue={typeSourceCarto.find(
              (type) => type.id === values.coucheSource,
            )}
            required={true}
          />
        </Col>
        <Col xs={12} lg={6}>
          <TextInput
            name={`coucheUrl`}
            disabled={values.coucheProtected}
            value={values.coucheUrl}
            label={"URL"}
            placeholder={"URL"}
            required={true}
          />
        </Col>
        {values.coucheSource !== SOURCE_CARTO.OSM &&
          !values.coucheProtected && (
            <>
              <Col xs={12} lg={6}>
                <TextInput
                  name={`coucheNom`}
                  value={values.coucheNom}
                  label={"Nom"}
                  placeholder={"Nom"}
                  required={true}
                />
              </Col>
              <Col xs={12} lg={6}>
                <TextInput
                  name={`coucheProjection`}
                  value={values.coucheProjection}
                  label={"Projection"}
                  placeholder={"Projection"}
                  required={true}
                />
              </Col>
              <Col xs={12} lg={6}>
                <TextInput
                  name={`coucheFormat`}
                  value={values.coucheFormat}
                  label={"Format"}
                  placeholder={"Format"}
                  required={true}
                />
              </Col>
            </>
          )}
        {(values.coucheSource === SOURCE_CARTO.WMTS ||
          values.coucheSource === SOURCE_CARTO.OSM) && (
          <Col xs={12} lg={6}>
            <TextInput
              name={`coucheCrossOrigin`}
              value={values.coucheCrossOrigin}
              label={"CrossOrigin"}
              placeholder={"CrossOrigin"}
              required={false}
            />
          </Col>
        )}

        {/* Section Paramètres */}
        <Col xs={12} className="p-2 bg-secondary mb-3 mt-3">
          <h4 className={"m-0 p-0 text-center h6"}>Paramètres</h4>
        </Col>
        <Col xs={12} lg={6}>
          <CheckBoxInput name={`couchePublic`} label={"Publique"} />
        </Col>
        <Col xs={12} lg={6}>
          <CheckBoxInput name={`coucheActive`} label={"Active par défaut"} />
        </Col>
        <Col xs={12} lg={6}>
          <CheckBoxInput
            name={`coucheProxy`}
            label={"Utiliser le proxy pour charger la couche"}
            tooltipText="Si la case est décochée, la ressource sera chargée directement sans passer par le proxy de l'application. Utile pour l'affichage des fonds de plan."
          />
        </Col>
        {values.coucheSource === SOURCE_CARTO.WMS && (
          <Col xs={12} lg={6}>
            <CheckBoxInput
              name={`coucheTuilage`}
              label={"Utiliser le tuilage WMS"}
              tooltipText="Si la case est cochée, la couche sera chargée sous forme de tuiles. Attention, Geoserver ne peut pas gérer convenablement l'affichage des attributs calculés dans une couche tuilée (un libellé affiché au centroïde, une couleur de remplissage aléatoire, ...), donc pour toute couche tuilée il conviendra de vérifier son style associé."
            />
          </Col>
        )}

        {/* Section Autorisations */}
        <Col xs={12} className="p-2 bg-secondary mb-3 mt-3">
          <h4 className={"m-0 p-0 text-center h6"}>Autorisations</h4>
        </Col>
        <Col xs={12}>
          <Multiselect
            name={"moduleList"}
            label="Modules"
            options={moduleList}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            value={moduleList.filter((el) =>
              values.moduleList?.some((value) => value === el.id),
            )}
            onChange={(value) => {
              setFieldValue(
                `moduleList`,
                value.map((el) => el.id),
              );
            }}
            isClearable={true}
            required={false}
          />
        </Col>
        <Col xs={12} lg={6}>
          <Multiselect
            name={"groupeFonctionnalitesZcList"}
            label="Profils autorisés sur zone de compétence"
            tooltipText="Profils pour lesquels la couche est visible uniquement sur la zone de compétence de l'utilisateur connecté"
            options={groupeFonctionnalitesState.data.filter(
              (el) =>
                !values.groupeFonctionnalitesHorsZcList?.some(
                  (value) => value === el.id,
                ),
            )}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            value={groupeFonctionnalitesState.data.filter((el) =>
              values.groupeFonctionnalitesZcList?.some(
                (value) => value === el.id,
              ),
            )}
            onChange={(value) => {
              setFieldValue(
                `groupeFonctionnalitesZcList`,
                value.map((el) => el.id),
              );
            }}
            isClearable={true}
            required={false}
          />
        </Col>
        <Col xs={12} lg={6}>
          <Multiselect
            name={"groupeFonctionnalitesHorsZcList"}
            label="Profils autorisés hors zone de compétence"
            tooltipText="Profils pour lesquels la couche est complètement visible, sans prendre en compte la zone de compétence de l'utilisateur connecté"
            options={groupeFonctionnalitesState.data.filter(
              (el) =>
                !values.groupeFonctionnalitesZcList?.some(
                  (value) => value === el.id,
                ),
            )}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            value={groupeFonctionnalitesState.data.filter((el) =>
              values.groupeFonctionnalitesHorsZcList?.some(
                (value) => value === el.id,
              ),
            )}
            onChange={(value) => {
              setFieldValue(
                `groupeFonctionnalitesHorsZcList`,
                value.map((el) => el.id),
              );
            }}
            isClearable={true}
            required={false}
          />
        </Col>

        {/* Section Images */}
        <Col xs={12} className="p-2 bg-secondary mb-3 mt-3">
          <h4 className={"m-0 p-0 text-center h6"}>Images</h4>
        </Col>
        <Col xs={12} lg={6}>
          <FileInput
            name={`icone`}
            accept="image/*"
            label="Icône"
            required={false}
            onChange={(e) => {
              setFieldValue(`icone`, e.target.files[0]);
              setFieldValue(`coucheIconeUrl`, null);
            }}
          />
          {values.coucheIconeUrl && (
            <div className="mt-3 d-flex align-items-center gap-3">
              <Image
                thumbnail={true}
                src={values.coucheIconeUrl}
                style={{ maxWidth: "100px" }}
              />
              <Button
                variant="link"
                className={"text-danger text-decoration-none p-0"}
                onClick={() => {
                  setFieldValue(`icone`, null);
                  setFieldValue(`coucheIconeUrl`, null);
                }}
              >
                <IconDelete /> Supprimer l&apos;icône
              </Button>
            </div>
          )}
        </Col>
        <Col xs={12} lg={6}>
          <FileInput
            name={`legende`}
            accept="image/*"
            label="Légende"
            required={false}
            onChange={(e) => {
              setFieldValue(`legende`, e.target.files[0]);
              setFieldValue(`coucheLegendeUrl`, null);
            }}
          />
          {values.coucheLegendeUrl && (
            <div className="mt-3">
              <Image
                thumbnail={true}
                src={values.coucheLegendeUrl}
                style={{ maxWidth: "300px" }}
              />
              <Button
                variant="link"
                className={"text-danger text-decoration-none d-block mt-2 p-0"}
                onClick={() => {
                  setFieldValue(`legende`, null);
                  setFieldValue(`coucheLegendeUrl`, null);
                }}
              >
                <IconDelete /> Supprimer la légende
              </Button>
            </div>
          )}
        </Col>
      </Row>
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default Couche;
