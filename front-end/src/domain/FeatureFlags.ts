import http from "../utils/http/http";

export interface FeatureFlag {
  id?: string;
  featureKey: string;
  description: FeatureFlagDescription;
}

export interface PageResponse<T> {
  total: number;
  page: number;
  pageSize: number;
  content: T[];
}

export interface FeatureFlagDescription {
  name: string;
  description: string;
  dataType: string;
  defaultValue?: unknown | string | object;
  status: string;
  template?: FeatureFlagTemplate;
}

export interface FeatureFlagTemplate {
  items: FeatureFlagTemplateItem[];
}

export interface FeatureFlagTemplateItem {
  key: string;
  name: string;
  description: string;
  dataType: TemplateDataType;
  defaultValue?: unknown | string | object;
  subItems?: FeatureFlagTemplateItem[];
}

export enum DataType {
  BOOLEAN = "BOOLEAN",
  STRING = "STRING",
  NUMBER = "NUMBER",
  JSON_STRING = "JSON_STRING",
  JSON = "JSON",
}

export enum TemplateDataType {
  BOOLEAN = "BOOLEAN",
  STRING = "STRING",
  NUMBER = "NUMBER",
  OBJECT = "OBJECT",
  LIST_STRING = "LIST_STRING",
  LIST_NUMBER = "LIST_NUMBER",
  LIST_OBJECT = "LIST_OBJECT",
}

export const getDefaultValue = (featureFlag: FeatureFlag): any => {
  if (featureFlag.description.dataType === DataType.BOOLEAN) {
    return featureFlag.description.defaultValue == "true";
  } else if (featureFlag.description.dataType === DataType.NUMBER) {
    return Number(featureFlag.description.defaultValue) || 0;
  } else if (featureFlag.description.dataType === DataType.STRING) {
    return featureFlag.description.defaultValue || "";
  } else if (featureFlag.description.dataType === DataType.JSON_STRING) {
    return featureFlag.description.defaultValue &&
      isJSONString(featureFlag.description.defaultValue as string)
      ? featureFlag.description.defaultValue
      : "{}";
  } else {
    // JSON
    return getJSONDefaultValue(featureFlag.description?.template?.items || []);
  }

  return featureFlag.description.defaultValue || null;
};

const isJSONString = (str: string): boolean => {
  try {
    JSON.parse(str);
    return true;
  } catch (e) {
    return false;
  }
};

export const getJSONDefaultValue = (items: FeatureFlagTemplateItem[]): any => {
  if (!items || items.length === 0) {
    return {};
  }

  const result = {} as any;
  items.forEach((item) => {
    if (item.dataType === TemplateDataType.OBJECT) {
      result[item.key] = getJSONDefaultValue(item.subItems || []);
    } else if (item.dataType === TemplateDataType.LIST_OBJECT) {
      result[item.key] = [getJSONDefaultValue(item.subItems || [])];
    } else if (item.dataType === TemplateDataType.LIST_STRING) {
      result[item.key] = [""];
    } else if (item.dataType === TemplateDataType.LIST_NUMBER) {
      result[item.key] = [0];
    } else if (item.dataType === TemplateDataType.BOOLEAN) {
      result[item.key] = item.defaultValue == "true";
    } else if (item.dataType === TemplateDataType.STRING) {
      result[item.key] = item.defaultValue || "";
    } else if (item.dataType === TemplateDataType.NUMBER) {
      result[item.key] = item.defaultValue || 0;
    } else {
      result[item.key] = item.defaultValue || "";
    }
  });

  return result;
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
      .put(
        this.getUrl(`/feature-flags/${featureFlag.featureKey}`),
        featureFlag.description
      )
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
