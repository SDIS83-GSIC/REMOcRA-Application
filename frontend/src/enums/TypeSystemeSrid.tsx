const TypeSystemeSrid = [
  { srid: 2154, nomSystem: "RGF93 v1 / Lambert-93", actif: false },
  { srid: 2972, nomSystem: "RGFG95 / UTM zone 22N", actif: false },
  { srid: 3857, nomSystem: "WGS 84 / Pseudo-Mercator", actif: true },
  { srid: 4326, nomSystem: "WGS 84 degrés décimaux", actif: true },
  { srid: 32620, nomSystem: "WGS 84 / UTM zone 20N", actif: false },
  { srid: -1, nomSystem: "WGS 84 degrés sexagésimaux", actif: true },
] as const;

export default TypeSystemeSrid;
