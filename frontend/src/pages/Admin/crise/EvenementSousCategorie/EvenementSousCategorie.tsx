import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  SelectInput,
  TextInput,
} from "../../../../components/Form/Form.tsx";
import SelectForm from "../../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";
import TYPE_GEOMETRIE from "../../../../enums/TypeGeometrie.tsx";
import url from "../../../../module/fetch.tsx";
import { requiredString } from "../../../../module/validators.tsx";
import SortableAddRemoveComponent from "../../../../components/DragNDrop/SortableAddRemoveComponent.tsx";
import SousTypeEvenementType from "../../../../Entities/SousTypesEvenementsEntity.tsx";
import { createComponentSousTypeEvenementToRepeat } from "./SortableComplementSousCategorie.tsx";

export const prepareValues = (values: SousTypeEvenementType) => ({
  evenementSousCategorieId: values.evenementSousCategorieId,
  evenementSousCategorieCode: values.evenementSousCategorieCode,
  evenementSousCategorieLibelle: values.evenementSousCategorieLibelle,
  evenementSousCategorieActif: values.evenementSousCategorieActif,
  evenementSousCategorieTypeGeometrie:
    values.evenementSousCategorieTypeGeometrie,
  evenementSousCategorieEvenementCategorieId:
    values.evenementSousCategorieEvenementCategorieId,
  evenementSousCategorieComplement: values.evenementSousCategorieComplement.map(
    (e, index) => {
      return {
        ...e,
        evenementSousCategorieComplementOrdre: index,
        sousCategorieComplementSql:
          e.sousCategorieComplementSqlDebut +
          " " +
          e.sousCategorieComplementSqlId +
          " as id, " +
          e.sousCategorieComplementSqlLibelle +
          " as libelle " +
          e.sousCategorieComplementSqlFin,
      };
    },
  ),
});

export const evenementSousCategorieValidationSchema = object({
  evenementSousCategorieCode: requiredString,
  evenementSousCategorieLibelle: requiredString,
  evenementSousCategorieTypeGeometrie: requiredString,
  evenementSousCategorieEvenementCategorieId: requiredString,
});

export const getInitialEvenementSousCategorieValue = (
  data?: SousTypeEvenementType,
) => {
  return {
    evenementSousCategorieId: data?.evenementSousCategorieId ?? null,
    evenementSousCategorieCode: data?.evenementSousCategorieCode ?? null,
    evenementSousCategorieActif: data?.evenementSousCategorieActif ?? true,
    evenementSousCategorieLibelle: data?.evenementSousCategorieLibelle ?? null,
    evenementSousCategorieTypeGeometrie:
      data?.evenementSousCategorieTypeGeometrie ?? null,
    evenementSousCategorieEvenementCategorieId:
      data?.evenementSousCategorieEvenementCategorieId ?? null,
    evenementSousCategorieComplement:
      data?.evenementSousCategorieComplement.map((e) => ({
        ...e,
        sousCategorieComplementSqlDebut:
          e.sousCategorieComplementSql && e.sousCategorieComplementSqlId
            ? e.sousCategorieComplementSql.split(
                e.sousCategorieComplementSqlId,
              )[0]
            : null,
        sousCategorieComplementSqlFin:
          e.sousCategorieComplementSql && e.sousCategorieComplementSqlLibelle
            ? e.sousCategorieComplementSql
                .split(e.sousCategorieComplementSqlLibelle + " as libelle")
                .slice(-1)[0]
            : null,
        id: Math.random(),
      })) ?? [],
  };
};

export const EvenementSousCategorie = () => {
  const listTypeGeometrie = Object.values(TYPE_GEOMETRIE).map((e) => {
    return { id: e.toString(), code: e.toString(), libelle: e.toString() };
  });
  const { values, setValues, setFieldValue }: any = useFormikContext();
  const evenementCategorieState = useGet(
    url`/api/nomenclatures/evenement_categorie`,
  );

  function setListeComplementetres(value: any) {
    setFieldValue("evenementSousCategorieComplement", value);
  }

  return (
    evenementCategorieState?.data && (
      <FormContainer>
        <CheckBoxInput name={"evenementSousCategorieActif"} label={"Actif"} />

        <TextInput
          name="evenementSousCategorieCode"
          label="Code"
          required={true}
        />
        <TextInput
          name="evenementSousCategorieLibelle"
          label="Libellé"
          required={true}
        />
        <SelectForm
          name={"evenementSousCategorieTypeGeometrie"}
          listIdCodeLibelle={listTypeGeometrie}
          label="Type de géométrie"
          defaultValue={listTypeGeometrie?.find(
            (e) => e.code === values.evenementSousCategorieTypeGeometrie,
          )}
          required={true}
          setValues={setValues}
        />

        <SelectInput
          name={"evenementSousCategorieEvenementCategorieId"}
          label="Catégorie de l'évènement"
          required={false}
          options={
            evenementCategorieState?.data
              ? Object.values(evenementCategorieState?.data)
              : []
          }
          getOptionValue={(t) => t.evenementCategorieId}
          getOptionLabel={(t) => t.evenementCategorieLibelle}
          defaultValue={
            Object.values(evenementCategorieState.data)?.find(
              (r) =>
                r.evenementCategorieId ===
                values.evenementSousCategorieEvenementCategorieId,
            ) ?? null
          }
          onChange={(e) => {
            setFieldValue(
              `evenementSousCategorieEvenementCategorieId`,
              Object.values(evenementCategorieState?.data)?.find(
                (categorie: any) =>
                  categorie.evenementCategorieId === e.evenementCategorieId,
              )?.evenementCategorieId,
            );
          }}
        />

        <div className="mt-4">
          <SortableAddRemoveComponent
            buttonLibelle={"Ajouter des paramètres"}
            createComponentToRepeat={createComponentSousTypeEvenementToRepeat}
            nomListe={"evenementSousCategorieComplement"}
            setData={setListeComplementetres}
            defaultElement={{
              sousCategorieComplementLibelle: "",
              sousCategorieComplementCode: "",
              sousCategorieComplementEstRequis: false,
              sousCategorieComplementDescription: "",
              sousCategorieComplementType: null,
              sousCategorieComplementValeurDefaut: "",
            }}
          />
        </div>

        <SubmitFormButtons returnLink={true} />
      </FormContainer>
    )
  );
};
