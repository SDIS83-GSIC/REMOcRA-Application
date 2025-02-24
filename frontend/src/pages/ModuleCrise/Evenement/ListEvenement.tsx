import { Button, Container } from "react-bootstrap";
import { SetStateAction, useState } from "react";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import MessageElement from "../../../components/message/messageElement.tsx";
import useModal from "../../../components/Modal/ModalUtils.tsx";
import EditModal from "../../../components/Modal/EditModal.tsx";
import MessageForm, {
  getInitialValue,
  messageValidationSchema,
  prepareMessageValues,
} from "./message/MessageForm.tsx";

const ListEvenement = ({ criseId }: { criseId: string }) => {
  const getEvents = useGet(url`/api/crise/${criseId}/evenement`)?.data;
  const { activesKeys, handleShowClose } = useAccordionState(
    Array(6).fill(false),
  );
  const { user } = useAppContext();

  const { visible, show, close } = useModal();
  const [evenementId, setEvenementId] = useState();

  function shortenString(str: string, maxLength: number): string {
    if (str.length > maxLength) {
      return str.substring(0, maxLength) + "...";
    } else {
      return str;
    }
  }

  const tableau: { header: string; content: JSX.Element }[] = [];
  getEvents?.map(
    (e: {
      evenementLibelle: string;
      evenementId: SetStateAction<undefined>;
    }) => {
      tableau.push({
        header: shortenString(e.evenementLibelle, 35),
        content: (
          <>
            <Button
              style={{ marginBottom: "10px" }}
              onClick={() => {
                setEvenementId(e.evenementId);
                show();
              }}
            >
              nouveau message
            </Button>
            <MessageElement />
          </>
        ),
      });
    },
  );

  return (
    <Container>
      <AccordionCustom
        activesKeys={activesKeys}
        list={tableau}
        handleShowClose={handleShowClose}
      />

      <EditModal
        closeModal={close}
        canModify={true}
        query={url`/api/crise/evenement/${evenementId}/message/create`}
        submitLabel={"Valider"}
        visible={visible}
        header={null}
        validationSchema={messageValidationSchema}
        onSubmit={() => true}
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
