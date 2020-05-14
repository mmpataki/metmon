import {
  DataQueryRequest,
  DataQueryResponse,
  DataSourceApi,
  DataSourceInstanceSettings,
  MutableDataFrame,
  FieldType,
} from '@grafana/data';

import * as rm from 'typed-rest-client/RestClient';

import { MyQuery, MyDataSourceOptions } from './types';

export class MetricKey {
  name: string;
  desc: string;
  id: number;
  constructor(name: string, id: number, desc: string) {
    this.name = name;
    this.id = id;
    this.desc = desc;
  }
}

export class Process {
  name: string;
  pg: string;
  rest: rm.RestClient;
  p: Promise<Record<string, MetricKey[]>> | undefined = undefined;
  metricGroups: Record<string, MetricKey[]> = {};
  constructor(name: string, pg: string, rs: rm.RestClient) {
    this.name = name;
    this.rest = rs;
    this.pg = pg;
  }
  addMetric(metricGrp: string, metricKey: MetricKey) {
    if (!this.metricGroups[metricGrp]) {
      this.metricGroups[metricGrp] = [];
    }
    this.metricGroups[metricGrp].push(metricKey);
  }
  async getMetricGroups(): Promise<Record<string, MetricKey[]>> {
    if (this.p !== undefined) {
      return this.p;
    }
    console.log('new m promise');
    this.p = new Promise((resolve, reject) => {
      this.rest.get<ApiResponse<KeysResp>>(`/metmon/meta/metrics/${this.pg}/${this.name}`).then(mresp => {
        if (!mresp.result?.success) {
          return;
        }
        Object.entries(mresp.result.item.keys).forEach((v: [string, number]) => {
          let mg = v[0].split(':')[0];
          let mk = v[0].split(':')[1];
          let mko = new MetricKey(mk, v[1], '');
          this.addMetric(mg, mko);
        });
        resolve(this.metricGroups);
      });
    });
    return this.p;
  }
  getMetricGroup(name: string): Promise<MetricKey[]> {
    return new Promise((resolve, reject) => {
      this.getMetricGroups().then(mg => resolve(this.metricGroups[name]));
    });
  }
}

export class ProcessGroup {
  name: string;
  p: Promise<Process[]> | undefined = undefined;
  processes: Record<string, Process> = {};
  rest: rm.RestClient;
  constructor(name: string, rs: rm.RestClient) {
    this.name = name;
    this.rest = rs;
  }
  addProcess(process: Process) {
    this.processes[process.name] = process;
  }
  async getProcesses(): Promise<Process[]> {
    if (this.p !== undefined) {
      return this.p;
    }
    console.log('new p promise');
    this.p = new Promise((resolve, reject) => {
      this.rest.get<ApiResponse<string[]>>(`/metmon/meta/processes/${this.name}/processes`).then(presp => {
        if (!presp.result?.success) {
          return;
        }
        presp.result.item.forEach(p => {
          let po = new Process(p, this.name, this.rest);
          this.addProcess(po);
        });
        resolve(Object.values(this.processes));
      });
    });
    return this.p;
  }
  getProcess(name: string): Promise<Process> {
    return new Promise((resolve, reject) => {
      this.getProcesses().then(pg => resolve(this.processes[name]));
    });
  }
}

export class MetricsMeta {
  rest: rm.RestClient;
  p: Promise<ProcessGroup[]> | undefined = undefined;
  procGrps: Record<string, ProcessGroup> = {};

  constructor(r: rm.RestClient) {
    this.rest = r;
  }
  addProcessGroup(pg: ProcessGroup) {
    this.procGrps[pg.name] = pg;
  }

  async getProcessGrps(): Promise<ProcessGroup[]> {
    if (this.p !== undefined) {
      return this.p;
    }
    console.log('new pg promise');
    this.p = new Promise((resolve, reject) => {
      this.rest.get<ApiResponse<string[]>>('/metmon/meta/processes/procgroups').then(pgresp => {
        if (!pgresp.result?.success) {
          return;
        }
        pgresp.result?.item.forEach(pg => {
          let pgo = new ProcessGroup(pg, this.rest);
          this.addProcessGroup(pgo);
        });
        resolve(Object.values(this.procGrps));
      });
    });
    return this.p;
  }

  getProcessGrp(name: string): Promise<ProcessGroup> {
    return new Promise((resolve, reject) => {
      this.getProcessGrps().then(pgs => resolve(this.procGrps[name]));
    });
  }
}

interface ApiResponse<RetType> {
  error: string;
  success: boolean;
  item: RetType;
}

interface KeysResp {
  keys: Map<string, number>;
}

class PID {
  process: string;
  processGrp: string;
  constructor(pg: string, p: string) {
    this.process = p;
    this.processGrp = pg;
  }
}

interface MetricEntry {
  key: number;
  value: number;
}

interface Metrics {
  ts: number;
  id: PID;
  records: MetricEntry[];
}

export class DataSource extends DataSourceApi<MyQuery, MyDataSourceOptions> {
  rest: rm.RestClient;
  dsInfo: MyDataSourceOptions;
  metricsMeta: MetricsMeta;

  constructor(instanceSettings: DataSourceInstanceSettings<MyDataSourceOptions>) {
    super(instanceSettings);
    this.dsInfo = instanceSettings.jsonData;
    this.rest = new rm.RestClient('metmonds', this.dsInfo.url);
    this.metricsMeta = new MetricsMeta(this.rest);
  }

  getMetaData() {
    return this.metricsMeta;
  }

  async query(options: DataQueryRequest<MyQuery>): Promise<DataQueryResponse> {
    let refIdMap: Record<string, string> = {};
    let queries: Record<string, number[]> = {};

    options.targets.map(tgt => {
      if (tgt.processGroup === '' || tgt.process === '' || tgt.metric === -1) {
        return;
      }
      let key = tgt.processGroup + ':' + tgt.process;
      if (!queries[key]) {
        queries[key] = [];
      }
      queries[key].push(tgt.metric);
      refIdMap[key + ':' + tgt.metric] = tgt.refId;
    });

    let ret: Record<string, MutableDataFrame> = {};

    let sent = 0;
    let comp = 0;

    await new Promise((resolve, reject) => {
      Object.entries(queries).forEach(async e => {
        this.rest.update;

        sent++;
        let metReq = {
          from: options.range.from.valueOf(),
          id: {
            process: e[0].split(':')[1],
            processGrp: e[0].split(':')[0],
          },
          to: options.range.to.valueOf(),
          keys: e[1],
        };
        console.log(JSON.stringify(metReq));
        let resp = (await this.rest.create<ApiResponse<Metrics[]>>('/metmon/metrics', metReq)).result;

        if (!resp?.success) {
          return;
        }
        resp?.item.forEach(r => {
          let id = r.id.processGrp + ':' + r.id.process;

          r.records.forEach(m => {
            let key = id + ':' + m.key;
            if (ret[key] === undefined) {
              ret[key] = new MutableDataFrame({
                refId: refIdMap[key],
                fields: [
                  { name: 'Time', values: [], type: FieldType.time },
                  { name: 'Value', values: [], type: FieldType.number },
                ],
              });
            }

            //time
            ret[key].fields[0].values.add(r.ts);
            ret[key].fields[1].values.add(m.value);
            comp++;
            if (comp === sent) {
              resolve();
            }
          });
        });
      });
    });

    return { data: Object.values(ret) };
  }

  async testDatasource() {
    // Implement a health check for your data source.
    return {
      status: 'success',
      message: 'Success',
    };
  }
}
