import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { Document } from "../../components/Form/FormDocuments.tsx";
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
  readOnly = false,
}: UpdatePermisType) => {
  const data = useGet(url`/api/permis/${permisId}`);

  if (!data.isResolved) {
    return;
  }

  const resolvedData: {
    permis: PermisEntity;
    permisDocument: Document[];
    permisCadastreParcelle: string[];
    permisLastUpdateDate: Date | undefined;
    permisInstructeurUsername: string;
  } = data.data;

  // Si mes coordonnées ont changé, c'est que j'ai déplacé mon permis
  // Dans ce cas là, je réinitilise la commune, voie_id, voie_text, complement, parcelles
  const contextDeplacement =
    !resolvedData.permis.permisGeometrie.includes(coordonneeX) &&
    !resolvedData.permis.permisGeometrie.includes(coordonneeY);

  const pageTitle = readOnly
    ? resolvedData.permis.permisLibelle
      ? `Visualisation du permis ${resolvedData.permis.permisLibelle}`
      : "Visualisation d'un permis"
    : resolvedData.permis.permisLibelle
      ? `Modification du permis ${resolvedData.permis.permisLibelle}`
      : "Modification d'un permis";

  return (
    <Container>
      <PageTitle
        icon={<IconPermis />}
        title={pageTitle}
        displayReturnButton={false}
      />
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
          permisCadastreParcelle: contextDeplacement
            ? []
            : resolvedData.permisCadastreParcelle,
          voieSaisieText: contextDeplacement
            ? false
            : resolvedData.permis.permisVoieText !== null ||
              resolvedData.permis.permisVoieText.trim() !== "",
          permisVoieId: contextDeplacement
            ? null
            : resolvedData.permis.permisVoieId,
          permisVoieText: contextDeplacement
            ? null
            : resolvedData.permis.permisVoieText,
          permisComplement: contextDeplacement
            ? null
            : resolvedData.permis.permisComplement,
          permisLastUpdateDate: resolvedData.permisLastUpdateDate
            ? formatDateHeure(new Date(resolvedData.permisLastUpdateDate))
            : null,
          permisInstructeurUsername: resolvedData.permisInstructeurUsername,

          documents: resolvedData.permisDocument ?? [],
        })}
        isPost={false}
        isMultipartFormData={true}
        prepareVariables={(values) =>
          prepareVariables(values, resolvedData.permisDocument)
        }
        onSubmit={onSubmit}
        submitUrl={`/api/permis/${permisId}`}
      >
        <Permis readOnly={readOnly} />
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
  readOnly: boolean;
};

export default UpdatePermis;
