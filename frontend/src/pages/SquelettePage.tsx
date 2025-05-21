import { Container } from "react-bootstrap";
import React, { ReactNode, useEffect } from "react";
import { useGet } from "../components/Fetch/useFetch.tsx";
import url from "../module/fetch.tsx";
import Footer from "../components/Footer/Footer.tsx";
import BanniereHeader from "../components/Header/BanniereHeader.tsx";
import GoTopButton from "../components/GoTopButton/GoTopButton.tsx";
import PARAMETRE from "../enums/ParametreEnum.tsx";

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
  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([
        PARAMETRE.MENTION_CNIL,
        PARAMETRE.MESSAGE_ENTETE,
        PARAMETRE.TITRE_PAGE,
      ]),
    }}`,
  );
  const mentionCnil =
    listeParametre.data?.[PARAMETRE.MENTION_CNIL].parametreValeur;
  const messageEntete =
    listeParametre.data?.[PARAMETRE.MESSAGE_ENTETE].parametreValeur;
  const titrePage = listeParametre.data?.[PARAMETRE.TITRE_PAGE].parametreValeur;
  useEffect(() => {
    if (titrePage) {
      document.title = titrePage;
    }
  }, [titrePage]);
  return (
    <div id={"page"}>
      {banner && (
        <Container fluid id={"banner"}>
          <BanniereHeader messageEntete={messageEntete} />
        </Container>
      )}
      <Container fluid id={"navbar"}>
        {navbar}
      </Container>
      <Container fluid={fluid} id={"main"}>
        <div className={"d-flex flex-column h-100 pb-2"}>{children}</div>
        <GoTopButton />
      </Container>
      <Container fluid id={"footer"}>
        <Footer mentionCnil={mentionCnil} />
      </Container>
    </div>
  );
};

export default SquelettePage;
