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

interface FeatureFlagDescription {
  name: string;
  description: string;
  dataType: string;
  defaultValue?: unknown | string | object;
  status: string;
  template?: any;
}

export enum DataType {
  BOOLEAN = "BOOLEAN",
  STRING = "STRING",
  NUMBER = "NUMBER",
  JSON_STRING = "JSON_STRING",
  JSON = "JSON",
}

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
