import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import Header from "../../components/Header/Header.tsx";
import { IconDFCI } from "../../components/Icon/Icon.tsx";
import MapDFCI from "../../components/Map/MapDFCI/MapDFCI.tsx";
import SquelettePage from "../SquelettePage.tsx";

const DFCI = () => {
  return (
    <SquelettePage navbar={<Header />}>
      <PageTitle title={"Carte DFCI"} icon={<IconDFCI />} />
      <MapDFCI />
    </SquelettePage>
  );
};

export default DFCI;
