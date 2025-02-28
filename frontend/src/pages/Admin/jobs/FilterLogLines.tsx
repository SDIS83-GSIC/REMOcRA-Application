type FilterLogLines = {
  logLineGravity?: string | undefined;
};

const filterValuesToVariable = ({ logLineGravity }: FilterLogLines) => {
  const filter: FilterLogLines = {};

  filterProperty(filter, logLineGravity, "logLineGravity");

  return filter;
};

export default filterValuesToVariable;

function filterProperty(
  filter: FilterLogLines,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}
