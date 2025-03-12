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
  prochaineDateRecop?: PROCHAINE_DATE_ENUM;
  prochaineDateCtp?: PROCHAINE_DATE_ENUM;
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
  prochaineDateRecop,
  prochaineDateCtp,
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

  if (prochaineDateRecop != null && prochaineDateRecop.trim() !== "") {
    filter.prochaineDateRecop = prochaineDateRecop;
  }

  if (prochaineDateCtp != null && prochaineDateCtp.trim() !== "") {
    filter.prochaineDateCtp = prochaineDateCtp;
  }

  return filter;
};

const FilterPei = () => {
  return <Form.Text />;
};

export default FilterPei;
