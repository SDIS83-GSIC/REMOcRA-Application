import { useEffect, useMemo, useState } from "react";
import {
  ChartConfig,
  ColorMode,
  Limit,
  UseChartColorConfigProps,
} from "../components/Utils.tsx";

const useChartColorConfig = ({
  config,
  setConfig,
  defaultLimits,
}: UseChartColorConfigProps) => {
  // Initialiser limits une seule fois au montage, pas de synch constante
  const [limits, setLimits] = useState<Limit[]>(() =>
    config.limits && config.limits.length > 0 ? config.limits : defaultLimits,
  );

  // Mémoriser isGradient pour éviter les recalculs inutiles à chaque render
  const isGradient = useMemo(
    () =>
      config.colorMode === ColorMode.GRADIANT ||
      (config.colorMode == null && limits.length > 0),
    [config.colorMode, limits.length],
  );

  // Initialiser les limites par défaut uniquement au changement du colorMode
  useEffect(() => {
    if (
      config.colorMode === ColorMode.GRADIANT &&
      (!Array.isArray(config.limits) || config.limits.length === 0) &&
      limits.length === 0
    ) {
      setLimits(defaultLimits);
      setConfig({
        ...config,
        limits: defaultLimits,
        highColor: config.highColor || "#e74c3c",
        colorMode: ColorMode.GRADIANT,
      });
    }
  }, [config.colorMode, setConfig, config, defaultLimits, limits.length]);

  const updateConfig = <K extends keyof ChartConfig>(
    field: K,
    value: ChartConfig[K],
  ) => {
    setConfig({ ...config, [field]: value });
  };

  const updateLimits = (newLimits: Limit[]) => {
    setLimits(newLimits);
    setConfig({ ...config, limits: newLimits });
  };

  const handleColorChange = (index: number, color: string) => {
    const updated = [...limits];
    updated[index].color = color;
    updateLimits(updated);
  };

  const handleMaxChange = (index: number, max: number) => {
    const updated = [...limits];
    updated[index].max = max;
    updateLimits(updated);
  };

  const addLimit = () => {
    updateLimits([...limits, { color: "#000000", max: 0 }]);
  };

  const removeLimit = (index: number) => {
    updateLimits(limits.filter((_, i) => i !== index));
  };

  return {
    limits,
    isGradient,
    updateConfig,
    handleColorChange,
    handleMaxChange,
    addLimit,
    removeLimit,
  };
};

export default useChartColorConfig;
