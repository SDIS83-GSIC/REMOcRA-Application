import { Form } from "react-bootstrap";

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

  return filter;
};

const FilterPei = () => {
  return <Form.Text />;
};

export default FilterPei;
