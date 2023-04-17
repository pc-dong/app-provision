import * as districtData from "./data/district.json";
export class Districts {
  getProvinceTree(): Tree[] {
    return districtData.districts.map((province: any) => {
      return {
        value: province.adcode,
        label: province.name,
        children: province.districts?.map((city: any) => {
          return {
            value: city.adcode,
            label: city.name,
            children: [],
          };
        }),
      } as Tree;
    });
  }
}

export interface Tree {
  value: string;
  label: string;
  children?: Tree[];
}
