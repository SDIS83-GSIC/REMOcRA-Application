import { Container } from "react-bootstrap";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconEdit, IconOverview } from "../../components/Icon/Icon.tsx";
import { DfciPanneauEntity } from "../../Entities/DfciPanneauEntity.tsx";
import url from "../../module/fetch.tsx";
import DfciPanneau, {
  getInitialValuesPanneau,
  prepareVariablesPanneau,
  validationSchemaPanneau,
} from "./DfciPanneau.tsx";

export const UpdateDfciPanneau = ({
  panneauId,
  onSubmit,
  readOnly,
}: {
  panneauId: string;
  onSubmit: () => void;
  readOnly: boolean;
}) => {
  const { data }: { data?: DfciPanneauEntity } = useGet(
    url`/api/dfci-panneau/${panneauId}`,
  );

  if (!data) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle
        icon={readOnly ? <IconOverview /> : <IconEdit />}
        title={
          readOnly ? "Visualisation du panneau" : "Modification du panneau"
        }
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValuesPanneau(data)}
        isPost={false}
        prepareVariables={prepareVariablesPanneau}
        validationSchema={validationSchemaPanneau}
        onSubmit={onSubmit}
        submitUrl=""
      >
        <DfciPanneau readOnly={readOnly} />
      </MyFormik>
    </Container>
  );
};
