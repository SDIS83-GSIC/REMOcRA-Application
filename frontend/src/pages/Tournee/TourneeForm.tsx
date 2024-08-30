import { useFormikContext } from "formik";
import { Button, Col } from "react-bootstrap";
import { FormContainer, TextInput } from "../../components/Form/Form.tsx";
import { TourneeFormEntity } from "../../Entities/TourneeEntity.tsx";
import url from "../../module/fetch.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconCreate } from "../../components/Icon/Icon.tsx";

export const getInitialValues = () => ({
  tourneeId: null,
  tourneeLibelle: null,
  tourneeOrganismeId: null,
});

export const prepareVariables = (values: TourneeFormEntity) => ({
  tourneeId: values.tourneeId ?? null,
  tourneeLibelle: values.tourneeLibelle ?? null,
  tourneeOrganismeId: values.tourneeOrganismeId ?? null,
});

const TourneeForm = () => {
  const { setValues }: { values: TourneeFormEntity } = useFormikContext();

  const organismeState = useGet(url`/api/organisme/get-libelle-organisme`); // TODO : ne remonter que l'organisme et les enfants de l'utilisateur courant
  if (!organismeState.isResolved) {
    return;
  }

  return (
    <FormContainer>
      <PageTitle icon={<IconCreate />} title="Création d'une tournée" />
      <Col>
        <TextInput name="tourneeLibelle" label="Nom de la tournée :" />
        <SelectForm
          name={"tourneeOrganismeId"}
          listIdCodeLibelle={organismeState.data}
          label="Organisme :"
          required={true}
          setValues={setValues}
        />
      </Col>
      <Button type="submit" variant="primary">
        Valider
      </Button>
    </FormContainer>
  );
};

export default TourneeForm;
