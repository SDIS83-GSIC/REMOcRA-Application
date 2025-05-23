import { useGet } from "../../components/Fetch/useFetch.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import url from "../../module/fetch.tsx";

const AccueilPublic = () => {
  const parametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(PARAMETRE.ACCUEIL_PUBLIC),
    }}`,
  );
  return (
    parametre.data?.[PARAMETRE.ACCUEIL_PUBLIC].parametreValeur != null && (
      <>
        <div
          dangerouslySetInnerHTML={{
            __html: parametre.data?.[PARAMETRE.ACCUEIL_PUBLIC].parametreValeur,
          }}
        />
      </>
    )
  );
};

export default AccueilPublic;
