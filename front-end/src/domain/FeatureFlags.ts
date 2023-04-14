import http from "../utils/http/http";

export interface FeatureFlag {
  id?: string;
  featureKey: string;
  name: string;
  description?: string;
  status: string;
  template: FeatureFlagTemplate;
}

export interface PageResponse<T> {
  total: number;
  page: number;
  pageSize: number;
  content: T[];
}

export interface FeatureFlagTemplate {
  key?: string;
  name?: string;
  description?: string;
  dataType: TemplateDataType;
  defaultValue?: unknown | string | object;
  items?: FeatureFlagTemplate[];
}

export enum TemplateDataType {
  BOOLEAN = "BOOLEAN",
  STRING = "STRING",
  NUMBER = "NUMBER",
  OBJECT = "OBJECT",
  ARRAY = "ARRAY",
}

export const getDefaultValue = (featureFlag: FeatureFlag): any => {
  return getTemplateDefaultValue(featureFlag.template);
};

export const getTemplateDefaultValue = (
  template: FeatureFlagTemplate
): any | any[] => {
  if (template.dataType === TemplateDataType.OBJECT) {
    const result = {} as any;
    (template.items || []).forEach((item: FeatureFlagTemplate) => {
      result[item.key as string] = getTemplateDefaultValue(item);
    });
    return result;
  } else if (template.dataType === TemplateDataType.ARRAY) {
    const result = [] as any[];
    if (template.items && template.items.length > 0) {
      result.push(getTemplateDefaultValue(template.items[0]));
    }
    return result;
  } else if (template.dataType === TemplateDataType.BOOLEAN) {
    return template.defaultValue == "true" || template.defaultValue == true;
  } else if (template.dataType === TemplateDataType.STRING) {
    return template.defaultValue || "";
  } else if (template.dataType === TemplateDataType.NUMBER) {
    return Number(template.defaultValue) || 0;
  }

  return template.defaultValue || "";
};

export class FeatureFlags {
  constructor(private readonly baseUrl: string = "/api") {}

  private getUrl(path: string) {
    return this.baseUrl + path;
  }

  fetchAll(
    page: number,
    pageSize: number,
    featureKey?: string
  ): Promise<PageResponse<FeatureFlag>> {
    return http
      .get(this.getUrl(`/feature-flags/page`), { page, pageSize, featureKey })
      .then((res: any) => res?.data as PageResponse<FeatureFlag>);
  }

  fetch(featureKey: string): Promise<FeatureFlag> {
    return http
      .get(this.getUrl(`/feature-flags/${featureKey}`), {})
      .then((res: any) => res?.data as FeatureFlag);
  }

  create(featureFlag: FeatureFlag): Promise<FeatureFlag> {
    return http
      .post(this.getUrl(`/feature-flags`), featureFlag)
      .then((res: any) => res?.data as FeatureFlag);
  }

  update(featureFlag: FeatureFlag): Promise<void> {
    return http
      .put(this.getUrl(`/feature-flags/${featureFlag.featureKey}`), featureFlag)
      .then();
  }

  delete(featureKey: string): Promise<void> {
    return http.del(this.getUrl(`/feature-flags/${featureKey}`)).then();
  }

  publish(featureKey: string): Promise<void> {
    return http
      .put(this.getUrl(`/feature-flags/${featureKey}/publish`), null)
      .then();
  }

  disable(featureKey: string): Promise<void> {
    return http
      .put(this.getUrl(`/feature-flags/${featureKey}/disable`), null)
      .then();
  }
}
