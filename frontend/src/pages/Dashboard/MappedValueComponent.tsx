export const setSimpleValueMapped = (data: any, config: any) => {
  // Extraire toutes les clés de dataConfig
  const configKeys = Object.keys(config);

  // Mapped les data d'après la valeur de la config
  return data.map((item: { [x: string]: any }) => {
    const transformedItem: { [dataKey: string]: any } = {};

    // Itérer sur toutes les clés de la configuration et construire l'objet
    configKeys.forEach((configKey: string) => {
      // Clé correspondante dans `data`
      const dataKey = config[configKey as keyof typeof config];

      // Associer la valeur depuis `data`
      transformedItem[configKey] = item[dataKey as keyof typeof item]
        ? item[dataKey as keyof typeof item]
        : dataKey;
    });

    return transformedItem;
  });
};

export const setGaugeValueMapped = (data: any, config: any) => {
  // Extraire toutes les clés de dataConfig
  const configKeys = ["value", "max"];

  // Mapped les data d'après la valeur de la config
  return data.map((item: { [x: string]: any }) => {
    const transformedItem: { [dataKey: string]: any } = {};

    // Itérer sur toutes les clés de la configuration et construire l'objet
    configKeys.forEach((configKey) => {
      // Clé correspondante dans `data`
      const dataKey = config[configKey as keyof typeof config];
      // Associer la valeur depuis `data`
      transformedItem[configKey] = item[dataKey as keyof typeof item]
        ? item[dataKey as keyof typeof item]
        : dataKey;
    });
    return transformedItem;
  });
};

export const setTableValueMapped = (data: any, config: any) => {
  // Mapped les data d'après la valeur de la config
  return data.map((item: { [x: string]: any }) => {
    const transformedItem: { [dataKey: string]: any } = {};

    // Itérer sur toutes les clés de la configuration et construire l'objet
    config.forEach((configKey: string) => {
      // Associer la valeur depuis `data`
      transformedItem[configKey] =
        item[configKey as keyof typeof item] ?? configKey;
    });
    return transformedItem;
  });
};
