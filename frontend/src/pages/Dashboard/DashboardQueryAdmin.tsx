import { useRef, useState } from "react";
import { Row, Col } from "react-bootstrap";
import { Outlet } from "react-router-dom";
import url from "../../module/fetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { FormContainer } from "../../components/Form/Form.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import ConfigForm from "./QueryAdminDynamicForm/ConfigForm.tsx";
import QueryForm from "./QueryAdminDynamicForm/QueryForm.tsx";
import QueryList from "./QueryAdminDynamicForm/QueryList.tsx";
import ConfigDynamicComponent from "./QueryAdminDynamicForm/ConfigDynamicComponent.tsx";
import { ComponentDashboard, QueryParam } from "./Constants.tsx";

const ComponentBoardQueryAdmin = () => {
  const { error: errorToast } = useToastContext();
  const [queryGlobalData, setQueryGlobalData] = useState<any | null>(null); // Données globales retournées par la requête
  const [data, setData] = useState<any | null>(); // Données mappées pour le composant à l'écran

  const [activeQuery, setActiveQuery] = useState<QueryParam | null>(null); // Requête actif
  const [openListQuery, setOpenListQuery] = useState<QueryParam[] | null>(); // Liste des requêtes

  const [selectedComponent, setSelectedComponent] =
    useState<ComponentDashboard | null>(null); // Index composant ouvert actif
  const [openListComponent, setOpenListComponent] =
    useState<ComponentDashboard[]>(); // Liste composants de la requête

  const [availableOptions, setAvailableOptions] = useState([]); // liste des options disponibles pour les champs

  const [isAdding, setIsAdding] = useState(false);
  const formikRef = useRef(); // Créer une référence pour Formik

  const urlApiRegister = url`/api/dashboard/create-query`;
  const urlApiUpdateRegister = url`/api/dashboard/update-query`;

  const getPrepareVariables = () => {
    const componentData: { id: number; key: any; title: any; config: any }[] =
      []; // Liste des onglets ouverts

    // Liste des composants paramétrés pour la requête
    const components = openListComponent
      ? openListComponent.map((component) => {
          const componentInfo = {
            ...(component.id && { componentId: component.id }),
            ...(component.queryId && { componentQueryId: component.queryId }),
            componentKey: component.key,
            componentTitle: component.title,
            componentConfig: JSON.stringify(component.config),
          };

          componentData.push(componentInfo); // Ajout au tableau global
          return componentInfo; // Ajout au tableau local
        })
      : null;

    if (components) {
      const queryComponentData = {
        ...(activeQuery?.id, { queryId: activeQuery?.id }),
        queryTitle: activeQuery?.title ?? "",
        queryQuery: activeQuery?.query ?? "",
        queryComponents: components,
      };

      return queryComponentData;
    } else {
      errorToast("Veuillez ajouter au moins un composant");
    }
  };

  const getInitialValues = () => ({
    titleComponent: selectedComponent ? selectedComponent.title : "",
  });

  const toSubmit = () => {
    setIsAdding(false);
    setIsAdding(false);
    setSelectedComponent(null);
    setActiveQuery(null);
    setOpenListComponent([]);
    setOpenListQuery(null);
  };

  return (
    <>
      <Outlet />

      <Row>
        <Col sm={3}>
          <QueryList
            setData={setData}
            openListQuery={openListQuery}
            setOpenListQuery={setOpenListQuery}
            activeQuery={activeQuery}
            setActiveQuery={setActiveQuery}
            openListComponent={openListComponent}
            setOpenListComponent={setOpenListComponent}
            setSelectedComponent={setSelectedComponent}
            queryGlobalData={queryGlobalData}
            setQueryGlobalData={setQueryGlobalData}
            setAvailableOptions={setAvailableOptions}
            formikRef={formikRef}
            isAdding={isAdding}
            setIsAdding={setIsAdding}
          />
        </Col>
        <Col sm={9}>
          {activeQuery ? (
            <QueryForm
              activeQuery={activeQuery}
              setActiveQuery={setActiveQuery}
              setQueryData={setQueryGlobalData}
              setAvailableOptions={setAvailableOptions}
            />
          ) : null}
        </Col>
        {activeQuery ? (
          <MyFormik
            initialValues={getInitialValues()}
            innerRef={formikRef}
            isPost={activeQuery.id ? false : true}
            prepareVariables={getPrepareVariables}
            submitUrl={activeQuery.id ? urlApiUpdateRegister : urlApiRegister}
            onSubmit={toSubmit}
            successToastMessage="La requête à est correctement enregistré"
          >
            <FormContainer>
              <Row>
                <Col sm={3}>
                  {/* Formulaire de configuration */}
                  <ConfigForm
                    openListComponent={openListComponent}
                    setOpenListComponent={setOpenListComponent}
                    selectedComponent={selectedComponent}
                    setSelectedComponent={setSelectedComponent}
                    availableOptions={availableOptions}
                    queryGlobalData={queryGlobalData}
                    setData={setData}
                  />
                </Col>
                <Col sm={9}>
                  {/* Contenu principal avec les onglets */}
                  <ConfigDynamicComponent
                    data={data}
                    setData={setData}
                    openListComponent={openListComponent}
                    setOpenListComponent={setOpenListComponent}
                    queryGlobalData={queryGlobalData}
                    seletedComponent={selectedComponent}
                    setSelectedComponent={setSelectedComponent}
                  />
                </Col>
              </Row>
            </FormContainer>
          </MyFormik>
        ) : null}
      </Row>
    </>
  );
};

export default ComponentBoardQueryAdmin;
