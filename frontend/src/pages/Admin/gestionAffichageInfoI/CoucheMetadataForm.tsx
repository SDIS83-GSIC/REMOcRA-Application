import { object } from "yup";
import { useFormikContext } from "formik";
import { useEffect, useMemo, useState } from "react";
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

function generateMetadataProperties(properties: any) {
  return properties
    .map((property: { name: string }) => {
      return `[b]${property.name} : [/b] #${property.name}#\n[br]`;
    })
    .join("");
}

export const getInitialValues = (coucheMetadataId?: string, data?: any) => ({
  groupeCoucheId: data?.groupeCoucheId ?? null,
  coucheId: data?.coucheId ?? null,
  coucheMetadataId: coucheMetadataId ?? "",
  coucheMetadataActif: data?.coucheMetadataActif ?? true,
  coucheMetadataPublic: data?.coucheMetadataPublic ?? false,
  coucheMetadataStyle: data?.coucheMetadataStyle ?? null,

  groupeFonctionnaliteIds: data?.groupeFonctionnaliteIds ?? null,
});

export const prepareValues = (
  values: {
    groupeCoucheId: any;
    coucheId: any;

    coucheMetadataActif: boolean;
    coucheMetadataPublic: boolean;
    coucheMetadataStyle: any;

    groupeFonctionnaliteIds: any;
  },
  coucheMetadataId?: string,
) => ({
  coucheMetadataId: coucheMetadataId,
  groupeCoucheId: values.groupeCoucheId,
  coucheId: values.coucheId,
  groupeFonctionnaliteIds: values.groupeFonctionnaliteIds,
  coucheMetadataStyle: values.coucheMetadataStyle,
  coucheMetadataActif: values.coucheMetadataActif,
  coucheMetadataPublic: values.coucheMetadataPublic,
});

export const validationSchema = object({
  groupeCoucheId: requiredString,
  coucheId: requiredString,

  coucheMetadataStyle: requiredString,

  groupeFonctionnaliteIds: requiredArray,
});

const CoucheMetadataForm = ({
  coucheInitiale,
}: {
  coucheInitiale?: string;
}) => {
  const [coucheId, setCoucheId] = useState<string | null>(coucheInitiale);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const { handleShowClose, activesKeys } = useAccordionState([false, false]);

  async function getError(errorPending: any) {
    return await errorPending?.text();
  }

  const { setValues, setFieldValue, values } = useFormikContext<{
    groupeCoucheId: any;
    coucheId: any;
    groupeFonctionnaliteIds: any;
    coucheMetadata: any;
    coucheMetadataFlag: boolean;
    coucheMetadataPublicAccess: boolean;
    coucheMetadataId: string | null;
  }>();
  const {
    run: fetchOption,
    data: describeFeatureType,
    ...dataLayer
  } = useGetRun(`/api/geoserver/describe-feature-type/${coucheId!}`, {});

  const queryParam =
    values.coucheMetadataId !== null && values.coucheMetadataId !== ""
      ? { coucheMetadataId: values.coucheMetadataId }
      : "";
  const coucheData = useGet(
    url`/api/admin/couche-metadata/get-available-layers?${queryParam}`,
  )?.data;

  const properties = useMemo(() => {
    return describeFeatureType?.featureTypes?.[0]?.properties ?? [];
  }, [describeFeatureType]);

  useEffect(() => {
    if (!coucheInitiale) {
      setFieldValue(
        "coucheMetadataStyle",
        generateMetadataProperties(properties),
      );
    }
  }, [setFieldValue, describeFeatureType, coucheInitiale, properties]);

  const handleLayerStyleChange = (event: any) => {
    if (event.target.value === "") {
      setFieldValue(
        "coucheMetadataStyle",
        generateMetadataProperties(properties),
      );
    } else {
      setFieldValue("coucheMetadataStyle", event.target.value);
    }
  };

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

  if (!coucheData) {
    return <Loading />;
  }

  // Liste des groupes de couches
  const listGroupeCouche = coucheData.map(
    (groupeCouche: {
      groupeCoucheId: any;
      groupeCoucheCode: any;
      groupeCoucheLibelle: any;
      coucheList: any;
    }) => ({
      id: groupeCouche.groupeCoucheId,
      code: groupeCouche.groupeCoucheCode,
      libelle: groupeCouche.groupeCoucheLibelle,
      listeCouches: groupeCouche.coucheList,
    }),
  );

  // On récupère le groupe de couche sélectionné directement depuis values (FormData)
  const selectedGroupeCouche = listGroupeCouche.find(
    (groupeCouche: { id: any }) => groupeCouche.id === values.groupeCoucheId,
  );

  // Liste des couches en fonction du groupe sélectionné
  let listCouches = [];
  if (selectedGroupeCouche) {
    listCouches = selectedGroupeCouche.listeCouches.map(
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
  const selectedCouche = selectedGroupeCouche?.listeCouches.find(
    (couche: { coucheId: any }) => couche.coucheId === values.coucheId,
  );

  // Liste des groupes de fonctionnalités en fonction de la couche sélectionnée
  let listGroupeFonctionnalites = [];
  if (selectedCouche) {
    listGroupeFonctionnalites = selectedCouche.groupeFonctionnaliteList.map(
      (groupeFonctionnalite: {
        groupeFonctionnaliteId: any;
        groupeFonctionnaliteCode: any;
        groupeFonctionnaliteLibelle: any;
      }) => ({
        id: groupeFonctionnalite.groupeFonctionnaliteId,
        code: groupeFonctionnalite.groupeFonctionnaliteCode,
        libelle: groupeFonctionnalite.groupeFonctionnaliteLibelle,
      }),
    );

    // Tri de la liste des profils droits par libelle
    listGroupeFonctionnalites = listGroupeFonctionnalites.sort(
      (a: { libelle: string }, b: { libelle: string }) =>
        a.libelle.localeCompare(b.libelle),
    );
  }

  const paramNotFound = "Aucun paramètre trouvé";
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

      <SelectForm
        name={"groupeCoucheId"}
        listIdCodeLibelle={listGroupeCouche}
        label="Groupe de couche"
        required={true}
        setValues={setValues}
        defaultValue={listGroupeCouche.find(
          (e: any) => e.id === values?.groupeCoucheId,
        )}
        onChange={(value: any) => {
          setFieldValue("groupeCoucheId", value?.id);
          setFieldValue("coucheId", undefined);
        }}
      />

      <Container>
        {errorMessage && <div className="text-danger">{errorMessage}</div>}
      </Container>

      <SelectForm
        name={"coucheId"}
        listIdCodeLibelle={listCouches}
        label="Couche"
        required={true}
        setValues={setValues}
        defaultValue={listCouches.find((e: any) => e.id === values?.coucheId)}
        onChange={(value: any) => {
          setFieldValue("coucheId", value?.id);
          setFieldValue("groupeFonctionnaliteIds", undefined);
          setCoucheId(value?.id);
        }}
      />

      <Multiselect
        name={"listGroupeFonctionnalites"}
        label="Groupes de fonctionnalités"
        options={listGroupeFonctionnalites}
        getOptionValue={(t) => t.id}
        value={
          values.groupeFonctionnaliteIds?.map((e: any) =>
            listGroupeFonctionnalites.find((p: any) => p.id === e),
          ) ??
          listGroupeFonctionnalites.filter((e: { id: any }) =>
            values.groupeFonctionnaliteIds?.includes(e.id),
          )
        }
        getOptionLabel={(t) => t.libelle}
        isClearable={true}
        onChange={(groupesFonctionnalites) => {
          setFieldValue(
            "groupeFonctionnaliteIds",
            groupesFonctionnalites.map((e: any) => e?.id),
          );
        }}
      />

      <CheckBoxInput name={"coucheMetadataActif"} label={"Actif"} />
      <CheckBoxInput
        name={"coucheMetadataPublic"}
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
              name="coucheMetadataStyle"
              readOnly={params === null}
              label="Métadonnées à afficher"
              onChange={handleLayerStyleChange}
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

export default CoucheMetadataForm;
