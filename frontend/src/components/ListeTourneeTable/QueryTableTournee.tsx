import { Container } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import ListTournee from "../../pages/Tournee/ListTournee.tsx";
import useQueryParams from "../Fetch/useQueryParams.tsx";

const QueryTableTournee = () => {
  const location = useLocation();
  const queryParams = useQueryParams();
  const filterBy = queryParams?.filterBy as any;
  const peiId = filterBy?.peiId || null;
  const peiNumeroComplet = location.state?.peiNumeroComplet || null;

  return (
    <Container>
      {peiId && (
        <ListTournee peiId={peiId} peiNumeroComplet={peiNumeroComplet} />
      )}
    </Container>
  );
};
export default QueryTableTournee;
