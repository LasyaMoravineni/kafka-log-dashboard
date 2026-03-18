import { Component, NgZone, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LogStreamService } from '../../services/log-stream';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-order-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-table.html',
  styleUrls:['./order-table.css']
})
export class OrderTableComponent implements OnInit {

  logs: any[] = [];

  selectedService = '';
  customerId = '';

  connected = false;
  streaming = false;

  logsPerSecond = 0;
  logCounter = 0;

  serviceCounts:any = {
    OrderService:0,
    PaymentService:0,
    AuthService:0,
    InventoryService:0
  };

  logLevels:any = {
    INFO:0,
    WARN:0,
    ERROR:0
  };

  serviceChart:any;
  levelChart:any;
  servicePie:any;
  lineChart:any;

  constructor(
    private logStreamService: LogStreamService,
    private zone: NgZone,
    private cd: ChangeDetectorRef
  ){}

  ngOnInit(): void {

    setInterval(() => {

      if(this.streaming){
        this.logsPerSecond = this.logCounter;
      }else{
        this.logsPerSecond = 0;
      }

      if(this.lineChart){

        this.lineChart.data.labels.push(new Date().toLocaleTimeString());
        this.lineChart.data.datasets[0].data.push(this.logsPerSecond);

        if(this.lineChart.data.labels.length > 20){
          this.lineChart.data.labels.shift();
          this.lineChart.data.datasets[0].data.shift();
        }

        this.lineChart.update();
      }

      this.logCounter = 0;

    },1000);

  }

  initCharts(){

    const serviceCtx = document.getElementById('serviceChart') as any;
    const levelCtx = document.getElementById('levelChart') as any;
    const pieCtx = document.getElementById('servicePie') as any;
    const lineCtx = document.getElementById('lineChart') as any;

    // Bar Chart
    this.serviceChart = new Chart(serviceCtx,{
      type:'bar',
      data:{
        labels:['Order','Payment','Auth','Inventory'],
        datasets:[{
          label:'Logs',
          data:[0,0,0,0]
        }]
      },
      options:{ responsive:true }
    });

    // Log level chart
    this.levelChart = new Chart(levelCtx,{
      type:'doughnut',
      data:{
        labels:['INFO','WARN','ERROR'],
        datasets:[{
          data:[0,0,0]
        }]
      }
    });

    // Service distribution pie
    this.servicePie = new Chart(pieCtx,{
      type:'pie',
      data:{
        labels:['Order','Payment','Auth','Inventory'],
        datasets:[{
          data:[0,0,0,0]
        }]
      }
    });

    // Logs/sec timeline
    this.lineChart = new Chart(lineCtx,{
      type:'line',
      data:{
        labels:[],
        datasets:[{
          label:'Logs/sec',
          data:[]
        }]
      },
      options:{
        responsive:true,
        animation:false
      }
    });

  }

  connectLogs(){

    if(!this.customerId){
      alert("Enter customer ID");
      return;
    }

    if(this.connected){
      return;
    }

    setTimeout(() => {
      this.initCharts();
    }, 100);

    this.streaming = true;

    this.logStreamService.connect(

      this.customerId,

      (log:any)=>{

        if(!this.streaming) return;

        this.zone.run(()=>{

          console.log("Log received:",log);

          this.logs = [log,...this.logs];

          if(this.logs.length > 100){
            this.logs.pop();
          }

          if(this.serviceCounts[log.serviceName] !== undefined){
            this.serviceCounts[log.serviceName]++;
          }

          if(this.logLevels[log.logLevel] !== undefined){
            this.logLevels[log.logLevel]++;
          }

          this.updateCharts();

          this.logCounter++;

          this.cd.detectChanges();

        });

      },

      (metric:any)=>{

        if(!this.streaming) return;

        const parts = metric.split(":");

        const service = parts[0];
        const count = Number(parts[1]);

        this.serviceCounts[service] = count;

        this.updateCharts();

      }

    );

    this.connected = true;

  }

  startStreaming(){
    this.streaming = true;
  }

  stopStreaming(){
    this.streaming = false;
  }

  updateCharts(){

    if(this.serviceChart){

      this.serviceChart.data.datasets[0].data = [
        this.serviceCounts.OrderService,
        this.serviceCounts.PaymentService,
        this.serviceCounts.AuthService,
        this.serviceCounts.InventoryService
      ];

      this.serviceChart.update();

    }

    if(this.servicePie){

      this.servicePie.data.datasets[0].data = [
        this.serviceCounts.OrderService,
        this.serviceCounts.PaymentService,
        this.serviceCounts.AuthService,
        this.serviceCounts.InventoryService
      ];

      this.servicePie.update();

    }

    if(this.levelChart){

      this.levelChart.data.datasets[0].data = [
        this.logLevels.INFO,
        this.logLevels.WARN,
        this.logLevels.ERROR
      ];

      this.levelChart.update();

    }

  }

  filteredLogs(){

    return this.logs.filter(log =>
      this.selectedService === '' ||
      log.serviceName === this.selectedService
    );

  }

}