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
  const queryListRef = useRef<HTMLDivElement>();

  const { error: errorToast } = useToastContext();
  const [queryGlobalData, setQueryGlobalData] = useState<any | null>(null); // Données globales retournées par la requête
  const [data, setData] = useState<any | null>(); // Données mappées pour le composant à l'écran

  const [activeQuery, setActiveQuery] = useState<QueryParam | null>(null); // Requête actif

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
      openListComponent
        ? openListComponent.map((component) => {
            return {
              ...(component.id && { componentId: component.id }),
              ...(component.queryId && { componentQueryId: component.queryId }),
              componentKey: component.key,
              componentTitle: component.title,
              componentConfig: JSON.stringify(component.config),
            };
          })
        : null;

    if (componentData) {
      const queryComponentData = {
        ...(activeQuery?.id, { queryId: activeQuery?.id }),
        queryTitle: activeQuery?.title ?? "",
        queryQuery: activeQuery?.query ?? "",
        queryComponents: componentData,
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
    setOpenListComponent(null);
    queryListRef.current.setOpenListQuery(null);
  };

  return (
    <>
      <Outlet />

      <Row>
        <Col sm={3}>
          <QueryList
            ref={queryListRef}
            setData={setData}
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
          {activeQuery && (
            <QueryForm
              activeQuery={activeQuery}
              setActiveQuery={setActiveQuery}
              setQueryData={setQueryGlobalData}
              setAvailableOptions={setAvailableOptions}
            />
          )}
        </Col>
        {activeQuery && (
          <MyFormik
            initialValues={getInitialValues()}
            innerRef={formikRef}
            isPost={activeQuery.id ? false : true}
            prepareVariables={getPrepareVariables}
            submitUrl={activeQuery.id ? urlApiUpdateRegister : urlApiRegister}
            onSubmit={toSubmit}
            successToastMessage="La requête à été correctement enregistrée"
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
                    selectedComponent={selectedComponent}
                    setSelectedComponent={setSelectedComponent}
                  />
                </Col>
              </Row>
            </FormContainer>
          </MyFormik>
        )}
      </Row>
    </>
  );
};

export default ComponentBoardQueryAdmin;
