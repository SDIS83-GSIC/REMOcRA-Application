import { useState, useEffect, ReactNode } from "react";
import classnames from "classnames";
import ReactSelect from "react-select";
import useQueryParams from "../Fetch/useQueryParams.tsx";
import styles from "./Pagination.module.css";

const PAGINATION = [10, 25, 50, 100];
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

const DisabledLink = (props) => {
  return (
    <span className={classnames(styles.linkA, styles.disabled)} {...props} />
  );
};

const Pagination = ({
  className,
  isLoading,
  previousText = <>&lt;</>,
  nextText = <>&gt;</>,
  count = 0,
  paginationState,
  dataLength = 0,
}: PaginationType) => {
  const [pagination, setPagination] = paginationState;
  useEffect(() => {
    if (pagination.offset >= count) {
      setPagination({ offset: 0, limit: pagination.limit });
    }
  }, []);
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

  return (
    <div className={classnames(className, styles.pagination)}>
      <div className={styles.resultsDisplayed}>
        {dataLength > 0 && (
          <>
            Affichage des r&eacute;sultats {itemsPerPage * currentPage + 1} à{" "}
            {currentPage > 0
              ? itemsPerPage * (currentPage + 1) - (itemsPerPage - dataLength)
              : dataLength}
            &nbsp;sur {count}
          </>
        )}
      </div>
      <div className={styles.paginationBlock}>
        <ul>
          <li>
            {canPrevious && !isLoading ? (
              <a
                className={classnames(styles.navigate)}
                onClick={() => {
                  setPagination({
                    limit: itemsPerPage,
                    offset: parseInt(pagination.offset) + itemsPerPage * -1,
                  });
                }}
              >
                {previousText}
              </a>
            ) : (
              <DisabledLink>
                <span className={styles.navigateDisable}>{previousText}</span>
              </DisabledLink>
            )}
          </li>
          {getVisiblePages().map((val, i, array) => {
            const page = val - 1;
            return (
              <li key={i}>
                {!isLoading ? (
                  <span
                    type="button"
                    key={i}
                    className={classnames(styles.linkA, {
                      [styles.active]: currentPage === page,
                    })}
                    onClick={() => {
                      setPagination({
                        limit: itemsPerPage,
                        offset: itemsPerPage * page,
                      });
                    }}
                  >
                    {array[i - 1] + 1 < val ? (
                      <span className={styles.pointille}>........... </span>
                    ) : (
                      ""
                    )}
                    {val}
                  </span>
                ) : (
                  <DisabledLink>
                    {array[i - 1] + 1 < val ? (
                      <span className={styles.pointille}>........... </span>
                    ) : (
                      ""
                    )}
                    {val}
                  </DisabledLink>
                )}
              </li>
            );
          })}
          <li>
            {canNext && !isLoading ? (
              <span
                className={classnames(styles.navigate)}
                onClick={() => {
                  setPagination({
                    limit: itemsPerPage,
                    offset: parseInt(pagination.offset) + itemsPerPage * 1,
                  });
                }}
              >
                {nextText}
              </span>
            ) : (
              <DisabledLink>
                <span className={styles.navigateDisable}>{nextText}</span>
              </DisabledLink>
            )}
          </li>
        </ul>
      </div>
      <div className={styles.pageSizeOptions}>
        {dataLength > 0 && (
          <div className={styles.paginationLabel}>
            Résultats par page :
            <ReactSelect
              menuPlacement={"auto"}
              classNamePrefix="select"
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
              placeholder={"Sélectionnez une valeur..."}
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

export default Pagination;
