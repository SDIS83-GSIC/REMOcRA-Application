import { useParams } from "react-router-dom";
import { useState } from "react";
import { ButtonGroup, ToggleButton } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconCrise } from "../../../components/Icon/Icon.tsx";
import MapCrise from "../../../components/Map/MapCrise/MapDeCrise.tsx";
import url from "../../../module/fetch.tsx";

const ModuleMapCrise = () => {
  const { criseId } = useParams();
  const criseState = useGet(url`/api/crise/` + criseId);

  const options = [
    {
      name: "Opérationnel",
      code: "OPERATIONNEL",
    },
    {
      name: "Anticipation",
      code: "ANTICIPATION",
    },
  ];
  const [checkBoxOption, setCheckBoxOption] = useState(options[0]);

  return (
    criseId && (
      <>
        <PageTitle
          title={criseState?.data?.criseLibelle}
          icon={<IconCrise />}
          right={
            <ButtonGroup>
              {options.map((opt, idx) => (
                <ToggleButton
                  key={idx}
                  id={`opt-${idx}`}
                  type="radio"
                  variant={idx % 2 ? "outline-warning" : "outline-primary"}
                  name="switch-operationnel-anticipation"
                  value={opt.code}
                  checked={checkBoxOption.code === opt.code}
                  onChange={() => setCheckBoxOption(options[idx])}
                >
                  {opt.name}
                </ToggleButton>
              ))}
            </ButtonGroup>
          }
        />
        <MapCrise
          criseId={criseId}
          state={checkBoxOption.code}
          variant={
            checkBoxOption.code === options[1].code ? "warning" : "primary"
          }
        />
      </>
    )
  );
};

export default ModuleMapCrise;
