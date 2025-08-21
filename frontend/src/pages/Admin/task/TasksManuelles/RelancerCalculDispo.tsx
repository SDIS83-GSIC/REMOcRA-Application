import { useFormikContext } from "formik";
import {
  CheckBoxInput,
  FormContainer,
} from "../../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";

const RelancerCalculDispo = () => {
  const { values } = useFormikContext<RelancerCalculDispoType>();
  return (
    <FormContainer>
      <p>
        Cette tâche permet de relancer en masse le calcul de disponibilité de
        tous les PEI suite à un changement de règles de gestion. Deux booleans
        sont disponibles pour configurer le comportement de la tâche.
      </p>
      <CheckBoxInput
        name="eventTracabilite"
        label="Ajouter les changements dans la traçabilité"
        checked={values?.eventTracabilite}
        tooltipText={
          "Si la case est cochée, la table traçabilité sera mise à jour."
        }
      />

      <CheckBoxInput
        name="eventNexSis"
        label="Mettre à jour les PEI dans NexSIS"
        checked={values?.eventNexSis}
        tooltipText={
          "Si la case est cochée, les changements seront notifiés à NexSIS."
        }
      />
      <SubmitFormButtons returnLink={false} submitTitle="Exécuter" />
    </FormContainer>
  );
};

export default RelancerCalculDispo;

type RelancerCalculDispoType = {
  eventTracabilite: boolean;
  eventNexSis: boolean;
};
