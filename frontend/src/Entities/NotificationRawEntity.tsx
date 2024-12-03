export type NotificationRawEntity = {
  typeDestinataire: TypeDestinataireEntity;
  objet: string;
  corps: string;
};

type TypeDestinataireEntity = {
  utilisateurOrganisme?: string[];
  contactOrganisme?: string[];
  contactGestionnaire: boolean;
  saisieLibre?: string[];
};
