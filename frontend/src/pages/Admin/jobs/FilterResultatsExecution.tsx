type FilterResultatsExecution = {
  typeTask?: string | undefined;
  jobEtatJob?: string | undefined;
};

const filterValuesToVariable = ({
  typeTask,
  etatJob,
}: FilterResultatsExecution) => {
  const filter: FilterResultatsExecution = {};

  filterPropertyEtude(filter, typeTask, "typeTask");
  filterPropertyEtude(filter, etatJob, "etatJob");

  return filter;
};

export default filterValuesToVariable;

function filterPropertyEtude(
  filter: FilterResultatsExecution,
  value: string | undefined,
  name: string,
) {
  if (value != null && value.trim() !== "") {
    filter[name] = value;
  }
}
