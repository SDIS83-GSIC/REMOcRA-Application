function isEmptyOrNull(value: string) {
  return value == null || value.trim().length === 0;
}

export default isEmptyOrNull;
