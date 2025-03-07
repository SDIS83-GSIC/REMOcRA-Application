import { Row, Col, Container, Button } from "react-bootstrap";
import { FieldSet } from "../../../components/Form/Form.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import SelectInputBar from "../../../components/Filter/SelectInputBar.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import DATE_FILTER, {
  LAST_MESSAGE_FILTER,
} from "../../../enums/FilterEvent.tsx";

const FilterEvent = ({
  criseId,
  setSearchParam,
  run,
  statut,
}: {
  criseId: string;
  run: any;
  setSearchParam: any;
  statut: string;
}) => {
  const typeCriseState = useGet(
    url`/api/crise/${criseId}/getTypeEventFromCrise/${statut}`,
  )?.data?.[0];

  const setValue = (name: string, value: string) => {
    setSearchParam((previous: any) => ({
      ...(previous || {}),
      [name]: value === "0" || value === "" || value === "[]" ? null : value,
    }));
  };

  return (
    <Container>
      <FieldSet title={"Filtrer les évènements"}>
        <Row>
          <p>Type d&apos;évènement</p>
          <MultiSelectFilterFromList
            name={"listTypeEvent"}
            listIdCodeLibelle={typeCriseState?.typeEvenement}
            onChange={(e: { value: string }) => setValue("filterType", e.value)}
          />
        </Row>

        <Row>
          <p>Auteur</p>
          <MultiSelectFilterFromList
            name={"listAuteur"}
            listIdCodeLibelle={typeCriseState?.utilisateur}
            onChange={(e: { value: string }) =>
              setValue("filterAuthor", e.value)
            }
          />
        </Row>

        <Row>
          <Col>
            <p>Dernier message</p>
            <SelectEnumOption
              options={LAST_MESSAGE_FILTER}
              name={"dernierMessage"}
              onChange={(e: { value: string }) =>
                setValue("filterMessage", e.value)
              }
            />
          </Col>

          <Col>
            <p>Importance</p>
            <SelectInputBar
              step={1}
              min={0}
              name={"evenementImportance"}
              max={5}
              required={false}
              onChange={(e: { value: string }) =>
                setValue("filterImportance", e.value)
              }
            />
          </Col>
        </Row>

        <Row>
          <Col>
            <p>Statut</p>
            <SelectEnumOption
              options={DATE_FILTER}
              name={"statut"}
              onChange={(e: { value: string }) =>
                setValue("filterStatut", e.value)
              }
            />
          </Col>

          <Col className="text-center">
            <Button className="mt-4" onClick={() => run()}>
              Rechercher
            </Button>
          </Col>
        </Row>
      </FieldSet>
      <br />
    </Container>
  );
};

export default FilterEvent;
