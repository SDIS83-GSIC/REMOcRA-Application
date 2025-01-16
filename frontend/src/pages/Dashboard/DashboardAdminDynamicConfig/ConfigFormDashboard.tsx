import { Alert, Row, Col, Button } from "react-bootstrap";
import { useFormikContext } from "formik";
import { useState, useEffect, useMemo } from "react";
import {
  FormContainer,
  TextInput,
  Multiselect,
  NumberInput,
} from "../../../components/Form/Form.tsx";
import { IconInfo } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import { ComponentDashboard } from "../Constants.tsx";
import ConfirmModal from "../../../components/Modal/ConfirmModal.tsx";
import { useGetRun } from "../../../components/Fetch/useFetch.tsx";

type ConfigFormDashboardProps = {
  editTabIndex: number | undefined | null;
  componentSelected: ComponentDashboard | null | undefined;
  setComponentSelected: (arg0: any) => void;
  componentsListDashboard: ComponentDashboard[];
  setComponentsListDashboard: (arg0: any) => void;
  setcomponentListIdSelected: (arg0: string) => void;
  numberRowGrid: number;
  setNumberRowGrid: (arg0: number) => void;
  removeRowUnused: (arg0: ComponentDashboard[]) => void;
  dashboardProfilsUtilisateur: any[] | null;
  dashboardTitle: string;
  setDashboardTitle: (arg0: any) => void;
  setDashboardProfil: (arg0: any) => void;
};

const ConfigFormDashboard = (props: ConfigFormDashboardProps) => {
  const { values, setFieldValue } = useFormikContext<any>();
  const { error: errorToast } = useToastContext();

  const [disabledModal, setDisabledModal] = useState(false); // Affiche la modal de confirmation

  const [profilUtilisateurList, setProfilUtilisateurList] = useState<any[]>([]); // Liste des profils utilisateurs

  const urlApiProfilUtilisateurList = url`/api/dashboard/get-dashboard-profil-available/`;

  // Récupère la liste de tous les profils utilisateur
  const { run, isResolved, data } = useGetRun(urlApiProfilUtilisateurList, {});
  useMemo(() => {
    if (isResolved) {
      setProfilUtilisateurList(data);
    }
  }, [data, isResolved]);

  const handleChangeSize = (size: string, id: number) => {
    // Change la taille du composant
    if (props.componentsListDashboard) {
      const componentFiltered = props.componentsListDashboard.find(
        (component) => component.id === id,
      );
      // Vérifie qu'on ne dépasse pas la grille
      if (
        componentFiltered &&
        componentFiltered.configPosition &&
        componentFiltered.configPosition.x + parseInt(size) <= 4 &&
        componentFiltered.configPosition.y + parseInt(size) <=
          props.numberRowGrid
      ) {
        componentFiltered.configPosition.size = parseInt(size);
        props.setComponentSelected(componentFiltered);

        const newComponentList = props.componentsListDashboard.map(
          (component) =>
            component.index === id ? componentFiltered : component,
        );
        props.setComponentsListDashboard(newComponentList);
        props.removeRowUnused(newComponentList);
      } else {
        errorToast("Le composant ne doit pas dépasser de la grille");
      }
    }
  };

  const handleRemoveComponent = () => {
    // Supprime de la grille le composant sélectionné
    if (props.componentSelected) {
      const newComponentList = props.componentsListDashboard.filter(
        (componentInList: { id: any }) =>
          componentInList.id !== props.componentSelected?.id,
      );
      props.setComponentsListDashboard(newComponentList);
      props.setComponentSelected(null);
      props.setcomponentListIdSelected("");

      props.removeRowUnused(newComponentList);
    }
  };

  useEffect(() => {
    if (profilUtilisateurList.length === 0) {
      run();
    }
  }, [profilUtilisateurList.length, run]);

  return (
    <FormContainer className="mb-3">
      {props.editTabIndex !== null && (
        <>
          <Alert variant="info" className="mb-3 d-flex align-items-center">
            <span>
              {" "}
              {IconInfo()} Sélectionner un composant dans la liste de droite
              pour l&apos;ajouter dans la grille, puis déplacer le composant
              voulu à l&apos;endroit voulu.{" "}
            </span>
          </Alert>
          <Row className="align-items-start">
            <Col xs={8}>
              <TextInput
                name="title"
                label="Titre du dashboard :"
                value={values.title ? values.title : props.dashboardTitle}
                onChange={(event) => {
                  if (!event) {
                    setFieldValue("title", "");
                    return;
                  }
                  props.setDashboardTitle(event.target.value);
                }}
              />
            </Col>
            <Col
              xs={4}
              style={{ zIndex: "11" }} // Si composant sélectionné, s'affiche par dessus
            >
              <Multiselect
                name="listGroupAuthorized"
                label="Liste des groupes autorisés :"
                options={profilUtilisateurList}
                value={
                  values.profilsId
                    ? profilUtilisateurList.filter((p) =>
                        values.profilsId.includes(p),
                      )
                    : props.dashboardProfilsUtilisateur
                }
                getOptionValue={(p) => p.profilUtilisateurId}
                getOptionLabel={(p) => p.profilUtilisateurCode}
                onChange={(profils) => {
                  if (!profils) {
                    setFieldValue("profilsId", []);
                    return;
                  }

                  props.setDashboardProfil(profils);
                }}
                isClearable={true}
                required={false}
              />
            </Col>
          </Row>
          <Row>
            <Col xs={3}>
              <Button type="submit" variant="success" className="mt-3">
                Enregistrer
              </Button>
            </Col>
          </Row>
        </>
      )}
      {props.componentSelected && (
        <>
          <hr />
          <div className="d-flex justify-content-between align-items-end">
            <NumberInput
              min={1}
              step={1}
              max="4"
              required={false}
              name="sizeComponent"
              label="Taille du composant (1-4) :"
              onChange={(e: any) => {
                handleChangeSize(e.target.value, props.componentSelected?.id);
              }}
            />
            <Button
              variant="danger"
              className="ms-2"
              onClick={() => setDisabledModal(true)}
            >
              Retirer le composant
            </Button>
          </div>
          {disabledModal && (
            <ConfirmModal
              visible={true}
              content="Retirer le composant ?"
              closeModal={() => setDisabledModal(false)}
              query={""}
              href="#"
              onConfirm={() => handleRemoveComponent()}
            />
          )}
        </>
      )}
    </FormContainer>
  );
};

export default ConfigFormDashboard;
