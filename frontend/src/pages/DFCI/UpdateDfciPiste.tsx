import { Container } from "react-bootstrap";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconEdit, IconOverview } from "../../components/Icon/Icon.tsx";
import { DfciPisteEntity } from "../../Entities/DfciPisteEntity.tsx";
import url from "../../module/fetch.tsx";
import DfciPiste, {
  getInitialValuesPiste,
  prepareVariablesPiste,
  validationSchemaDfciPiste,
} from "./DfciPiste.tsx";

export const UpdateDfciPiste = ({
  pisteId,
  onSubmit,
  readOnly,
}: {
  pisteId: string;
  onSubmit: () => void;
  readOnly: boolean;
}) => {
  const { data }: { data?: DfciPisteEntity } = useGet(
    url`/api/dfci-pistes/${pisteId}`,
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
            ? "Visualisation de la piste " + data.dfciPisteLibelle
            : "Modification de la piste " + data.dfciPisteLibelle
        }
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValuesPiste(data)}
        isPost={false}
        prepareVariables={prepareVariablesPiste}
        validationSchema={validationSchemaDfciPiste}
        onSubmit={onSubmit}
        submitUrl="/api/dfci-pistes/update"
      >
        <DfciPiste readOnly={readOnly} />
      </MyFormik>
    </Container>
  );
};
