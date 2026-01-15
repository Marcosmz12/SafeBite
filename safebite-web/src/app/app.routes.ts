import { Routes } from '@angular/router';
import { RecetasComponent } from './pages/recetas/recetas.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { RecetaDetalleComponent } from './pages/receta-detalle/receta-detalle.component';
import { HeroComponent } from './pages/hero/hero.component';

export const routes: Routes = [
  { path: '', component: HeroComponent },
  { path: 'recetas', component: RecetasComponent },
  { path: 'receta/:id', component: RecetaDetalleComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '**', redirectTo: '' }
];