import { useFormikContext } from "formik";
import { FormContainer, TextInput } from "../../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";

const ImporterCadastre = () => {
  const { values } = useFormikContext<ImporterCadastreType>();

  return (
    <FormContainer>
      <p>
        Cette tâche permet de télécharger les données du cadastre de votre
        territoire sur le site &quot;files.data.gouv.fr/&quot;, et de faire le
        lien avec les communes existantes. Les données non intégrables
        apparaîtront en échec dans les logs, mais ne bloqueront pas.
      </p>
      <TextInput
        label="Millésime"
        name="millesime"
        placeholder="2023-01-01"
        required={false}
        value={values?.millesime}
      />

      <SubmitFormButtons returnLink={false} submitTitle="Exécuter" />
    </FormContainer>
  );
};

export default ImporterCadastre;

type ImporterCadastreType = {
  millesime: string;
};
