const TypeSystemeSrid = [
  { srid: 2154, nomSystem: "Lambert 93", actif: false },
  { srid: 2972, nomSystem: "RGFG95", actif: false },
  { srid: 4326, nomSystem: "WGS84 degrés décimaux", actif: true },
  { srid: -1, nomSystem: "WGS84 degrés sexagésimaux", actif: true },
] as const;

export default TypeSystemeSrid;
