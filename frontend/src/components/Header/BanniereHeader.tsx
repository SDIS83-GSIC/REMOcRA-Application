import React, { useEffect, useState } from "react";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { Badge, Image } from "react-bootstrap";
import { useGet } from "../Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import TYPE_ENVIRONNEMENT from "../../enums/TypeEnvironnement.tsx";

// Affiche les images seulement si elles existent
const ImageIfExists = ({
  imageSrc,
  alt,
}: {
  imageSrc: string;
  alt: string;
}) => {
  const [exists, setExists] = useState<boolean | null>(null);

  useEffect(() => {
    const img = new window.Image();
    img.onload = () => setExists(true);
    img.onerror = () => setExists(false);
    img.src = imageSrc;
  }, [imageSrc]);

  if (!exists) {
    return null;
  }

  return <Image fluid alt={alt} src={imageSrc} />;
};

const BanniereHeader = ({ messageEntete }: { messageEntete: string }) => {
  const typeEnvironment = useGet(url`/api/app-settings/environment`);
  return (
    <Row className={"bg-primary h-100 p-0"}>
      <Col
        xs={1}
        className={"d-flex justify-content-center align-items-center"}
      >
        <ImageIfExists imageSrc="images/logo" alt="Logo" />
      </Col>

      <Col className={"position-relative text-center"}>
        <p
          className={
            "h3 text-light col-12 position-absolute bottom-0 start-50 translate-middle-x"
          }
        >
          {messageEntete}
        </p>
        {typeEnvironment &&
          TYPE_ENVIRONNEMENT[typeEnvironment.value] !==
            TYPE_ENVIRONNEMENT.PRODUCTION && (
            <p className={"fs-4 m-2 position-absolute top-0 end-0"}>
              <Badge pill bg={getBadgeEnvironment(typeEnvironment.value)?.bg}>
                {getBadgeEnvironment(typeEnvironment.value)?.libelle}
              </Badge>
            </p>
          )}
        <ImageIfExists imageSrc="/images/banniere" alt={"Bandeau d'En-tête"} />
      </Col>
      <Col xs={1} className={"mt-3 text-center"} />
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
