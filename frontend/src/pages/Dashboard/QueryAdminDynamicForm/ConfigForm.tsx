import { Card } from "react-bootstrap";
import { TextInput } from "../../../components/Form/Form.tsx";
import { ComponentDashboard } from "../Constants.tsx";

export type ExtraField = { [x: string]: { fieldName: any } }; // Champs pour composant Table

type ConfigFormProps = {
  openListComponent: ComponentDashboard[] | undefined | null;
  setOpenListComponent: (arg0: any) => void;
  selectedComponent: ComponentDashboard | null;
  setSelectedComponent: (arg0: any) => void;
  availableOptions: any[];
  queryGlobalData: any[];
  setData: (arg0: any) => void;
};

const ConfigForm = (props: ConfigFormProps) => {
  // Config pour l'initialisation des champs
  const config = props.selectedComponent
    ? props.selectedComponent.config
    : null;

  // Options disponibles pour les champs select
  const fieldOptions = props.availableOptions.map((key: any) => ({
    value: key,
    label: key,
  }));

  // Mise à jour de la config et des datas après chaque changement
  const setConfig = (newConfig: any) => {
    const updateSelectedTab = { ...props.selectedComponent, config: newConfig };
    const updateOpenListComponent = props.openListComponent
      ? props.openListComponent.map((tab) =>
          tab.index === updateSelectedTab.index ? updateSelectedTab : tab,
        )
      : null;
    props.setSelectedComponent(updateSelectedTab);
    props.setOpenListComponent(updateOpenListComponent);
  };

  // Mise à jour du titre du composant
  const setTitleActive = (newTitle: string) => {
    const updateSelectedTab = { ...props.selectedComponent, title: newTitle };
    const newOpenListComponent = props.openListComponent
      ? props.openListComponent.map((tab) =>
          tab.index === props.selectedComponent?.index
            ? updateSelectedTab
            : tab,
        )
      : null;
    props.setSelectedComponent(updateSelectedTab);
    props.setOpenListComponent(newOpenListComponent);
  };

  return (
    <>
      {props.selectedComponent && (
        <Card bg="secondary" className="m-3">
          <Card.Body>
            {props.selectedComponent && props.selectedComponent.formConfig && (
              <>
                <TextInput
                  required={false}
                  name="titleComponent"
                  label="Titre composant"
                  onBlur={(e: any) => {
                    setTitleActive(e.target.value);
                  }}
                />
                <hr />
                <props.selectedComponent.formConfig
                  config={config}
                  fieldOptions={fieldOptions}
                  setConfig={setConfig}
                />
              </>
            )}
          </Card.Body>
        </Card>
      )}
    </>
  );
};

export default ConfigForm;
