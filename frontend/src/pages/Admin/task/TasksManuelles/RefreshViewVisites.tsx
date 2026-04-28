import { FormContainer } from "../../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";

const RefreshViewVisites = () => {
  return (
    <FormContainer>
      <p>Cette action permet de rafraîchir la vue des visites.</p>

      <SubmitFormButtons returnLink={false} submitTitle="Exécuter" />
    </FormContainer>
  );
};

export default RefreshViewVisites;
