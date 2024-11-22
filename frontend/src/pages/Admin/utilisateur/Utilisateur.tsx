import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
import { Col, Row } from "react-bootstrap";
import { object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import {
  IconInfo,
  IconNextPage,
  IconPlus,
} from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { requiredEmail } from "../../../module/validators.tsx";
import { URLS } from "../../../routes.tsx";
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

type UtilisateurType = {
  utilisateurActif: boolean;
  utilisateurEmail: string;
  utilisateurNom: string;
  utilisateurPrenom: string;
  utilisateurUsername: string;
  utilisateurTelephone: string;
  utilisateurCanBeNotified: boolean;
  utilisateurProfilUtilisateurId: string;
  utilisateurOrganismeId: string;
  utilisateurIsSuperAdmin: boolean;
};

export const getInitialValues = (data?: UtilisateurType) => ({
  utilisateurActif: data?.utilisateurActif ?? true,
  utilisateurEmail: data?.utilisateurEmail ?? null,
  utilisateurNom: data?.utilisateurNom ?? null,
  utilisateurPrenom: data?.utilisateurPrenom ?? null,
  utilisateurTelephone: data?.utilisateurTelephone ?? null,
  utilisateurUsername: data?.utilisateurUsername ?? null,
  utilisateurCanBeNotified: data?.utilisateurCanBeNotified ?? true,
  utilisateurProfilUtilisateurId: data?.utilisateurProfilUtilisateurId ?? null,
  utilisateurOrganismeId: data?.utilisateurOrganismeId ?? null,
  utilisateurIsSuperAdmin: data?.utilisateurIsSuperAdmin ?? false,
});

export const validationSchema = object({
  utilisateurEmail: requiredEmail,
});

export const prepareVariables = (values: UtilisateurType) => ({
  utilisateurActif: values?.utilisateurActif,
  utilisateurEmail: values.utilisateurEmail,
  utilisateurNom: values.utilisateurNom,
  utilisateurPrenom: values.utilisateurPrenom,
  utilisateurTelephone: values.utilisateurTelephone,
  utilisateurUsername: values.utilisateurUsername,
  utilisateurCanBeNotified: values.utilisateurCanBeNotified,
  utilisateurProfilUtilisateurId: values.utilisateurProfilUtilisateurId,
  utilisateurOrganismeId: values.utilisateurOrganismeId,
  utilisateurIsSuperAdmin: values.utilisateurIsSuperAdmin,
});

const Utilisateur = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  const [profilDroitDeduit, setProfilDroitDeduit] = useState<string>();
  const { data: organismeList } = useGet(url`/api/organisme/get-all`);
  const { data: profilUtilisateurList } = useGet(url`/api/profil-utilisateur`);
  const { data: profilDroitWithProfilsList } = useGet(
    url`/api/profil-droit/profils`,
  );

  const { values, setValues } = useFormikContext<UtilisateurType>();

  useEffect(() => {
    setProfilDroitDeduit(
      profilDroitWithProfilsList?.find(
        (e) =>
          e.profilUtilisateurId === values.utilisateurProfilUtilisateurId &&
          e.profilOrganismeId ===
            organismeList?.find((e) => e.id === values.utilisateurOrganismeId)
              ?.lienId,
      )?.libelle,
    );
  }, [
    profilDroitWithProfilsList,
    organismeList,
    profilUtilisateurList,
    values.utilisateurProfilUtilisateurId,
    values.utilisateurOrganismeId,
  ]);

  return (
    <FormContainer>
      <Row className="mt-3">
        <Col>
          <CheckBoxInput name="utilisateurActif" label="Actif" />
        </Col>
        <Col>
          <CheckBoxInput
            name="utilisateurCanBeNotified"
            label="Peut être notifié ?"
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <TextInput label="Email" name="utilisateurEmail" required={true} />
        </Col>
        <Col>
          <TextInput
            label="Identifiant"
            name="utilisateurUsername"
            required={true}
          />
        </Col>
        <Col>
          <TextInput
            label="Téléphone"
            name="utilisateurTelephone"
            required={false}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <TextInput label="Nom" name="utilisateurNom" required={true} />
        </Col>
        <Col>
          <TextInput label="Prénom" name="utilisateurPrenom" required={true} />
        </Col>
      </Row>
      {user.isSuperAdmin && (
        <Row className="mt-3">
          <Col>
            <TooltipCustom
              tooltipId={"superAdmin"}
              tooltipText={
                "Si vous cochez cette case, l'utilisateur aura tous les droits sur l'application indépendamment d'une zone de compétence."
              }
            >
              <CheckBoxInput
                name="utilisateurIsSuperAdmin"
                label="Est super administrateur ?"
              />
            </TooltipCustom>
          </Col>
          {/* TODO trouver mieux pour que la tooltip soit en phase avec l'élément */}
          <Col> </Col> <Col> </Col>
        </Row>
      )}
      {((!values.utilisateurIsSuperAdmin && user.isSuperAdmin) ||
        !values.utilisateurIsSuperAdmin) && (
        <Row className="mt-3">
          <Col>
            <SelectForm
              name={"utilisateurOrganismeId"}
              listIdCodeLibelle={organismeList}
              label="Organisme"
              defaultValue={organismeList?.find(
                (e) => e.id === values.utilisateurOrganismeId,
              )}
              required={true}
              setValues={setValues}
            />
          </Col>
          <Col className="mt-3  d-flex align-items-center justify-content-center display-6">
            <IconPlus />
          </Col>
          <Col>
            <SelectForm
              name={"utilisateurProfilUtilisateurId"}
              listIdCodeLibelle={profilUtilisateurList}
              label="Profil utilisateur"
              defaultValue={profilUtilisateurList?.find(
                (e) => e.id === values.utilisateurProfilUtilisateurId,
              )}
              required={true}
              setValues={setValues}
            />
          </Col>
          <Col className="text-center d-flex align-items-center justify-content-center display-6">
            <IconNextPage />
          </Col>
          <Col className="bg-light p-3 border rounded">
            <div className="fw-bold p-2 text-center">
              <IconInfo /> Profil droit
            </div>
            <div className="text-center">
              {profilDroitDeduit != null ? (
                <>
                  Le groupe de fonctionnalités qui sera utilisé pour cet
                  utilisateur sera : <b>{profilDroitDeduit}</b>
                </>
              ) : (
                "Aucun groupe de fonctionnalités trouvé."
              )}
            </div>
          </Col>
        </Row>
      )}
      <SubmitFormButtons returnLink={URLS.LIST_UTILISATEUR} />
    </FormContainer>
  );
};

export default Utilisateur;
