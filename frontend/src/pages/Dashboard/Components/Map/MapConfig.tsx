import { useRef, useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useDebouncedCallback } from "use-debounce";
import { SelectInput } from "../../../../components/Form/Form.tsx";

const MapConfig = (options: any) => {
  // Valeur par défaut de `geojson`
  const fieldGeojson =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.geojson,
    ) || "";

  // Valeur par défaut de `geojson`
  const fieldValue =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.value,
    ) || "";

  // Valeur par défaut de `geojson`
  const fieldMax =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.max,
    ) || "";

  const fieldLibelle =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.libelle,
    ) || "";

  const nextId = useRef(0);

  // Gérer les limites dynamiquement, chaque limite porte un _id stable
  const [limits, setLimits] = useState(() =>
    (options.config.limits || []).map((l: any) => ({
      ...l,
      _id: nextId.current++,
    })),
  );

  // Mettre à jour la config du composant avec la nouvelle valeur
  const handleChange = (fieldName: string, newValue: string) => {
    const updatedConfig = { ...options.config, [fieldName]: newValue };
    options.setConfig(updatedConfig);
  };

  // Debounce pour le color picker pour limiter le nombre de refraîchissements OpenLayers
  const debounceColorPick = useDebouncedCallback(
    (id: number, field: string, value: string) =>
      handleLimitsChange(id, field, value),
    500,
  );

  // Mettre à jour les limites et trier par valeur croissante
  const handleLimitsChange = (id: number, field: string, value: string) => {
    const updatedLimits = limits.map(
      (limit: { color: string; value: string; _id: number }) =>
        limit._id === id ? { ...limit, [field]: value } : limit,
    );
    const sortedLimits = [...updatedLimits].sort(
      (a, b) => parseFloat(a.value || "0") - parseFloat(b.value || "0"),
    );
    setLimits(sortedLimits);

    // Propager sans les _id internes
    const configLimits = sortedLimits.map(
      ({ _id, ...rest }: { _id: number; color: string; value: string }) => rest,
    );
    const updatedConfig = { ...options.config, limits: configLimits };
    options.setConfig(updatedConfig);
  };

  // Ajouter une nouvelle limite
  const addLimit = () => {
    const newLimit = { color: "#000000", value: "", _id: nextId.current++ };
    const updatedLimits = [...limits, newLimit];
    setLimits(updatedLimits);

    const configLimits = updatedLimits.map(
      ({ _id, ...rest }: { _id: number; color: string; value: string }) => rest,
    );
    const updatedConfig = { ...options.config, limits: configLimits };
    options.setConfig(updatedConfig);
  };

  // Supprimer une limite
  const removeLimit = (id: number) => {
    const updatedLimits = limits.filter(
      (l: { _id: number; color: string; value: string }) => l._id !== id,
    );
    setLimits(updatedLimits);

    const configLimits = updatedLimits.map(
      ({ _id, ...rest }: { _id: number; color: string; value: string }) => rest,
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
        onChange={(value) => handleChange("libelle", value.value)}
        defaultValue={fieldLibelle}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />
      {/* Champ pour `geojson` */}
      <SelectInput
        required={false}
        name={"geojson"}
        label="Valeur géométrique"
        onChange={(value) => handleChange("geojson", value.value)}
        defaultValue={fieldGeojson}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />

      {/* Champ pour `value` */}
      <SelectInput
        required={false}
        name={"value"}
        label="Valeur de référence"
        onChange={(value) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />

      {/* Champ pour `max` */}
      <SelectInput
        required={false}
        name={"max"}
        label="Valeur maximale"
        onChange={(value) => handleChange("max", value.value)}
        defaultValue={fieldMax}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />

      <hr />
      {/* Champ pour `limits` */}
      <div>
        <Form.Label className="fw-bold mt-2">
          Couleur du pourcentage :
        </Form.Label>
        <br />
        {limits.map((limit: { _id: number; color: string; value: string }) => (
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
