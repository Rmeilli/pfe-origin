import { Component } from '@angular/core';
import { AuthService } from "../../services/auth.service";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {
  loginForm: FormGroup;
  registerForm: FormGroup;
  errorMessage: string = '';
  
  constructor(
    private auth: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
    
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  handleLogin() {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;
      this.auth.login(username, password).subscribe({
        next: () => this.router.navigate(['/structures']),
        error: (err) => this.errorMessage = err.message
      });
    }
  }

  handleRegister() {
    if (this.registerForm.valid) {
      const { username, password } = this.registerForm.value;
      this.auth.register(username, password).subscribe({
        next: () => this.router.navigate(['/auth/login']),
        error: (err) => this.errorMessage = err.message
      });
    }
  }
}
