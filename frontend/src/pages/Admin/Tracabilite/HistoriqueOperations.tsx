import { useEffect } from "react";
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
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import { URLS } from "../../../routes.tsx";
import { useGetRun } from "../../../components/Fetch/useFetch.tsx";
import { formatDateHeure } from "../../../utils/formatDateUtils.tsx";
import { SelectOption, useRefs } from "./useRefs.ts";

type FormValues = {
  typeObjet: string | null; // null plutôt qu'un optional à cause de URLSearchParams (cf. en dessous)
  typeOperation: string | null;
  typeUtilisateur: string | null;
  debut: string | null;
  fin: string | null;
  utilisateur: string | null;
  objetId: string | null;
};

const HistoriqueOperations = () => {
  const { typeOperations, typeObjets, typeUtilisateurs, isLoading } = useRefs();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { run, data } = useGetRun(
    url`/api/tracabilite/search?${searchParams}`,
    {},
  );
  const tracabilites: Tracabilite[] = searchParams.size ? data || [] : [];

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
    utilisateur: searchParams.get("utilisateur") ?? "",
    objetId: searchParams.get("objetId") ?? "",
  };

  useEffect(() => {
    if (!searchParams.size) {
      return;
    }
    const hasSomeValueFiltered = searchParams
      .keys()
      .some((k) => Object.keys(initialValues).includes(k));

    if (hasSomeValueFiltered) {
      run();
    }
  }, [searchParams]);

  if (isLoading) {
    return <Loading />;
  }

  const handleSubmit = (values: FormValues) => {
    //@ts-expect-error url n'est pas content si on passe une variable string et non object 🤔
    navigate(url`${URLS.HISTORIQUE_OPERATIONS}?${nullifyEmptyValue(values)}`);
  };

  return (
    <Container>
      <h1>Historique des opérations</h1>
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
        <h4>Résultats de la recherche</h4>
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
            {tracabilites.length > 0 &&
              tracabilites.map((d, i) => (
                <tr key={i}>
                  <td>{d.tracabiliteTypeObjet}</td>
                  <td>{d.tracabiliteTypeOperation}</td>
                  <td>{formatDateHeure(d.tracabiliteDate)}</td>
                  <td>
                    login : {d.tracabiliteAuteurData.nom}
                    <br />
                    email : {d.tracabiliteAuteurData.email}
                  </td>
                  <td>
                    <pre>{JSON.stringify(d.tracabiliteObjetData, null, 2)}</pre>
                  </td>
                </tr>
              ))}
          </tbody>
        </Table>
      </Row>
    </Container>
  );
};

export default HistoriqueOperations;

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
    navigate(URLS.HISTORIQUE_OPERATIONS);
  };

  return (
    <FormContainer>
      <FormLabel label="Type d'objet" name="typeObjets" required={false} />
      <SelectInput
        name="typeObjet"
        required={false}
        isClearable
        options={typeObjets}
        getOptionLabel={(d) => d.label}
        getOptionValue={(d) => d.value}
        onChange={(e) =>
          setFieldValue(
            "typeObjet",
            typeObjets.find((to) => to.value === e?.value)?.value,
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
        required={false}
        isClearable
        options={typeOperations}
        getOptionLabel={(d) => d.label}
        getOptionValue={(d) => d.value}
        onChange={(e) =>
          setFieldValue(
            "typeOperation",
            typeOperations.find((to) => to.value === e?.value)?.value,
          )
        }
        defaultValue={
          typeOperations.find((to) => to.value === values.typeOperation) ?? null
        }
      />
      <FormLabel
        label="Période"
        name="periode"
        required={false}
        tooltipText={
          <>
            Saisies possibles :
            <ul>
              <li>date de début uniquement</li>
              <li>date de fin uniquement</li>
              <li>date de début et date de fin</li>
            </ul>
          </>
        }
      />
      <Row>
        <Col>
          <DateTimeInput
            name="debut"
            dateType="datetime-local"
            required={false}
          />
        </Col>
        <Col>
          <DateTimeInput
            name="fin"
            dateType="datetime-local"
            required={false}
          />
        </Col>
      </Row>
      <FormLabel
        label="Type d'utilisateur"
        name="typeUtilisateurs"
        required={false}
      />
      <SelectInput
        name="typeUtilisateur"
        required={false}
        isClearable
        options={typeUtilisateurs}
        getOptionLabel={(d) => d.label}
        getOptionValue={(d) => d.value}
        onChange={(e) => {
          setFieldValue(
            "typeUtilisateur",
            typeUtilisateurs.find((to) => to.value === e?.value)?.value,
          );
        }}
        defaultValue={
          typeUtilisateurs.find((to) => to.value === values.typeUtilisateur) ??
          null
        }
      />
      <FormLabel
        label="Utilisateur"
        name="utilisateur"
        required={false}
        tooltipText={
          <>
            Recherche inclusive (=&quot;contient la saisie&quot;) insensible à
            la casse sur le nom, le prénom et l&apos;email de l&apos;auteur de
            la modification
          </>
        }
      />
      <TextInput name="utilisateur" required={false} />
      <FormLabel label="Id de l'objet" name="objetId" required={false} />
      <TextInput name="objetId" required={false} />
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

type Tracabilite = {
  tracabiliteTypeObjet: string;
  tracabiliteTypeOperation: string;
  tracabiliteDate: string;
  tracabiliteAuteurData: { nom: string; email: string };
  tracabiliteObjetData: any;
};

const nullifyEmptyValue = (values: FormValues): FormValues =>
  Object.keys(values).reduce(
    (acc, k) => ({
      ...acc,
      [k]:
        values[k as keyof FormValues] === ""
          ? null
          : values[k as keyof FormValues],
    }),
    values,
  );
