type FilterOldebType = {
  oldebCommune?: string;
  oldebSection?: string;
  oldebParcelle?: string;
  oldebTypeZoneUrbanisme?: string;
  oldebTypeDebroussaillement?: string;
  oldebTypeAvis?: string;
};

const filterValuesToVariable = ({
  oldebCommune,
  oldebSection,
  oldebParcelle,
  oldebTypeZoneUrbanisme,
  oldebTypeDebroussaillement,
  oldebTypeAvis,
}: FilterOldebType) => {
  const filter: FilterOldebType = {};

  pushFilterItem(filter, oldebCommune, "oldebCommune");
  pushFilterItem(filter, oldebSection, "oldebSection");
  pushFilterItem(filter, oldebParcelle, "oldebParcelle");
  pushFilterItem(filter, oldebTypeZoneUrbanisme, "oldebTypeZoneUrbanisme");
  pushFilterItem(
    filter,
    oldebTypeDebroussaillement,
    "oldebTypeDebroussaillement",
  );
  pushFilterItem(filter, oldebTypeAvis, "oldebTypeAvis");

  return filter;
};

export default filterValuesToVariable;

function pushFilterItem(
  filter: FilterOldebType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim().length > 0) {
    filter[name] = value;
  }
}
