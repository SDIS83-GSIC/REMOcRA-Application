import { Button } from "react-bootstrap";
import { useState } from "react";
import { useParams } from "react-router-dom";
import SelectDateTimeInput from "../../../components/Filter/SelectDateTimeInput.tsx";
import CheckBoxInput from "../../../components/Filter/SelectCheckBoxInput.tsx";
import url from "../../../module/fetch.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";

const ExportCrise = () => {
  const { criseId } = useParams();
  const [params, setSearchParam] = useState<any>({});
  const [dateDebExtraction, setDateDebExtraction] = useState(
    formatDateTimeForDateTimeInput(new Date()),
  );
  const [dateFinExtraction, setDateFinExtraction] = useState(
    formatDateTimeForDateTimeInput(new Date()),
  );

  const setValue = (name: string, value: string) => {
    setSearchParam((previous: any) => ({
      ...(previous || {}),
      [name]: value === "0" || value === "" || value === "[]" ? null : value,
    }));
  };

  return (
    <>
      <h1>Période d&apos;extraction</h1>
      <SelectDateTimeInput
        name="dateDebExtraction"
        label="Extraire les informations entre le"
        required={true}
        onChange={(e: { value: string }) => {
          setDateDebExtraction(e.value);
          setValue("dateDebExtraction", new Date(e.value).toISOString());
        }}
        value={dateDebExtraction}
      />

      <SelectDateTimeInput
        name="dateFinExtraction"
        label="et le"
        required={true}
        onChange={(e: { value: string }) => {
          setDateFinExtraction(e.value);
          setValue("dateFinExtraction", new Date(e.value).toISOString());
        }}
        value={dateFinExtraction}
      />

      <h1>Export</h1>
      <CheckBoxInput
        name="hasMessage"
        label="Exporter les messages associés aux évènements"
        onChange={(e: { value: string }) => setValue("hasMessage", e.value)}
      />

      <CheckBoxInput
        name="hasDoc"
        label="Exporter les documents associés aux évènements"
        onChange={(e: { value: string }) => setValue("hasDoc", e.value)}
      />

      <Button
        variant="primary"
        href={url`/api/crise/${criseId}/export?${params}`}
      >
        Valider
      </Button>
    </>
  );
};

export default ExportCrise;
