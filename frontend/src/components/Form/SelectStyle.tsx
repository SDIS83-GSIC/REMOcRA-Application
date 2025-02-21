const selectStyle = {
  control: (provided) => ({
    ...provided,
    width: 150, // Largeur fixe en pixels
    margin: "0 auto",
  }),
  menu: (provided) => ({
    ...provided,
    width: 300, // Largeur du menu déroulant
  }),
};

export default selectStyle;
