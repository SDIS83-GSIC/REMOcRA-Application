import { SetStateAction, useEffect, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import ReactSelect from "react-select";
import SortableTableTourneePei from "../../components/DragNDrop/SortableItem.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet, usePut } from "../../components/Fetch/useFetch.js";
import { IconTournee } from "../../components/Icon/Icon.tsx";
import { PeiInfoEntity } from "../../Entities/PeiEntity.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";

const TourneePei = ({
  tourneeMapId,
  listePeiId,
  closeVolet,
}: {
  tourneeMapId?: string;
  listePeiId?: string[];
  closeVolet: () => void;
}) => {
  const { tourneeId } = useParams();

  const tourneeIdToUse = tourneeMapId ?? tourneeId;

  const tourneePeiInfo = useGet(
    url`/api/tournee/listPeiTournee/${tourneeIdToUse}?${{
      listePeiId: JSON.stringify(listePeiId),
    }}`,
  );

  // TODO ajouter la liste des PEI en plus
  const allPeiInfoSource = useGet(
    url`/api/tournee/listPei/` + tourneeIdToUse,
    {},
  );

  const [data, setData] = useState<PeiInfoEntity[]>(null);
  const [errorMessage, setErrorMessage] = useState<string>(null);
  const [displaySection, setDisplaySection] = useState<boolean>(false);
  const [selectOptions, setSelectOptions] = useState<PeiInfoEntity[]>(null);
  const [selectedPei, setSelectedPei] = useState<PeiInfoEntity>(null);

  const { success: successToast, error: errorToast } = useToastContext();

  const navigate = useNavigate();

  if (tourneePeiInfo.isResolved && data == null) {
    setData(
      tourneePeiInfo.data.listPeiTournee.map((e) => {
        return {
          id: e.peiId,
          peiNumeroComplet: e.peiNumeroComplet,
          natureDeciCode: e.natureDeciCode,
          natureLibelle: e.natureLibelle,
          peiNumeroVoie: e.peiNumeroVoie,
          voieLibelle: e.voieLibelle,
          communeLibelle: e.communeLibelle,
          tourneeId: tourneeIdToUse,
        };
      }),
    );
  }

  const execute = usePut(
    url`/api/tournee/listPeiTournee/update/` + tourneeIdToUse,
    {
      onResolve: () => {
        closeVolet ? closeVolet() : navigate(URLS.LIST_TOURNEE);
        successToast("L'élément a bien été déplacé.");
      },
      onReject: async (error: {
        text: () => SetStateAction<null> | PromiseLike<SetStateAction<null>>;
      }) => {
        setErrorMessage(await error.text());
        errorToast(error.text());
      },
    },
    true,
  );

  useEffect(() => {
    if (allPeiInfoSource.isResolved && data) {
      setSelectedPei(null);
      setSelectOptions(filterOptions());
    }
  }, [data]);

  const submitList = () => {
    const formData = new FormData();
    const formattedData = data.map((e, index) => {
      return {
        tourneeId: e.tourneeId,
        peiId: e.id,
        lTourneePeiOrdre: index + 1,
      };
    });
    formData.append("listTourneePei", JSON.stringify(formattedData));
    execute.run(formData);
  };

  const filterOptions = () => {
    let filteredList: PeiInfoEntity[] = allPeiInfoSource.data;
    if (data.length > 0) {
      filteredList = filteredList.filter(
        (e) => e.natureDeciCode === data[0].natureDeciCode,
      );
      filteredList = filteredList.filter(
        (v) => !data.map((e) => e.id).includes(v.peiId),
      );
    }
    return filteredList.map((e) => {
      return {
        id: e.peiId,
        peiNumeroComplet: e.peiNumeroComplet,
        natureDeciCode: e.natureDeciCode,
        natureLibelle: e.natureLibelle,
        peiNumeroVoie: e.peiNumeroVoie,
        voieLibelle: e.voieLibelle,
        communeLibelle: e.communeLibelle,
        tourneeId: tourneeIdToUse,
      };
    });
  };

  const showAddPeiSection = () => {
    setSelectOptions(filterOptions());
    setDisplaySection(!displaySection);
  };

  const addPei = () => {
    setData((data) => [...data, selectedPei]);
  };

  return (
    data && (
      <Container>
        <PageTitle
          displayReturnButton={tourneeMapId != null}
          icon={<IconTournee />}
          title={`Gestion des PEI de la tournée ${tourneePeiInfo.data.tourneeLibelle}`}
        />
        <Row className="my-3 mx-2">
          <Col>
            <div>Libellé tournée : {tourneePeiInfo.data.tourneeLibelle}</div>
            <div>Organisme : {tourneePeiInfo.data.organismeLibelle}</div>
          </Col>
          <Col sm={"auto"}>
            <Button onClick={submitList}>Enregistrer la liste</Button>
          </Col>
        </Row>
        <Button onClick={showAddPeiSection} className="my-3 mx-2">
          Ajouter un PEI
        </Button>
        {displaySection && allPeiInfoSource && (
          <Row className="m-2 p-1 bg-light border border-1 border-secondary">
            <Col>
              <ReactSelect
                options={selectOptions}
                getOptionValue={(t) => t.id}
                getOptionLabel={(t) => t.peiNumeroComplet}
                value={selectedPei}
                onChange={setSelectedPei}
                className="my-3 mx-2"
                placeholder={"Sélectionnez"}
                noOptionsMessage={() => "Aucune donnée trouvée"}
              />
            </Col>
            <Col sm={"auto"}>
              <Button
                onClick={addPei}
                className="my-3 mx-2"
                disabled={selectedPei === null}
              >
                Ajouter un PEI
              </Button>
            </Col>
          </Row>
        )}
        {/* Tableau triable */}
        {errorMessage !== null && <div>{errorMessage}</div>}
        <SortableTableTourneePei data={data} setData={setData} />
      </Container>
    )
  );
};

export default TourneePei;
