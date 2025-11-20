import { useFormikContext } from "formik";
import { Col, Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { FormContainer, TextInput } from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconCreate, IconEdit } from "../../components/Icon/Icon.tsx";
import { TourneeFormEntity } from "../../Entities/TourneeEntity.tsx";
import url from "../../module/fetch.tsx";

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
  tourneeLibelle = null,
  listePei = [],
}: {
  isCreation?: boolean;
  tourneeLibelle?: string;
  listePei?: string[];
}) => {
  const { values, setValues } = useFormikContext<TourneeFormEntity>();

  const organismeState = useGet(
    listePei
      ? url`/api/organisme/get-libelle-organisme-filter-with-pei?${{ listePei: JSON.stringify(listePei) }}`
      : url`/api/organisme/get-libelle-organisme`,
  ); // TODO : ne remonter que l'organisme et les enfants de l'utilisateur courant

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
              defaultValue={organismeState.data?.find(
                (value: any) => value.id === values.tourneeOrganismeId,
              )}
            />
          )}
        </Col>
        <SubmitFormButtons returnLink={true} />
      </Container>
    </FormContainer>
  );
};

export default TourneeForm;
