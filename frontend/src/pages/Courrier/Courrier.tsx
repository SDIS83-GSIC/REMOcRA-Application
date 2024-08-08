import { useFormikContext } from "formik";
import { Button, Container } from "react-bootstrap";
import { object } from "yup";
import {
  CourrierParametreEntity,
  ModeleCourrierWithParametres,
  TypeComposant,
} from "../../Entities/CourrierEntity.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { IconDocument, IconInfo } from "../../components/Icon/Icon.tsx";
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";

type ValuesType = {
  modeleCourrierId: string | null;
};

export const getInitialValues = (): ValuesType => ({
  modeleCourrierId: null,
});

export const validationSchema = object({});
export const prepareVariables = (values) => ({
  modeleCourrierId: values.modeleCourrierId,
  listParametres: Object.entries(values).map((e) => {
    return { nom: e[0], valeur: e[1] };
  }),
});

/**
 * Cette classe permet de générer le formulaire en fonction du modèle de courrier choisi.
 */
const Courrier = ({
  initialesValues,
}: {
  initialesValues: ModeleCourrierWithParametres[];
}) => {
  const { setValues, values, setFieldValue }: { values: ValuesType } =
    useFormikContext();

  const listModeleCourrier = Object.values(initialesValues).map((e) => {
    return {
      id: e.modeleCourrier.modeleCourrierId,
      code: e.modeleCourrier.modeleCourrierCode,
      libelle: e.modeleCourrier.modeleCourrierLibelle,
    };
  });

  const listParametres = initialesValues.find(
    (e: ModeleCourrierWithParametres) =>
      e.modeleCourrier.modeleCourrierId === values.modeleCourrierId,
  )?.listParametres;

  return (
    <FormContainer>
      <Container>
        <PageTitle icon={<IconDocument />} title={"Générer un courrier"} />
        <SelectForm
          name={"modeleCourrierId"}
          listIdCodeLibelle={listModeleCourrier}
          label="Choisissez un modèle de courrier"
          required={true}
          setValues={setValues}
          onChange={(i) => {
            setFieldValue("modeleCourrierId", i.target.value);

            // On set les valeurs par défaut
            initialesValues
              .find(
                (e: ModeleCourrierWithParametres) =>
                  e.modeleCourrier.modeleCourrierId === i.target.value,
              )
              ?.listParametres?.map((param: CourrierParametreEntity) =>
                setFieldValue(param.nameField, param.defaultValue),
              );
          }}
        />
        <br />
        {listParametres &&
          listParametres.map((e) => {
            switch (e.typeComposant) {
              case TypeComposant.SELECT_INPUT: {
                return (
                  checkConditionToDisplay(e, values) && (
                    <SelectForm
                      name={e.nameField}
                      listIdCodeLibelle={e.liste}
                      label={
                        <>
                          {e.label}
                          {e.description && (
                            <TooltipCustom
                              tooltipText={e.description}
                              tooltipId={e.nameField}
                            >
                              <IconInfo />
                            </TooltipCustom>
                          )}
                        </>
                      }
                      required={true}
                      setValues={setValues}
                    />
                  )
                );
              }
              case TypeComposant.CHECKBOX_INPUT: {
                return (
                  checkConditionToDisplay(e, values) && (
                    <CheckBoxInput
                      name={e.nameField}
                      label={
                        <>
                          {e.label}
                          {e.description && (
                            <TooltipCustom
                              tooltipText={e.description}
                              tooltipId={e.nameField}
                            >
                              <IconInfo />
                            </TooltipCustom>
                          )}
                        </>
                      }
                    />
                  )
                );
              }
              case TypeComposant.TEXT_INPUT: {
                return (
                  checkConditionToDisplay(e, values) && (
                    <TextInput
                      name={e.nameField}
                      label={
                        <>
                          {e.label}
                          {e.description && (
                            <TooltipCustom
                              tooltipText={e.description}
                              tooltipId={e.nameField}
                            >
                              <IconInfo />
                            </TooltipCustom>
                          )}
                        </>
                      }
                    />
                  )
                );
              }
            }
          })}
        <Button
          type="submit"
          variant="primary"
          className="d-block w-25 mt-3 mx-auto"
        >
          Générer le courrier
        </Button>
      </Container>
    </FormContainer>
  );
};

function checkConditionToDisplay(
  e: CourrierParametreEntity,
  values: ValuesType,
) {
  return (
    (e.conditionToDisplay != null &&
      values[e.conditionToDisplay.nameField] ===
        e.conditionToDisplay.valeurAttendue) ||
    e.conditionToDisplay == null
  );
}

export default Courrier;
