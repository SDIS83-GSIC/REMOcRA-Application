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
  groupeFonctionnalitesId: string;
};
export const getInitialValues = (data?: any) => ({
  profilOrganismeId: data?.profilOrganismeId ?? null,
  profilUtilisateurId: data?.profilUtilisateurId ?? null,
  groupeFonctionnalitesId: data?.groupeFonctionnalitesId ?? null,
});

export const prepareValues = (values: any) => ({
  profilOrganismeId: values.profilOrganismeId,
  profilUtilisateurId: values.profilUtilisateurId,
  groupeFonctionnalitesId: values.groupeFonctionnalitesId,
});

export const validationSchema = object({
  profilOrganismeId: requiredString,
  profilUtilisateurId: requiredString,
  groupeFonctionnalitesId: requiredString,
});

const LienProfilFonctionnaliteForm = () => {
  const { values, setFieldValue, setValues } = useFormikContext<LienType>();

  const lienProfilFonctionnaliteReferentielState = useGet(
    url`/api/lien-profil-fonctionnalite/referentiel`,
  );

  if (!lienProfilFonctionnaliteReferentielState.isResolved) {
    return <Loading />;
  }

  const {
    profilOrganismeList,
    profilUtilisateurList,
    groupeFonctionnalitesList,
  } = lienProfilFonctionnaliteReferentielState.data;

  const profilOrganismeCodeLibelleList = profilOrganismeList.map((o) => {
    return {
      id: o.profilOrganismeId,
      libelle: o.profilOrganismeLibelle,
    };
  });

  const profilUtilisateCodeLibelleList = profilUtilisateurList
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
    });

  const groupeFonctionnalitesCodeLibelleList = groupeFonctionnalitesList.map(
    (d) => {
      return {
        id: d.groupeFonctionnalitesId,
        libelle: d.groupeFonctionnalitesLibelle,
      };
    },
  );

  return (
    <FormContainer>
      <SelectForm
        name={"profilOrganismeId"}
        listIdCodeLibelle={profilOrganismeCodeLibelleList}
        defaultValue={profilOrganismeCodeLibelleList.find(
          (e) => e.id === values.profilOrganismeId,
        )}
        label={"Profil organisme"}
        setValues={setValues}
        required={true}
        onChange={(e) => {
          setFieldValue("profilOrganismeId", e?.id);
          const newValue = profilOrganismeList.find(
            (p) => p.profilOrganismeId === e?.id,
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
        listIdCodeLibelle={profilUtilisateCodeLibelleList}
        defaultValue={profilUtilisateCodeLibelleList.find(
          (e) => e.id === values.profilUtilisateurId,
        )}
        label={"Profil utilisateur"}
        setValues={setValues}
        required={true}
      />
      <SelectForm
        name={"groupeFonctionnalitesId"}
        listIdCodeLibelle={groupeFonctionnalitesCodeLibelleList}
        defaultValue={groupeFonctionnalitesCodeLibelleList.find(
          (e) => e.id === values.groupeFonctionnalitesId,
        )}
        label={"Groupe de fonctionnalités"}
        setValues={setValues}
        required={true}
      />
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default LienProfilFonctionnaliteForm;
