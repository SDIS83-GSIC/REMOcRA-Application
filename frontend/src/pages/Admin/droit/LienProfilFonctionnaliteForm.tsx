import { object } from "yup";
import { useFormikContext } from "formik";
import { FormContainer } from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { requiredString } from "../../../module/validators.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";

type LienType = {
  profilOrganismeId: string;
  profilUtilisateurId: string;
  profilDroitId: string;
};
export const getInitialValues = (data?: any) => ({
  profilOrganismeId: data?.profilOrganismeId ?? null,
  profilUtilisateurId: data?.profilUtilisateurId ?? null,
  profilDroitId: data?.profilDroitId ?? null,
});

export const prepareValues = (values: any) => ({
  profilOrganismeId: values.profilOrganismeId,
  profilUtilisateurId: values.profilUtilisateurId,
  profilDroitId: values.profilDroitId,
});

export const validationSchema = object({
  profilOrganismeId: requiredString,
  profilUtilisateurId: requiredString,
  profilDroitId: requiredString,
});

const LienProfilFonctionnaliteForm = ({
  returnLink,
}: {
  returnLink: string;
}) => {
  const { values, setFieldValue, setValues } = useFormikContext<LienType>();

  const lienProfilFonctionnaliteReferentielState = useGet(
    url`/api/lien-profil-fonctionnalite/referentiel`,
  );

  if (!lienProfilFonctionnaliteReferentielState.isResolved) {
    return <Loading />;
  }

  const { profilOrganismeList, profilUtilisateurList, profilDroitList } =
    lienProfilFonctionnaliteReferentielState.data;

  return (
    <FormContainer>
      <SelectForm
        name={"profilOrganismeId"}
        listIdCodeLibelle={profilOrganismeList.map((o) => {
          return {
            id: o.profilOrganismeId,
            libelle: o.profilOrganismeLibelle,
          };
        })}
        defaultValue={{ id: values.profilOrganismeId }}
        label={"Profil organisme"}
        setValues={setValues}
        required={true}
        onChange={(e) => {
          setFieldValue("profilOrganismeId", e.target.value);
          const newValue = profilOrganismeList.find(
            (p) => p.profilOrganismeId === e.target.value,
          );
          const currentUtilisateur = profilUtilisateurList.find(
            (u) => u.profilUtilisateurId === values.profilUtilisateurId,
          );
          if (
            !newValue ||
            (currentUtilisateur &&
              newValue.profilOrganismeTypeOrganismeId !==
                currentUtilisateur.profilUtilisateurTypeOrganismeId)
          ) {
            setFieldValue("profilUtilisateurId", null);
          }
        }}
      />
      <SelectForm
        name={"profilUtilisateurId"}
        listIdCodeLibelle={profilUtilisateurList
          .filter(
            (u) =>
              u.profilUtilisateurTypeOrganismeId ===
              profilOrganismeList.find(
                (p) => p.profilOrganismeId === values.profilOrganismeId,
              )?.profilOrganismeTypeOrganismeId,
          )
          .map((u) => {
            return {
              id: u.profilUtilisateurId,
              libelle: u.profilUtilisateurLibelle,
            };
          })}
        defaultValue={{ id: values.profilUtilisateurId }}
        label={"Profil utilisateur"}
        setValues={setValues}
        required={true}
      />
      <SelectForm
        name={"profilDroitId"}
        listIdCodeLibelle={profilDroitList.map((d) => {
          return {
            id: d.profilDroitId,
            libelle: d.profilDroitLibelle,
          };
        })}
        defaultValue={{ id: values.profilDroitId }}
        label={"Profil droit"}
        setValues={setValues}
        required={true}
      />
      <SubmitFormButtons returnLink={returnLink} />
    </FormContainer>
  );
};

export default LienProfilFonctionnaliteForm;
