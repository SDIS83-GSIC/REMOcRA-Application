import { Button, Col, Container, Row, Stack, Table } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Formik, useFormikContext } from "formik";
import { isValid, parseISO } from "date-fns";
import {
  DateTimeInput,
  FormContainer,
  TextInput,
  FormLabel,
  SelectInput,
} from "../../../components/Form/Form.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { useRefs } from "./useRefs.ts";

type SelectOption = {
  label: string;
  value: string;
};

export type Refs = {
  typeOperations: SelectOption[];
  typeObjets: SelectOption[];
  typeUtilisateurs: SelectOption[];
};

export const stringToSelectOption = (s: string) => ({
  label: s,
  value: s,
});

type FormValues = {
  typeObjet: string | null; // null plutôt qu'un optional à cause de URLSearchParams (cf. en dessous)
  typeOperation: string | null;
  typeUtilisateur: string | null;
  debut: string | null;
  fin: string | null;
  utilisateur: string | null;
};

const Tracabilite = () => {
  const { typeOperations, typeObjets, typeUtilisateurs } = useRefs();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const initialValues: FormValues = {
    typeObjet: searchParams.get("typeObjet"),
    typeOperation: searchParams.get("typeOperation"),
    typeUtilisateur: searchParams.get("typeUtilisateur"),
    debut: isValid(parseISO(searchParams.get("debut") ?? ""))
      ? searchParams.get("debut")
      : "",
    fin: isValid(parseISO(searchParams.get("fin") ?? ""))
      ? searchParams.get("fin")
      : "",
    utilisateur: "",
  };

  const handleSubmit = (values: FormValues) => {
    // @ts-expect-error url n'est pas content si on passe une variable string et non object 🤔
    navigate(url`${URLS.TRACABILITE}?${values}`);
  };

  return (
    <Container>
      <Row className="justify-content-md-center">
        <Col className="p-2 border rounded mx-2" xs={5}>
          <Formik
            initialValues={initialValues}
            enableReinitialize
            onSubmit={handleSubmit}
          >
            <SearchForm
              typeObjets={typeObjets}
              typeOperations={typeOperations}
              typeUtilisateurs={typeUtilisateurs}
            />
          </Formik>
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

type SearchFormProps = {
  typeOperations: SelectOption[];
  typeObjets: SelectOption[];
  typeUtilisateurs: SelectOption[];
};

const SearchForm = ({
  typeOperations,
  typeObjets,
  typeUtilisateurs,
}: SearchFormProps) => {
  const { values, setFieldValue } = useFormikContext<FormValues>();
  const navigate = useNavigate();

  const handleReset = () => {
    navigate(URLS.TRACABILITE);
  };

  return (
    <FormContainer>
      <FormLabel label="Type d'objet" name="typeObjets" required={false} />
      <SelectInput
        name="typeObjet"
        options={typeObjets}
        getOptionLabel={(d) => d.label}
        getOptionValue={(d) => d.value}
        onChange={(e) =>
          setFieldValue(
            "typeObjet",
            typeObjets.find((to) => to.value === e.value)?.value,
          )
        }
        defaultValue={
          typeObjets.find((to) => to.value === values.typeObjet) ?? null
        }
      />
      <FormLabel
        label="Type d'opération"
        name="typeOperations"
        required={false}
      />
      <SelectInput
        name="typeOperation"
        options={typeOperations}
        getOptionLabel={(d) => d.label}
        getOptionValue={(d) => d.value}
        onChange={(e) =>
          setFieldValue(
            "typeOperation",
            typeOperations.find((to) => to.value === e.value)?.value,
          )
        }
        defaultValue={
          typeOperations.find((to) => to.value === values.typeOperation) ?? null
        }
      />
      <FormLabel label="Période" name="periode" required={false} />
      <Row>
        <Col>
          <DateTimeInput name="debut" dateType="date" required={false} />
        </Col>
        <Col>
          <DateTimeInput name="fin" dateType="date" required={false} />
        </Col>
      </Row>
      <FormLabel
        label="Type d'utilisateur"
        name="typeUtilisateurs"
        required={false}
      />
      <SelectInput
        name="typeUtilisateur"
        options={typeUtilisateurs}
        getOptionLabel={(d) => d.label}
        getOptionValue={(d) => d.value}
        onChange={(e) =>
          setFieldValue(
            "typeUtilisateur",
            typeUtilisateurs.find((to) => to.value === e.value)?.value,
          )
        }
        defaultValue={
          typeUtilisateurs.find((to) => to.value === values.typeUtilisateur) ??
          null
        }
      />
      <FormLabel label="Utilisateur" name="utilisateur" required={false} />
      <TextInput name="utilisateur" required={false} />
      <Stack
        direction="horizontal"
        gap={2}
        className="mt-3 justify-content-end"
      >
        <Button variant="secondary" type="reset" onClick={handleReset}>
          Réinitialiser
        </Button>
        <Button type="submit">Rechercher</Button>
      </Stack>
    </FormContainer>
  );
};
