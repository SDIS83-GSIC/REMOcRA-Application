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
  deplacement = false,
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

  // Si le volet est ouvert suite à un déplacement sur la carte,
  // on réinitialise commune, voie, complément, parcelles.
  const contextDeplacement = !!deplacement;

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
            : !!(
                resolvedData.permis.permisVoieText &&
                resolvedData.permis.permisVoieText.trim() !== ""
              ),
          permisVoieId: contextDeplacement
            ? undefined
            : (resolvedData.permis.permisVoieId ?? undefined),
          permisVoieText: contextDeplacement
            ? undefined
            : (resolvedData.permis.permisVoieText ?? undefined),
          permisComplement: contextDeplacement
            ? undefined
            : (resolvedData.permis.permisComplement ?? undefined),
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
  readOnly?: boolean;
  deplacement?: boolean;
};

export default UpdatePermis;
