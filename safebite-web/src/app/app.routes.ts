import { Routes } from '@angular/router';
import { PerfilComponent } from './pages/perfil/perfil.component';
import { HeroComponent } from './pages/hero/hero.component';
import { RecetasComponent } from './pages/recetas/recetas.component';
import { RecetaDetalleComponent } from './pages/receta-detalle/receta-detalle.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { BuscadorSuperComponent } from './components/buscador-super/buscador-super.component';
import { SubirRecetaComponent } from './components/subir-receta/subir-receta.component';
import { ContactoComponent } from './pages/contacto/contacto.component';

export const routes: Routes = [
  { path: '', component: HeroComponent },
  { path: 'recetas', component: RecetasComponent },
  { path: 'receta/:id', component: RecetaDetalleComponent },
  { path: 'comparador', component: BuscadorSuperComponent }, 
  { path: 'subir-receta', component: SubirRecetaComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'perfil', component: PerfilComponent },
  { path: 'contacto', component: ContactoComponent },
  { path: '**', redirectTo: '' }
];