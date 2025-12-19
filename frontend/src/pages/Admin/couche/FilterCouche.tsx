type FilterCoucheType = {
  coucheCode?: string;
  coucheLibelle?: string;
  coucheSource?: string;
  coucheNom?: string;
  coucheProjection?: string;
  coucheFormat?: string;
  couchePublic?: string;
  coucheProxy?: string;
  coucheActive?: string;
  coucheProtected?: string;
  groupeFonctionnalitesZc?: string[];
  groupeFonctionnalitesHorsZc?: string[];
  moduleList?: string[];
};

const filterValuesToVariable = ({
  coucheCode,
  coucheLibelle,
  coucheSource,
  coucheNom,
  coucheProjection,
  coucheFormat,
  couchePublic,
  coucheProxy,
  coucheActive,
  coucheProtected,
  groupeFonctionnalitesZc,
  groupeFonctionnalitesHorsZc,
  moduleList,
}: FilterCoucheType) => {
  const filter: FilterCoucheType = {};

  filterPropertyStyle(filter, coucheCode, "coucheCode");
  filterPropertyStyle(filter, coucheLibelle, "coucheLibelle");
  filterPropertyStyle(filter, coucheSource, "coucheSource");
  filterPropertyStyle(filter, coucheNom, "coucheNom");
  filterPropertyStyle(filter, coucheProjection, "coucheProjection");
  filterPropertyStyle(filter, coucheFormat, "coucheFormat");
  filterPropertyStyle(filter, couchePublic, "couchePublic");
  filterPropertyStyle(filter, coucheProxy, "coucheProxy");
  filterPropertyStyle(filter, coucheActive, "coucheActive");
  filterPropertyStyle(filter, coucheProtected, "coucheProtected");

  if (groupeFonctionnalitesZc?.length > 0) {
    filter.groupeFonctionnalitesZc = groupeFonctionnalitesZc;
  }
  if (groupeFonctionnalitesHorsZc?.length > 0) {
    filter.groupeFonctionnalitesHorsZc = groupeFonctionnalitesHorsZc;
  }

  if (moduleList?.length > 0) {
    filter.moduleList = moduleList;
  }

  return filter;
};

export default filterValuesToVariable;

function filterPropertyStyle(
  filter: FilterCoucheType,
  value: string | undefined,
  name: keyof FilterCoucheType,
) {
  if (value != null && value?.trim().length > 0) {
    filter[name] = value;
  }
}
