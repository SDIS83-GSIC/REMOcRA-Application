import { Button, Container } from "react-bootstrap";
import { WKT } from "ol/format";
import { useEffect, useState } from "react";
import { shortenString } from "../../../utils/fonctionsUtils.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useGet, useGetRun } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import useModal from "../../../components/Modal/ModalUtils.tsx";
import EditModal from "../../../components/Modal/EditModal.tsx";
import { IconFilter, IconLocation } from "../../../components/Icon/Icon.tsx";
import MessageElement from "../../../components/message/messageElement.tsx";
import MessageForm, {
  getInitialValue,
  messageValidationSchema,
  prepareMessageValues,
} from "./message/MessageForm.tsx";
import FilterEvent from "./filterEvent.tsx";

type FilterEvenement = {
  filterType: string;
  filterAuthor: string;
  filterMessage: string;
  filterStatut: string;
  filterImportance: string;
};

const ListEvenement = ({
  criseId,
  map,
  stateEvent,
}: {
  criseId: string;
  map: any;
  stateEvent: string;
}) => {
  const [params, setSearchParam] = useState<FilterEvenement>({});
  const [buttonClicked, setButtonClick] = useState<boolean>(false);

  const { data, run } = useGetRun(
    url`/api/crise/${criseId}/evenement/${stateEvent}/?${params}`,
    {},
  );

  useEffect(() => {
    if (!data) {
      run();
    }
  }, [data, run]);

  const { user } = useAppContext();
  const { visible, show, close } = useModal();

  const [evenementId, setEvenementId] = useState();

  const tableau: { header: string; content: JSX.Element }[] = [];
  const listMessage = useGet(url`/api/crise/evenement/message`);
  const { activesKeys, handleShowClose } = useAccordionState(
    Array(6).fill(false),
  );

  const showEventLocation = (eventId: string) => {
    for (let i = 0; i < data?.length; i++) {
      const eventGeometry = data?.[i].evenementGeometrie;
      if (eventGeometry && data?.[i].evenementId === eventId) {
        const geom = new WKT().readFeature(eventGeometry.split(";").pop());
        map?.getView().fit(geom.get("geometry"), {
          padding: [50, 50, 50, 50],
          maxZoom: 20,
        });
      }
    }
  };

  data?.map(
    (e: {
      evenementGeometrie: string;
      evenementLibelle: string;
      evenementId: string;
    }) => {
      const eventMessages = listMessage?.data?.filter(
        (message: { messageEvenementId: string }) =>
          message.messageEvenementId === e.evenementId,
      );

      tableau.push({
        header: shortenString(e.evenementLibelle, 35),
        content: (
          <>
            <Button
              style={{ marginBottom: "10px", marginRight: "15px" }}
              onClick={() => {
                setEvenementId(e.evenementId);
                show();
              }}
            >
              nouveau message
            </Button>

            {e.evenementGeometrie && (
              <Button
                style={{ marginBottom: "10px" }}
                onClick={() => {
                  showEventLocation(e.evenementId);
                }}
              >
                <IconLocation />
              </Button>
            )}

            {eventMessages?.map((message: any, index: number) => (
              <div key={index}>
                <MessageElement message={message} />
              </div>
            ))}
          </>
        ),
      });
    },
  );

  return (
    <Container>
      <Button
        style={{ marginBottom: "10px" }}
        onClick={() => {
          setButtonClick(!buttonClicked);
        }}
      >
        <IconFilter />
      </Button>

      {buttonClicked && (
        <FilterEvent
          statut={stateEvent}
          setSearchParam={setSearchParam}
          run={run}
          criseId={criseId}
        />
      )}

      <AccordionCustom
        activesKeys={activesKeys}
        list={tableau}
        handleShowClose={handleShowClose}
      />

      {/* messages */}
      <EditModal
        closeModal={close}
        canModify={true}
        query={url`/api/crise/evenement/${evenementId}/message/create`}
        submitLabel={"Valider"}
        visible={visible}
        header={null}
        validationSchema={messageValidationSchema}
        onSubmit={() => {
          listMessage.reload();
        }}
        prepareVariables={(values) => prepareMessageValues(values)}
        getInitialValues={(values) =>
          getInitialValue(values, user.utilisateurId)
        }
      >
        <MessageForm />
      </EditModal>
    </Container>
  );
};

export default ListEvenement;
