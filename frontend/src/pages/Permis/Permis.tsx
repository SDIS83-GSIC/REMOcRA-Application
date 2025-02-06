import { useFormikContext } from "formik";
import { Button, Col, Row } from "react-bootstrap";
import PermisEntity from "../../Entities/PermisEntity.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  DateInput,
  FormContainer,
  Multiselect,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import url from "../../module/fetch.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

export const getInitialValues = (data: PermisEntity) => ({
  permisId: data?.permisId,
  permisLibelle: data?.permisLibelle,
  permisNumero: data?.permisNumero,
  permisServiceInstructeurId: data?.permisServiceInstructeurId,
  permisTypePermisInterserviceId: data?.permisTypePermisInterserviceId,
  permisTypePermisAvisId: data?.permisTypePermisAvisId,
  permisRiReceptionnee: data?.permisRiReceptionnee,
  permisDossierRiValide: data?.permisDossierRiValide,
  permisObservations: data?.permisObservations,
  permisVoieText: data?.permisVoieText,
  permisVoieId: data?.permisVoieId,
  permisComplement: data?.permisComplement,
  permisCommuneId: data?.permisCommuneId,
  permisAnnee: data?.permisAnnee,
  permisDatePermis: data?.permisDatePermis,

  permisCadastreParcelle: data.permisCadastreParcelle,

  permisCoordonneeX: data?.permisCoordonneeX,
  permisCoordonneeY: data?.permisCoordonneeY,
  permisSrid: data?.permisSrid,

  voieSaisieText: false,
});

export const prepareVariables = (values: PermisEntity) => ({
  permisId: values.permisId,
  permisLibelle: values.permisLibelle,
  permisNumero: values.permisNumero,
  permisServiceInstructeurId: values.permisServiceInstructeurId,
  permisTypePermisInterserviceId: values.permisTypePermisInterserviceId,
  permisTypePermisAvisId: values.permisTypePermisAvisId,
  permisRiReceptionnee: values.permisRiReceptionnee,
  permisDossierRiValide: values.permisDossierRiValide,
  permisObservations: values.permisObservations?.trim() || null,
  permisVoieText: values.voieSaisieText ? values.permisVoieText : null,
  permisVoieId: values.voieSaisieText ? null : values.permisVoieId,
  permisComplement: values.permisComplement?.trim() || null,
  permisCommuneId: values.permisCommuneId,
  permisAnnee: values.permisAnnee,
  permisDatePermis: values.permisDatePermis
    ? new Date(values.permisDatePermis).toISOString()
    : null,

  permisCadastreParcelle: values.permisCadastreParcelle,

  permisGeometrie:
    "SRID=" +
    values.permisSrid +
    ";POINT(" +
    values.permisCoordonneeX +
    " " +
    values.permisCoordonneeY +
    ")",
});

const Permis = () => {
  const { values, setFieldValue }: { values: any } = useFormikContext();
  const { user }: { user: UtilisateurEntity } = useAppContext();

  const fetchPermisData = useGet(
    url`/api/permis/fetchPermisData?${{
      coordonneeX: values.permisCoordonneeX.toString(),
      coordonneeY: values.permisCoordonneeY.toString(),
      srid: values.permisSrid,
    }}`,
  );

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(PARAMETRE.VOIE_SAISIE_LIBRE),
    }}`,
  );

  if (!fetchPermisData.isResolved || !listeParametre.isResolved) {
    return;
  }

  const isSaisieVoieTextEnabled = JSON.parse(
    listeParametre?.data[PARAMETRE.VOIE_SAISIE_LIBRE].parametreValeur,
  );

  const permisData: {
    communeData: IdCodeLibelleType & { pprif: boolean };
    listeVoie: (IdCodeLibelleType & { communeId: string })[];
    listeAvis: (IdCodeLibelleType & { pprif: boolean })[];
    listeInterservice: (IdCodeLibelleType & { pprif: boolean })[];
    listeServiceInstructeur: IdCodeLibelleType[];
    listeCadastreParcelle: IdCodeLibelleType[];
  } = fetchPermisData.data;

  // On ne laisse pas le choix de la commune, le permis est déclaré là où le point à été posé
  if (values.permisCommuneId === "") {
    setFieldValue("permisCommuneId", permisData.communeData.id);
  }

  const communeAsListe: IdCodeLibelleType[] = [
    {
      id: permisData.communeData.id,
      code: permisData.communeData.code,
      libelle: permisData.communeData.libelle,
    },
  ];
  const filteredListeAvis = permisData.listeAvis.filter(
    (e) => e.pprif === permisData.communeData.pprif || e.code === "ATTENTE",
  );
  const filteredlisteInterservice = permisData.listeInterservice.filter(
    (e) => e.pprif === permisData.communeData.pprif,
  );

  const sectionCadastrale = [
    ...new Set(
      permisData.listeCadastreParcelle
        ?.filter((a) => values.permisCadastreParcelle?.includes(a.id))
        ?.map((e) => e.code),
    ),
  ].join(", ");

  return (
    <FormContainer>
      <Row>
        <TextInput name="permisLibelle" label="Nom" />
      </Row>
      <Row>
        <DateInput name="permisDatePermis" label="Date permis" />
      </Row>
      <Row>
        <SelectForm
          name="permisCommuneId"
          listIdCodeLibelle={communeAsListe}
          label="Commune"
          defaultValue={communeAsListe[0]}
          required={true}
          setFieldValue={setFieldValue}
          disabled={true}
        />
      </Row>
      <Row>
        <SelectForm
          name="permisVoieId"
          listIdCodeLibelle={permisData.listeVoie}
          label="Voie"
          defaultValue={permisData.listeVoie.find(
            (e) => e.id === values.permisVoieId,
          )}
          required={false}
          setFieldValue={setFieldValue}
          disabled={values.voieSaisieText}
        />
      </Row>
      {isSaisieVoieTextEnabled && (
        <Row>
          <Col>
            <CheckBoxInput name="voieSaisieText" label="Voie non trouvée ?" />
            {values.voieSaisieText && (
              <TextInput
                name="permisVoieText"
                label="Voie (saisie libre)"
                required={false}
              />
            )}
          </Col>
        </Row>
      )}
      <Row>
        <TextInput
          name="permisComplement"
          label="Complément adresse"
          required={false}
        />
      </Row>
      <Row>
        <Multiselect
          name="permisCadastreParcelle"
          label="Parcelle cadastrale"
          options={permisData.listeCadastreParcelle}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.code + " - " + t.libelle}
          value={
            values.permisCadastreParcelle?.map((e) =>
              permisData.listeCadastreParcelle?.find(
                (r: IdCodeLibelleType) => r.id === e,
              ),
            ) ?? undefined
          }
          onChange={(parcelle) => {
            const parcelleId = parcelle.map((e) => e.id);
            parcelleId.length > 0
              ? setFieldValue(`permisCadastreParcelle`, parcelleId)
              : setFieldValue(`permisCadastreParcelle`, undefined);
          }}
          isClearable={false}
          required={false}
          tooltipText="Les options proposées sont les 25 parcelles les plus proches du point de déclaration du permis."
        />
      </Row>
      {values.permisCadastreParcelle?.length > 0 && (
        <Row>
          <p>Section cadastrale : {sectionCadastrale}</p>
        </Row>
      )}
      <Row>
        <TextInput name="permisNumero" label="N° permis" />
      </Row>
      <Row>
        <Col>
          <CheckBoxInput
            name="permisDossierRiValide"
            label="Dossier RI validé"
          />
        </Col>
        <Col>
          <CheckBoxInput name="permisRiReceptionnee" label="RI réceptionnée" />
        </Col>
      </Row>
      <Row>
        <SelectForm
          name="permisTypePermisAvisId"
          listIdCodeLibelle={filteredListeAvis}
          label="Avis"
          defaultValue={filteredListeAvis.find(
            (e) => e.id === values.permisTypePermisAvisId,
          )}
          required={true}
          setFieldValue={setFieldValue}
        />
      </Row>
      <Row>
        <SelectForm
          name="permisTypePermisInterserviceId"
          listIdCodeLibelle={filteredlisteInterservice}
          label="Interservice"
          defaultValue={filteredlisteInterservice.find(
            (e) => e.id === values.permisTypePermisInterserviceId,
          )}
          required={true}
          setFieldValue={setFieldValue}
        />
      </Row>
      <Row>
        <TextAreaInput
          name="permisObservations"
          label="Observations"
          required={false}
        />
      </Row>
      <Row>
        <SelectForm
          name="permisServiceInstructeurId"
          listIdCodeLibelle={permisData.listeServiceInstructeur}
          label="Service instructeur"
          defaultValue={permisData.listeServiceInstructeur.find(
            (e) => e.id === values.permisServiceInstructeurId,
          )}
          required={true}
          setFieldValue={setFieldValue}
        />
      </Row>
      <Row>
        <PositiveNumberInput name="permisAnnee" label="Année" />
      </Row>
      <Row>
        <p>Dernière modification : {formatDateTime(new Date())}</p>
        <p>Instructeur : {user.username}</p>
      </Row>
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Valider
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

export default Permis;
