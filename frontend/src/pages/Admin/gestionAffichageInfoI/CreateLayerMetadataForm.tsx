import { object } from "yup";
import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import {
  CheckBoxInput,
  FormContainer,
  Multiselect,
  TextAreaInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { useGet, useGetRun } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import { requiredArray, requiredString } from "../../../module/validators.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import { IconInfo } from "../../../components/Icon/Icon.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";

export const getInitialValues = (styleId?: string, data?: any) => ({
  groupLayerId: data?.groupLayerId ?? null,
  layerId: data?.layerId ?? null,
  layerProfilId: data?.layerProfilId ?? null,
  layerStyleFlag: data?.layerStyleFlag ?? false,
  layerStylePublicAccess: data?.layerStylePublicAccess ?? false,
  layerStyleId: styleId ?? "",
  layerStyle:
    data?.layerStyle ??
    "[i]Les adresses sont mises à jour tous les jours à 01h00 [/i]\n[br]\n[b]Description : [/b] #adresse_description#\n[br]\n[b]adresse_date_modification : [/b] [u]#adresse_date_modification#[/u]",
});

export const prepareValues = (
  values: {
    groupLayerId: any;
    layerId: any;
    layerProfilId: any;
    layerStyle: any;
    layerStyleFlag: boolean;
    layerStylePublicAccess: boolean;
  },
  styleId?: string,
) => ({
  layerStyleId: styleId,
  groupLayerId: values.groupLayerId,
  layerId: values.layerId,
  layerProfilId: values.layerProfilId,
  layerStyle: values.layerStyle,
  layerStyleFlag: values.layerStyleFlag,
  layerStylePublicAccess: values.layerStylePublicAccess,
});

export const validationSchema = object({
  groupLayerId: requiredString,
  layerId: requiredString,
  layerStyle: requiredString,
  layerProfilId: requiredArray,
});

const CreateLayerMetadataForm = ({ initalLayer }: { initalLayer?: string }) => {
  const [coucheId, setCoucheId] = useState<string | null>(initalLayer);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const { handleShowClose, activesKeys } = useAccordionState([false, false]);

  async function getError(errorPending: any) {
    return await errorPending?.text();
  }

  const queryParam = initalLayer !== undefined ? "" : "?excludeExisting=true";
  const layerData = useGet(
    url`/api/admin/couche/get-available-layers${queryParam}`,
  )?.data?.list;

  const { setValues, setFieldValue, values } = useFormikContext<{
    groupLayerId: any;
    layerId: any;
    layerProfilId: any;
    layerStyle: any;
    layerStyleFlag: boolean;
    layerStylePublicAccess: boolean;
  }>();
  const {
    run: fetchOption,
    data: describeFeatureType,
    ...dataLayer
  } = useGetRun(`/api/geoserver/describe-feature-type/${coucheId!}`, {});

  useEffect(() => {
    if (dataLayer?.error) {
      getError(dataLayer?.error).then((error) => {
        setErrorMessage(error);
      });
    } else {
      setErrorMessage(null);
    }
  }, [dataLayer?.error]);

  useEffect(() => {
    if (coucheId) {
      fetchOption();
    }
  }, [coucheId, fetchOption]);

  if (!layerData) {
    return <Loading />;
  }

  // Liste des groupes de couches
  const listGroupLayers = layerData.map(
    (groupLayer: {
      groupeCoucheId: any;
      groupeCoucheCode: any;
      groupeCoucheLibelle: any;
      coucheList: any;
    }) => ({
      id: groupLayer.groupeCoucheId,
      code: groupLayer.groupeCoucheCode,
      libelle: groupLayer.groupeCoucheLibelle,
      listeCouches: groupLayer.coucheList,
    }),
  );

  // On récupère le groupe de couche sélectionné directement depuis values (FormData)
  const selectedGroupLayer = listGroupLayers.find(
    (groupLayer: { id: any }) => groupLayer.id === values.groupLayerId,
  );

  // Liste des couches en fonction du groupe sélectionné
  let listLayers = [];
  if (selectedGroupLayer) {
    listLayers = selectedGroupLayer.listeCouches.map(
      (layer: {
        coucheId: any;
        coucheCode: any;
        coucheLibelle: any;
        groupeFonctionnaliteList: any;
      }) => ({
        id: layer.coucheId,
        code: layer.coucheCode,
        libelle: layer.coucheLibelle,
        groupeFonctionnaliteList: layer.groupeFonctionnaliteList,
      }),
    );
  }

  // On récupère la couche sélectionnée (valeurs du formulaire)
  const selectedLayer = selectedGroupLayer?.listeCouches.find(
    (layer: { coucheId: any }) => layer.coucheId === values.layerId,
  );

  // Liste des profils de droits en fonction de la couche sélectionnée
  let listProfilsDroits = [];
  if (selectedLayer) {
    listProfilsDroits = selectedLayer.groupeFonctionnaliteList.map(
      (profil: {
        groupeFonctionnaliteId: any;
        groupeFonctionnaliteCode: any;
        groupeFonctionnaliteLibelle: any;
      }) => ({
        id: profil.groupeFonctionnaliteId,
        code: profil.groupeFonctionnaliteCode,
        libelle: profil.groupeFonctionnaliteLibelle,
      }),
    );

    // Tri de la liste des profils droits par libelle
    listProfilsDroits = listProfilsDroits.sort(
      (a: { libelle: string }, b: { libelle: string }) =>
        a.libelle.localeCompare(b.libelle),
    );
  }

  const paramNotFound = "Aucun paramètre trouvé";
  const properties = describeFeatureType?.featureTypes?.[0].properties ?? [];
  const params = properties.length
    ? properties.map((p: { name: any }, idx: number) => (
        <span key={idx}>
          - {p.name}
          <br />
        </span>
      ))
    : null;

  return (
    <FormContainer>
      <AccordionCustom
        activesKeys={activesKeys}
        handleShowClose={handleShowClose}
        list={[
          {
            header: "Informations générales",
            content:
              "Seules les couches requêtant Geoserver sont accessibles dans la liste déroulante ci-dessous, donc sont exclus les types GeoJSON et OSM",
          },
          {
            header: "Balises disponibles pour la mise en forme",
            content: (
              <>
                <table>
                  <tbody>
                    <tr>
                      <td>
                        <b>[b]...[/b]</b>
                      </td>
                      <td>Texte en gras</td>
                    </tr>
                    <tr>
                      <td>
                        <b>[i]...[/i]</b>
                      </td>
                      <td>Texte en italique</td>
                    </tr>
                    <tr>
                      <td>
                        <b>[u]...[/u]</b>
                      </td>
                      <td>Texte souligné</td>
                    </tr>
                    <tr>
                      <td>
                        <b>[br]</b>
                      </td>
                      <td>Saut de ligne</td>
                    </tr>
                    <tr>
                      <td>
                        <b>#...#</b>
                      </td>
                      <td>Paramètre à insérer</td>
                    </tr>
                  </tbody>
                </table>
              </>
            ),
          },
        ]}
      />

      {/* group layer */}
      <SelectForm
        name={"groupLayerId"}
        listIdCodeLibelle={listGroupLayers}
        label="Groupe de couche"
        required={true}
        setValues={setValues}
        defaultValue={listGroupLayers.find(
          (e: any) => e.id === values?.groupLayerId,
        )}
        onChange={(value: any) => {
          setFieldValue("groupLayerId", value.id);
          setFieldValue("layerId", undefined);
        }}
      />

      <Container>
        {errorMessage && <div className="text-danger">{errorMessage}</div>}
      </Container>

      {/* layer */}
      <SelectForm
        name={"layerId"}
        listIdCodeLibelle={listLayers}
        label="Couche"
        required={true}
        setValues={setValues}
        defaultValue={listLayers.find((e: any) => e.id === values?.layerId)}
        onChange={(value: any) => {
          setFieldValue("layerId", value.id);
          setFieldValue("layerProfilId", undefined);
          setCoucheId(value.id);
        }}
      />

      <Multiselect
        name={"layerProfilId"}
        label="Groupes de fonctionnalités"
        options={listProfilsDroits}
        getOptionValue={(t) => t.id}
        value={
          values.layerProfilId?.map((e: any) =>
            listProfilsDroits.find((p: any) => p.id === e),
          ) ??
          listProfilsDroits.filter((e: { id: any }) =>
            values.layerProfilId?.includes(e.id),
          )
        }
        getOptionLabel={(t) => t.libelle}
        isClearable={true}
        onChange={(profilGroup) => {
          setFieldValue(
            "layerProfilId",
            profilGroup.map((e: any) => e.id),
          );
        }}
      />

      <CheckBoxInput name={"layerStyleFlag"} label={"Actif"} />
      <CheckBoxInput
        name={"layerStylePublicAccess"}
        label={"Autoriser l'accès public"}
        tooltipText="Si la case est cochée, les métadonnées seront accessibles publiquement, y compris par un utilisateur déconnecté."
      />

      <Row className="mt-3">
        <Col md={4} className="bg-light border p-2 rounded">
          <IconInfo /> Attributs affichables : (
          <i>
            ce sont les attributs fournis par Geoserver lors de
            l&apos;interrogation de la vue. Assurez-vous qu&apos;ils soient au
            plus proche de ce que vous voulez afficher en créant une vue
            Geoserver adaptée à vos besoins
          </i>
          )
          <br /> {params ?? paramNotFound}
        </Col>

        {params !== null && errorMessage === null && (
          <Col md={8}>
            <TextAreaInput
              rows={10}
              name="layerStyle"
              readOnly={params === null}
              label="Métadonnées à afficher"
              value={values.layerStyle}
            />
          </Col>
        )}
      </Row>

      <Row className="mt-3">
        <Col>
          <SubmitFormButtons
            disabledValide={params === null || errorMessage !== null}
            returnLink={true}
          />
        </Col>
      </Row>
    </FormContainer>
  );
};

export default CreateLayerMetadataForm;
