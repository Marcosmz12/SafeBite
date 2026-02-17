import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
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
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'contacto', component: ContactoComponent },
  // PROTEGEMOS SUBIR RECETA
  { 
    path: 'subir-receta', 
    component: SubirRecetaComponent, 
    canActivate: [authGuard] 
  },
  
  // PROTEGEMOS TAMBIÉN EL PERFIL (Opcional pero recomendado)
  { 
    path: 'perfil', 
    component: PerfilComponent,
    canActivate: [authGuard] 
  },

  { path: '**', redirectTo: '' }
];