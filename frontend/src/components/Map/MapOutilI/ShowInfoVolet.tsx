import { Container } from "react-bootstrap";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconInfo } from "../../Icon/Icon.tsx";
import ListInfos from "./ListInfos.tsx";

export type CoucheMetadata = {
  groupeCoucheId: string;
  coucheId: string;
  coucheLibelle: string;
  coucheMetadataId?: string;
  coucheMetadataActif: boolean;
  coucheMetadataPublic: boolean;
  coucheMetadataStyle?: string | null;
  groupeFonctionnaliteIds?: string[];
};

const OutilIVolet = ({
  generalsInfos,
  coucheMetadata,
}: {
  generalsInfos: any[];
  coucheMetadata: CoucheMetadata[];
}) => {
  return (
    <Container>
      <PageTitle
        icon={<IconInfo />}
        title={"Informations"}
        displayReturnButton={false}
      />

      <ListInfos data={generalsInfos} coucheMetadata={coucheMetadata} />
    </Container>
  );
};

export default OutilIVolet;
