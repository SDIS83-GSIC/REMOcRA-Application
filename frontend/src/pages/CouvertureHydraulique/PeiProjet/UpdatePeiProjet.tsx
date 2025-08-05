import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import PeiProjet, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./PeiProjet.tsx";

const UpdatePeiProjet = ({
  etudeId,
  peiProjetId,
  coordonneeX,
  coordonneeY,
  srid,
  onSubmit,
}: {
  etudeId: string;
  peiProjetId: string;
  coordonneeX: number;
  coordonneeY: number;
  srid: string;
  onSubmit: () => void;
}) => {
  const { data } = useGet(
    url`/api/couverture-hydraulique/pei-projet/` + peiProjetId,
  );

  return (
    <Container>
      <PageTitle icon={<IconPei />} title="Modification d'un PEI en projet" />
      <MyFormik
        initialValues={getInitialValues({
          peiProjetNatureDeciId: data?.peiProjetNatureDeciId,
          peiProjetTypePeiProjet: data?.peiProjetTypePeiProjet,
          peiProjetDiametreId: data?.peiProjetDiametreId,
          peiProjetDiametreCanalisation: data?.peiProjetDiametreCanalisation,
          peiProjetCapacite: data?.peiProjetCapacite,
          peiProjetDebit: data?.peiProjetDebit,
          peiProjetCoordonneeX: coordonneeX,
          peiProjetCoordonneeY: coordonneeY,
          peiProjetSrid: srid,
          peiProjetEtudeId: etudeId,
        })}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={
          `/api/couverture-hydraulique/etude/` +
          etudeId +
          `/pei-projet/` +
          peiProjetId
        }
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
      >
        <PeiProjet />
      </MyFormik>
    </Container>
  );
};

export default UpdatePeiProjet;
