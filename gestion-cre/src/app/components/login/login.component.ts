import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  loading: boolean = false;
  authMode: string = 'Keycloak (Nouveau)'; // Mode fixe

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
    
    // Pas besoin d'initialiser le mode d'authentification, il est fixe
  }
  
  // Supprimer la méthode formatAuthModeDisplay
  
  // Supprimer ou désactiver la méthode toggleAuthMode
  toggleAuthMode(event: Event): void {
    event.preventDefault();
    // Ne rien faire, mode fixe
    console.log('Mode d\'authentification fixé sur Keycloak (Nouveau)');
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const { username, password } = this.loginForm.value;
    
    this.authService.login(username, password).subscribe({
      next: (response) => {
        this.router.navigate(['/structures']);
      },
      error: (error) => {
        this.errorMessage = 'Échec de la connexion. Veuillez vérifier vos identifiants.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}