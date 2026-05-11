import { Container } from "react-bootstrap";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconEdit, IconOverview } from "../../components/Icon/Icon.tsx";
import { DfciAireEntity } from "../../Entities/DfciAireEntity.tsx";
import url from "../../module/fetch.tsx";
import DfciAire, {
  getInitialValuesDfciAire,
  prepareVariablesDfciAire,
  validationSchemaDfciAire,
} from "./DfciAire.tsx";

/**
 * Composant permettant d'afficher les données d'une aire
 * @param aireSelected Aire selectionné sur la carte
 * @returns le composant pour l'aire
 */
export const UpdateDfciAire = ({
  aireId,
  onSubmit,
  readOnly,
}: {
  aireId: string;
  onSubmit: () => void;
  readOnly: boolean;
}) => {
  const { data }: { data?: DfciAireEntity } = useGet(
    url`/api/dfci-aires/${aireId}`,
  );

  if (!data) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle
        icon={readOnly ? <IconOverview /> : <IconEdit />}
        title={readOnly ? "Visualisation de l'aire" : "Modification de l'aire"}
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValuesDfciAire(data)}
        isPost={false}
        prepareVariables={prepareVariablesDfciAire}
        validationSchema={validationSchemaDfciAire}
        onSubmit={onSubmit}
        submitUrl="/api/dfci-aires/update"
      >
        <DfciAire readOnly={readOnly} />
      </MyFormik>
    </Container>
  );
};
