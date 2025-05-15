import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mt-4">
      <h1>Dashboard</h1>
      <p>Welcome to your dashboard!</p>
    </div>
  `,
  styles: []
})
export class DashboardComponent {
  constructor() {}
} 