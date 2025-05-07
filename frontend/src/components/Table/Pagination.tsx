import { useState, useEffect, ReactNode } from "react";
import classnames from "classnames";
import ReactSelect from "react-select";
import { Button, Form, FloatingLabel } from "react-bootstrap";
import useQueryParams from "../Fetch/useQueryParams.tsx";
import { IconNextPage, IconPreviousPage } from "../Icon/Icon.tsx";
import decorateInteger from "../../utils/formatNumberUtils.tsx";
import styles from "./Pagination.module.css";
const PAGINATION = [10, 15, 25, 50, 100];
const PAGINATION_DEFAULT = "10";
const PAGINATION_KEY = "itemsPerPage";
const PAGINATION_VALUES = PAGINATION.map((data) => {
  return { value: data, label: data };
});

const usePaginationInLocalStorage = () => {
  const [itemsPerPage, setItemsPerPage] = useState(
    parseInt(localStorage.getItem(PAGINATION_KEY) || PAGINATION_DEFAULT),
  );

  useEffect(() => {
    localStorage.setItem(PAGINATION_KEY, itemsPerPage.toString());
  }, [itemsPerPage]);

  return [itemsPerPage, setItemsPerPage];
};

export const usePaginationState = () => {
  const { limit: l, offset: o = 0 }: { limit: number; offset: number } =
    useQueryParams();
  let val = l;
  if (l === undefined) {
    val = parseInt(localStorage.getItem(PAGINATION_KEY) || PAGINATION_DEFAULT);
  }
  const [pagination, setPagination] = useState({
    limit: val,
    offset: o,
  });

  const setValue = ({ limit, offset }: { limit: number; offset: number }) => {
    const l = limit;
    const o = offset;
    setPagination({
      limit: !isNaN(l)
        ? l
        : parseInt(localStorage.getItem(PAGINATION_KEY) || PAGINATION_DEFAULT),
      offset: !isNaN(o) ? o : 0,
    });
  };

  return [pagination, setValue];
};

export const usePagination = ({
  total = 0,
  offset = 0,
}: {
  total: number;
  offset: number;
}) => {
  const [itemsPerPage, setItemsPerPage] = usePaginationInLocalStorage();
  const maxPage = Math.ceil(total / itemsPerPage);
  const currentPage = Math.trunc(offset / itemsPerPage);

  return {
    itemsPerPage,
    setItemsPerPage,
    currentPage,
    total,
    canPrevious: currentPage > 0,
    canNext: currentPage < maxPage - 1,

    getVisiblePages() {
      if (maxPage < 7) {
        return [1, 2, 3, 4, 5, 6].filter((page) => page <= maxPage);
      } else {
        if (currentPage >= 0 && currentPage > 4 && currentPage + 2 < maxPage) {
          return [1, currentPage - 1, currentPage, currentPage + 1, maxPage];
        } else if (
          currentPage >= 0 &&
          currentPage > 4 &&
          currentPage + 2 >= maxPage
        ) {
          return [1, maxPage - 3, maxPage - 2, maxPage - 1, maxPage];
        } else {
          return [1, 2, 3, 4, 5, maxPage];
        }
      }
    },
  };
};

export const setOffsetToSearchParams = (search, itemsPerPage, offset) => {
  const searchParams = new URLSearchParams(search);
  searchParams.set("offset", offset < 0 ? 0 : offset);
  searchParams.set("limit", itemsPerPage);
  return decodeURIComponent(searchParams.toString());
};

const Pagination = ({
  className,
  isLoading,
  previousText = <IconPreviousPage />,
  nextText = <IconNextPage />,
  count = 0,
  paginationState,
  dataLength = 0,
}: PaginationType) => {
  const [pagination, setPagination] = paginationState;

  const {
    canPrevious,
    canNext,
    getVisiblePages,
    currentPage,
    itemsPerPage,
    setItemsPerPage,
  } = usePagination({
    total: count,
    offset: pagination.offset,
  });
  const [inputPage, setInputPage] = useState<string | number>(currentPage + 1);
  /**
   * Synchronise le champ de saisie `inputPage` avec la page actuelle (`currentPage`).
   *
   * Ce cas se produit si la page change par un autre moyen que le champ (ex : clic sur les boutons Précédent/Suivant).
   * On met alors à jour le champ pour qu'il reflète bien la nouvelle page.
   *
   * Note : on ajoute 1, car `currentPage` est basé sur un index 0, même si l'affichage commence à 1.
   */
  useEffect(() => {
    setInputPage(currentPage + 1); // synchronise champ si currentPage change ailleurs
  }, [currentPage]);

  /**
   * Met à jour la pagination avec un petit délai (500ms) après la dernière saisie.
   *
   * Cela évite d'exécuter une requête à chaque frappe lorsque l'utilisateur tape un numéro de page.
   * Exemple : si l'utilisateur tape "123", on attend qu'il ait terminé (ou qu'il s'arrête un court moment)
   * avant de lancer le setPagination — sinon on aurait 3 appels pour "1", "12", "123".
   *
   * Si le champ est vide ou contient une valeur invalide (ex: lettres), la pagination n'est pas déclenchée.
   */
  useEffect(() => {
    const timeout = setTimeout(() => {
      if (inputPage === "" || isNaN(Number(inputPage))) {
        return;
      }

      const offset = itemsPerPage * (Number(inputPage) - 1);

      if (pagination.limit !== itemsPerPage || pagination.offset !== offset) {
        setPagination({
          limit: itemsPerPage,
          offset: offset,
        });
      }
      if (pagination.offset >= count) {
        setPagination({ offset: 0, limit: pagination.limit });
      }
    }, 500); // délai de 500ms

    return () => clearTimeout(timeout);
  }, [inputPage, itemsPerPage, setPagination, pagination, count]); // déclenche seulement si inputPage change
  return (
    <div className={classnames(className, styles.pagination)}>
      <div className={styles.resultsDisplayed}>
        {dataLength > 0 && (
          <>
            Affichage des r&eacute;sultats{" "}
            {decorateInteger(itemsPerPage * currentPage + 1)} à{" "}
            {decorateInteger(
              currentPage > 0
                ? itemsPerPage * (currentPage + 1) - (itemsPerPage - dataLength)
                : dataLength,
            )}
            &nbsp;sur {decorateInteger(count)}
          </>
        )}
      </div>
      <div className={styles.paginationBlock}>
        <div className="d-flex align-items-center gap-2 flex-wrap">
          <InputPage
            page={inputPage}
            onChange={(e) => {
              const value = e.target.value;

              /**
               * Autorise uniquement :
               * - une chaîne vide (pour laisser l'utilisateur effacer)
               * - un entier positif ≤ au nombre total de pages
               *
               * Empêche la saisie de lettres ou d'une page hors limites.
               */ if (
                value === "" ||
                (/^\d+$/.test(value) &&
                  value <= Math.ceil(count / itemsPerPage))
              ) {
                setInputPage(value === "" ? "" : Number(value));
              }
            }}
          />

          <Button
            variant="outline-primary"
            size="sm"
            className={"border-0"}
            disabled={!canPrevious || isLoading}
            onClick={() => {
              setPagination({
                limit: itemsPerPage,
                offset: pagination.offset - itemsPerPage,
              });
            }}
          >
            {previousText}
          </Button>

          {getVisiblePages().map((val, i, array) => {
            const page = val - 1;
            const showDots = i > 0 && array[i - 1] + 1 < val;

            return (
              <div key={i} className="d-flex align-items-center">
                {showDots && <span className="mx-1 text-muted">…</span>}
                <Button
                  variant={currentPage === page ? "info" : "outline-primary"}
                  size="sm"
                  className={"border-0"}
                  onClick={() =>
                    setPagination({
                      limit: itemsPerPage,
                      offset: itemsPerPage * page,
                    })
                  }
                >
                  {val}
                </Button>
              </div>
            );
          })}

          <Button
            variant="outline-primary"
            className={"border-0"}
            size="sm"
            disabled={!canNext || isLoading}
            onClick={() => {
              setPagination({
                limit: itemsPerPage,
                offset: pagination.offset + itemsPerPage,
              });
            }}
          >
            {nextText}
          </Button>
        </div>
      </div>
      <div className={styles.pageSizeOptions}>
        {dataLength > 0 && (
          <div className={styles.paginationLabel}>
            Résultats par page :
            <ReactSelect
              menuPlacement={"auto"}
              className={"ms-2"}
              value={
                PAGINATION_VALUES.find(
                  (data) => data.value === itemsPerPage,
                ) || { value: itemsPerPage, label: itemsPerPage }
              }
              options={PAGINATION_VALUES}
              onChange={({ value }) => {
                setItemsPerPage(value);
                setPagination({ limit: value, offset: 0 });
              }}
              closeMenuOnSelect={true}
              isClearable={false}
            />
          </div>
        )}
      </div>
    </div>
  );
};
type PaginationType = {
  className?: string | undefined;
  isLoading: boolean;
  previousText: ReactNode;
  nextText: ReactNode;
  count: number;
  limit: string | number;
  offset: string | number;
  dataLength: number;
  paginationState: [
    { offset: number; limit: number },
    ({ limit, offset }: { limit: number; offset: number }) => void,
  ];
};

const InputPage = ({
  onChange,
  page = 1,
}: {
  onChange?: () => void;
  page?: number;
}) => {
  return (
    <FloatingLabel
      controlId="floatingPage"
      label="Page"
      style={{ width: "70px" }}
    >
      <Form.Control
        type="number"
        step={1}
        min={1}
        value={page}
        onChange={onChange}
        size="sm"
        placeholder="Page"
      />
    </FloatingLabel>
  );
};

export default Pagination;
