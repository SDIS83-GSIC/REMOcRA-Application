import { Form } from "react-bootstrap";
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
  listeAnomalie?: string;
  tourneeLibelle?: string;
  adresse?: string;
  prochaineDateRop?: PROCHAINE_DATE_ENUM;
  prochaineDateCtp?: PROCHAINE_DATE_ENUM;
  tourneeId?: string;
  gestionnaireId?: string;
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
  listeAnomalie,
  tourneeLibelle,
  adresse,
  prochaineDateRop,
  prochaineDateCtp,
  tourneeId,
  gestionnaireId,
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
    filter.peiDisponibiliteTerrestre = peiDisponibiliteTerrestre;
  }
  if (penaDisponibiliteHbe != null && penaDisponibiliteHbe.trim() !== "") {
    filter.penaDisponibiliteHbe = penaDisponibiliteHbe;
  }
  if (peiNumeroInterne != null && peiNumeroInterne !== "") {
    filter.peiNumeroInterne = peiNumeroInterne;
  }
  if (listeAnomalie != null && listeAnomalie !== "") {
    filter.listeAnomalie = listeAnomalie;
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
