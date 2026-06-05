import { FormContainer } from "../../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";

const LaunchSigTask = () => {
  return (
    <FormContainer>
      <p>Cette action permet de lancer la synchronisation SIG.</p>

      <SubmitFormButtons returnLink={false} submitTitle="Exécuter" />
    </FormContainer>
  );
};

export default LaunchSigTask;
