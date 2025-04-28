import { SetStateAction, useState, FC } from "react";
import { DndContext } from "@dnd-kit/core";
import { arrayMove, SortableContext, useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import Table from "react-bootstrap/Table";
import { Container } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import PageTitle from "../Elements/PageTitle/PageTitle.tsx";
import { useGet, usePut } from "../Fetch/useFetch.tsx";
import SubmitFormButtons from "../Form/SubmitFormButtons.tsx";
import { IconDragNDrop, IconInfo, IconSortList } from "../Icon/Icon.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import { NomenclatureInfoEntity } from "../../Entities/PeiEntity.tsx";
import url from "../../module/fetch.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { navigateGoBack } from "../../utils/fonctionsUtils.tsx";
import { URLS } from "../../routes.tsx";

type IdCodeLibelleItem = {
  id: string;
  code: string;
  libelle: string;
};

type SortableItemType = {
  id: string;
  item: IdCodeLibelleItem;
};

const SortableRowIdCodeLibelle: FC<SortableItemType> = ({
  id, // La propriété id doit impérativement s'appeler id
  item,
}) => {
  const { setNodeRef, listeners, transform, transition } = useSortable({ id });

  const styles = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <tr ref={setNodeRef} style={styles}>
      <td>
        <span {...listeners} role="button" className="pe-2">
          <IconDragNDrop />
        </span>
        {item.code}
      </td>
      <td>{item.libelle}</td>
    </tr>
  );
};

type SortableTableIdCodeLibelleType = {
  data: NomenclatureInfoEntity[];
  setData: React.Dispatch<React.SetStateAction<NomenclatureInfoEntity[]>>;
};

const SortableTableIdCodeLibelle = ({
  data,
  setData,
}: SortableTableIdCodeLibelleType) => {
  function dragEndEvent(e: DndContext) {
    const { over, active } = e;
    setData((data) => {
      return arrayMove(
        data,
        data.findIndex((item) => item.id === active.id),
        data.findIndex((item) => item.id === over?.id),
      );
    });
  }

  return (
    <DndContext onDragEnd={dragEndEvent}>
      <Table bordered striped>
        <thead>
          <tr>
            <th>
              Code
              <TooltipCustom
                tooltipText={
                  <>
                    Utilisez la fonctionnalité de glisser / déplacer à
                    l&apos;aide du bouton <IconDragNDrop /> en début de ligne
                    pour placer les éléments dans l&apos;ordre souhaité
                  </>
                }
                tooltipId={"dragDropAnomalie"}
              >
                <IconInfo />
              </TooltipCustom>
            </th>
            <th>Libellé</th>
          </tr>
        </thead>
        <tbody>
          <SortableContext items={data}>
            {data.map((v, index) => (
              <SortableRowIdCodeLibelle id={v.id} item={v} key={index} />
            ))}
          </SortableContext>
        </tbody>
      </Table>
    </DndContext>
  );
};

const SortIdCodeLibelle = ({
  title,
  apiAdressForGetOrder,
  apiAdressForPutUpdateOrder,
}: {
  title: string;
  apiAdressForGetOrder: string;
  apiAdressForPutUpdateOrder: string;
}) => {
  const nomenclatureInfo = useGet(url`${apiAdressForGetOrder}`);

  const [data, setData] = useState<ItemNomenclature[]>(null);
  const [errorMessage, setErrorMessage] = useState<string>(null);
  const { success: successToast, error: errorToast } = useToastContext();

  const navigate = useNavigate();
  const location = useLocation();

  if (nomenclatureInfo.isResolved && data == null) {
    setData(
      nomenclatureInfo.data.map((e, index) => {
        return {
          id: e.id,
          code: e.code,
          libelle: e.libelle,
          index: index,
        };
      }),
    );
  }

  const execute = usePut(
    url`${apiAdressForPutUpdateOrder}`,
    {
      onResolve: () => {
        navigateGoBack(location, navigate, URLS.LIST_ANOMALIE_CATEGORIE);
        successToast("L'élément a bien été déplacé.");
      },
      onReject: async (error: {
        text: () => SetStateAction<null> | PromiseLike<SetStateAction<null>>;
      }) => {
        setErrorMessage(await error.text());
        errorToast(await error.text());
      },
    },
    false,
  );

  const submitList = () => {
    const formattedData = data
      .map((e, index) => {
        return {
          id: e.id,
          idx: index,
        };
      })
      .sort((a, b) => {
        return a.idx - b.idx;
      })
      .map((e) => {
        return e.id;
      });
    execute.run({ listeObjet: formattedData });
  };

  return (
    data && (
      <Container>
        <PageTitle icon={<IconSortList />} title={title} />
        {/* Tableau triable */}
        {errorMessage !== null && (
          <div className="text-danger">{errorMessage}</div>
        )}
        <SortableTableIdCodeLibelle data={data} setData={setData} />
        <SubmitFormButtons onClick={submitList} />
      </Container>
    )
  );
};

export default SortIdCodeLibelle;
