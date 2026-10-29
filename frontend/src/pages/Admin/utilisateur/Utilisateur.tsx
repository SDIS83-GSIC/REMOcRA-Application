import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
import { Alert, Col, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { object } from "yup";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import {
  IconAdd,
  IconInfo,
  IconNextPage,
  IconWarning,
} from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import url from "../../../module/fetch.tsx";
import { requiredEmail } from "../../../module/validators.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

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
  utilisateurFromLdap?: boolean;
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
  utilisateurFromLdap: values.utilisateurFromLdap ?? false,
});

const Utilisateur = () => {
  const { user } = useAppContext();
  const { utilisateurId } = useParams();

  const [groupeFonctionnalitesDeduit, setGroupeFonctionnalitesDeduit] =
    useState<string>();
  const { data: organismeList } = useGet(url`/api/organisme/get-all`);
  const { data: profilUtilisateurList } = useGet(url`/api/profil-utilisateur`);
  const { data: groupeFonctionnalitesWithProfilsList } = useGet(
    url`/api/groupe-fonctionnalites/profils`,
  );

  const { values, setValues, setFieldValue } =
    useFormikContext<UtilisateurType>();

  // Vérification de l'origine LDAP de l'utilisateur.
  const { error: fromLdapError } = useGet(
    utilisateurId
      ? url`/api/utilisateur/get/` + utilisateurId + `/from-ldap`
      : "",
    {
      onResolve: (data: { utilisateurFromLdap: boolean }) =>
        setFieldValue(
          "utilisateurFromLdap",
          Boolean(data?.utilisateurFromLdap),
        ),
      onReject: () => setFieldValue("utilisateurFromLdap", false),
    },
  );
  const utilisateurFromLdap = Boolean(values.utilisateurFromLdap);

  useEffect(() => {
    setGroupeFonctionnalitesDeduit(
      groupeFonctionnalitesWithProfilsList?.find(
        (e: {
          profilUtilisateurId: string;
          profilOrganismeId: string;
          libelle: string;
        }) =>
          e.profilUtilisateurId === values.utilisateurProfilUtilisateurId &&
          e.profilOrganismeId ===
            organismeList?.find(
              (e: { id: string }) => e.id === values.utilisateurOrganismeId,
            )?.lienId,
      )?.libelle,
    );
  }, [
    groupeFonctionnalitesWithProfilsList,
    organismeList,
    values.utilisateurProfilUtilisateurId,
    values.utilisateurOrganismeId,
  ]);

  return (
    <FormContainer>
      {utilisateurFromLdap && (
        <Row className="mt-3">
          <Col>
            <Alert variant="info">
              <IconInfo /> Cet utilisateur provient du LDAP : l'identifiant,
              l'email, le nom et le prénom ne peuvent pas être modifiés depuis
              REMOcRA
            </Alert>
          </Col>
        </Row>
      )}
      {fromLdapError && (
        <Row className="mt-3">
          <Col>
            <Alert variant="warning">
              <IconWarning /> Impossible de vérifier si cet utilisateur provient
              du LDAP (Keycloak indisponible). Il sera peut être impossible de
              modifier l'identifiant, l'email, le nom ou le prénom de cet
              utilisateur.
            </Alert>
          </Col>
        </Row>
      )}
      <Row className="mt-3">
        <Col>
          <CheckBoxInput name="utilisateurActif" label="Actif" />
        </Col>
        <Col>
          <CheckBoxInput
            name="utilisateurCanBeNotified"
            label="Peut être notifié"
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <TextInput
            label="Email"
            name="utilisateurEmail"
            required={true}
            disabled={utilisateurFromLdap}
          />
        </Col>
        <Col>
          <TextInput
            label="Identifiant"
            name="utilisateurUsername"
            required={true}
            disabled={utilisateurFromLdap}
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
          <TextInput
            label="Nom"
            name="utilisateurNom"
            required={false}
            disabled={utilisateurFromLdap}
          />
        </Col>
        <Col>
          <TextInput
            label="Prénom"
            name="utilisateurPrenom"
            required={false}
            disabled={utilisateurFromLdap}
          />
        </Col>
      </Row>
      {user?.isSuperAdmin && (
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
                label="Est super administrateur"
              />
            </TooltipCustom>
          </Col>
          {/* TODO trouver mieux pour que la tooltip soit en phase avec l'élément */}
          <Col> </Col> <Col> </Col>
        </Row>
      )}
      {((!values.utilisateurIsSuperAdmin && user?.isSuperAdmin) ||
        !values.utilisateurIsSuperAdmin) && (
        <Row className="mt-3">
          <Col>
            <SelectForm
              name={"utilisateurOrganismeId"}
              listIdCodeLibelle={organismeList}
              label="Organisme"
              defaultValue={organismeList?.find(
                (e: IdCodeLibelleType) =>
                  e.id === values.utilisateurOrganismeId,
              )}
              onChange={(e) => {
                setFieldValue("utilisateurOrganismeId", e?.id);
                setFieldValue("utilisateurProfilUtilisateurId", undefined);
              }}
              required={true}
            />
          </Col>
          <Col className="mt-3  d-flex align-items-center justify-content-center display-6">
            <IconAdd />
          </Col>
          <Col>
            <SelectForm
              name={"utilisateurProfilUtilisateurId"}
              listIdCodeLibelle={profilUtilisateurList?.filter(
                (profil: { id: string | any[] }) =>
                  groupeFonctionnalitesWithProfilsList
                    ?.filter(
                      (item: { profilOrganismeId: string }) =>
                        item.profilOrganismeId ===
                        organismeList?.find(
                          (e: { id: string }) =>
                            e.id === values.utilisateurOrganismeId,
                        )?.lienId,
                    )
                    .map(
                      (item: { profilUtilisateurId: any }) =>
                        item.profilUtilisateurId,
                    )
                    .includes(profil.id),
              )}
              label="Profil utilisateur"
              defaultValue={profilUtilisateurList?.find(
                (e: IdCodeLibelleType) =>
                  e.id === values.utilisateurProfilUtilisateurId,
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
              <IconInfo /> Groupe de fonctionnalités{" "}
              <span className="text-danger">*</span>
            </div>
            <div className="text-center">
              {groupeFonctionnalitesDeduit != null ? (
                <>
                  Le groupe de fonctionnalités qui sera utilisé pour cet
                  utilisateur sera : <b>{groupeFonctionnalitesDeduit}</b>
                </>
              ) : (
                "Aucun groupe de fonctionnalités trouvé."
              )}
            </div>
          </Col>
        </Row>
      )}
      <SubmitFormButtons
        returnLink={true}
        disabledValide={
          !groupeFonctionnalitesDeduit && !values.utilisateurIsSuperAdmin
        }
      />
    </FormContainer>
  );
};

export default Utilisateur;
