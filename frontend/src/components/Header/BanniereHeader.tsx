import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { Badge, Image } from "react-bootstrap";
import React from "react";
import { useGet } from "../Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import TYPE_ENVIRONNEMENT from "../../enums/TypeEnvironnement.tsx";

const BanniereHeader = () => {
  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(PARAMETRE.MESSAGE_ENTETE),
    }}`,
  );

  const imagesHeader = useGet(url`/api/image/get-image-header`).data;

  const banniereChemin = imagesHeader?.[PARAMETRE.BANNIERE_CHEMIN];
  const logo = imagesHeader?.[PARAMETRE.LOGO_CHEMIN];
  const messageEntete =
    listeParametre.data?.[PARAMETRE.MESSAGE_ENTETE].parametreValeur;
  const typeEnvironment = useGet(url`/api/app-settings/environment`);
  return (
    <Row className={"bg-primary m-0 p-0"}>
      <Col
        xs={1}
        className={"d-flex justify-content-center align-items-center"}
      >
        {logo && <Image fluid alt={"Logo"} src={logo} />}
      </Col>
      <Col className={"position-relative text-center"}>
        <p
          className={
            "h3 text-light col-12 position-absolute bottom-0 start-50 translate-middle-x"
          }
        >
          {messageEntete}
        </p>
        <Image fluid alt={"Bandeau d'En-tête"} src={banniereChemin} />
      </Col>
      <Col xs={1} className={"mt-3 text-center"}>
        {typeEnvironment &&
          (TYPE_ENVIRONNEMENT[typeEnvironment.value] !==
          TYPE_ENVIRONNEMENT.PRODUCTION ? (
            <p className={"h4"}>
              <Badge pill bg={getBadgeEnvironment(typeEnvironment.value)?.bg}>
                {getBadgeEnvironment(typeEnvironment.value)?.libelle}
              </Badge>
            </p>
          ) : (
            <></>
          ))}
      </Col>
    </Row>
  );
};

type BadgeType = { bg: string; libelle: string };

function getBadgeEnvironment(typeEnvironement): BadgeType {
  switch (TYPE_ENVIRONNEMENT[typeEnvironement]) {
    case TYPE_ENVIRONNEMENT.DEVELOPPEMENT:
      return { bg: "success", libelle: "Développement" };
    case TYPE_ENVIRONNEMENT.RECETTE:
      return { bg: "success", libelle: "Recette" };
    case TYPE_ENVIRONNEMENT.PREPRODUCTION:
      return { bg: "warning", libelle: "Préprod" };
    case TYPE_ENVIRONNEMENT.FORMATION:
      return { bg: "info", libelle: "Formation" };
  }
}

export default BanniereHeader;
