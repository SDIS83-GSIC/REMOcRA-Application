type FilterMetadataType = {
  groupeCoucheLibelle?: string;
  coucheCode?: string;
  coucheLibelle?: string;
  groupeFonctionnaliteList?: string;
  coucheMetadataActif?: string;
  coucheMetadataPublic?: string;
};

const filterValuesToVariable = ({
  groupeCoucheLibelle,
  coucheCode,
  coucheLibelle,
  groupeFonctionnaliteList,
  coucheMetadataActif: coucheMetadataActif,
  coucheMetadataPublic: coucheMetadataPublic,
}: FilterMetadataType) => {
  const filter: FilterMetadataType = {};

  filterPropertyStyle(filter, groupeCoucheLibelle, "groupeCoucheLibelle");
  filterPropertyStyle(filter, coucheCode, "coucheCode");
  filterPropertyStyle(filter, coucheLibelle, "coucheLibelle");
  filterPropertyStyle(
    filter,
    groupeFonctionnaliteList,
    "groupeFonctionnaliteList",
  );
  filterPropertyStyle(filter, coucheMetadataActif, "coucheMetadataActif");
  filterPropertyStyle(filter, coucheMetadataPublic, "coucheMetadataPublic");

  return filter;
};

export default filterValuesToVariable;

function filterPropertyStyle(
  filter: FilterMetadataType,
  value: string | undefined,
  name: string,
) {
  if (value != null && value?.trim().length > 0) {
    filter[name] = value;
  }
}
