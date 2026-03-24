import { useFormikContext } from "formik";
import { useMemo } from "react";
import { Col, Row } from "react-bootstrap";
import { object } from "yup";
import AddRemoveComponent from "../../../components/AddRemoveComponent/AddRemoveComponent.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  Multiselect,
  SelectInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { CoucheWMSType, CriseType } from "../../../Entities/CriseEntity.tsx";
import url from "../../../module/fetch.tsx";
import {
  requiredArray,
  requiredDate,
  requiredString,
} from "../../../module/validators.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

export const getInitialValues = (data?: CriseType) => ({
  typeCriseId: data?.typeCriseId ?? null,
  criseLibelle: data?.criseLibelle ?? null,
  criseDescription: data?.criseDescription ?? null,
  listeCommuneId: data?.listeCommuneId ?? null,
  criseDateDebut: data?.criseDateDebut
    ? formatDateTimeForDateTimeInput(data?.criseDateDebut)
    : formatDateTimeForDateTimeInput(new Date()),
  listeToponymieId: data?.listeToponymieId ?? null,
  couchesWMS: data?.couchesWMS ?? [],
});

export const criseValidationSchema = object({
  typeCriseId: requiredString,
  criseLibelle: requiredString,
  criseDateDebut: requiredDate,
  listeCommuneId: requiredArray,
});

export const prepareCriseValues = (values: {
  typeCriseId: string | null;
  criseLibelle: string | null;
  criseDescription: string | null;
  listeCommuneId: string[];
  criseDateDebut: Date;
  listeToponymieId: string[] | null;
  couchesWMS: CoucheWMSType[];
}) => ({
  typeCriseId: values.typeCriseId,
  criseLibelle: values.criseLibelle,
  criseDescription: values.criseDescription,
  listeCommuneId: values.listeCommuneId,
  criseDateDebut: new Date(values.criseDateDebut).toISOString(),
  listeToponymieId: values.listeToponymieId,
  couchesWMS: values.couchesWMS,
});

const Crise = () => {
  const { setValues, setFieldValue, values } = useFormikContext<CriseType>();

  const typeCriseState = useGet(url`/api/crise/get-type-crise`);
  const communeState = useGet(url`/api/commune/get-libelle-commune`);
  const toponymieList = useGet(url`/api/toponymie/get-libelle-toponymie`);

  // mapper le dictionnaire retourné par "typeCriseState" pour récupérer l'id et le libellé
  const listTypeCrise = useMemo(() => {
    if (!typeCriseState.data) {
      return [];
    }
    return typeCriseState.data.map(
      (crise: { criseId: string; criseNom: string }) => {
        return {
          id: crise.criseId,
          code: crise.criseNom,
          libelle: crise.criseNom,
        };
      },
    );
  }, [typeCriseState.data]);

  // Récupération des couches disponibles (toutes les couches WMS)
  const couchesWMSState = useGet(url`/api/crise/get-couches-wms`);
  const toutesLesCouches = couchesWMSState.data || [];

  // Cas où il n'y a aucune couche associée au module de Crise
  const aucuneCouchesAssociee = toutesLesCouches.length === 0;
  // Cas où toutes les couches sont déjà ajoutées
  const plusDeCouchesDispo =
    toutesLesCouches.length > 0 &&
    toutesLesCouches.every((c: { coucheId: string }) =>
      values.couchesWMS?.some((w) => w.coucheId === c.coucheId),
    );

  return (
    <FormContainer noValidate>
      <h3 className="mt-1">Informations générales</h3>
      <SelectForm
        name={"typeCriseId"}
        listIdCodeLibelle={listTypeCrise}
        label="Type de la crise"
        required={true}
        setValues={setValues}
        defaultValue={listTypeCrise.find(
          (e: IdCodeLibelleType) => e.id === values?.typeCriseId,
        )}
      />

      <TextInput label="Nom" name="criseLibelle" required={true} />

      <TextAreaInput
        name="criseDescription"
        label="Description"
        required={false}
      />

      <DateTimeInput
        name="criseDateDebut"
        label="Date et heure d’activation"
        value={values.criseDateDebut}
        required={true}
      />

      <Multiselect
        name={"listeCommuneId"}
        label="Liste des communes de la crise"
        options={communeState.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        value={
          values.listeCommuneId?.map((e: any) =>
            communeState.data?.find((c: IdCodeLibelleType) => c.id === e),
          ) ?? undefined
        }
        onChange={(commune) => {
          const communeId = commune.map((e: any) => e.id);
          communeId.length > 0
            ? setFieldValue("listeCommuneId", communeId)
            : setFieldValue("listeCommuneId", undefined);
        }}
        isClearable={true}
      />

      <Multiselect
        name={"listeTyponymieId"}
        label="Répertoire des lieux"
        options={toponymieList.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        isClearable={true}
        required={false}
        value={
          values.listeToponymieId?.map((e: any) =>
            toponymieList.data?.find((c: IdCodeLibelleType) => c.id === e),
          ) ?? undefined
        }
        onChange={(toponymie) => {
          const toponymieId = toponymie.map((e: any) => e.id);
          toponymieId.length > 0
            ? setFieldValue("listeToponymieId", toponymieId)
            : setFieldValue("listeToponymieId", undefined);
        }}
      />

      <AddRemoveComponent
        name="couchesWMS"
        label="Liste des couches WMS"
        canAdd={true}
        createComponentToRepeat={createComponentToRepeat}
        listeElements={values.couchesWMS}
        defaultElement={{
          coucheId: null,
          operationnel: false,
          anticipation: false,
        }}
        isDisabledButton={plusDeCouchesDispo || aucuneCouchesAssociee}
        disableText={
          aucuneCouchesAssociee
            ? "Aucune couche associée au module de Crise"
            : plusDeCouchesDispo
              ? "Toutes les couches disponibles ont déjà été ajoutées"
              : undefined
        }
      />

      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

function createComponentToRepeat(index: any, listeElements: any[]) {
  return <ComposantToRepeat index={index} listeElements={listeElements} />;
}

const ComposantToRepeat = ({
  index,
  listeElements,
}: {
  index: number;
  listeElements: CoucheWMSType[];
}) => {
  const couchesWMS = useGet(url`/api/crise/get-couches-wms`);

  // logique de mappage et d'unicité
  const selectedIds = listeElements
    ?.map(({ coucheId }) => coucheId)
    .filter(Boolean);
  const TypesCouches = useMemo(
    () =>
      couchesWMS.data?.filter(
        ({ coucheId }: { coucheId: string }) => !selectedIds.includes(coucheId),
      ) || [],
    [couchesWMS.data, selectedIds],
  );

  const { setFieldValue } = useFormikContext();

  return (
    <Row className="align-items-center mt-3">
      <Col>
        <SelectInput
          name={`couchesWMS[${index}].coucheId`}
          label="Type"
          options={TypesCouches}
          getOptionValue={(t) => t.coucheId}
          getOptionLabel={(t) => t.coucheLibelle}
          onChange={(e) => {
            setFieldValue(
              `couchesWMS[${index}].coucheId`,
              TypesCouches.find(
                (type: { coucheId: string }) => type.coucheId === e.coucheId,
              )?.coucheId,
            );
          }}
          defaultValue={couchesWMS.data?.find(
            (type: { coucheId: string }) =>
              type.coucheId === listeElements[index].coucheId,
          )}
          required={true}
        />
      </Col>
      <Col>
        <CheckBoxInput
          name={`couchesWMS[${index}].operationnel`}
          label={"Opérationnel"}
        />
      </Col>
      <Col>
        <CheckBoxInput
          name={`couchesWMS[${index}].anticipation`}
          label={"Anticipation"}
        />
      </Col>
    </Row>
  );
};

export default Crise;
