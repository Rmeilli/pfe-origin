import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { StructureCREComponent } from './components/structure-cre/structure-cre.component';
import { ChampCREComponent } from './components/champ-cre/champ-cre.component';
import { CreFileComponent } from './components/cre-file/cre-file.component';
import { ImportStructureComponent } from './components/import-structure/import-structure.component';
import { CreValidationComponent } from './components/cre-validation/cre-validation.component';
import { AuthGuard } from './guards/auth.guard';
import { AuthComponent } from './components/auth/auth.component';

const routes: Routes = [
  {
    path: 'auth',
    component: AuthComponent,
    children: [
      { path: '', redirectTo: 'login', pathMatch: 'full' },
      { path: 'login', component: LoginComponent },

    ]
  },
  {
    path: 'structures',
    component: StructureCREComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'import-structure',
    component: ImportStructureComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'cre-file',
    component: CreFileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'cre-validation',
    component: CreValidationComponent,
    canActivate: [AuthGuard]
  },
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
