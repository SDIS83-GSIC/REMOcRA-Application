import { SetStateAction, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import { useGet, usePut } from "../../components/Fetch/useFetch.js";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import SortableTableTourneePei from "../../components/DragNDrop/SortableItem.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconTournee } from "../../components/Icon/Icon.tsx";
import { PeiInfoEntity } from "../../Entities/PeiEntity.tsx";

const TourneePei = () => {
  const { tourneeId } = useParams();

  const tourneePeiInfo = useGet(
    url`/api/tournee/listPeiTournee/` + tourneeId,
    {},
  );

  const [data, setData] = useState<PeiInfoEntity[]>(null);
  const [errorMessage, setErrorMessage] = useState<string>(null);
  const navigate = useNavigate();

  if (tourneePeiInfo.isResolved && data == null) {
    setData(
      tourneePeiInfo.data.listPeiTournee.map((e) => {
        return {
          id: e.peiId,
          peiNumeroComplet: e.peiNumeroComplet,
          natureLibelle: e.natureLibelle,
          peiNumeroVoie: e.peiNumeroVoie,
          voieLibelle: e.voieLibelle,
          communeLibelle: e.communeLibelle,
          tourneeId: tourneeId,
        };
      }),
    );
  }

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

  const submitList = () => {
    const formData = new FormData();
    const formatedData = data.map((e, index) => {
      return {
        tourneeId: e.tourneeId,
        peiId: e.id,
        lTourneePeiOrdre: index + 1,
      };
    });
    formData.append("listTourneePei", JSON.stringify(formatedData));

    execute.run(formData);
  };

  return (
    data && (
      <Container>
        <PageTitle
          icon={<IconTournee />}
          title={`Gestion des PEI de la tournée ${tourneePeiInfo.data.tourneeLibelle}`}
        />
        <Row className="my-3 mx-2">
          <Col>
            <div>Libelle tournée : {tourneePeiInfo.data.tourneeLibelle}</div>
            <div>Organisme : {tourneePeiInfo.data.organismeLibelle}</div>
          </Col>
          <Col sm={"auto"}>
            <Button onClick={submitList}>Enregistrer la liste</Button>
          </Col>
        </Row>
        {/* Tableau triable */}
        {errorMessage !== null && <div>{errorMessage}</div>}
        <SortableTableTourneePei data={data} setData={setData} />
      </Container>
    )
  );
};

export default TourneePei;
