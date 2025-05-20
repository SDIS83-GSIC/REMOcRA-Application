import { useLocation } from "react-router-dom";

const useQueryParams = () => {
  const { search } = useLocation();
  const searchParams = new URLSearchParams(search);
  const res: Record<string, unknown> = {};
  searchParams.forEach((value, key) => {
    let val;
    try {
      val = JSON.parse(value);
    } catch (e) {
      val = value;
    }
    res[key] = val;
  });
  return res;
};

export default useQueryParams;
