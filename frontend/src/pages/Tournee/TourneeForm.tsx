import { useFormikContext } from "formik";
import { Col, Container } from "react-bootstrap";
import { FormContainer, TextInput } from "../../components/Form/Form.tsx";
import { TourneeFormEntity } from "../../Entities/TourneeEntity.tsx";
import url from "../../module/fetch.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconCreate, IconEdit } from "../../components/Icon/Icon.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { URLS } from "../../routes.tsx";

export const getInitialValues = (data?: TourneeFormEntity) => ({
  tourneeId: data?.tourneeId ?? null,
  tourneeLibelle: data?.tourneeLibelle ?? null,
  tourneeOrganismeId: data?.tourneeOrganismeId ?? null,
});

export const prepareVariables = (values: TourneeFormEntity) => ({
  tourneeId: values.tourneeId ?? null,
  tourneeLibelle: values.tourneeLibelle ?? null,
  tourneeOrganismeId: values.tourneeOrganismeId ?? null,
});

const TourneeForm = ({
  isCreation = false,
  isFromMap = false,
  tourneeLibelle = null,
}: {
  isCreation?: boolean;
  isFromMap: boolean;
  tourneeLibelle?: string;
}) => {
  const { setValues }: { values: TourneeFormEntity } = useFormikContext();

  const organismeState = useGet(url`/api/organisme/get-libelle-organisme`); // TODO : ne remonter que l'organisme et les enfants de l'utilisateur courant
  if (!organismeState.isResolved) {
    return;
  }

  return (
    <FormContainer>
      <Container>
        <PageTitle
          icon={isCreation ? <IconCreate /> : <IconEdit />}
          title={
            isCreation
              ? "Création d'une tournée"
              : "Modification de la tournée " + tourneeLibelle
          }
        />
        <Col>
          <TextInput name="tourneeLibelle" label="Nom de la tournée :" />
          {isCreation && (
            <SelectForm
              name={"tourneeOrganismeId"}
              listIdCodeLibelle={organismeState.data}
              label="Organisme :"
              required={true}
              setValues={setValues}
            />
          )}
        </Col>
        <SubmitFormButtons
          returnLink={isFromMap ? undefined : URLS.LIST_TOURNEE}
        />
      </Container>
    </FormContainer>
  );
};

export default TourneeForm;
