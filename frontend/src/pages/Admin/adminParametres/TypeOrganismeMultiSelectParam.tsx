import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { Multiselect } from "../../../components/Form/Form.tsx";
import TYPE_PARAMETRE from "../../../enums/TypesParametres.tsx";
import url from "../../../module/fetch.tsx";
import { AdminParametre } from "./AdminParametres.tsx";

const TypeOrganismeMultiSelectParam = ({
  name,
  label,
  selectedCodes,
  setFieldValue,
  required = true,
}: {
  name: string;
  label: string;
  selectedCodes: string[] | undefined;
  setFieldValue: (name: string, value: string[] | undefined) => void;
  required?: boolean;
}) => {
  const typeOrganismeState = useGet(url`/api/type-organisme/get-active`);

  return (
    <AdminParametre type={TYPE_PARAMETRE.MULTI_STRING}>
      <Multiselect
        name={name}
        label={label}
        required={required}
        options={typeOrganismeState?.data ?? []}
        getOptionValue={(t) => t.typeOrganismeCode}
        getOptionLabel={(t) => t.typeOrganismeLibelle}
        value={
          selectedCodes?.map((code) =>
            typeOrganismeState?.data?.find(
              (r: { typeOrganismeCode: string }) =>
                r.typeOrganismeCode === code,
            ),
          ) ?? undefined
        }
        onChange={(typeOrganisme = []) => {
          const typeOrganismeCode = typeOrganisme.map(
            (e: { typeOrganismeCode: string }) => e.typeOrganismeCode,
          );
          setFieldValue(
            name,
            typeOrganismeCode.length > 0 ? typeOrganismeCode : [],
          );
        }}
      />
    </AdminParametre>
  );
};

export default TypeOrganismeMultiSelectParam;
