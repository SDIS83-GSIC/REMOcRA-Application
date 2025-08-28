import { Container } from "react-bootstrap";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { IconInfo } from "../../Icon/Icon.tsx";

const ShowInfoVolet = ({ generalsInfos }: { generalsInfos: string }) => {
  return (
    <Container>
      <PageTitle
        icon={<IconInfo />}
        title={generalsInfos}
        displayReturnButton={false}
      />
    </Container>
  );
};

export default ShowInfoVolet;
