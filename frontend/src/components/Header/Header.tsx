import React, { ReactNode } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { NavLink } from "react-router-dom";
import Row from "react-bootstrap/Row";
import { URLS } from "../../routes.tsx";
import BanniereHeader from "./BanniereHeader.tsx";

export interface NavToProps {
  path: string;
  label: ReactNode;
}

const NavTo = ({ path, label }: NavToProps) => {
  return (
    <Nav.Item>
      <Nav.Link as={NavLink} to={path} className="nav-link">
        {label}
      </Nav.Link>
    </Nav.Item>
  );
};
const Header = ({ links }: { links?: NavToProps[] }) => {
  return (
    <Row className={"header"}>
      <BanniereHeader />
      <Navbar className="mb-3" expand="lg" bg={"primary"} data-bs-theme="dark">
        <Container>
          <Navbar.Brand href={URLS.ACCUEIL}>REMOcRA</Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="me-auto">
              {links?.map((item, index) => (
                <NavTo key={index} path={item.path} label={item.label} />
              ))}
            </Nav>
            <Nav>
              <a
                href={URLS.LOGOUT}
                className={"text-decoration-none text-light"}
              >
                {" "}
                Déconnexion{" "}
              </a>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </Row>
  );
};

export default Header;
