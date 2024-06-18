import React, { ReactNode, useEffect, useRef, useState } from "react";
import { useFormik } from "formik";
import { Table } from "react-bootstrap";
import { useDebouncedCallback } from "use-debounce";
import { useNavigate } from "react-router-dom";
import classnames from "classnames";
import useQueryParams from "../Fetch/useQueryParams.tsx";
import url from "../../module/fetch.tsx";
import { usePost } from "../Fetch/useFetch.tsx";
import Pagination, {
  setOffsetToSearchParams,
  usePaginationState,
} from "./Pagination.tsx";
import styles from "./Table.module.css";

export const useSortBy = () => {
  const { sortBy: init = {} }: any = useQueryParams();
  const [sortBy, setSortBy] = useState(init);

  return [sortBy, setSortBy];
};

export const useFilterContext = (initialValues: any) => {
  const formik = useFormik({
    initialValues: initialValues,
    onSubmit: (values) => {
      alert(JSON.stringify(values, null, 2));
    },
    validateOnChange: false,
    validateOnBlur: true,
  });

  const { values, setValues } = {
    values: {},
    setValues: () => {},
  };

  const handleChange = (e: any) => {
    formik.handleChange(e);
  };

  return { values, setValues, formik, handleChange };
};

function QueryTable({
  query,
  queryParams,
  asyncOptions,
  getList = (data) => data?.list,
  getCount = (data) => data?.count,
  columns = [],
  idName = "id",
  className,
  trClassName,
  filterContext = {},
  filterValuesToVariable = (values: object[]) => values,
  watchedValues = [],
}: QueryTableType) {
  const { formik, setValues } = filterContext;
  const [tableState, setTableState] = useState([]);

  const getIdForKey = (row: any, append = "") => row[idName] + "" + append;

  const navigate = useNavigate();

  // eslint-disable-next-line react-hooks/rules-of-hooks
  const [sortBy, setSortBy] = useSortBy();
  const [pagination, setPaginationTemp] = usePaginationState(); // eslint-disable-line react-hooks/rules-of-hooks
  let setPagination = setPaginationTemp;
  // eslint-disable-next-line react-hooks/rules-of-hooks
  setPagination = ({ offset, limit }: { offset: number; limit: number }) => {
    setPaginationTemp({ limit, offset });
    navigate({
      ...location,
      search: setOffsetToSearchParams(location.search, limit, offset),
    });
  };

  const [filterBy, setFilterBy] = useState(filterValuesToVariable({}));

  const historyPush = () => {
    const f = filterValuesToVariable(formik?.values);
    const filter = Object.values(f).filter(Boolean).length
      ? JSON.stringify(f)
      : null;
    const sort = Object.values(sortBy).filter(Boolean).length
      ? JSON.stringify(sortBy)
      : null;
    const searchParams = new URLSearchParams(document.location?.search);
    if (
      filter !== searchParams.get("filterBy") ||
      sort !== searchParams.get("sortBy")
    ) {
      if (filter) {
        searchParams.set("filterBy", filter);
      } else {
        searchParams.delete("filterBy");
      }
      if (sort) {
        searchParams.set("sortBy", sort);
      } else {
        searchParams.delete("sortBy");
      }
      searchParams.set("limit", pagination.limit);
      searchParams.set("offset", pagination.offset ?? "0");
      navigate({
        pathname: location.pathname,
        search: decodeURIComponent(searchParams.toString()),
      });
      setFilterBy(filterValuesToVariable(formik?.values));

      // Si on change de filtres, on se remet à la première page
      setPagination({ offset: 0, limit: pagination.limit });
    } else if (
      searchParams.get("offset") !== pagination.offset.toString() ||
      searchParams.get("limit") !== pagination.limit.toString()
    ) {
      searchParams.set("limit", pagination.limit);
      searchParams.set("offset", pagination.offset ?? "0");
      navigate(
        {
          pathname: location.pathname,
          search: decodeURIComponent(searchParams.toString()),
        },
        { replace: true },
      );
    }
  };

  const { callback: debounceSearch } = useDebouncedCallback(historyPush, 500);

  useEffect(() => {
    debounceSearch();
  }, [formik?.values]);

  useEffect(() => {
    historyPush();
  }, [sortBy, pagination.offset, pagination.limit, ...watchedValues]);

  useEffect(() => {
    const searchParams = new URLSearchParams(document.location?.search);
    const filterByParams = searchParams.get("filterBy");
    const sortByParams = searchParams.get("sortBy");
    const offsetParams = searchParams.get("offset");
    const limitParams = searchParams.get("limit");

    if (filterByParams || (!filterByParams && formik?.values)) {
      const temp = filterValuesToVariable(formik?.values);
      const filter = Object.values(temp).filter(Boolean).length
        ? JSON.stringify(temp)
        : null;
      if (filter !== filterByParams) {
        // initializer && setValues(initializer(JSON.parse(filterByParams) ?? {}));
        setValues(formik?.values);
        setFilterBy(filterValuesToVariable(formik?.values));
      }
    }
    if (sortByParams || (!sortByParams && sortBy)) {
      const sort = Object.values(sortBy).filter(Boolean).length
        ? JSON.stringify(sortBy)
        : null;
      if (sort !== sortByParams) {
        setSortBy(JSON.parse(sortByParams) ?? {});
      }
    }
    if (
      offsetParams &&
      limitParams &&
      (offsetParams !== pagination.offset.toString() ||
        limitParams !== pagination.limit.toString())
    ) {
      navigate({ offset: offsetParams, limit: limitParams });
    }
  }, [location]);

  const { isRejected, isLoading, data, run } = usePost(
    queryParams ? query + "?" + url`${queryParams}` : query,
    asyncOptions,
  );
  const firstMount = useRef(true);

  const fetchData = () => {
    // on ne veut pas relancer un chargement immédiatement si on passe un state parent
    if (firstMount.current) {
      firstMount.current = false;
    }
    if (
      JSON.stringify(tableState) !==
      JSON.stringify([filterBy, sortBy, pagination.limit, pagination.offset])
    ) {
      setTableState([filterBy, sortBy, pagination.limit, pagination.offset]);
      if (pagination.limit >= 0 && pagination.offset >= 0) {
        run({
          limit: pagination.limit,
          offset: pagination.offset,
          filterBy: filterBy,
          sortBy: sortBy,
        });
      }
    }
  };

  useEffect(() => {
    fetchData();
  }, [filterBy, sortBy, pagination.limit, pagination.offset]);

  if (isRejected) {
    return <div>Une erreur est survenue.</div>;
  }

  const makeRow = (row: any) => {
    return (
      <tr key={getIdForKey(row)} className={trClassName}>
        {columns.map((column, j) =>
          makeCell(column, row, getIdForKey(row, j.toString())),
        )}
      </tr>
    );
  };

  const makeCell = (column: any, row: any, key: string) => {
    const { Cell, accessor, className, width } = column;
    let res = null;
    let acc = null;
    if (typeof accessor === "function") {
      acc = accessor(row);
    } else {
      acc = row[accessor];
    }
    if (Cell) {
      res = <Cell {...{ value: acc, original: row }} />;
    } else {
      res = acc;
    }
    return (
      <td key={key} className={className} style={{ maxWidth: width }}>
        {res}
      </td>
    );
  };

  const makeHeader = (column: any, key: string) => {
    const { Header, sortField, classNameHeader, width, Filter } = column;
    return (
      <th key={key} className={classNameHeader} style={{ width: width }}>
        <div className={styles.headerWrapper}>
          <div className={styles.headerTitle}>
            {Header}
            {sortField && (
              <div className={styles.sortWrapper}>
                <div
                  onClick={() => {
                    if (!sortField) {
                      return;
                    }

                    // Si on appuie une nouvelle fois sur la flèche, on réinitialise le tableau
                    if (sortBy[sortField] === "1") {
                      setSortBy({});
                    } else {
                      const asc = [];
                      asc[sortField] = "1";
                      setSortBy({ ...asc });
                    }
                  }}
                  className={classnames({
                    [styles.sort]: sortField,
                    [styles.noSort]: !sortField,
                    [styles.sortActive]: sortBy[sortField] === "1",
                  })}
                >
                  ▲
                </div>

                <div
                  onClick={() => {
                    if (!sortField) {
                      return;
                    }
                    // Si on appuie une nouvelle fois sur la flèche, on réinitialise le tableau
                    if (sortBy[sortField] === "-1") {
                      setSortBy({});
                    } else {
                      const desc = [];
                      desc[sortField] = "-1";
                      setSortBy({ ...desc });
                    }
                  }}
                  className={classnames({
                    [styles.sort]: sortField,
                    [styles.noSort]: !sortField,
                    [styles.sortActive]: sortBy[sortField] === "-1",
                  })}
                >
                  ▼
                </div>
              </div>
            )}
          </div>
          {Filter && (
            <div className={styles.headerFilter}>
              {React.cloneElement(Filter, {
                onChange: ({ name, value }) => {
                  formik.setValues((prevValues) => ({
                    ...prevValues,
                    [name]: value,
                  }));
                  // formik.handleChange(e)
                },
                onBlur: formik.handleBlur,
                value: formik?.values[Filter.name],
              })}
            </div>
          )}
        </div>
      </th>
    );
  };
  return (
    <div>
      <Table striped bordered hover className={className}>
        <thead>
          <tr>
            {columns.map((column, i) => makeHeader(column, i.toString()))}
          </tr>
        </thead>
        <tbody>
          {isLoading ? (
            <tr>
              <td>Chargement en cours</td>
            </tr>
          ) : getList(data)?.length === 0 ? (
            <tr>
              <td>Aucune donnée</td>
            </tr>
          ) : (
            <>{getList(data)?.map(makeRow)}</>
          )}
        </tbody>
      </Table>
      <Pagination
        isLoading={isLoading}
        count={getCount(data)}
        paginationState={[pagination, setPagination]}
        dataLength={getList(data)?.length}
      />
    </div>
  );
}

type QueryTableType = {
  query: string;
  queryParams: object[];
  asyncOptions: object[];
  getList: (data: any) => object[];
  getCount: (data: any) => number;
  count?: number;
  paginationState?: [
    { offset: number; limit: number },
    ({ limit, offset }: { limit: number; offset: number }) => void,
  ];
  className?: string;
  columns: columnType[];
  idName: string;
  trClassName?: string;
  filterValuesToVariable?: any;
  filterContext?: any;
  watchedValues?: any;
};

export type columnType = {
  Header?: string | ReactNode;
  Filter?: ReactNode;
  accessor: string | ReactNode;
  sortField?: () => string | string;
  Cell?: ReactNode;
  className?: string;
  classNameHeader?: string;
  width?: string;
};

export default QueryTable;
