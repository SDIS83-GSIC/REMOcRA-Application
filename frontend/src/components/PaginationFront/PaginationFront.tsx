import { useMemo, useState } from "react";
import { Col, Pagination, Row } from "react-bootstrap";

export const LIMIT = 10;

const PaginationFront = ({
  values,
  offset,
  setOffset,
}: {
  values: any[];
  offset: number;
  setOffset: (n: number) => void;
}) => {
  const [currentPage, setCurrentPage] = useState<number>(1);

  const [totalPage, setTotalPage] = useState<number>();

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
            onClick={() => {
              setCurrentPage(i);
              setOffset(i * LIMIT - LIMIT);
            }}
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
            onClick={() => {
              setCurrentPage(1);
              setOffset(0);
            }}
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
            onClick={() => {
              setCurrentPage(currentPage - 1);
              setOffset(LIMIT * (currentPage - 1) - LIMIT);
            }}
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
            onClick={() => {
              setCurrentPage(currentPage + 1);
              setOffset(LIMIT * (currentPage + 1) - LIMIT);
            }}
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
            onClick={() => {
              setCurrentPage(nbPage);
              setOffset(LIMIT * nbPage - LIMIT);
            }}
          >
            {nbPage}
          </Pagination.Item>,
        );
      }
    }

    return pages;
  }, [values, currentPage, setCurrentPage, setOffset]);

  return (
    <Row className="align-items-center">
      <Col xs={10} className="text-center">
        <Pagination className={"my-3 d-flex justify-content-center"}>
          <Pagination.Prev
            onClick={() => {
              setCurrentPage(currentPage - 1);
              setOffset(offset - LIMIT);
            }}
            disabled={currentPage === 1}
          />
          {paginationItems}
          <Pagination.Next
            onClick={() => {
              setCurrentPage(currentPage + 1);
              setOffset(offset + LIMIT);
            }}
            disabled={currentPage === totalPage}
          />
        </Pagination>
      </Col>
      <Col xs={2}>{values?.length} résultat(s)</Col>
    </Row>
  );
};

export default PaginationFront;
