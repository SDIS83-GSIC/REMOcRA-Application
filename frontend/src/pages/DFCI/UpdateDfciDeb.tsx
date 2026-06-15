import { Container } from "react-bootstrap";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconEdit, IconOverview } from "../../components/Icon/Icon.tsx";
import { DfciDebEntity } from "../../Entities/DfciDebEntity.tsx";
import url from "../../module/fetch.tsx";
import DfciDeb, {
  getInitialValuesDeb,
  prepareVariablesDeb,
  validationSchemaDeb,
} from "./DfciDeb.tsx";

export const UpdateDfciDeb = ({
  debId,
  onSubmit,
  readOnly,
}: {
  debId: string;
  onSubmit: () => void;
  readOnly: boolean;
}) => {
  const { data }: { data?: DfciDebEntity } = useGet(
    url`/api/dfci-deb/${debId}`,
  );

  if (!data) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle
        icon={readOnly ? <IconOverview /> : <IconEdit />}
        title={
          readOnly
            ? `Visualisation de ${data.dfciDebLibelle}`
            : `Modification de ${data.dfciDebLibelle}`
        }
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValuesDeb(data)}
        isPost={false}
        prepareVariables={prepareVariablesDeb}
        validationSchema={validationSchemaDeb}
        onSubmit={onSubmit}
        submitUrl=""
      >
        <DfciDeb readOnly={readOnly} />
      </MyFormik>
    </Container>
  );
};
