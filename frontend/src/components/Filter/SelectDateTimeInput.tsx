export const SelectDateTimeInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  onChange: onChangeCallback,
  value,
  dateType = "datetime-local",
}: any) => {
  return (
    <>
      <label>{label}</label>
      <input
        id={name}
        type={dateType}
        value={value}
        required={required}
        disabled={readOnly}
        max="9999-12-31T23:59"
        onChange={(data) => {
          onChangeCallback({ name: name, value: data.currentTarget.value });
        }}
      />
    </>
  );
};

export default SelectDateTimeInput;
