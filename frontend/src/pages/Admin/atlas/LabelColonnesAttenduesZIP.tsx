import { Row } from "react-bootstrap";
import { IconInfo } from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

export const LabelColonnesAttenduesZIP = () => {
  return (
    <>
      Fichier ZIP :
      <TooltipCustom
        placement="right"
        tooltipText={
          <>
            <Row>
              <div>
                <span className="text-danger">*</span> : Champs obligatoires
              </div>
            </Row>

            <br />

            <Row>
              <div className="fw-bold">Contenu attendu du fichier ZIP</div>

              <div className="ms-2">
                Le fichier ZIP doit contenir les éléments suivants :
                <ul>
                  <li>
                    Un dossier <b>PAGES</b>.
                  </li>
                  <li>
                    Dans ce dossier, les fichiers PDF des pages de l'atlas.
                  </li>
                  <li>
                    Un fichier SHAPE au format <code>.zip</code> contenant les
                    géométries des pages. Chaque entité du SHAPE doit porter le
                    même identifiant que le fichier PDF correspondant (par
                    exemple <code>001</code> ↔ <code>001.pdf</code>). Les
                    fichiers SHAPE doivent contenir les colones suivantes :
                    <ul>
                      <li>
                        <code>fichier</code> : nom du fichier PDF
                      </li>
                      <li>
                        <code>the_geom</code> : géométrie du fichier
                      </li>
                      <li>
                        <code>actif</code> : dit si le fichier est actif ou non
                        (TRUE/FALSE)
                      </li>
                    </ul>
                    <li>
                      Le dossier <b>ANNEXES</b> est facultatif. Il peut contenir
                      des documents PDF complémentaires. Un fichier csv est
                      requis pour l'association des pages visibles aux pages de
                      l'atlas. Il doit contenir les colonnes suivantes :
                      <ul>
                        <li>
                          <code>nom_fichier_pdf</code> : nom du fichier PDF
                          présent dans le dossier ANNEXES
                        </li>

                        <li>
                          <code>actif</code> : dit si le fichier est actif ou
                          non (TRUE/FALSE)
                        </li>

                        <li>
                          <code>nom_annexe</code> : nom du fichier. Peut être
                          non défini. Si non défini, le nom du fichier PDF sera
                          utilisé.
                        </li>
                      </ul>
                    </li>
                  </li>
                </ul>
              </div>
            </Row>
          </>
        }
        tooltipHeader="Contenu du fichier ZIP"
        tooltipId={"importAtlasZIP"}
      >
        <IconInfo />
      </TooltipCustom>
    </>
  );
};
