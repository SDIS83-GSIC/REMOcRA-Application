import { useMemo, useState, useCallback } from "react";
import { Col, Form, InputGroup, Pagination, Row } from "react-bootstrap";

export const LIMIT = 10;

const PaginationFront = ({
  values,
  setOffset,
}: {
  values: any[];
  setOffset: (n: number) => void;
}) => {
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageInput, setPageInput] = useState<string>("1");

  const [totalPage, setTotalPage] = useState<number>();

  // Fonction pour naviguer vers une page spécifique
  const goToPage = useCallback(
    (page: number) => {
      if (page >= 1 && page <= (totalPage || 1)) {
        setCurrentPage(page);
        setOffset((page - 1) * LIMIT);
        setPageInput(page.toString());
      }
    },
    [totalPage, setOffset],
  );

  const paginationItems = useMemo(() => {
    const pages = [];

    const nbPage = Math.ceil((values?.length || 0) / LIMIT);
    setTotalPage(nbPage);

    if (nbPage <= 5) {
      // Affiche toutes les pages si le total est <= 5
      for (let i = 1; i <= nbPage; i++) {
        pages.push(
          <Pagination.Item
            key={i}
            active={i === currentPage}
            onClick={() => goToPage(i)}
          >
            {i}
          </Pagination.Item>,
        );
      }
    } else {
      // Toujours afficher les premières, dernières et autour de la page actuelle
      if (currentPage !== 1) {
        pages.push(
          <Pagination.Item
            key={1}
            active={1 === currentPage}
            onClick={() => goToPage(1)}
          >
            1
          </Pagination.Item>,
        );
      }

      if (currentPage > 3) {
        pages.push(<Pagination.Ellipsis />);
      }
      if (currentPage > 2) {
        pages.push(
          <Pagination.Item
            key={currentPage - 1}
            active={false}
            onClick={() => goToPage(currentPage - 1)}
          >
            {currentPage - 1}
          </Pagination.Item>,
        );
      }
      pages.push(
        <Pagination.Item key={currentPage} active={true}>
          {currentPage}
        </Pagination.Item>,
      );
      if (currentPage < nbPage - 1) {
        pages.push(
          <Pagination.Item
            key={currentPage + 1}
            active={false}
            onClick={() => goToPage(currentPage + 1)}
          >
            {currentPage + 1}
          </Pagination.Item>,
        );
      }
      if (currentPage < nbPage - 2) {
        pages.push(<Pagination.Ellipsis />);
      }

      if (currentPage !== nbPage) {
        pages.push(
          <Pagination.Item
            key={nbPage}
            active={nbPage === currentPage}
            onClick={() => goToPage(nbPage)}
          >
            {nbPage}
          </Pagination.Item>,
        );
      }
    }

    return pages;
  }, [values, currentPage, goToPage]);

  // Gérer la saisie dans le champ de page
  const handlePageInputChange = (value: string) => {
    setPageInput(value);
  };

  // Gérer l'appui sur Entrée ou la perte de focus
  const handlePageInputSubmit = () => {
    const page = parseInt(pageInput, 10);
    if (!isNaN(page)) {
      goToPage(page);
    } else {
      // Remettre la valeur actuelle si la saisie est invalide
      setPageInput(currentPage.toString());
    }
  };

  return (
    <Row className="align-items-center">
      <Col xs={2} className="text-center">
        <InputGroup size="sm">
          <InputGroup.Text>Page</InputGroup.Text>
          <Form.Control
            type="text"
            value={pageInput}
            onChange={(e) => handlePageInputChange(e.target.value)}
            onBlur={handlePageInputSubmit}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                handlePageInputSubmit();
              }
            }}
            style={{ maxWidth: "60px" }}
          />
          <InputGroup.Text>/ {totalPage || 1}</InputGroup.Text>
        </InputGroup>
      </Col>
      <Col xs={8} className="text-center">
        <Pagination className={"my-3 d-flex justify-content-center"}>
          <Pagination.Prev
            onClick={() => goToPage(currentPage - 1)}
            disabled={currentPage === 1}
          />
          {paginationItems}
          <Pagination.Next
            onClick={() => goToPage(currentPage + 1)}
            disabled={currentPage === totalPage}
          />
        </Pagination>
      </Col>
      <Col xs={2} className="text-center">
        <small>{values?.length} résultat(s)</small>
      </Col>
    </Row>
  );
};

export default PaginationFront;
