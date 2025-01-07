import { Button, Col, Container, Row, Stack, Table } from "react-bootstrap";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import {
  DateTimeInput,
  FormContainer,
  TextInput,
  FormLabel,
  SelectInput,
} from "../../../components/Form/Form.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";

type SelectOption = {
  label: string;
  value: string;
};

type Refs = {
  typeOperations: SelectOption[];
  typeObjets: SelectOption[];
  typeUtilisateurs: SelectOption[];
};

const stringToSelectOption = (d: string) => ({
  label: d,
  value: d,
});

const useRefs = (): Refs => {
  const { data } = useGet("/api/tracabilite/refs");

  const typeOperations = data
    ? data.typeOperations.map(stringToSelectOption)
    : [];

  const typeObjets = data ? data.typeObjets.map(stringToSelectOption) : [];

  const typeUtilisateurs = data
    ? data.typeUtilisateurs.map(stringToSelectOption)
    : [];

  return {
    typeOperations,
    typeObjets,
    typeUtilisateurs,
  };
};

const Tracabilite = () => {
  const { typeOperations, typeObjets, typeUtilisateurs } = useRefs();

  return (
    <Container>
      <Row className="justify-content-md-center">
        <Col className="p-2 border rounded mx-2" xs={5}>
          <MyFormik
            initialValues={[]}
            prepareVariables={() => {}}
            validationSchema={{}}
            submitUrl=""
            isPost
            onSubmit={() => {}}
          >
            <FormContainer>
              <FormLabel
                label="Type d'objet"
                name="typeObjet"
                required={false}
              />
              <SelectInput
                name="typeObjet"
                options={typeObjets}
                getOptionLabel={(d) => d.label}
                getOptionValue={(d) => d.value}
                onChange={() => {}}
              />
              <FormLabel
                label="Type d'operation"
                name="typeOperation"
                required={false}
              />
              <SelectInput
                name="typeOperation"
                options={typeOperations}
                getOptionLabel={(d) => d.label}
                getOptionValue={(d) => d.value}
                onChange={() => {}}
              />
              <FormLabel label="Période" name="periode" required={false} />
              <Row>
                <Col>
                  <DateTimeInput name="debut" dateType="date" />
                </Col>
                <Col>
                  <DateTimeInput name="fin" dateType="date" />
                </Col>
              </Row>
              <FormLabel
                label="Type d'utilisateur"
                name="typeUtilisateur"
                required={false}
              />
              <SelectInput
                name="typeUtilisateur"
                options={typeUtilisateurs}
                getOptionLabel={(d) => d.label}
                getOptionValue={(d) => d.value}
                onChange={() => {}}
              />
              <FormLabel
                label="Utilisateur"
                name="utilisateur"
                required={false}
              />
              <TextInput name="utilisateur" />
              <Stack
                direction="horizontal"
                gap={2}
                className="mt-3 justify-content-end"
              >
                <Button variant="secondary" type="reset">
                  Réinitialiser
                </Button>
                <Button type="submit">Rechercher</Button>
              </Stack>
            </FormContainer>
          </MyFormik>
        </Col>
      </Row>
      <Row className="mt-5">
        <h4>Résultat de la recherche</h4>
        <Table bordered striped>
          <thead>
            <tr>
              <th>Type objet</th>
              <th>Type opération</th>
              <th>Date / Heure</th>
              <th>Auteur</th>
              <th>Objet</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>PEI</td>
              <td>Création</td>
              <td>01/01/2025 09/00</td>
              <td>
                login: johndoe
                <br />
                email: john@doe.com
              </td>
              <td>
                <pre>
                  id: XXX numéro: 1234 5678 commune: Dijon localisation:
                  Boulevard Carnot
                </pre>
              </td>
            </tr>
          </tbody>
        </Table>
      </Row>
    </Container>
  );
};

export default Tracabilite;
