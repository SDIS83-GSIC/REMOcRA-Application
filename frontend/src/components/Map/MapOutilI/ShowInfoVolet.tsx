import { Container } from "react-bootstrap";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconInfo } from "../../Icon/Icon.tsx";
import ListInfos from "./ListInfos.tsx";

const OutilIVolet = ({ generalsInfos }: { generalsInfos: any[] }) => {
  return (
    <Container>
      <PageTitle
        icon={<IconInfo />}
        title={"Informations"}
        displayReturnButton={false}
      />

      <ListInfos data={generalsInfos} />
    </Container>
  );
};

export default OutilIVolet;
