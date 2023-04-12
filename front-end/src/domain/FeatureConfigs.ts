import { PageResponse } from "./FeatureFlags";
import http from "../utils/http/http";

export class FeatureConfigs {
  constructor(private readonly baseUrl: string = "/api") {}

  private getUrl(path: string) {
    return this.baseUrl + path;
  }

  fetchAll(
    type: string,
    page: number,
    pageSize: number,
    key?: string
  ): Promise<PageResponse<FeatureConfig>> {
    return http
      .get(this.getUrl(`/configurations/page`), { type, page, pageSize, key })
      .then((res: any) => res?.data as PageResponse<FeatureConfig>);
  }

  fetchById(id: string): Promise<FeatureConfig> {
    return http
      .get(this.getUrl(`/configurations/${id}`), {})
      .then((res: any) => res?.data as FeatureConfig);
  }

  create(config: FeatureConfig): Promise<FeatureConfig> {
    return http
      .post(this.getUrl(`/configurations`), config)
      .then((res: any) => res?.data as FeatureConfig);
  }

  update(id: string, config: FeatureConfig): Promise<FeatureConfig> {
    return http.put(this.getUrl(`/configurations/${id}`), config).then();
  }

  delete(id: string): Promise<void> {
    return http.del(this.getUrl(`/configurations/${id}`), {}).then();
  }

  publish(id: string): Promise<void> {
    return http.put(this.getUrl(`/configurations/${id}/publish`), {}).then();
  }

  disable(id: string): Promise<void> {
    return http.put(this.getUrl(`/configurations/${id}/disable`), {}).then();
  }
}

export interface FeatureConfig {
  id?: string;
  type: string;
  title?: string;
  description?: string;
  key?: string;
  data: any;
  trackingData?: any;
  staticStatus?: StaticStatus;
  status?: string;
  timeRange?: TimeRange;
  displayRule?: DisplayRule;
  priority?: number;
  customerCriteriaCondition?: CustomerCriteriaCondition;
}

interface CustomerCriteriaCondition {
  whiteListCondition?: WhiteListCondition;
  locationCondition?: LocationCondition;
  abTestingCondition?: AbTestingCondition;
}

interface WhiteListCondition {
  whiteList: string[];
}

interface LocationCondition {
  adCodes: string[];
}

interface AbTestingCondition {
  experimentId: string;
  bucketKey: string;
}

interface DisplayRule {
  type: DisplayRuleType;
  times: number;
  dailyConditions?: DailyCondition[];
}

enum DisplayRuleType {
  EVERYTIME = "EVERYTIME",
  FIX_TIMES = "FIX_TIMES",
  DAY_FIX_TIMES = "DAY_FIX_TIMES",
}

interface DailyCondition {
  dayOfWeek: number;
  start: string;
  end: string;
}

interface TimeRange {
  startDate: string;
  endDate: string;
}

enum StaticStatus {
  PUBLISHED = "PUBLISHED",
  DRAFT = "DRAFT",
  DISABLED = "DISABLED",
  DELETED = "DELETED",
}
