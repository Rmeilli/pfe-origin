import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'gestion-cre';
  isLoggedIn = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    // Check if user is logged in
    this.authService.isAuthenticated().subscribe(isAuthenticated => {
      this.isLoggedIn = isAuthenticated;
    });
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        console.log('Logout successful');
        this.isLoggedIn = false;
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        console.error('Logout error:', err);
        // Still navigate to login page even if there's an error
        this.isLoggedIn = false;
        this.router.navigate(['/auth/login']);
      }
    });
  }
}
