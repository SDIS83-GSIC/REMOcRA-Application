import { FieldArray, useFormikContext } from "formik";
import { useState } from "react";
import { Button, Image, ListGroup } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import { array, object } from "yup";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import DeleteButton from "../../../components/Button/DeleteButton.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  FileInput,
  FormContainer,
  Multiselect,
  SelectInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconDelete } from "../../../components/Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../../components/ModuleRemocra/ModuleRemocra.tsx";
import SOURCE_CARTO from "../../../enums/SourceCartoEnum.tsx";
import url from "../../../module/fetch.tsx";
import CustomLinkButton from "../../../components/Button/CustomLinkButton.tsx";
import { URLS } from "../../../routes.tsx";

type CoucheType = {
  coucheProtected: boolean;
  coucheId?: string;
  coucheCode: string;
  coucheLibelle: string;
  coucheOrdre: number;
  coucheSource: SOURCE_CARTO;
  coucheProjection: string;
  coucheUrl: string;
  coucheNom: string;
  coucheFormat: string;
  coucheCrossOrigin: string;
  couchePublic: boolean;
  coucheActive: boolean;
  coucheProxy: boolean;
  coucheTuilage: boolean;
  coucheIconeUrl?: string;
  coucheLegendeUrl?: string;
  coucheIcone?: File;
  coucheLegende?: File;
  groupeFonctionnalitesList?: string[];
  moduleList?: string[];
};

type GroupeCoucheType = {
  groupeCoucheId?: string;
  groupeCoucheCode: string;
  groupeCoucheLibelle: string;
  groupeCoucheOrdre: number;
  coucheList: CoucheType[];
  groupeCoucheProtected: boolean;
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
              coucheCrossOrigin: couche.coucheCrossOrigin,
              couchePublic: couche.couchePublic,
              coucheActive: couche.coucheActive,
              coucheProxy: couche.coucheProxy,
              coucheTuilage: couche.coucheTuilage,
              coucheIcone: couche.coucheIcone,
              coucheLegende: couche.coucheLegende,
              coucheIconeUrl: couche.coucheIconeUrl,
              coucheLegendeUrl: couche.coucheLegendeUrl,
              groupeFonctionnalitesList: couche.groupeFonctionnalitesList,
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
  const groupeFonctionnalitesState = useGet(url`/api/groupe-fonctionnalites`);
  const moduleList = Object.entries(TypeModuleRemocra).map(([key, value]) => {
    return {
      id: key,
      libelle: value,
    };
  });
  const { values, setFieldValue } = useFormikContext<CoucheFormType>();
  const [fileInputKeys, setFileInputKeys] = useState({});

  if (!groupeFonctionnalitesState.isResolved) {
    return;
  }

  const listGroups = values.groupeCoucheList;
  const accordionGroups: { header: string; content: ReactNode }[] = [];

  const typeSourceCarto = Object.values(SOURCE_CARTO).map((key) => {
    return {
      id: key,
      code: key,
      libelle: key,
    };
  });

  listGroups.map((group, index) => {
    const contentGroup = (
      <>
        <Row>
          <Col>
            <h2>Gestion du groupe {group.groupeCoucheLibelle}</h2>
          </Col>
        </Row>
        <Row>
          <Col xs={4}>
            <TextInput
              name={`groupeCoucheList.${index}.groupeCoucheCode`}
              disabled={group.groupeCoucheProtected}
              value={group.groupeCoucheCode}
              label={"Code"}
              placeholder={"Code"}
              required={true}
            />
          </Col>
          <Col xs={4}>
            <TextInput
              name={`groupeCoucheList.${index}.groupeCoucheLibelle`}
              value={group.groupeCoucheLibelle}
              label={"Libellé"}
              placeholder={"Libellé"}
              required={true}
            />
          </Col>
          <Col xs={4}>
            <PositiveNumberInput
              name={`groupeCoucheList.${index}.groupeCoucheOrdre`}
              value={group.groupeCoucheOrdre}
              label={"Ordre (valeur décroissante)"}
              min={0}
              step={1}
              placeholder={"Ordre"}
              required={true}
            />
          </Col>
        </Row>
        <Row className="mt-3">
          <h2>Gestion des couches</h2>
        </Row>
        {group?.coucheList?.length > 0 &&
          group.coucheList.map((couche, groupIndex) => {
            const accordionCouche: {
              header: string;
              content: ReactNode;
            }[] = [];

            // Génère une key unique pour chaque FileInput
            // - Permet à React de re-render uniquement le champ ciblé lors du reset
            // - fileInputKeys est un objet de type { [cléUnique]: keyValue }
            // Si aucune key n'existe encore, on en crée une valeur "initiale" pour éviter les erreurs
            const fileInputKeyIcone =
              fileInputKeys[`icone-${index}-${groupIndex}`] ||
              `${index}-${groupIndex}-initiale`;
            const fileInputKeyLegende =
              fileInputKeys[`legende-${index}-${groupIndex}`] ||
              `${index}-${groupIndex}-initiale`;

            const contentCouche = (
              <FieldArray
                name={`groupeCoucheList.${index}.coucheList`}
                render={(arrayHelpers2) => (
                  <ListGroup as="ol">
                    <ListGroup.Item
                      key={`${index}.coucheList.${groupIndex}`}
                      variant={"light"}
                    >
                      <Row>
                        <Col xs={12} className="p-2 bg-secondary">
                          <h4 className={"m-0 p-0 text-center h6"}>Général</h4>
                        </Col>
                        <Col xs={12} lg={6} xxl={4}>
                          <TextInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheCode`}
                            disabled={couche.coucheProtected}
                            value={couche.coucheCode}
                            label={"Code"}
                            placeholder={"Code"}
                            required={true}
                          />
                        </Col>
                        <Col xs={12} lg={6} xxl={4}>
                          <TextInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLibelle`}
                            value={couche.coucheLibelle}
                            label={"Libellé"}
                            placeholder={"Libellé"}
                            required={true}
                          />
                        </Col>
                        <Col xs={12} lg={6} xxl={4}>
                          <PositiveNumberInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheOrdre`}
                            value={couche.coucheOrdre}
                            label={"Ordre"}
                            min={0}
                            step={1}
                            placeholder={"Ordre"}
                            required={true}
                          />
                        </Col>
                        <Col xs={12} lg={6} xxl={4}>
                          <CustomLinkButton
                            className="text-underline text-start"
                            pathname={URLS.LIST_COUCHE_METADATA()}
                            search={new URLSearchParams({
                              filterBy: JSON.stringify({
                                coucheLibelle: couche.coucheLibelle,
                              }),
                            }).toString()}
                          >
                            Modification des métadonnées
                          </CustomLinkButton>
                        </Col>
                        <Col xs={12} className="p-2 bg-secondary mt-2">
                          <h4 className={"m-0 p-0 text-center h6"}>Flux</h4>
                        </Col>
                        <Col xs={12} lg={6} xxl={4}>
                          <SelectInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheSource`}
                            disabled={couche.coucheProtected}
                            label="Source"
                            options={typeSourceCarto}
                            getOptionValue={(t) => t.id}
                            getOptionLabel={(t) => t.libelle}
                            onChange={(e) => {
                              setFieldValue(
                                `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheSource`,
                                typeSourceCarto.find((type) => type.id === e.id)
                                  ?.id,
                              );
                            }}
                            defaultValue={typeSourceCarto.find(
                              (type) => type.id === couche.coucheSource,
                            )}
                            required={true}
                          />
                        </Col>
                        <Col xs={12} lg={6} xxl={4}>
                          <TextInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheUrl`}
                            disabled={couche.coucheProtected}
                            value={couche.coucheUrl}
                            label={"URL"}
                            placeholder={"URL"}
                            required={true}
                          />
                        </Col>
                        {couche.coucheSource !== SOURCE_CARTO.OSM &&
                          !couche.coucheProtected && (
                            <>
                              <Col xs={12} lg={6} xxl={4}>
                                <TextInput
                                  name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheNom`}
                                  value={couche.coucheNom}
                                  label={"Nom"}
                                  placeholder={"Nom"}
                                  required={true}
                                />
                              </Col>
                              <Col xs={12} lg={6} xxl={4}>
                                <TextInput
                                  name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheProjection`}
                                  value={couche.coucheProjection}
                                  label={"Projection"}
                                  placeholder={"Projection"}
                                  required={true}
                                />
                              </Col>
                              <Col xs={12} lg={6} xxl={4}>
                                <TextInput
                                  name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheFormat`}
                                  value={couche.coucheFormat}
                                  label={"Format"}
                                  placeholder={"Format"}
                                  required={true}
                                />
                              </Col>
                            </>
                          )}
                        {(couche.coucheSource === SOURCE_CARTO.WMTS ||
                          couche.coucheSource === SOURCE_CARTO.OSM) && (
                          <Col xs={12} lg={6} xxl={4}>
                            <TextInput
                              name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheCrossOrigin`}
                              value={couche.coucheCrossOrigin}
                              label={"CrossOrigin"}
                              placeholder={"CrossOrigin"}
                              required={false}
                            />
                          </Col>
                        )}
                        <Col xs={12} className="p-2 bg-secondary mt-2">
                          <h4 className={"m-0 p-0  text-center h6"}>
                            Autorisations
                          </h4>
                        </Col>
                        <Col xs={12} xxl={6}>
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
                        </Col>
                        <Col xs={12} xxl={6}>
                          <Multiselect
                            name={"groupeFonctionnalitesList"}
                            label="Profils autorisés"
                            options={groupeFonctionnalitesState.data}
                            getOptionValue={(t) => t.id}
                            getOptionLabel={(t) => t.libelle}
                            value={groupeFonctionnalitesState.data.filter(
                              (el) =>
                                couche.groupeFonctionnalitesList?.some(
                                  (value) => value === el.id,
                                ),
                            )}
                            onChange={(value) => {
                              setFieldValue(
                                `groupeCoucheList.${index}.coucheList.${groupIndex}.groupeFonctionnalitesList`,
                                value.map((el) => el.id),
                              );
                            }}
                            isClearable={true}
                            required={false}
                          />
                        </Col>
                        <Col xs={12}>
                          <CheckBoxInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.couchePublic`}
                            value={couche.couchePublic}
                            label={"Publique"}
                          />
                          {couche.coucheSource === SOURCE_CARTO.WMS && (
                            <CheckBoxInput
                              name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheTuilage`}
                              value={couche.coucheTuilage}
                              label={"Utiliser le tuilage WMS"}
                              tooltipText="Si la case est cochée, la couche sera chargée sous forme de tuiles. Attention, Geoserver ne peut pas gérer convenablement l'affichage des attributs calculés dans une couche tuilée (un libellé affiché au centroïde, une couleur de remplissage aléatoire, ...), donc pour toute couche tuilée il conviendra de vérifier son style associé."
                            />
                          )}
                          <CheckBoxInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheProxy`}
                            value={couche.coucheProxy}
                            label={"Utiliser le proxy pour charger la couche ?"}
                            tooltipText="Si la case est décochée, la ressource sera chargée directement sans passer par le proxy de l'application. Utile pour l'affichage des fonds de plan."
                          />
                          <CheckBoxInput
                            name={`groupeCoucheList.${index}.coucheList.${groupIndex}.coucheActive`}
                            value={couche.coucheActive}
                            label={"Active par défaut"}
                          />
                        </Col>
                        <Col xs={12} className="p-2 bg-secondary mt-2">
                          <h4 className={"m-0 p-0 text-center h6"}>Images</h4>
                        </Col>
                        <Col xs={12} lg={6}>
                          <Row>
                            <Col>
                              <FileInput
                                key={fileInputKeyIcone}
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
                            </Col>
                          </Row>
                          <Row>
                            {values.groupeCoucheList[index].coucheList[
                              groupIndex
                            ].coucheIconeUrl && (
                              <Col xs={2}>
                                <Image
                                  thumbnail={true}
                                  src={
                                    values.groupeCoucheList[index].coucheList[
                                      groupIndex
                                    ].coucheIconeUrl
                                  }
                                />
                              </Col>
                            )}
                            <Col xs={"auto"}>
                              <Button
                                variant="link"
                                className={"text-danger text-decoration-none"}
                                onClick={() => {
                                  setFieldValue(
                                    `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheIcone`,
                                    null,
                                  );
                                  setFieldValue(
                                    `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheIconeUrl`,
                                    null,
                                  );
                                  // Remonte uniquement le FileInput "icône"
                                  // - On change la clé associée pour forcer React à démonter/remonter le composant
                                  // - Cela réinitialise la valeur du champ <input type="file" />
                                  setFileInputKeys((prev) => ({
                                    ...prev,
                                    [`icone-${index}-${groupIndex}`]: `${index}-${groupIndex}-icone-${Date.now()}`,
                                  }));
                                }}
                              >
                                <IconDelete /> Supprimer l&apos;icône
                              </Button>
                            </Col>
                          </Row>
                        </Col>
                        <Col xs={12} lg={6}>
                          <Row>
                            <Col>
                              <FileInput
                                key={fileInputKeyLegende}
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
                            </Col>
                          </Row>
                          <Row>
                            {values.groupeCoucheList[index].coucheList[
                              groupIndex
                            ].coucheLegendeUrl && (
                              <Col xs={6}>
                                <Image
                                  thumbnail={true}
                                  src={
                                    values.groupeCoucheList[index].coucheList[
                                      groupIndex
                                    ].coucheLegendeUrl
                                  }
                                />
                              </Col>
                            )}
                            <Col>
                              <Button
                                variant="link"
                                className={"text-danger text-decoration-none"}
                                onClick={() => {
                                  setFieldValue(
                                    `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLegende`,
                                    null,
                                  );
                                  setFieldValue(
                                    `groupeCoucheList.${index}.coucheList.${groupIndex}.coucheLegendeUrl`,
                                    null,
                                  );

                                  // Même logique pour le champ "légende"
                                  setFileInputKeys((prev) => ({
                                    ...prev,
                                    [`legende-${index}-${groupIndex}`]: `${index}-${groupIndex}-legende-${Date.now()}`,
                                  }));
                                }}
                              >
                                <IconDelete /> Supprimer la légende
                              </Button>
                            </Col>
                          </Row>
                        </Col>

                        <Row className="m-3">
                          <Col className="text-center">
                            <DeleteButton
                              onClick={() => arrayHelpers2.remove(groupIndex)}
                              disabled={couche.coucheProtected}
                              title={"Supprimer la couche"}
                            />
                          </Col>
                        </Row>
                      </Row>
                    </ListGroup.Item>
                  </ListGroup>
                )}
              />
            );

            accordionCouche.push({
              header: couche.coucheLibelle,
              content: contentCouche,
            });

            return (
              <>
                <AccordionCouche list={accordionCouche} />
              </>
            );
          })}

        <FieldArray
          name={"groupeCoucheList"}
          render={(arrayHelpers) => (
            <Row>
              <Col xs={"auto"}>
                <ListGroup as="ol">
                  <ListGroup.Item
                    key={index}
                    variant={"transparent"}
                    className={"border-0"}
                  >
                    <Button
                      variant="link"
                      disabled={group.groupeCoucheProtected}
                      className={"text-danger"}
                      onClick={() => arrayHelpers.remove(index)} // remove a friend from the list
                    >
                      <IconDelete /> Supprimer le groupe
                    </Button>
                  </ListGroup.Item>
                </ListGroup>
              </Col>

              <Col xs={"auto"}>
                <FieldArray
                  name={`groupeCoucheList.${index}.coucheList`}
                  render={(arrayHelpers2) => (
                    <ListGroup as="ol">
                      <ListGroup.Item
                        variant={"transparent"}
                        className={"border-0"}
                      >
                        <CreateButton
                          onClick={() =>
                            arrayHelpers2.push({
                              coucheId: undefined,
                              coucheCode: undefined,
                              coucheLibelle: "COUCHE EN CREATION...",
                              coucheOrdre: undefined,
                              coucheSource: undefined,
                              coucheProjection: undefined,
                              coucheUrl: undefined,
                              coucheNom: undefined,
                              coucheFormat: undefined,
                              couchePublic: undefined,
                              coucheActive: undefined,
                              coucheIconeUrl: undefined,
                              coucheLegendeUrl: undefined,
                              coucheIcone: undefined,
                              coucheLegende: undefined,
                              groupeFonctionnalitesList: undefined,
                              moduleList: undefined,
                            })
                          }
                          title={"Ajouter une couche"}
                        />
                      </ListGroup.Item>
                    </ListGroup>
                  )}
                />
              </Col>
            </Row>
          )}
        />
      </>
    );
    accordionGroups.push({
      header: group.groupeCoucheLibelle,
      content: contentGroup,
    });
  });
  return (
    <FormContainer>
      {accordionGroups && <AccordionGroup list={accordionGroups} />}
      <FieldArray
        name={"groupeCoucheList"}
        render={(arrayHelpers) => (
          <ListGroup as="ol">
            <ListGroup.Item variant={"light"} className={"text-center p-3"}>
              <CreateButton
                title={"Ajouter un groupe"}
                onClick={() =>
                  arrayHelpers.push({
                    groupeCoucheId: undefined,
                    groupeCoucheCode: undefined,
                    groupeCoucheLibelle: "GROUPE EN CREATION ...",
                    groupeCoucheOrdre: undefined,
                    coucheList: [],
                  })
                }
              />
            </ListGroup.Item>
          </ListGroup>
        )}
      />
      <SubmitFormButtons returnLink={false} />
    </FormContainer>
  );
};

const AccordionGroup = ({
  list,
}: {
  list: { header: string; content: ReactNode }[];
}) => {
  const { handleShowClose, activesKeys } = useAccordionState([false]); //tous les onglet fermé
  return (
    list && (
      <AccordionCustom
        list={list}
        handleShowClose={handleShowClose}
        activesKeys={activesKeys}
      />
    )
  );
};

const AccordionCouche = ({
  list,
}: {
  list: { header: string; content: ReactNode }[];
}) => {
  const { handleShowClose, activesKeys } = useAccordionState([false]); //tous les onglet fermé
  return (
    list && (
      <AccordionCustom
        list={list}
        handleShowClose={handleShowClose}
        activesKeys={activesKeys}
      />
    )
  );
};

export default CoucheForm;
