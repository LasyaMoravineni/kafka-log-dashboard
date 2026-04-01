import { Component, NgZone, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { LogStreamService } from '../../services/log-stream';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-order-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-table.html',
  styleUrls: ['./order-table.css']
})
export class OrderTableComponent implements OnInit, OnDestroy {

  logs: any[] = [];

  selectedCustomer = '';
  selectedService  = '';

  connected = false;
  streaming = false;

  logsPerSecond = 0;
  logCounter    = 0;

  serviceCounts: any = {
    OrderService:     0,
    PaymentService:   0,
    AuthService:      0,
    InventoryService: 0
  };

  logLevels: any = { INFO: 0, WARN: 0, ERROR: 0 };

  // serviceChart removed — servicePie covers the same data
  levelChart: any;
  servicePie: any;
  lineChart:  any;

  private readonly BASE = 'http://localhost:8080/log';

  constructor(
    private logStreamService: LogStreamService,
    private zone: NgZone,
    private cd: ChangeDetectorRef,
    private http: HttpClient
  ) {}

  ngOnInit(): void {

    setInterval(() => {

      this.logsPerSecond = this.streaming ? this.logCounter : 0;

      if (this.lineChart) {

        this.lineChart.data.labels.push(new Date().toLocaleTimeString());
        this.lineChart.data.datasets[0].data.push(this.logsPerSecond);

        if (this.lineChart.data.labels.length > 20) {
          this.lineChart.data.labels.shift();
          this.lineChart.data.datasets[0].data.shift();
        }

        this.lineChart.update();
      }

      this.logCounter = 0;

    }, 1000);
  }

  ngOnDestroy(): void {
    this.logStreamService.disconnect();
  }

  connectLogs() {

    if (this.connected) return;

    // ✅ Guard at click time — before anything connects
    // if (!this.selectedCustomer && !this.selectedService) {
    //   alert('Select at least one filter before connecting');
    //   return;
    // }

    setTimeout(() => this.initCharts(), 100);

    this.logStreamService.connect(
      this.selectedCustomer,
      this.selectedService,
      (log: any)       => this.onLogReceived(log),
      (metric: string) => this.onMetricReceived(metric)
    );

    this.connected = true;
    this.streaming = false;

    // ✅ Auto-start the backend producer thread on connect
    this.startStreaming();
  }

  // ✅ Called by both dropdowns on (ngModelChange)
  onFilterChange() {

    if (!this.connected) return;

    this.logs = [];
    this.resetCounts();

    this.logStreamService.resubscribe(
      this.selectedCustomer,
      this.selectedService,
      (log: any)       => this.onLogReceived(log),
      (metric: string) => this.onMetricReceived(metric)
    );
  }

  // ✅ Calls backend to start the Kafka producer thread
  startStreaming() {
    this.http.get(this.BASE + '/stream', { responseType: 'text' }).subscribe();
    this.streaming = true;
  }

  // ✅ Calls backend to stop the Kafka producer thread
  stopStreaming() {
    this.http.get(this.BASE + '/stream/stop', { responseType: 'text' }).subscribe();
    this.streaming = false;
  }

  // ── private helpers ──────────────────────────────────────────────────────

  private onLogReceived(log: any) {
    this.zone.run(() => {

      this.logs = [log, ...this.logs];

      if (this.logs.length > 100) this.logs.pop();

      if (log.serviceName && this.serviceCounts[log.serviceName] !== undefined) {
        this.serviceCounts[log.serviceName]++;
      }

      if      (log.logLevel === 'ERROR') this.logLevels.ERROR++;
      else if (log.logLevel === 'WARN')  this.logLevels.WARN++;
      else                               this.logLevels.INFO++;

      this.updateCharts();
      this.logCounter++;
      this.cd.detectChanges();
    });
  }

  private onMetricReceived(metric: string) {
    const parts   = metric.split(':');
    const service = parts[0];
    const count   = Number(parts[1]);

    if (this.serviceCounts[service] !== undefined) {
      this.serviceCounts[service] = count;
      this.updateCharts();
    }
  }

  private resetCounts() {
    this.serviceCounts = { OrderService: 0, PaymentService: 0, AuthService: 0, InventoryService: 0 };
    this.logLevels     = { INFO: 0, WARN: 0, ERROR: 0 };
    this.updateCharts();
  }

  initCharts() {

    // Doughnut — log level distribution
    this.levelChart = new Chart(document.getElementById('levelChart') as any,{
      type:'doughnut',
      data:{
        labels:['INFO','WARN','ERROR'],
        datasets:[{ data:[0,0,0] }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });

    // Pie — service distribution (driven by Kafka Streams KTable)
    this.servicePie = new Chart(document.getElementById('servicePie') as any,{
      type:'pie',
      data:{
        labels:['Order','Payment','Auth','Inventory'],
        datasets:[{ data:[0,0,0,0] }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });

    // Line — logs per second rolling 20s window
    this.lineChart = new Chart(document.getElementById('lineChart') as any,{
      type:'line',
      data:{
        labels:[],
        datasets:[{ label:'Logs/sec', data:[] }]
      },
      options:{
        animation:false,
        responsive:true,
        maintainAspectRatio: false
      }
    });
  }

  updateCharts() {

    if (this.servicePie) {
      this.servicePie.data.datasets[0].data = Object.values(this.serviceCounts);
      this.servicePie.update();
    }

    if (this.levelChart) {
      this.levelChart.data.datasets[0].data = Object.values(this.logLevels);
      this.levelChart.update();
    }
  }
}