import { FormContainer } from "../../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";

const RefreshViewMesures = () => {
  return (
    <FormContainer>
      <p>Cette action permet de rafraîchir la vue des mesures.</p>

      <SubmitFormButtons returnLink={false} submitTitle="Exécuter" />
    </FormContainer>
  );
};

export default RefreshViewMesures;
