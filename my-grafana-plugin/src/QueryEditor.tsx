import React, { PureComponent } from 'react';
import { Select, Button } from '@grafana/ui';
import { SelectableValue } from '@grafana/data';
import { QueryEditorProps } from '@grafana/data';
import { DataSource, Process, ProcessGroup, MetricsMeta, MetricKey } from './DataSource';
import { MyDataSourceOptions, MyQuery } from './types';
type Props = QueryEditorProps<DataSource, MyQuery, MyDataSourceOptions>;

export class MyState {
  processGroup: string | undefined = undefined;
  process: string | undefined = undefined;
  mgroup: string | undefined = undefined;
  metric: string | undefined = undefined;

  pgOptions: Array<SelectableValue<string>> = [];
  pOptions: Array<SelectableValue<string>> = [];
  mgOptions: Array<SelectableValue<string>> = [];
  mOptions: Array<SelectableValue<string>> = [];
  constructor(query: MyQuery) {
    this.processGroup = query.processGroup;
    this.process = query.process;
    this.mgroup = query.mgroup;
    this.metric = query.metric + '';
  }
}

export class QueryEditor extends PureComponent<Props, MyState> {
  MC: MetricsMeta;
  query: MyQuery;

  constructor(props: Props) {
    super(props);
    this.query = this.props.query;
    this.state = new MyState(this.query);
    this.MC = props.datasource.getMetaData();
    this.setPgOptions();
  }

  setPgOptions = async () => {
    console.log('===> pg promise');
    this.setState({ pgOptions: [] });
    this.MC.getProcessGrps().then((pgs: ProcessGroup[]) => {
      let newPgs: Array<SelectableValue<string>> = [];
      pgs.forEach((pg: ProcessGroup) => {
        newPgs.push({ label: pg.name, value: pg.name });
      });
      this.setState({ pgOptions: newPgs });
      console.log('<=== pg promise');
      this.setPOptions();
    });
  };

  setPOptions = async () => {
    console.log(`===> p promise ${this.query.processGroup}`);
    this.setState({ pOptions: [] });
    let pg: ProcessGroup = await this.MC.getProcessGrp(this.query.processGroup);
    if (pg === undefined) {
      this.setState({ pOptions: [] });
      return;
    }
    pg.getProcesses().then(ps => {
      let newPs: Array<SelectableValue<string>> = [];
      ps.forEach((p: Process) => newPs.push({ label: p.name, value: p.name }));
      this.setState({ pOptions: newPs });
      console.log(`<=== p promise ${this.query.processGroup}`);
      this.setMgOptions();
    });
  };

  setMgOptions = async () => {
    console.log(`===> mg promise [${this.query.processGroup}, ${this.query.process}]`);
    this.setState({ mgOptions: [] });
    let pg: ProcessGroup = await this.MC.getProcessGrp(this.query.processGroup);
    if (pg === undefined) {
      this.setState({ mgOptions: [] });
      return;
    }

    let p: Process = await pg.getProcess(this.query.process);
    if (p === undefined) {
      this.setState({ mgOptions: [] });
      return;
    }
    p.getMetricGroups().then((mg: Record<string, MetricKey[]>) => {
      let ret: Array<SelectableValue<string>> = [];
      Object.entries(mg).forEach(mge => {
        ret.push({ label: mge[0], value: mge[0] });
      });
      this.setState({ mgOptions: ret });
      console.log(`<=== mg promise [${this.query.processGroup}, ${this.query.process}]`);
      this.setMOptions();
    });
  };

  setMOptions = async () => {
    console.log(`===> m promise [${this.query.processGroup}, ${this.query.process}, ${this.query.mgroup}]`);
    this.setState({ mOptions: [] });
    let pg: ProcessGroup = await this.MC.getProcessGrp(this.query.processGroup);
    if (pg === undefined) {
      this.setState({ mOptions: [] });
      return;
    }

    let p: Process = await pg.getProcess(this.query.process);
    if (p === undefined) {
      this.setState({ mOptions: [] });
      return;
    }

    let mks = await p.getMetricGroup(this.query.mgroup);
    if (mks === undefined) {
      this.setState({ mOptions: [] });
      return;
    }

    let ret: Array<SelectableValue<string>> = [];
    mks.forEach(mk => {
      ret.push({ label: mk.name, value: mk.id + '' });
    });
    this.setState({ mOptions: ret });
    console.log(`<=== m promise [${this.query.processGroup}, ${this.query.process}, ${this.query.mgroup}]`);
  };

  onProcessGroupChange = (value: SelectableValue<string>) => {
    const { onChange, query } = this.props;
    this.query = { ...query, processGroup: value.value + '', process: '', mgroup: '', metric: -1 };
    onChange({ ...query, processGroup: value.value + '' });
    this.setState({ processGroup: value.value, process: undefined, mgroup: undefined, metric: undefined });
    this.setPOptions();
  };

  onProcessChange = (value: SelectableValue<string>) => {
    const { onChange, query } = this.props;
    this.query = { ...query, process: value.value + '', mgroup: '', metric: -1 };
    onChange({ ...this.query });
    this.setState({ process: value.value, mgroup: undefined, metric: undefined });
    this.setMgOptions();
  };

  onMetricGroupChange = (value: SelectableValue<string>) => {
    const { onChange, query } = this.props;
    this.query = { ...query, mgroup: value.value + '', metric: -1 };
    onChange({ ...query, mgroup: value.value + '', metric: -1 });
    this.setState({ mgroup: value.value, metric: undefined });
    this.setMOptions();
  };

  onMetricChange = (value: SelectableValue<string>) => {
    var m = parseInt(value.value + '', 10);
    if (m < 0) {
      return;
    }
    const { onChange, query, onRunQuery } = this.props;
    onChange({ ...query, metric: m });
    this.setState({ metric: value.value });
    onRunQuery();
  };

  render() {
    return (
      <>
        <div className="gf-form">
          <Button>Process Group</Button>
          <Select
            options={this.state.pgOptions}
            onChange={this.onProcessGroupChange}
            isLoading={this.state.pgOptions.length === 0}
            value={this.state.processGroup}
            placeholder="choose"
          />
          &nbsp;&nbsp;
          <Button>Process</Button>
          <Select
            options={this.state.pOptions}
            onChange={this.onProcessChange}
            isLoading={this.state.pOptions.length === 0}
            value={this.state.process}
            placeholder="choose"
          />
        </div>
        <div className="gf-form">
          <Button>Metric Group</Button>
          <Select
            options={this.state.mgOptions}
            isLoading={this.state.mgOptions.length === 0}
            onChange={this.onMetricGroupChange}
            value={this.state.mgroup}
            noOptionsMessage="choose"
            placeholder="choose"
          />
          &nbsp;&nbsp;
          <Button>Metric</Button>
          <Select
            options={this.state.mOptions}
            onChange={this.onMetricChange}
            isLoading={this.state.mOptions.length === 0}
            value={this.state.metric}
            placeholder="choose"
          />
        </div>
      </>
    );
  }
}
