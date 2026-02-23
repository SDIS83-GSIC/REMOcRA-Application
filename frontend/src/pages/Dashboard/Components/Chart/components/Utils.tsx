// Utilisations d'interfaces et pas de types, car cela définit
// les contrats auquel les objets doivent se conformer.

import { Margin } from "recharts/types/util/types";

export interface OptionsList {
  value: string;
}

export interface Limit {
  color: string;
  max: number;
}

export interface BaseData {
  value: number;
  max: number;
}

export interface ChartConfig {
  name?: string;
  value?: string;
  max?: string;
  colorMode?: ColorMode;
  barColor?: string;
  highColor?: string;
  limits?: Limit[];
  xAxisOrientation?: number;
}

export interface Props {
  config: ChartConfig;
  setConfig: React.Dispatch<React.SetStateAction<ChartConfig>>;
  fieldOptions: OptionsList[];
  defaultLimits: Limit[];
}

export interface ElementsGradient {
  limits: Limit[];
  config: ChartConfig;
  updateConfig: (field: keyof ChartConfig, value: string) => void;
  handleColorChange: (index: number, color: string) => void;
  handleMaxChange: (index: number, max: number) => void;
  addLimit: () => void;
  removeLimit: (index: number) => void;
}

export interface UseChartColorConfigProps {
  config: ChartConfig;
  setConfig: React.Dispatch<React.SetStateAction<ChartConfig>>;
  defaultLimits: Limit[];
}

export interface ElementsChart {
  config: ChartConfig;
  setConfig: React.Dispatch<React.SetStateAction<ChartConfig>>;
  fieldOptions: { value: string }[];
}

export const defaultLimits: Limit[] = [
  { color: "#2ecc71", max: 25 },
  { color: "#f1c40f", max: 50 },
  { color: "#e67e22", max: 75 },
];

export enum Orientation {
  HORIZONTAL = "horizontal",
  VERTICAL = "vertical",
}

export enum Alignement {
  TOP = "top",
  LEFT = "left",
  BOTTOM = "bottom",
}

export enum ColorMode {
  SOLID = "solid",
  GRADIANT = "gradient",
}

export type ChartSection = {
  from: number;
  to: number;
  color: string;
};

export type BaseChartData = {
  value: number;
  max: number;
};

export interface BaseChartConfig {
  barColor?: string;
  highColor?: string;
  limits?: Limit[];
  colorMode?: ColorMode;
  useThresholdColors?: boolean;
}

export interface BaseBarChartProps {
  data: BaseChartData[];
  config: ChartConfig;
  layout: Orientation.HORIZONTAL | Orientation.VERTICAL;
  xAxis: number | string;
  yAxis: number | string;
  legendPosition: Alignement.TOP | Alignement.BOTTOM;
  margin: Margin;
}

export interface PropsAxis {
  value: number;
  onChange: (value: number) => void;
}
