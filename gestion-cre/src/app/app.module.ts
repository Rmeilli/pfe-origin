import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { HttpClientModule, provideHttpClient, withFetch, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

// Services
import { AuthService } from './services/auth.service';
import { MockAuthService } from './services/mock-auth.service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { StructureCREComponent } from './components/structure-cre/structure-cre.component';
import { ChampCREComponent } from './components/champ-cre/champ-cre.component';
import { CreFileComponent } from './components/cre-file/cre-file.component';
import { ImportStructureComponent } from './components/import-structure/import-structure.component';
import { CreValidationComponent } from './components/cre-validation/cre-validation.component';
import { CreFileFlowComponent } from './components/cre-file-flow/cre-file-flow.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AuthComponent } from './components/auth/auth.component';
import { SecurityConfig } from './config/security-config';
import { DateInterceptor } from './interceptors/date.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    StructureCREComponent,
    ChampCREComponent,
    CreFileComponent,
    ImportStructureComponent,
    CreValidationComponent,
    CreFileFlowComponent,
    NavbarComponent,
    AuthComponent
    
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule
  ],
  // Make sure your HTTP_INTERCEPTORS are properly registered
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    { provide: HTTP_INTERCEPTORS, useClass: SecurityConfig, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: DateInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
