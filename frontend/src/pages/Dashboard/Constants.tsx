import {
  IconCounterComponent,
  IconGaugeComponent,
  IconHorizontalChartComponent,
  IconMapComponent,
  IconPieChartComponent,
  IconTableComponent,
} from "../../components/Icon/Icon.tsx";
import CounterComponent from "./Compenents/Counter/CounterComponent.tsx";
import CounterConfig from "./Compenents/Counter/CounterConfig.tsx";
import GaugeComponent from "./Compenents/Gauge/GaugeComponent.tsx";
import GaugeConfig from "./Compenents/Gauge/GaugeConfig.tsx";
import HorizontalCharConfig from "./Compenents/HorizontalChar/HorizontalCharConfig.tsx";
import HorizontalChartComponent from "./Compenents/HorizontalChar/HorizontalChartComponent.tsx";
import MapComponent from "./Compenents/Map/MapComponent.tsx";
import MapConfig from "./Compenents/Map/MapConfig.tsx";
import PieChartComponent from "./Compenents/PieChart/PieChartComponent.tsx";
import PieChartConfig from "./Compenents/PieChart/PieChartConfig.tsx";
import TableComponent from "./Compenents/Table/TableComponent.tsx";
import TableConfig from "./Compenents/Table/TableConfig.tsx";

export const formatData = (data: { name: any; values: any }) => {
  // Format les données en sortie de service pour exploitation par les composants
  if (data) {
    const { name, values } = data;

    return values?.map((valueArray: { [x: string]: any }) => {
      const dict: { [key: string]: any } = {};
      name.forEach((key: string, index: string | number) => {
        dict[key] = valueArray[index];
      });
      return dict;
    });
  }
};

// Convertit les données en entrée en type attendu par le composant
export const fieldType = {
  TEXT: {
    type: "text",
    valueType: "string",
    validate: (value: string) => typeof value === "string",
  },
  NUMBER: {
    type: "number",
    valueType: "number",
    validate: (value: number) => !isNaN(value),
  },
  SELECT: {
    type: "select",
    valueType: "string",
    validate: (value: any) => Array.isArray(value),
  },
};

/* Liste des types de données par composant
 *
 * initField : valeur initial par défaut
 */
export const getDataTypePieChart = {
  name: "",
  value: "",
};

export const getDataTypeGauge = {
  value: "",
  max: "",
  limits: [],
};

export const getHorizontalChart = {
  name: "",
  value: "",
  max: "",
};

export const getTable = [];

export const getCounter = {
  label: "",
  value: 0,
};

export const getMap = {
  geojson: "",
};

/* Liste des dataTypes associés au composant respectif
 *
 * includeFormFieldType : Boolean // si true génère les Form HTML associé au composant
 */
export const INIT_DATA = {
  PIECHART: getDataTypePieChart,
  GAUGE: getDataTypeGauge,
  HORIZONTALCHAR: getHorizontalChart,
  TABLE: getTable,
  COUNTER: getCounter,
  MAP: getMap,
};

// Liste des composants
export const COMPONENTS = {
  PIECHART: PieChartComponent,
  GAUGE: GaugeComponent,
  HORIZONTALCHAR: HorizontalChartComponent,
  TABLE: TableComponent,
  COUNTER: CounterComponent,
  MAP: MapComponent,
};

// Liste des formulaire de configuration
export const FORM_CONFIG = {
  PIECHART: PieChartConfig,
  GAUGE: GaugeConfig,
  HORIZONTALCHAR: HorizontalCharConfig,
  TABLE: TableConfig,
  COUNTER: CounterConfig,
  MAP: MapConfig,
};

// Liste des icones des composants
export const ICONS = {
  PIECHART: IconPieChartComponent(),
  GAUGE: IconGaugeComponent(),
  HORIZONTALCHAR: IconHorizontalChartComponent(),
  TABLE: IconTableComponent(),
  COUNTER: IconCounterComponent(),
  MAP: IconMapComponent(),
};

// Liste des types d'objets utilisés dans le module
export type QueryData = {
  name: string[];
  queryId: string;
  values: any[];
  queryTitle: string;
  querySql: string;
};

export type QueryDataFormated = QueryData & {
  data: any;
  id: string;
};

export type ComponentDashboard = {
  index?: number;
  id: any;
  queryId: string;
  dashboardId?: string;
  key: any;
  title: any;
  config: any;
  configPosition?: { x: number; y: number; largeur: number; hauteur: number };
  component?: any;
  formConfig?: any;
  data?: any;
};

export type DashboardParam = {
  id: string;
  title: string;
  components: any;
};

export type QueryParam = {
  id?: string;
  query: string;
  title: string;
};

export type DashboardItemParam = {
  id?: string;
  title: string;
  index?: number;
};

export type DashboardComponentConfig = {
  componentKey: any;
  componentId: string;
  componentQueryId: string;
  componentConfigPosition?: {
    componentLargeur: number;
    componentHauteur: number;
    componentX: number;
    componentY: number;
  };
  componentConfig: any;
  componentTitle: string;
};
