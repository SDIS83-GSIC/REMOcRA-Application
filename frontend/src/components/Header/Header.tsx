import React, { ReactNode } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { NavLink } from "react-router-dom";
import { URLS } from "../../routes.tsx";

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
    <Navbar className="bg-body-tertiary justify-content-between">
      <Container>
        <Navbar.Brand href={URLS.ACCUEIL}>REMOcRA</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            {links?.map((item, index) => (
              <NavTo key={index} path={item.path} label={item.label} />
            ))}
          </Nav>
        </Navbar.Collapse>
        <NavTo path={URLS.LOGOUT} label="Déconnexion" />
      </Container>
    </Navbar>
  );
};

export default Header;
