import { useFormikContext } from "formik";
import { Alert, Button, Card, Col } from "react-bootstrap";
import { object } from "yup";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  FormContainer,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { IconInfo } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { formatData, QueryParam } from "../Constants.tsx";

type QueryFormProps = {
  activeQuery: QueryParam;
  setActiveQuery: any;
  setQueryData: any;
  setAvailableOptions: any;
};

const QueryForm = (props: QueryFormProps) => {
  const urlApiQuery = url`/api/dashboard/validate-query`;

  const updateData = (data: any) => {
    props.setActiveQuery({
      ...props.activeQuery,
      title: data.queryTitle,
      query: data.querySql,
    });
    const dataFormatted = formatData(data);
    props.setQueryData(dataFormatted);
    props.setAvailableOptions(Object.keys(dataFormatted[0]));
  };

  const getInitialValues = (activeQuery: QueryParam) => ({
    queryId: activeQuery.id,
    query: activeQuery.query,
    queryTitle: activeQuery.title,
  });

  return (
    <Card bg="secondary" className="m-3">
      <Card.Body>
        <MyFormik
          initialValues={getInitialValues(props.activeQuery)}
          isPost={true}
          submitUrl={urlApiQuery}
          onSubmit={(values) => updateData(values)}
          successToastMessage="La requête est valide"
          prepareVariables={() => ({})}
          validationSchema={object({})}
        >
          <InnerQueryForm />
        </MyFormik>
      </Card.Body>
    </Card>
  );
};

const InnerQueryForm = () => {
  const { setValues } = useFormikContext();
  const { data: zoneCompetenceList } = useGet(url`/api/zone-integration/list`);
  const { data: utilisateurList } = useGet(url`/api/utilisateur/list`);
  const { data: organismeList } = useGet(url`/api/organisme/get-all`);

  const { activesKeys, handleShowClose } = useAccordionState(
    Array(2).fill(false),
  );

  return (
    <FormContainer>
      <AccordionCustom
        activesKeys={activesKeys}
        list={[
          {
            header: "Informations générales",
            content: (
              <span>
                {IconInfo()} Certaines requêtes ont besoin de s&apos;appuyer sur
                des paramètres qui dépendent de l&apos;utilisateur connecté :{" "}
                <ul>
                  <li>l&apos;organisme</li>
                  <li>la zone de compétence</li>
                  <li>l&apos;utilisateur en lui-même</li>
                </ul>
                Ces paramètres sont accessibles au travers de 3 constantes qui
                seront remplacées à l&apos;exécution :
                <ul>
                  <li>#ORGANISME_ID#</li>
                  <li>#ZONE_COMPETENCE_ID#</li>
                  <li>#UTILISATEUR_ID#</li>
                </ul>
                A utiliser tel quel, et ne pas entourer de quotes ou autre.
                Exemple pour une jointure sur les données de l&apos;organisme de
                l&apos;utilisateur connecté : &quot;WHERE organisme_id =
                #ORGANISME_ID#&quot;
              </span>
            ),
          },
          {
            header: "Paramètres",
            content: (
              <>
                <span>
                  Lorsque les constantes sont utilisées, il faut fournir une
                  valeur &quot;cohérente&quot; dans la requête, pour que
                  celle-ci retourne des résultats qui permettront de construire
                  les visualisations. Les valeurs saisies ne sont ni stockées ni
                  utilisées par l&apos;utilisateur final du tableau de bord,
                  pour lequel elles sont remplacées par la &quot;bonne&quot;
                  valeur
                </span>
                <SelectForm
                  name={"zoneCompetenceId"}
                  listIdCodeLibelle={zoneCompetenceList}
                  label="Zone de compétence"
                  required={false}
                  setValues={setValues}
                />
                <SelectForm
                  name={"utilisateurId"}
                  listIdCodeLibelle={utilisateurList}
                  label="Utilisateur"
                  required={false}
                  setValues={setValues}
                />
                <SelectForm
                  name={"organismeId"}
                  listIdCodeLibelle={organismeList}
                  label="Organisme"
                  required={false}
                  setValues={setValues}
                />
              </>
            ),
          },
        ]}
        handleShowClose={handleShowClose}
      />

      <TextInput
        required={false}
        name="queryTitle"
        label="Titre de la requête"
      />

      <TextAreaInput name="query" label="Requête SQL :" required={false} />
      <Col>
        <Alert
          variant="info"
          className="mt-2 mb-2 text-muted d-flex align-items-center"
        >
          <span>
            {" "}
            {IconInfo()} Tester la requête pour enregistrer les modifications
            sur le titre et/ou la requête .{" "}
          </span>
        </Alert>

        <Button type="submit" variant={"info"} className="mt-3">
          Tester la requête
        </Button>
      </Col>
    </FormContainer>
  );
};

export default QueryForm;
