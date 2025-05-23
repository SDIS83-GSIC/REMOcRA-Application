import { Container } from "react-bootstrap";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconPermis } from "../../components/Icon/Icon.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import Permis, { getInitialValues, prepareVariables } from "./Permis.tsx";

const CreatePermis = ({
  coordonneeX,
  coordonneeY,
  srid,
  onSubmit,
}: CreatePermisType) => {
  const { user } = useAppContext();
  return (
    <Container>
      <PageTitle
        icon={<IconPermis />}
        title="Déclaration d'un permis"
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValues({
          permisLibelle: "",
          permisNumero: "",
          permisServiceInstructeurId: "",
          permisTypePermisInterserviceId: "",
          permisTypePermisAvisId: "",
          permisRiReceptionnee: false,
          permisDossierRiValide: false,
          permisObservations: "",
          permisVoieText: "",
          permisVoieId: "",
          permisComplement: "",
          permisCommuneId: "",
          permisAnnee: new Date().getFullYear(),
          permisDatePermis: null,

          permisCoordonneeX: coordonneeX,
          permisCoordonneeY: coordonneeY,
          permisSrid: srid,

          voieSaisieText: false,
          permisLastUpdateDate: formatDateTime(new Date()),
          permisInstructeurUsername: user.username,
        })}
        isPost={true}
        isMultipartFormData={true}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
        submitUrl={`/api/permis/create`}
      >
        <Permis />
      </MyFormik>
    </Container>
  );
};

type CreatePermisType = {
  coordonneeX: number;
  coordonneeY: number;
  srid: string;
  onSubmit: () => void;
};

export default CreatePermis;
