import { ReactNode } from "react";
import { Button, Table } from "react-bootstrap";
import url from "../../module/fetch.tsx";
import { IconExport } from "../Icon/Icon.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import PageTitle from "../Elements/PageTitle/PageTitle.tsx";
import THEMATIQUE from "../../enums/ThematiqueEnum.tsx";

const ListeDocumentThematique = ({
  codeThematique,
  titre,
  icon,
  displayReturnButton = false,
}: {
  codeThematique: THEMATIQUE;
  titre: string;
  icon: ReactNode;
  displayReturnButton?: boolean;
}) => {
  const { isResolved, data: listeDocument } = useGet(
    url`/api/document-habilitable/` + codeThematique,
  );
  return (
    <>
      <PageTitle
        title={titre}
        icon={icon}
        displayReturnButton={displayReturnButton}
      />
      <Table bordered striped>
        <thead>
          <tr>
            <th>Libellé</th>
            <th>Date de dernière mise à jour</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {isResolved &&
            listeDocument?.map((e, index) => {
              return (
                <tr key={index}>
                  <td>{e.libelle}</td>
                  <td>{e.date && formatDateTime(e.date)}</td>
                  <td>
                    <Button
                      className={"text-warning"}
                      href={url`/api/document-habilitable/telecharger/` + e.id}
                    >
                      <IconExport />
                    </Button>
                  </td>
                </tr>
              );
            })}
        </tbody>
      </Table>
    </>
  );
};

export default ListeDocumentThematique;
