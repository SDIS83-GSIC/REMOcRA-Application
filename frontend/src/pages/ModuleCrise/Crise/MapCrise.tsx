import { useParams } from "react-router-dom";
import { useState } from "react";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconCrise } from "../../../components/Icon/Icon.tsx";
import MapCrise from "../../../components/Map/MapCrise/MapDeCrise.tsx";
import url from "../../../module/fetch.tsx";
import { CheckBoxInput } from "../../../components/Filter/SelectCheckBoxInput.tsx";

const options = {
  operationnel: {
    code: "OPERATIONNEL",
    label: "Opérationnel",
  },
  anticipation: {
    code: "ANTICIPATION",
    label: "Anticipation",
  },
};

const ModuleMapCrise = () => {
  const { criseId } = useParams();
  const criseState = useGet(url`/api/crise/` + criseId);

  const [checkBoxOption, setCheckBoxOption] = useState(options.operationnel);
  const handleChange = (event: { value: boolean }) => {
    setCheckBoxOption(
      event.value ? options.anticipation : options.operationnel,
    );
  };

  return (
    criseId && (
      <>
        <PageTitle
          title={criseState?.data?.criseLibelle}
          icon={<IconCrise />}
          right={
            <CheckBoxInput
              label={checkBoxOption.label}
              type="switch"
              checked={checkBoxOption.code === options.anticipation.code}
              onChange={handleChange}
            />
          }
        />
        <MapCrise criseId={criseId} state={checkBoxOption.code} />
      </>
    )
  );
};

export default ModuleMapCrise;
