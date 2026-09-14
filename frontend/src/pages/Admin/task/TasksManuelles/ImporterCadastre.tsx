import { useFormikContext } from "formik";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../../components/Form/Form.tsx";
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
      <CheckBoxInput
        label="Nettoyer les données du cadastre avant l'import"
        tooltipText="Supprime toutes les données du cadastre non utilisées avant de réintégrer les nouvelles données. Permet une réintégration propre sans données obsolètes."
        name="supprimerDonneesCadastreNonUtilisees"
        required={false}
        checked={values?.supprimerDonneesCadastreNonUtilisees}
      />
      <CheckBoxInput
        label="Remplacer les géométries existantes en cas de doublon"
        tooltipText="En cas de doublon (même parcelle/section dans la même commune), remplace la géométrie existante par celle du nouveau cadastre. Si non sélectionné, conserve la géométrie d'origine."
        name="remplacerDonneesCadastre"
        required={false}
        checked={values?.remplacerDonneesCadastre}
      />
      <SubmitFormButtons returnLink={false} submitTitle="Exécuter" />
    </FormContainer>
  );
};

export default ImporterCadastre;

type ImporterCadastreType = {
  millesime: string;
  supprimerDonneesCadastreNonUtilisees: boolean;
  remplacerDonneesCadastre: boolean;
};
