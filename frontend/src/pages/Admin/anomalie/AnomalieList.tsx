import { useState } from "react";
import { Badge, Button, Table } from "react-bootstrap";
import Container from "react-bootstrap/Container";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import {
  IconAnomalie,
  IconDelete,
  IconEdit,
  IconSortList,
} from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import DeleteModal from "../../../components/Modal/DeleteModal.tsx";
import useModal from "../../../components/Modal/ModalUtils.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import CustomLinkButton from "../../../components/Button/CustomLinkButton.tsx";

const AnomalieList = () => {
  const anomalieListState = useGet(url`/api/anomalie/list`);
  const { visible, show, close, ref } = useModal();
  const [anomalieidToDelete, setAnomalieidToDelete] = useState<string>();

  if (!anomalieListState.isResolved) {
    return <Loading />;
  }

  const { anomalieList, categorieList, natureList, typeList } =
    anomalieListState.data;
  const typeMap = typeList.map((t) => {
    return {
      label: t,
      count: natureList.filter((n) => n.natureTypePei === t).length,
    };
  });
  const natureCount = natureList.length;

  return (
    <Container>
      <PageTitle
        title="Anomalies"
        icon={<IconAnomalie />}
        right={
          <CreateButton
            href={URLS.ANOMALIE_CREATE}
            title={"Ajouter une anomalie"}
          />
        }
      />
      <Table striped bordered hover>
        <thead>
          <tr>
            <th rowSpan={2}>Anomalie</th>
            {typeMap.map((_type, idxNT) => (
              <th key={idxNT} colSpan={_type.count}>
                {_type.label}
              </th>
            ))}
          </tr>
          <tr>
            {natureList.map((nature, idxN) => (
              <th key={idxN}>{nature.natureLibelle}</th>
            ))}
          </tr>
        </thead>
        {categorieList.map((categorie, idxC) => (
          <tbody key={idxC}>
            <tr>
              <th
                colSpan={natureCount + 1}
                className={"bg-primary text-light text-center"}
              >
                <span className="fs-6">
                  {categorie.anomalieCategorieLibelle}
                </span>
                <CustomLinkButton
                  className="float-end fs-6"
                  pathname={URLS.ANOMALIE_SORT(categorie.anomalieCategorieId)}
                  variant={"light"}
                >
                  <TooltipCustom
                    tooltipText={"Changer l'ordre des éléments"}
                    tooltipId={"tooltipOrder"}
                  >
                    <IconSortList />
                  </TooltipCustom>
                </CustomLinkButton>
              </th>
            </tr>
            {anomalieList
              .filter(
                ({ anomalie }) =>
                  anomalie.anomalieAnomalieCategorieId ===
                  categorie.anomalieCategorieId,
              )
              .map(({ anomalie, anomaliePoidsList }, idxA) => (
                <tr key={idxA}>
                  <th>
                    {anomalie.anomalieLibelle}&nbsp;
                    <CustomLinkButton
                      pathname={URLS.ANOMALIE_UPDATE(anomalie.anomalieId)}
                    >
                      <IconEdit />
                    </CustomLinkButton>
                    {!anomalie.anomalieProtected && (
                      <Button
                        variant={"link"}
                        className={"text-danger"}
                        onClick={() => {
                          setAnomalieidToDelete(anomalie.anomalieId);
                          show();
                        }}
                      >
                        <IconDelete />
                      </Button>
                    )}
                  </th>
                  {natureList.map((nature, idxN) => (
                    <td key={`${idxA}${idxN}`}>
                      {anomaliePoidsList
                        .filter(
                          (ap) => ap.poidsAnomalieNatureId === nature.natureId,
                        )
                        .map((ap) =>
                          ap.poidsAnomalieTypeVisite?.length > 0 ? (
                            <TooltipCustom
                              key={ap.anomaliePoidsId}
                              tooltipId={ap.anomaliePoidsId}
                              tooltipHeader={"Types de visite"}
                              tooltipText={ap.poidsAnomalieTypeVisite.join(
                                ", ",
                              )}
                            >
                              {ap.poidsAnomalieValIndispoTerrestre || "-"}
                              &nbsp;/&nbsp;
                              {ap.poidsAnomalieValIndispoHbe || "-"}
                              &nbsp;
                              <Badge bg="primary" pill={true}>
                                {ap.poidsAnomalieTypeVisite.length}
                              </Badge>
                            </TooltipCustom>
                          ) : (
                            <>
                              {ap.poidsAnomalieValIndispoTerrestre || "-"}
                              &nbsp;/&nbsp;
                              {ap.poidsAnomalieValIndispoHbe || "-"}
                            </>
                          ),
                        )}
                    </td>
                  ))}
                </tr>
              ))}
          </tbody>
        ))}
      </Table>
      {visible && anomalieidToDelete && (
        <DeleteModal
          visible={visible}
          closeModal={close}
          query={url`/api/anomalie/delete/` + anomalieidToDelete}
          ref={ref}
          onDelete={() => {
            setAnomalieidToDelete(null);
            anomalieListState.reload();
          }}
        />
      )}
    </Container>
  );
};

export default AnomalieList;
