import { Container } from "react-bootstrap";
import { ReactNode } from "react";
import Footer from "../components/Footer/Footer.tsx";

type SquelettePageType = { children?; header?: ReactNode; fluid?: boolean };
const SquelettePage = ({
  children,
  header,
  fluid = true,
}: SquelettePageType) => {
  return (
    <>
      <Container fluid>{header}</Container>
      <Container fluid={fluid} className={"main"}>
        {children}
      </Container>
      <Container fluid>
        <Footer />
      </Container>
    </>
  );
};

export default SquelettePage;
