import { array, object } from "yup";
import { Button, Image, ListGroup } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import { FieldArray, useFormikContext } from "formik";
import PositiveNumberInput, {
  CheckBoxInput,
  FileInput,
  FormContainer,
  Multiselect,
  TextInput,
} from "../../../components/Form/Form.tsx";
import { IconDelete } from "../../../components/Icon/Icon.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import { TypeModuleRemocra } from "../../../components/ModuleRemocra/ModuleRemocra.tsx";

type CoucheType = {
  coucheId?: string;
  coucheCode: string;
  coucheLibelle: string;
  coucheOrdre: number;
  coucheSource: string;
  coucheProjection: string;
  coucheUrl: string;
  coucheNom: string;
  coucheFormat: string;
  couchePublic: boolean;
  coucheActive: boolean;
  coucheIconeUrl?: string;
  coucheLegendeUrl?: string;
  coucheIcone?: File;
  coucheLegende?: File;
  profilDroitList?: string[];
  moduleList?: string[];
};

type GroupeCoucheType = {
  groupeCoucheId?: string;
  groupeCoucheCode: string;
  groupeCoucheLibelle: string;
  groupeCoucheOrdre: number;
  coucheList: CoucheType[];
};

type CoucheFormType = {
  groupeCoucheList: GroupeCoucheType[];
};

export const getInitialValues = (data?: any) => ({
  groupeCoucheList: data?.groupeCoucheList ?? [],
});

type FileType = {
  code: string;
  file: File;
};

export const prepareValues = (values: CoucheFormType) => {
  const formData = new FormData();
  const iconeList: FileType[] = [];
  const legendeList: FileType[] = [];
  formData.append(
    "data",
    JSON.stringify(
      values.groupeCoucheList.map((groupeCouche) => {
        return {
          groupeCoucheId: groupeCouche.groupeCoucheId,
          groupeCoucheCode: groupeCouche.groupeCoucheCode,
          groupeCoucheLibelle: groupeCouche.groupeCoucheLibelle,
          groupeCoucheOrdre: groupeCouche.groupeCoucheOrdre,
          coucheList: groupeCouche.coucheList.map((couche) => {
            if (couche.coucheIcone) {
              iconeList.push({
                code: couche.coucheCode,
                file: couche.coucheIcone,
              });
            }
            if (couche.coucheLegende) {
              legendeList.push({
                code: couche.coucheCode,
                file: couche.coucheLegende,
              });
            }
            return {
              coucheId: couche.coucheId,
              coucheCode: couche.coucheCode,
              coucheLibelle: couche.coucheLibelle,
              coucheOrdre: couche.coucheOrdre,
              coucheSource: couche.coucheSource,
              coucheProjection: couche.coucheProjection,
              coucheUrl: couche.coucheUrl,
              coucheNom: couche.coucheNom,
              coucheFormat: couche.coucheFormat,
              couchePublic: couche.couchePublic,
              coucheActive: couche.coucheActive,
              coucheIcone: couche.coucheIcone,
              coucheLegende: couche.coucheLegende,
              coucheIconeUrl: couche.coucheIconeUrl,
              coucheLegendeUrl: couche.coucheLegendeUrl,
              profilDroitList: couche.profilDroitList,
              moduleList: couche.moduleList,
            };
          }),
        };
      }),
    ),
  );

  iconeList.forEach((file) => {
    formData.append(`icone_${file.code}`, file.file);
  });
  legendeList.forEach((file) => {
    formData.append(`legende_${file.code}`, file.file);
  });

  return formData;
};

export const validationSchema = object({
  groupeCoucheList: array(),
});

const CoucheForm = () => {
  const profilDroitState = useGet(url`/api/profil-droit`);
  const moduleList = Object.entries(TypeModuleRemocra).map(([key, value]) => {
    return {
      id: key,
      libelle: value,
    };
  });
  const { values, setFieldValue } = useFormikContext<CoucheFormType>();

  if (!profilDroitState.isResolved) {
    return;
  }

  return (
    <FormContainer>
      <Button type="submit" variant="primary">
        Valider
      </Button>
      <FieldArray
        name={"groupeCoucheList"}
        render={(arrayHelpers) => (
          <ListGroup as="ol">
            {values.groupeCoucheList?.length > 0 &&
              values.groupeCoucheList
                .sort((a, b) => {
                  return b.groupeCoucheOrdre - a.groupeCoucheOrdre;
                })
                .map((groupeCouche, index) => (
                  <ListGroup.Item key={index} variant={"dark"}>
                    <Row>
                      <Col xs={4}>
                        <TextInput
                          name={`groupeCoucheList.${index}.groupeCoucheCode`}
                          value={groupeCouche.groupeCoucheCode}
                          label={"Code"}
                          placeholder={"Code"}
                          required={true}
                        />
                        <TextInput
                          name={`groupeCoucheList.${index}.groupeCoucheLibelle`}
                          value={groupeCouche.groupeCoucheLibelle}
                          label={"Libellé"}
                          placeholder={"Libellé"}
                          required={true}
                        />
                        <PositiveNumberInput
                          name={`groupeCoucheList.${index}.groupeCoucheOrdre`}
                          value={groupeCouche.groupeCoucheOrdre}
                          label={"Ordre (valeur décroissante)"}
                          min={0}
                          step={1}
                          placeholder={"Ordre"}
                          required={true}
                        />
                      </Col>
                      <Col xs={8}>
                        <FieldArray
                          name={`groupeCoucheList.${index}.coucheList`}
                          render={(arrayHelpers2) => (
                            <ListGroup as="ol">
                              {groupeCouche.coucheList?.length > 0 &&
                                groupeCouche.coucheList
                                  .sort((a, b) => {
                                    return a.coucheOrdre - b.coucheOrdre;
                                  })
                                  .map((couche, groupIndex) => (
                                    <ListGroup.Item
                                      key={`${index}.coucheList.${groupIndex}`}
                                      variant={"primary"}
                                    >
                                      <Row>
                                        <Col xs={6}>
                                          <TextInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheCode`}
                                            value={couche.coucheCode}
                                            label={"Code"}
                                            placeholder={"Code"}
                                            required={true}
                                          />
                                          <TextInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLibelle`}
                                            value={couche.coucheLibelle}
                                            label={"Libellé"}
                                            placeholder={"Libellé"}
                                            required={true}
                                          />
                                          <PositiveNumberInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheOrdre`}
                                            value={couche.coucheOrdre}
                                            label={"Ordre"}
                                            min={0}
                                            step={1}
                                            placeholder={"Ordre"}
                                            required={true}
                                          />
                                          <TextInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheSource`}
                                            value={couche.coucheSource}
                                            label={"Source"}
                                            placeholder={"Source"}
                                            required={true}
                                          />
                                          <TextInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheProjection`}
                                            value={couche.coucheProjection}
                                            label={"Projection"}
                                            placeholder={"Projection"}
                                            required={true}
                                          />
                                          <TextInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheUrl`}
                                            value={couche.coucheUrl}
                                            label={"URL"}
                                            placeholder={"URL"}
                                            required={true}
                                          />
                                          <TextInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheNom`}
                                            value={couche.coucheNom}
                                            label={"Nom"}
                                            placeholder={"Nom"}
                                            required={true}
                                          />
                                          <TextInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheFormat`}
                                            value={couche.coucheFormat}
                                            label={"Format"}
                                            placeholder={"Format"}
                                            required={true}
                                          />
                                        </Col>
                                        <Col xs={6}>
                                          <Multiselect
                                            name={"moduleList"}
                                            label="Modules"
                                            options={moduleList}
                                            getOptionValue={(t) => t.id}
                                            getOptionLabel={(t) => t.libelle}
                                            value={moduleList.filter((el) =>
                                              couche.moduleList?.some(
                                                (value) => value === el.id,
                                              ),
                                            )}
                                            onChange={(value) => {
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.moduleList`,
                                                value.map((el) => el.id),
                                              );
                                            }}
                                            isClearable={true}
                                            required={false}
                                          />
                                          <Multiselect
                                            name={"profilDroitList"}
                                            label="Profils autorisés"
                                            options={profilDroitState.data}
                                            getOptionValue={(t) => t.id}
                                            getOptionLabel={(t) => t.libelle}
                                            value={profilDroitState.data.filter(
                                              (el) =>
                                                couche.profilDroitList?.some(
                                                  (value) => value === el.id,
                                                ),
                                            )}
                                            onChange={(value) => {
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.profilDroitList`,
                                                value.map((el) => el.id),
                                              );
                                            }}
                                            isClearable={true}
                                            required={false}
                                          />
                                          <CheckBoxInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.couchePublic`}
                                            value={couche.couchePublic}
                                            label={"Publique"}
                                          />
                                          <CheckBoxInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheActive`}
                                            value={couche.coucheActive}
                                            label={"Active par défaut"}
                                          />
                                          <FileInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheIcone`}
                                            accept="image/*"
                                            label="Icône"
                                            required={false}
                                            onChange={(e) => {
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheIcone`,
                                                e.target.files[0],
                                              );
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheIconeUrl`,
                                                null,
                                              );
                                            }}
                                          />
                                          <Button
                                            variant="primary"
                                            onClick={() => {
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheIcone`,
                                                null,
                                              );
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheIconeUrl`,
                                                null,
                                              );
                                            }}
                                          >
                                            <IconDelete />
                                          </Button>
                                          {values.groupeCoucheList[index]
                                            .coucheList[groupIndex]
                                            .coucheIcone && (
                                            <Image
                                              thumbnail={true}
                                              src={URL.createObjectURL(
                                                values.groupeCoucheList[index]
                                                  .coucheList[groupIndex]
                                                  .coucheIcone,
                                              )}
                                            />
                                          )}
                                          {values.groupeCoucheList[index]
                                            .coucheList[groupIndex]
                                            .coucheIconeUrl && (
                                            <Image
                                              thumbnail={true}
                                              src={
                                                values.groupeCoucheList[index]
                                                  .coucheList[groupIndex]
                                                  .coucheIconeUrl
                                              }
                                            />
                                          )}
                                          <FileInput
                                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLegende`}
                                            accept="image/*"
                                            label="Légende"
                                            required={false}
                                            onChange={(e) => {
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLegende`,
                                                e.target.files[0],
                                              );
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLegendeUrl`,
                                                null,
                                              );
                                            }}
                                          />
                                          <Button
                                            variant="primary"
                                            onClick={() => {
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLegende`,
                                                null,
                                              );
                                              setFieldValue(
                                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLegendeUrl`,
                                                null,
                                              );
                                            }}
                                          >
                                            <IconDelete />
                                          </Button>
                                          {values.groupeCoucheList[index]
                                            .coucheList[groupIndex]
                                            .coucheLegendeUrl && (
                                            <Image
                                              thumbnail={true}
                                              src={
                                                values.groupeCoucheList[index]
                                                  .coucheList[groupIndex]
                                                  .coucheLegendeUrl
                                              }
                                            />
                                          )}
                                          {values.groupeCoucheList[index]
                                            .coucheList[groupIndex]
                                            .coucheLegende && (
                                            <Image
                                              thumbnail={true}
                                              src={URL.createObjectURL(
                                                values.groupeCoucheList[index]
                                                  .coucheList[groupIndex]
                                                  .coucheLegende,
                                              )}
                                            />
                                          )}
                                        </Col>
                                      </Row>
                                    </ListGroup.Item>
                                  ))}
                              <ListGroup.Item
                                variant={"primary"}
                                action
                                onClick={() => arrayHelpers2.push({})}
                              >
                                Ajouter une couche
                              </ListGroup.Item>
                            </ListGroup>
                          )}
                        />
                      </Col>
                    </Row>
                    <Button
                      onClick={() => arrayHelpers.remove(index)} // remove a friend from the list
                    >
                      Supprimer le groupe
                    </Button>
                  </ListGroup.Item>
                ))}
            <ListGroup.Item
              variant={"primary"}
              action
              onClick={() => arrayHelpers.push({ coucheList: [] })}
            >
              Ajouter un groupe
            </ListGroup.Item>
          </ListGroup>
        )}
      />
    </FormContainer>
  );
};

export default CoucheForm;
