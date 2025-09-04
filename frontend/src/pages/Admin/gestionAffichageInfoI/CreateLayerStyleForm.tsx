import { object } from "yup";
import { useFormikContext } from "formik";
import {
  CheckBoxInput,
  FormContainer,
  Multiselect,
  TextAreaInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import { requiredArray, requiredString } from "../../../module/validators.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";

export const getInitialValues = (styleId?: string, data?: any) => ({
  groupLayerId: data?.groupLayerId ?? null,
  layerId: data?.layerId ?? null,
  layerProfilId: data?.layerProfilId ?? null,
  layerStyleFlag: data?.layerStyleFlag ?? false,
  layerStyleId: styleId ?? "",
  layerStyle:
    data?.layerStyle ??
    "[i]Les adresses sont mises à jour tous les jours à 01h00 [/i]\n[b]Description : [/b] #adresse_description#\n[br]\n[b]adresse_date_modification : [/b] [u]#adresse_date_modification#[/u]",
});

export const prepareValues = (
  values: {
    groupLayerId: any;
    layerId: any;
    layerProfilId: any;
    layerStyle: any;
    layerStyleFlag: boolean;
  },
  styleId?: string,
) => ({
  layerStyleId: styleId,
  groupLayerId: values.groupLayerId,
  layerId: values.layerId,
  layerProfilId: values.layerProfilId,
  layerStyle: values.layerStyle,
  layerStyleFlag: values.layerStyleFlag,
});

export const validationSchema = object({
  groupLayerId: requiredString,
  layerId: requiredString,
  layerStyle: requiredString,
  layerProfilId: requiredArray,
});

const CreateLayerStyleForm = () => {
  const layerData = useGet(url`/api/admin/couche/get-couches`)?.data?.list;
  const { setValues, setFieldValue, values } = useFormikContext<{
    groupLayerId: any;
    layerId: any;
    layerProfilId: any;
    layerStyle: any;
    layerStyleFlag: boolean;
  }>();

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
        profilDroitList: any;
      }) => ({
        id: layer.coucheId,
        code: layer.coucheCode,
        libelle: layer.coucheLibelle,
        profilDroitList: layer.profilDroitList,
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
    listProfilsDroits = selectedLayer.profilDroitList.map(
      (profil: {
        profilDroitId: any;
        profilDroitCode: any;
        profilDroitLibelle: any;
      }) => ({
        id: profil.profilDroitId,
        code: profil.profilDroitCode,
        libelle: profil.profilDroitLibelle,
      }),
    );

    // Tri de la liste des profils droits par libelle
    listProfilsDroits = listProfilsDroits.sort(
      (a: { libelle: string }, b: { libelle: string }) =>
        a.libelle.localeCompare(b.libelle),
    );
  }

  return (
    <FormContainer>
      <h3 className="mt-1">Informations générales</h3>

      {/* group layer */}
      <SelectForm
        name={"groupLayerId"}
        listIdCodeLibelle={listGroupLayers}
        label="Selectionnez le groupe de couche"
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

      {/* layer */}
      <SelectForm
        name={"layerId"}
        listIdCodeLibelle={listLayers}
        label="Selectionnez le nom de la couche"
        required={true}
        setValues={setValues}
        defaultValue={listLayers.find((e: any) => e.id === values?.layerId)}
        onChange={(value: any) => {
          setFieldValue("layerId", value.id);
          setFieldValue("layerProfilId", undefined);
        }}
      />

      <Multiselect
        name={"layerProfilId"}
        label="Ajoutez des groupes de fonctionnalité"
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

      <CheckBoxInput name={"layerStyleFlag"} label={"Activer le style"} />

      <TextAreaInput
        rows={10}
        name="layerStyle"
        label="Entrez un nouveau style"
        value={values.layerStyle}
      />

      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default CreateLayerStyleForm;
