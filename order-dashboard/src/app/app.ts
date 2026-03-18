import { Component, signal } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { OrderTableComponent } from './components/order-table/order-table';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule, FormsModule, OrderTableComponent],
  template: `<app-order-table></app-order-table>`,
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('order-dashboard');
}
