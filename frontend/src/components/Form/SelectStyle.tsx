const selectStyle = {
  control: (provided) => ({
    ...provided,
    minWidth: 100, // Largeur fixe en pixels
    margin: "0 auto",
  }),
  menu: (provided) => ({
    ...provided,
    width: 300, // Largeur du menu déroulant
  }),
};

export default selectStyle;
