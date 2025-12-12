import "bootstrap/dist/css/bootstrap.min.css";
import { ReactNode } from "react";
import Button from "react-bootstrap/Button";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import Row from "react-bootstrap/Row";
import { URLS } from "../../routes.tsx";
import LinkButton from "../Button/LinkButton.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import url from "../../module/fetch.tsx";
import { useGet } from "../Fetch/useFetch.tsx";

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
  const { user } = useAppContext();

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([PARAMETRE.MESSAGE_ENTETE]),
    }}`,
  );

  return (
    <Row>
      <Navbar className="mb-3" expand="lg" bg={"primary"} data-bs-theme="dark">
        <Container>
          <Navbar.Brand href={URLS.ACCUEIL}>REMOcRA</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            {!links && (
              <p className={"h3 text-light mx-auto text-center align-self-end"}>
                {
                  listeParametre.data?.[PARAMETRE.MESSAGE_ENTETE]
                    .parametreValeur
                }
              </p>
            )}

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
              {user != null ? (
                <>
                  <p className="d-flex m-0 align-items-center text-light pe-3">
                    {(() => {
                      const hasName = user.prenom || user.nom;
                      const namePart = [user.prenom, user.nom]
                        .filter(Boolean)
                        .join(" ");
                      if (user.username) {
                        if (hasName) {
                          return `${namePart} (${user.username})`;
                        } else {
                          return user.username;
                        }
                      }
                      return namePart;
                    })()}
                  </p>
                  <form
                    method="post"
                    action={URLS.LOGOUT}
                    onSubmit={() => {
                      localStorage.clear();
                    }}
                  >
                    <Button type="submit" className={"btn-light"}>
                      Déconnexion
                    </Button>
                  </form>
                </>
              ) : (
                <>
                  <form
                    method="post"
                    action={URLS.LOGIN}
                    onSubmit={() => {
                      localStorage.clear();
                    }}
                  >
                    <Button type="submit" className={"btn-light"}>
                      Connexion
                    </Button>
                  </form>
                </>
              )}
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </Row>
  );
};

export default Header;
