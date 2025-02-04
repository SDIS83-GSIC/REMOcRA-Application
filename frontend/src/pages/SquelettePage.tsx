import { Container } from "react-bootstrap";
import { ReactNode } from "react";
import Footer from "../components/Footer/Footer.tsx";
import BanniereHeader from "../components/Header/BanniereHeader.tsx";
import GoTopButton from "../components/GoTopButton/GoTopButton.tsx";

type SquelettePageType = {
  children?;
  navbar?: ReactNode;
  fluid?: boolean;
  banner?: boolean;
};
const SquelettePage = ({
  children,
  navbar,
  fluid = true,
  banner = false,
}: SquelettePageType) => {
  return (
    <div id={"page"}>
      {banner && (
        <Container fluid id={"banner"}>
          <BanniereHeader />
        </Container>
      )}
      <Container fluid id={"navbar"}>
        {navbar}
      </Container>
      <Container fluid={fluid} id={"main"}>
        <div className={"d-flex flex-column h-100"}>{children}</div>
        <GoTopButton />
      </Container>
      <Container fluid id={"footer"}>
        <Footer />
      </Container>
    </div>
  );
};

export default SquelettePage;
