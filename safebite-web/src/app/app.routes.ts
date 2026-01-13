import { Routes } from '@angular/router';
import { RecetasComponent } from './components/recetas/recetas.component';
import { HeroComponent } from './components/hero/hero.component';

export const routes: Routes = [
  { path: '', component: HeroComponent },       // Al entrar se ve el Hero
  { path: 'recetas', component: RecetasComponent }, // En /recetas se ven las fotos de Firebase
  // Aquí irían las futuras: reviews, contacto...
];