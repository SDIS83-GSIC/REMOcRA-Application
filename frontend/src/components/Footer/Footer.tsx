import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import React from "react";
import { Image } from "react-bootstrap";
import europesengage from "../../img/europesengage.png";
import logo_prt3 from "../../img/logo_prt3.png";
import europe from "../../img/europe.png";
import atolcd from "../../img/atolcd.png";

const Footer = () => {
  return (
    <Row className={"footer bg-primary"}>
      <Col className={"h-100 text-light"}>
        <Image className={"h-100 p-1"} fluid src={europesengage} />
        <Image className={"h-100 p-1"} fluid src={logo_prt3} />
        <Image className={"h-100 p-1"} fluid src={europe} />
        <Image className={"h-100 p-1"} fluid src={atolcd} />
      </Col>
      <Col xs={2} className={"text-light"}>
        <p className={"copyright text-end"}>Copyright © 2015 SDIS 83</p>
      </Col>
    </Row>
  );
};

export default Footer;
