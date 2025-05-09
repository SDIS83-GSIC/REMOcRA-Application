import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconPrescrit } from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import { formatForDateInput } from "../../utils/formatDateUtils.tsx";
import PeiPrescrit, {
  getInitialValues,
  prepareVariables,
} from "./PeiPrescrit.tsx";

const UpdatePeiPrescrit = ({
  peiPrescritId,
  coordonneeX,
  coordonneeY,
  srid,
  onSubmit,
}: {
  peiPrescritId: string;
  coordonneeX: number;
  coordonneeY: number;
  srid: string;
  onSubmit: () => void;
}) => {
  const { data } = useGet(url`/api/pei-prescrit/${peiPrescritId}`);

  const pageTitle = data?.peiPrescritNumDossier
    ? `Modification de la prescription ${data?.peiPrescritNumDossier}`
    : "Modification d'une prescription de PEI";

  return (
    <Container>
      <PageTitle
        icon={<IconPrescrit />}
        title={pageTitle}
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValues({
          peiPrescritId: peiPrescritId,
          peiPrescritCoordonneeX: coordonneeX,
          peiPrescritCoordonneeY: coordonneeY,
          peiPrescritSrid: srid,
          peiPrescritDate: data?.peiPrescritDate
            ? formatForDateInput(data.peiPrescritDate)
            : null,
          peiPrescritDebit: data?.peiPrescritDebit,
          peiPrescritNbPoteaux: data?.peiPrescritNbPoteaux,
          peiPrescritCommentaire: data?.peiPrescritCommentaire,
          peiPrescritAgent: data?.peiPrescritAgent,
          peiPrescritNumDossier: data?.peiPrescritNumDossier,
        })}
        isPost={false}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
        submitUrl={`/api/pei-prescrit/update/${peiPrescritId}`}
      >
        <PeiPrescrit />
      </MyFormik>
    </Container>
  );
};

export default UpdatePeiPrescrit;
