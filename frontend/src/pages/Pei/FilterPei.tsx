import { Form } from "react-bootstrap";
import DISPONIBILITE_PEI from "../../enums/DisponibiliteEnum.tsx";
import PROCHAINE_DATE_ENUM from "../../enums/ProchaineDateEnum.tsx";

type filterPei = {
  peiNumeroComplet?: string;
  peiNumeroInterne?: number;
  communeId?: string;
  natureId?: string;
  peiDisponibiliteTerrestre?: string;
  penaDisponibiliteHbe?: string;
  typePei?: string;
  natureDeci?: string;
  autoriteDeci?: string;
  servicePublicDeci?: string;
  anomalieId?: string;
  tourneeLibelle?: string;
  adresse?: string;
  prochaineDateRop?: PROCHAINE_DATE_ENUM;
  prochaineDateCtp?: PROCHAINE_DATE_ENUM;
  tourneeId?: string;
  gestionnaireId?: string;
  hasIndispoTemp?: boolean;
};

// Transformation inverse : de l'URL vers le formulaire
// Utilisé pour initialiser les valeurs du formulaire depuis l'URL
export const filterVariableToValues = (filter: filterPei): filterPei => {
  const values = { ...filter };

  // Si on a peiDisponibiliteTerrestre=INDISPONIBLE + hasIndispoTemp=true
  // on doit transformer en INDISPONIBLE_TEMPORAIRE pour l'affichage
  if (
    values.peiDisponibiliteTerrestre === DISPONIBILITE_PEI.INDISPONIBLE &&
    (values.hasIndispoTemp === true || values.hasIndispoTemp === "true")
  ) {
    values.peiDisponibiliteTerrestre = "INDISPONIBLE_TEMPORAIRE";
  }

  return values;
};

export const filterValuesToVariable = ({
  peiNumeroComplet,
  peiNumeroInterne,
  communeId,
  natureId,
  peiDisponibiliteTerrestre,
  penaDisponibiliteHbe,
  typePei,
  natureDeci,
  autoriteDeci,
  servicePublicDeci,
  anomalieId,
  tourneeLibelle,
  adresse,
  prochaineDateRop,
  prochaineDateCtp,
  tourneeId,
  gestionnaireId,
  hasIndispoTemp,
}: filterPei) => {
  const filter: filterPei = {};

  if (peiNumeroComplet != null && peiNumeroComplet.trim() !== "") {
    filter.peiNumeroComplet = peiNumeroComplet;
  }
  if (communeId != null && communeId.trim() !== "") {
    filter.communeId = communeId;
  }
  if (typePei != null && typePei.trim() !== "") {
    filter.typePei = typePei;
  }
  if (natureDeci != null && natureDeci.trim() !== "") {
    filter.natureDeci = natureDeci;
  }
  if (autoriteDeci != null && autoriteDeci.trim() !== "") {
    filter.autoriteDeci = autoriteDeci;
  }
  if (servicePublicDeci != null && servicePublicDeci.trim() !== "") {
    filter.servicePublicDeci = servicePublicDeci;
  }
  if (natureId != null && natureId.trim() !== "") {
    filter.natureId = natureId;
  }
  if (
    peiDisponibiliteTerrestre != null &&
    peiDisponibiliteTerrestre.trim() !== ""
  ) {
    if (
      peiDisponibiliteTerrestre === "INDISPONIBLE_TEMPORAIRE" ||
      (peiDisponibiliteTerrestre === DISPONIBILITE_PEI.INDISPONIBLE &&
        hasIndispoTemp === true)
    ) {
      // Filtre pour "Indisponible temporairement"
      filter.peiDisponibiliteTerrestre = DISPONIBILITE_PEI.INDISPONIBLE;
      filter.hasIndispoTemp = true;
    } else if (peiDisponibiliteTerrestre === DISPONIBILITE_PEI.INDISPONIBLE) {
      // Filtre pour "Indisponible" uniquement (pas temporaire)
      filter.peiDisponibiliteTerrestre = DISPONIBILITE_PEI.INDISPONIBLE;
      // On ne retourne pas hasIndispoTemp
    } else {
      // Tous les autres filtres (Disponible, Non conforme, etc.)
      filter.peiDisponibiliteTerrestre = peiDisponibiliteTerrestre;
    }
  }
  if (penaDisponibiliteHbe != null && penaDisponibiliteHbe.trim() !== "") {
    filter.penaDisponibiliteHbe = penaDisponibiliteHbe;
  }
  if (peiNumeroInterne != null && peiNumeroInterne !== "") {
    filter.peiNumeroInterne = peiNumeroInterne;
  }
  if (anomalieId != null && anomalieId !== "") {
    filter.anomalieId = anomalieId;
  }
  if (tourneeLibelle != null && tourneeLibelle !== "") {
    filter.tourneeLibelle = tourneeLibelle;
  }
  if (adresse != null && adresse !== "") {
    filter.adresse = adresse;
  }

  if (prochaineDateRop != null && prochaineDateRop.trim() !== "") {
    filter.prochaineDateRop = prochaineDateRop;
  }

  if (prochaineDateCtp != null && prochaineDateCtp.trim() !== "") {
    filter.prochaineDateCtp = prochaineDateCtp;
  }
  if (tourneeId != null && tourneeId.trim() !== "") {
    filter.tourneeId = tourneeId;
  }
  if (gestionnaireId != null && gestionnaireId.trim() !== "") {
    filter.gestionnaireId = gestionnaireId;
  }

  return filter;
};

const FilterPei = () => {
  return <Form.Text />;
};

export default FilterPei;
