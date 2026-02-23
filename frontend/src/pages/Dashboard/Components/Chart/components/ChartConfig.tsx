import { setSimpleValueMapped } from "../../../MappedValueComponent.tsx";
import {
  BaseChartConfig,
  BaseData,
  ChartSection,
  ColorMode,
} from "./Utils.tsx";

export const mapAndConvertData = (
  rawData: BaseData[],
  config: BaseChartConfig,
) => {
  return setSimpleValueMapped(rawData, config).map(
    (item: { value: string; max: string }) => {
      const v = parseInt(item.value);
      const m = parseInt(item.max);
      const pct = m > 0 && Number.isFinite(m) ? (v / m) * 100 : 0;

      return {
        ...item,
        value: v,
        max: m,
        percentage: Math.max(0, Math.min(100, pct)),
      };
    },
  );
};

export const shouldUseThresholdColors = (config: BaseChartConfig): boolean => {
  const hasLimits = Array.isArray(config?.limits) && config.limits.length > 0;

  return (
    config?.colorMode === ColorMode.GRADIANT ||
    (config?.colorMode == null &&
      (hasLimits || config?.useThresholdColors === true))
  );
};

export const buildSections = (
  config: BaseChartConfig,
  useThresholdColors: boolean,
): ChartSection[] => {
  if (!useThresholdColors) {
    return [];
  }

  const highColor = config?.highColor || "#e74c3c";
  const rawLimits = Array.isArray(config?.limits) ? config.limits : [];

  const limits = rawLimits
    .map((l) => ({ color: l.color || "#cccccc", max: Number(l.max) }))
    .filter((l) => !Number.isNaN(l.max))
    .sort((a, b) => a.max - b.max)
    .map((l) => ({
      color: l.color,
      max: Math.max(0, Math.min(100, l.max)),
    }));

  const sections: ChartSection[] = [];
  let prev = 0;

  for (const lim of limits) {
    const to = Math.max(prev, lim.max);
    if (to > prev) {
      sections.push({ from: prev, to, color: lim.color });
    }
    prev = to;
  }

  if (sections.length === 0) {
    return [
      { from: 0, to: 25, color: "#2ecc71" },
      { from: 25, to: 50, color: "#f1c40f" },
      { from: 50, to: 75, color: "#e67e22" },
      { from: 75, to: 100, color: "#e74c3c" },
    ];
  }

  if (prev < 100) {
    sections.push({ from: prev, to: 100, color: highColor });
  }

  return sections;
};

export const buildThresholdLines = (sections: ChartSection[]) => {
  if (sections.length === 0) {
    return [25, 50, 75, 100];
  }

  return Array.from(new Set(sections.map((s) => Math.round(s.to))))
    .filter((val) => val > 0 && val <= 100)
    .sort((a, b) => a - b);
};

export const resolveColor = (
  pct: number,
  sections: ChartSection[],
  barColor: string,
) => {
  for (const sec of sections) {
    if (pct >= sec.from && pct <= sec.to) {
      return sec.color;
    }
  }
  return barColor;
};

export const legendPayload = (sections: ChartSection[]) =>
  sections.map((sec) => ({
    value: `${Math.round(sec.from)}–${Math.round(sec.to)}%`,
    type: "square" as const,
    id: `${sec.from}-${sec.to}`,
    color: sec.color,
  }));
