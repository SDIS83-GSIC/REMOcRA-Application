import { DndContext } from "@dnd-kit/core";
import { arrayMove, SortableContext } from "@dnd-kit/sortable";
import { SetStateAction, useEffect, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import SortableTourneePei from "../../components/DragNDrop/SortableItem.tsx";
import { useGet, usePut } from "../../components/Fetch/useFetch.js";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";

interface PeiIdNumero {
  id: string;
  peiNumeroComplet: string;
  tourneeId: number;
}

const TourneePei = () => {
  const { tourneeId } = useParams();
  const [items, setItems] = useState<PeiIdNumero[]>([]);
  const [errorMessage, setErrorMessage] = useState<string>(null);
  const navigate = useNavigate();

  const listPeiTournee = useGet(
    url`/api/tournee/listPeiTournee/` + tourneeId,
    {},
  );

  const execute = usePut(
    url`/api/tournee/listPeiTournee/update/` + tourneeId,
    {
      onResolve: () => {
        // TODO: Ajouter un toast
        navigate(URLS.TOURNEE);
      },
      onReject: async (error: {
        text: () => SetStateAction<null> | PromiseLike<SetStateAction<null>>;
      }) => {
        setErrorMessage(await error.text());
      },
    },
    true,
  );

  useEffect(() => {
    if (listPeiTournee.isResolved) {
      setItems(
        listPeiTournee.data.map((e) => {
          return {
            id: e.peiId,
            peiNumeroComplet: e.peiNumeroComplet,
            tourneeId: tourneeId,
          };
        }),
      );
    }
  }, [listPeiTournee.data, setItems]);

  function dragEndEvent(e: DndContext) {
    const { over, active } = e;
    setItems((items) => {
      return arrayMove(
        items,
        items.findIndex((item) => item.id === active.id),
        items.findIndex((item) => item.id === over?.id),
      );
    });
  }

  const submitList = () => {
    const formData = new FormData();
    const formatedData = items.map((e, index) => {
      return {
        peiId: e.id,
        tourneeId: e.tourneeId,
        lTourneePeiOrdre: index + 1,
      };
    });
    formData.append("listTourneePei", JSON.stringify(formatedData));

    execute.run(formData);
  };

  return (
    <Container>
      <h2>Mes PEI d&apos;une tournée</h2>
      <Button onClick={submitList}>Enregistrer la liste</Button>
      {/* Liste triable */}
      {errorMessage !== null && <div>{errorMessage}</div>}
      <div className="w-75">
        <DndContext onDragEnd={dragEndEvent}>
          <SortableContext items={items}>
            {items.map((v, index) => (
              <Row key={v.id}>
                <Col>{index + 1}</Col>
                <Col>
                  {/* LA PROPRIÉTÉ V.ID DOIT OBLIGATOIREMENT S'APPELER ID */}
                  <SortableTourneePei
                    key={v.id}
                    id={v.id}
                    peiNumeroComplet={v.peiNumeroComplet}
                  />
                </Col>
              </Row>
            ))}
          </SortableContext>
        </DndContext>
      </div>
    </Container>
  );
};

export default TourneePei;
