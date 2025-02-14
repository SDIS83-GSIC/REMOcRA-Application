import { Badge, Image } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { useGet } from "../Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import europesengage from "../../img/europesengage.png";
import logo_prt3 from "../../img/logo_prt3.png";
import europe from "../../img/europe.png";
import atolcd from "../../img/atolcd.png";

const Footer = () => {
  const version = useGet(url`/api/app-settings/version`);
  return (
    <Row className={"bg-primary h-100"} id="footer">
      <Col className={"h-100 text-light"}>
        <Image className={"h-100 p-1"} fluid src={europesengage} />
        <Image className={"h-100 p-1"} fluid src={logo_prt3} />
        <Image className={"h-100 p-1"} fluid src={europe} />
        <Image className={"h-100 p-1"} fluid src={atolcd} />
      </Col>
      <Col xs={2} className={"text-light"}>
        <p className={"copyright mb-2 text-end"}>Copyright © 2015 SDIS 83</p>
        {version.value && (
          <Badge pill bg="info">
            {version.value.version}
          </Badge>
        )}
      </Col>
    </Row>
  );
};

export default Footer;
