import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconPermis } from "../../components/Icon/Icon.tsx";
import PermisEntity from "../../Entities/PermisEntity.tsx";
import url from "../../module/fetch.tsx";
import {
  formatDateHeure,
  formatForDateInput,
} from "../../utils/formatDateUtils.tsx";
import Permis, { getInitialValues, prepareVariables } from "./Permis.tsx";

const UpdatePermis = ({
  permisId,
  coordonneeX,
  coordonneeY,
  srid,
  onSubmit,
}: UpdatePermisType) => {
  const data = useGet(url`/api/permis/${permisId}`);

  if (!data.isResolved) {
    return;
  }

  const resolvedData: {
    permis: PermisEntity;
    permisCadastreParcelle: string[];
    permisLastUpdateDate: Date;
    permisInstructeurUsername: string;
  } = data.data;

  const pageTitle = resolvedData.permis.permisLibelle
    ? `Modification du permis ${resolvedData.permis.permisLibelle}`
    : "Modification d'un permis";

  return (
    <Container>
      <PageTitle icon={<IconPermis />} title={pageTitle} />
      <MyFormik
        initialValues={getInitialValues({
          ...resolvedData.permis,
          permisId: permisId,
          permisCoordonneeX: coordonneeX,
          permisCoordonneeY: coordonneeY,
          permisSrid: srid,
          permisDatePermis: resolvedData.permis.permisDatePermis
            ? formatForDateInput(resolvedData.permis.permisDatePermis)
            : null,
          permisCadastreParcelle: resolvedData.permisCadastreParcelle,
          voieSaisieText:
            resolvedData.permis.permisVoieText !== null ||
            resolvedData.permis.permisVoieText.trim() !== "",
          permisLastUpdateDate: formatDateHeure(
            new Date(resolvedData.permisLastUpdateDate),
          ),
          permisInstructeurUsername: resolvedData.permisInstructeurUsername,
        })}
        isPost={false}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
        submitUrl={`/api/permis/${permisId}`}
      >
        <Permis />
      </MyFormik>
    </Container>
  );
};

type UpdatePermisType = {
  permisId: string;
  coordonneeX: number;
  coordonneeY: number;
  srid: string;
  onSubmit: () => void;
};

export default UpdatePermis;
