import { useFormikContext } from "formik";
import { Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  NumberInput,
  SelectInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconList } from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import { requiredString } from "../../module/validators.tsx";
import { TYPE_PARAMETRE_RAPPORT_PERSONNALISE } from "../Admin/rapportPersonnalise/SortableParametreRapportPersonnalise.tsx";

const ExecuteRapportPersonnalise = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title={"Exécuter des rapports personnalisés"}
      />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/rapport-personnalise/execute`}
        prepareVariables={(values) => prepareVariables(values)}
      >
        <ExecuteRapportPersonnaliseForm />
      </MyFormik>
    </Container>
  );
};

export const getInitialValues = () => ({
  rapportPersonnaliseId: null,
});

export const validationSchema = object({
  rapportPersonnaliseId: requiredString,
});
export const prepareVariables = () => ({
  // TODO
});

const ExecuteRapportPersonnaliseForm = () => {
  const { data: listeRapportPersoWithParametre } = useGet(
    url`/api/rapport-personnalise/parametres`,
  );
  const { setFieldValue, values } = useFormikContext();

  const listeRapportPerso = listeRapportPersoWithParametre?.map((e) => ({
    id: e.rapportPersonnaliseId,
    libelle: e.rapportPersonnaliseLibelle,
  }));

  const rapportPersonnaliseCourant = listeRapportPersoWithParametre?.find(
    (e) => e.rapportPersonnaliseId === values.rapportPersonnaliseId,
  );

  return (
    <>
      <Row>
        <SelectInput
          name={`rapportPersonnaliseId`}
          label="Rapport personnalisé"
          options={listeRapportPerso}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          onChange={(e) => {
            setFieldValue(
              `rapportPersonnaliseId`,
              listeRapportPerso?.find((r) => r.id === e.id).id,
            );
          }}
          defaultValue={listeRapportPerso?.find(
            (r) => r.id === values.rapportPersonnaliseId,
          )}
          required={true}
        />
      </Row>
      {rapportPersonnaliseCourant?.rapportPersonnaliseDescription && (
        <Row className="mt-3">
          <Col>
            <b>Description : </b>
            {rapportPersonnaliseCourant.rapportPersonnaliseDescription}
          </Col>
        </Row>
      )}
      <Row className="mt-3">
        {// Pour chacun des paramètres, on affiche le bon composant
        rapportPersonnaliseCourant?.listeParametre?.map((element) => {
          return buildComponent(element, values, setFieldValue);
        })}
      </Row>
    </>
  );
};

function buildComponent(element, values, setFieldValue) {
  switch (
    TYPE_PARAMETRE_RAPPORT_PERSONNALISE[
      element.rapportPersonnaliseParametreType
    ]
  ) {
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.CHECKBOX_INPUT:
      return (
        <CheckBoxInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          checked={
            values[element.rapportPersonnaliseParametreCode] ??
            element.rapportPersonnaliseParametreValeurDefaut
          }
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.NUMBER_INPUT:
      return (
        <NumberInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          value={element.rapportPersonnaliseParametreValeurDefaut}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.TEXT_INPUT:
      return (
        <TextInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          value={element.rapportPersonnaliseParametreValeurDefaut}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.DATE_INPUT:
      return (
        <DateTimeInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          value={element.rapportPersonnaliseParametreValeurDefaut}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.SELECT_INPUT:
      return (
        <SelectInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          options={element.listeSelectInput}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          onChange={(e) => {
            setFieldValue(
              element.rapportPersonnaliseParametreCode,
              element.listeSelectInput?.find((r) => r.id === e.id).id,
            );
          }}
          defaultValue={element.listeSelectInput?.find(
            (r) => r.id === values[element.rapportPersonnaliseParametreCode],
          )}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    default:
      return;
  }
}

export default ExecuteRapportPersonnalise;
