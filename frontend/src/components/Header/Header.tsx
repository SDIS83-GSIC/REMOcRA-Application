import "bootstrap/dist/css/bootstrap.min.css";
import { ReactNode } from "react";
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import Row from "react-bootstrap/Row";
import { URLS } from "../../routes.tsx";
import LinkButton from "../Button/LinkButton.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import { useAppContext } from "../App/AppProvider.tsx";

export interface NavToProps {
  path: string;
  label: ReactNode;
  aLeDroit: boolean;
}

const NavTo = ({ path, label, aLeDroit }: NavToProps) => {
  return (
    aLeDroit && (
      <Nav.Item>
        <LinkButton pathname={path} classname="nav-link">
          {label}
        </LinkButton>
      </Nav.Item>
    )
  );
};
const Header = ({ links }: { links?: NavToProps[] }) => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  return (
    <Row>
      <Navbar className="mb-3" expand="lg" bg={"primary"} data-bs-theme="dark">
        <Container>
          <Navbar.Brand href={URLS.ACCUEIL}>REMOcRA</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              {links?.map((item, index) => (
                <NavTo
                  key={index}
                  path={item.path}
                  label={item.label}
                  aLeDroit={item.aLeDroit}
                />
              ))}
            </Nav>
            <Nav>
              <p className="d-flex m-0 align-items-center text-light pe-3">
                {`${user.prenom} ${user.nom} (${user.username})`}
              </p>
              <form method="post" action={URLS.LOGOUT}>
                <Button
                  type="submit"
                  variant="link"
                  className={"text-decoration-none text-light"}
                >
                  Déconnexion
                </Button>
              </form>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </Row>
  );
};

export default Header;
