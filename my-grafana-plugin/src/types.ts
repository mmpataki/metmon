import { DataQuery, DataSourceJsonData } from '@grafana/data';

export interface MyQuery extends DataQuery {
  processGroup: string;
  process: string;
  mgroup: string;
  metric: number;
}

export const defaultQuery: Partial<MyQuery> = {
  processGroup: '',
  process: '',
  metric: -1,
  mgroup: '',
};

/**
 * These are options configured for each DataSource instance
 */
export interface MyDataSourceOptions extends DataSourceJsonData {
  url: string;
  path?: string;
}

/**
 * Value that is used in the backend, but never sent over HTTP to the frontend
 */
export interface MySecureJsonData {
  apiKey?: string;
}
