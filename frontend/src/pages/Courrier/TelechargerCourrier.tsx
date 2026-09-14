import { useEffect } from "react";
import { useParams } from "react-router-dom";
import url from "../../module/fetch.tsx";

const TelechargerCourrier = () => {
  const { documentId } = useParams();

  // On lance le téléchargement du courrier et on ferme la page après un délai
  // Page nécessaire pour pouvoir fermer après le téléchargement
  useEffect(() => {
    if (!documentId) {
      return;
    }

    // Télécharger le fichier
    window.location.assign(url`/api/documents/telecharger/${documentId}`);

    // Fermer la page après un délai
    setTimeout(() => {
      window.close();
    }, 1000);
  }, [documentId]);

  return null;
};

export default TelechargerCourrier;
