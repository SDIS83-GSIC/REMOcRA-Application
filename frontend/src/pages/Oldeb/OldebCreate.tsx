import { useLocation } from "react-router-dom";
import { WKT } from "ol/format";
import Container from "react-bootstrap/Container";
import { URLS } from "../../routes.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconOldeb } from "../../components/Icon/Icon.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import OldebForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./OldebForm.tsx";

const OldebCreate = () => {
  // En cas de création via la carte, on récupère les coordonnées passées dans le state
  const { state = {} } = useLocation();
  const { epsg: contextEpsg, srid: contextSrid } = useAppContext();
  const initialValues = getInitialValues();

  const { wkt: wkt = null, epsg: epsg = null, ...rest } = state;

  if (wkt && epsg) {
    // On récupère le WKT et on s'assure de repecter la projection du serveur
    const wkt = new WKT();
    const geometry = wkt.readFeature(state.wkt, {
      dataProjection: epsg, // Projection du WKT depuis la carte
      featureProjection: contextEpsg.name, // ESPG fourni par le serveur
    });
    initialValues.oldeb.oldebGeometrie = `SRID=${contextSrid};${wkt.writeFeature(geometry)}`;
    // On vide le state
    window.history.replaceState(rest, "");
  }

  return (
    <Container>
      <PageTitle
        icon={<IconOldeb />}
        title={"Création d'une Obligation Légale de Débroussaillement"}
      />
      {initialValues.oldeb.oldebGeometrie ? (
        <MyFormik
          initialValues={initialValues}
          validationSchema={validationSchema}
          isPost={true}
          isMultipartFormData={true}
          submitUrl={`/api/oldeb/create`}
          prepareVariables={(values) => prepareValues(values)}
          redirectUrl={URLS.OLDEB_LIST}
        >
          <OldebForm isNew={true} />
        </MyFormik>
      ) : (
        <>
          Impossible de créer une Obligation Légale de Débroussaillement sans
          utiliser l&apos;onglet Localisation.
        </>
      )}
    </Container>
  );
};

export default OldebCreate;
