import { useRef, useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useDebouncedCallback } from "use-debounce";
import { SelectInput } from "../../../../components/Form/Form.tsx";

type FieldOption = {
  value: string;
};

type LimitConfig = {
  color: string;
  value: string;
};

type LimitWithId = LimitConfig & {
  _id: number;
};

type MapConfiguration = {
  geojson?: string;
  value?: string;
  max?: string;
  libelle?: string;
  limits?: LimitConfig[];
};

type MapConfigProps = {
  fieldOptions: FieldOption[];
  config: MapConfiguration;
  setConfig: (config: MapConfiguration) => void;
};

const MapConfig = (options: MapConfigProps) => {
  // Valeur par défaut de `geojson`
  const fieldGeojson =
    options.fieldOptions.find(
      (option: FieldOption) => option.value === options.config.geojson,
    ) || "";

  // Valeur par défaut de `geojson`
  const fieldValue =
    options.fieldOptions.find(
      (option: FieldOption) => option.value === options.config.value,
    ) || "";

  // Valeur par défaut de `geojson`
  const fieldMax =
    options.fieldOptions.find(
      (option: FieldOption) => option.value === options.config.max,
    ) || "";

  const fieldLibelle =
    options.fieldOptions.find(
      (option: FieldOption) => option.value === options.config.libelle,
    ) || "";

  const nextId = useRef(0);

  // Gérer les limites dynamiquement, chaque limite porte un _id stable
  const [limits, setLimits] = useState<LimitWithId[]>(() =>
    (options.config.limits || []).map((l: LimitConfig) => ({
      ...l,
      _id: nextId.current++,
    })),
  );

  // Mettre à jour la config du composant avec la nouvelle valeur
  const handleChange = (
    fieldName: keyof MapConfiguration,
    newValue: string,
  ) => {
    const updatedConfig = { ...options.config, [fieldName]: newValue };
    options.setConfig(updatedConfig);
  };

  // Debounce pour le color picker pour limiter le nombre de refraîchissements OpenLayers
  const debounceColorPick = useDebouncedCallback(
    (id: number, field: keyof LimitConfig, value: string) =>
      handleLimitsChange(id, field, value),
    500,
  );

  // Mettre à jour les limites et trier par valeur croissante
  const handleLimitsChange = (
    id: number,
    field: keyof LimitConfig,
    value: string,
  ) => {
    const updatedLimits = limits.map((limit: LimitWithId) =>
      limit._id === id ? { ...limit, [field]: value } : limit,
    );
    const sortedLimits = [...updatedLimits].sort(
      (a, b) => parseFloat(a.value || "0") - parseFloat(b.value || "0"),
    );
    setLimits(sortedLimits);

    // Propager sans les _id internes
    const configLimits = sortedLimits.map(
      ({ _id, ...rest }: LimitWithId) => rest,
    );
    const updatedConfig = { ...options.config, limits: configLimits };
    options.setConfig(updatedConfig);
  };

  // Ajouter une nouvelle limite
  const addLimit = () => {
    const newLimit: LimitWithId = {
      color: "#000000",
      value: "",
      _id: nextId.current++,
    };
    const updatedLimits = [...limits, newLimit];
    setLimits(updatedLimits);

    const configLimits = updatedLimits.map(
      ({ _id, ...rest }: LimitWithId) => rest,
    );
    const updatedConfig = { ...options.config, limits: configLimits };
    options.setConfig(updatedConfig);
  };

  // Supprimer une limite
  const removeLimit = (id: number) => {
    const updatedLimits = limits.filter((l: LimitWithId) => l._id !== id);
    setLimits(updatedLimits);

    const configLimits = updatedLimits.map(
      ({ _id, ...rest }: LimitWithId) => rest,
    );
    const updatedConfig = { ...options.config, limits: configLimits };
    options.setConfig(updatedConfig);
  };

  return (
    <>
      {/* Champ pour `value` */}
      <SelectInput
        required={false}
        name={"value"}
        label="Libellé de l'objet"
        onChange={(value: FieldOption) => handleChange("libelle", value.value)}
        defaultValue={fieldLibelle}
        options={options.fieldOptions}
        getOptionLabel={(option: FieldOption) => option.value}
        getOptionValue={(option: FieldOption) => option.value}
      />
      {/* Champ pour `geojson` */}
      <SelectInput
        required={false}
        name={"geojson"}
        label="Valeur géométrique"
        onChange={(value: FieldOption) => handleChange("geojson", value.value)}
        defaultValue={fieldGeojson}
        options={options.fieldOptions}
        getOptionLabel={(option: FieldOption) => option.value}
        getOptionValue={(option: FieldOption) => option.value}
      />

      {/* Champ pour `value` */}
      <SelectInput
        required={false}
        name={"value"}
        label="Valeur de référence"
        onChange={(value: FieldOption) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
        getOptionLabel={(option: FieldOption) => option.value}
        getOptionValue={(option: FieldOption) => option.value}
      />

      {/* Champ pour `max` */}
      <SelectInput
        required={false}
        name={"max"}
        label="Valeur maximale"
        onChange={(value: FieldOption) => handleChange("max", value.value)}
        defaultValue={fieldMax}
        options={options.fieldOptions}
        getOptionLabel={(option: FieldOption) => option.value}
        getOptionValue={(option: FieldOption) => option.value}
      />

      <hr />
      {/* Champ pour `limits` */}
      <div>
        <Form.Label className="fw-bold mt-2">
          Couleur du pourcentage :
        </Form.Label>
        <br />
        {limits.map((limit: LimitWithId) => (
          <div key={limit._id} className="mb-2">
            <Form>
              <Form.Control
                type="color"
                value={limit.color}
                onChange={(e) =>
                  debounceColorPick(limit._id, "color", e.target.value)
                }
                style={{ width: "100px", height: "40px", marginRight: "10px" }}
              />
            </Form>
            <Form.Control
              type="number"
              min={0}
              step={1}
              max={100}
              required={false}
              defaultValue={limit.value}
              onBlur={(e) =>
                handleLimitsChange(limit._id, "value", e.target.value)
              }
            />
            <Button
              variant={"warning"}
              className="mt-3"
              onClick={() => removeLimit(limit._id)}
            >
              Supprimer
            </Button>
          </div>
        ))}
        <Button variant={"primary"} className="mt-3" onClick={addLimit}>
          Ajouter une limite
        </Button>
      </div>
    </>
  );
};

export default MapConfig;
