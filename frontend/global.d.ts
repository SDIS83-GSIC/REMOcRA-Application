declare module "*.module.css" {
  interface IClassNames {
    [className: string]: string;
  }
  const classNames: IClassNames;
  export = classNames;
}

declare module "*.png" {
  const src: string;
  export default src;
}

declare module "*.svg" {
  const content: React.ComponentType<React.SVGProps<SVGSVGElement>>;
  export default content;
}
