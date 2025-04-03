import { default as classNames } from "classnames";
import { useFormik } from "formik";
import React, { ReactNode, useEffect, useRef, useState } from "react";
import { Table } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import { useLocation, useNavigate } from "react-router-dom";
import { useDebouncedCallback } from "use-debounce";
import url from "../../module/fetch.tsx";
import { usePost } from "../Fetch/useFetch.tsx";
import useQueryParams from "../Fetch/useQueryParams.tsx";
import Pagination, {
  setOffsetToSearchParams,
  usePaginationState,
} from "./Pagination.tsx";
import styles from "./Table.module.css";
import { ButtonType } from "./TableActionColumn.tsx";

export const useSortBy = () => {
  const { sortBy: init = {} }: any = useQueryParams();
  const [sortBy, setSortBy] = useState(init);

  return [sortBy, setSortBy];
};

export const useFilterContext = (initialValues: any) => {
  const { filterBy } = useQueryParams();
  const formik = useFormik({
    initialValues: filterBy ?? initialValues,
    onSubmit: (values) => {
      alert(JSON.stringify(values, null, 2));
    },
    validateOnChange: false,
    validateOnBlur: true,
  });

  const { values, setValues, setFieldValue } = {
    values: formik.values,
    setValues: formik.setValues,
    setFieldValue: formik.setFieldValue,
  };

  const handleChange = (e: any) => {
    formik.handleChange(e);
  };

  return { values, setValues, formik, handleChange, setFieldValue };
};

function QueryTable({
  query,
  queryParams,
  asyncOptions,
  getList = (data) => data?.list,
  getCount = (data) => data?.count,
  columns = [],
  idName = "id",
  displayNone = false,
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
  const { filterBy: initialFilterBy } = useQueryParams();
  const location = useLocation();

  // eslint-disable-next-line react-hooks/rules-of-hooks
  const [sortBy, setSortBy] = useSortBy();
  const [pagination, setPaginationTemp] = usePaginationState(); // eslint-disable-line react-hooks/rules-of-hooks
  let setPagination = setPaginationTemp;
  // eslint-disable-next-line react-hooks/rules-of-hooks
  setPagination = ({ offset, limit }: { offset: number; limit: number }) => {
    setPaginationTemp({ limit, offset });
    navigate(
      {
        search: setOffsetToSearchParams(
          document.location.search,
          limit,
          offset,
        ),
      },
      {
        state: location.state,
      },
    );
  };

  const [filterBy, setFilterBy] = useState(
    filterValuesToVariable(initialFilterBy ?? {}),
  );

  const historyPush = () => {
    const f = filterValuesToVariable(formik?.values);
    const filter = Object.values(f).filter(Boolean).length
      ? JSON.stringify(f)
      : null;
    const sort = Object.values(sortBy).filter(Boolean).length
      ? JSON.stringify(sortBy)
      : null;
    const searchParams = new URLSearchParams(location?.search);
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
      navigate(
        {
          search: decodeURIComponent(searchParams.toString()),
        },
        {
          state: location.state,
        },
      );
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
          search: decodeURIComponent(searchParams.toString()),
        },
        {
          state: location.state,
        },
      );
    }
  };

  const debounceSearch = useDebouncedCallback(historyPush, 500);

  useEffect(() => {
    debounceSearch();
  }, [formik?.values]);

  useEffect(() => {
    historyPush();
  }, [sortBy, pagination.offset, pagination.limit, ...watchedValues]);

  useEffect(() => {
    const searchParams = new URLSearchParams(location?.search);
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
      // navigate({ offset: offsetParams, limit: limitParams }, { replace: true });
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
    const isSortedAsc = sortBy[sortField] === "1";
    const isSortedDesc = sortBy[sortField] === "-1";

    const toggleSort = () => {
      if (!sortField) {
        return;
      }
      setSortBy((prevSortBy) => {
        const newSortBy = { ...prevSortBy };
        if (isSortedAsc) {
          newSortBy[sortField] = "-1";
        } else if (isSortedDesc) {
          delete newSortBy[sortField];
        } else {
          newSortBy[sortField] = "1";
        }
        return newSortBy;
      });
    };
    return (
      <th
        key={key}
        className={classNames("text-center", classNameHeader)}
        style={{ width: width, verticalAlign: "top" }}
      >
        <div className={"d-flex flex-column h-100"}>
          <Row
            className="p-1"
            onClick={toggleSort}
            style={{ cursor: "pointer" }}
          >
            <Col xs={12} className="align-self-start text-nowrap">
              {Header}
              {sortField && (
                <span
                  className={classNames(
                    styles.sort,
                    {
                      [styles.sortActive]: isSortedAsc || isSortedDesc,
                    },
                    "m-1",
                  )}
                >
                  {isSortedAsc ? "▲" : isSortedDesc ? "▼" : "⬍"}
                </span>
              )}
            </Col>
          </Row>

          {Filter && (
            <Row>
              <Col xs={12}>
                {React.cloneElement(Filter, {
                  onChange: ({ name, value }) =>
                    formik.setFieldValue(name, value),
                  onBlur: formik.handleBlur,
                  defaultValue: formik?.values[Filter?.props?.name],
                  value: formik?.values[Filter?.props?.name],
                })}
              </Col>
            </Row>
          )}
        </div>
      </th>
    );
  };
  return (
    <div>
      <Table
        striped
        bordered
        hover
        className={classNames({ "d-none": displayNone }, className)}
      >
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
        className={classNames({ "d-none": displayNone })}
      />
    </div>
  );
}

type QueryTableType = {
  displayNone?: boolean;
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
  accessor: string | (() => ReactNode);
  sortField?: (() => string) | string;
  Cell?: (value: any) => ReactNode;
  className?: string;
  classNameHeader?: string;
  width?: number;
};
export type actionColumnType = columnType & { buttons: Array<ButtonType> };

export default QueryTable;
